package com.cniekirk.traintimes.ui.main

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.base.withFactory
import com.cniekirk.traintimes.databinding.FragmentDepBoardResultsBinding
import com.cniekirk.traintimes.di.Injectable
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.model.ui.DepartureItem
import com.cniekirk.traintimes.ui.adapter.DepartureListAdapter
import com.cniekirk.traintimes.ui.behaviour.FabShrinkingOnScrollListener
import com.cniekirk.traintimes.ui.custom.SwitchTrackTextDrawable
import com.cniekirk.traintimes.ui.viewmodel.HomeViewModel
import com.cniekirk.traintimes.ui.viewmodel.HomeViewModelFactory
import com.cniekirk.traintimes.utils.anim.DepartureListItemAnimtor
import com.cniekirk.traintimes.utils.extensions.cancel
import com.cniekirk.traintimes.utils.extensions.dp
import com.cniekirk.traintimes.utils.extensions.loop
import com.cniekirk.traintimes.utils.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.android.synthetic.main.fragment_dep_board_results.*
import java.util.*
import javax.inject.Inject

private const val TAG = "DepBoardResultsFragment"

class DepBoardResultsFragment: Fragment(R.layout.fragment_dep_board_results), Injectable,
    DepartureListAdapter.DepartureItemClickListener,
    DepartureListAdapter.LoadPreviousItemClickListener,
    DepartureListAdapter.LoadNextItemClickListener {

    @Inject
    lateinit var viewModelFactory: HomeViewModelFactory

    private val binding by viewBinding(FragmentDepBoardResultsBinding::bind)
    private val viewModel: HomeViewModel by activityViewModels { withFactory(viewModelFactory, arguments) }

    private val animatedLoadingIndicator by lazy(LazyThreadSafetyMode.NONE) { binding.loadingIndicator.drawable as AnimatedVectorDrawable }
    private val nrccList = ArrayList<View>()
    private var isFirst = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val backward = MaterialSharedAxis(MaterialSharedAxis.Z,  false)
        reenterTransition = backward

        val forward = MaterialSharedAxis(MaterialSharedAxis.Z,  true)
        exitTransition = forward

        enterTransition = forward
        returnTransition = backward

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            isEnabled = true
            viewModel.clearNrcc()
            findNavController().popBackStack()
        }
    }

    /**
     * [HomeViewModel] [LiveData] observers are registered with [LifecycleOwner] of fragment
     * to avoid unintentionally re-triggering the observer
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.connectionStateEmitter.observe(viewLifecycleOwner, { connected ->
            connected?.let {
                if (connected) {
                    Snackbar.make(binding.root, R.string.reconnected, Snackbar.LENGTH_SHORT).apply {
                        anchorView = binding.snackbarLocation
                        setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorGreen))
                    }.show()
                } else {
                    Snackbar.make(binding.root, R.string.disconnected, Snackbar.LENGTH_LONG).apply {
                        anchorView = binding.snackbarLocation
                        setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorRed))
                    }.show()
                }
            }
        })

        arguments?.let { bundle ->
            val position = bundle.getInt("recentQueries", 999)
            if (position != 999) {
                viewModel.performRecentQuery(position)
            }
        }

        arguments?.let { bundle ->
            val position = bundle.getInt("favourites", 999)
            if (position != 999) {
                viewModel.performFavouriteQuery(position)
            }
        }

        viewModel.depStation.observe(viewLifecycleOwner, { dep ->
            viewModel.destStation.value?.let { dest ->
                dep?.let {
                    binding.routeDescription.text = "${it.crs} to ${dest.crs}"
                }
            } ?: run {
                dep?.let {
                    binding.routeDescription.text = it.stationName
                }
            }
        })

        viewModel.services.observe(viewLifecycleOwner, { service ->

            //service
            val depAdapter =
                DepartureListAdapter(
                    service,
                    this,
                    this,
                    this
                )
            binding.homeServicesList.adapter = depAdapter
            postponeEnterTransition()
            binding.homeServicesList.viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
            // Don't show load previous by default
            binding.homeServicesList.scrollToPosition(1)
            animatedLoadingIndicator.cancel()
        })

        viewModel.nrccMessages.observe(viewLifecycleOwner, { messages ->

            binding.btnShowNrcc.visibility = View.VISIBLE
            binding.nrccBadge.text = "${messages.size}"
            binding.nrccBadge.visibility = View.VISIBLE

        })

        viewModel.failure.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Failure.NoCrsFailure -> {
                    animatedLoadingIndicator.cancel()
                    Snackbar.make(binding.root, "No station selected!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(resources.getColor(R.color.colorRed, null))
                        .show()
                }
            }
        })

        arguments?.let { isFirst = it.getBoolean("isFromSearch") }
        if (isFirst) {
            Log.e(TAG, "GET TRAINS")
            viewModel.getTrains()
        }

        arguments?.clear()

    }

    /**
     * Ensure [FragmentDepBoardResultsBinding] is safe to access by waiting for
     * view creation
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        animatedLoadingIndicator.loop(binding.loadingIndicator)

        binding.homeServicesList.addOnScrollListener(FabShrinkingOnScrollListener(binding.btnFavourites))

        binding.depArr.setOnClickListener {
            binding.depArr.text = if (binding.depArr.text.toString().equals(getString(R.string.departing), true)) {
                getString(R.string.arriving)
            } else {
                getString(R.string.departing)
            }

            viewModel.destStation.value?.let {
                if (it.crs.isEmpty()) {
                    viewModel.saveDestStation(viewModel.depStation.value!!)
                } else {
                    viewModel.saveDepStation(viewModel.destStation.value!!)
                }
            } ?: run {
                viewModel.depStation.value?.let {
                    if (it.crs.isEmpty()) {
                        viewModel.saveDepStation(viewModel.destStation.value!!)
                    } else {
                        viewModel.saveDestStation(viewModel.depStation.value!!)
                    }
                }
            }

            viewModel.getTrains()
        }

        binding.btnBack.setOnClickListener {
            binding.homeServicesList.adapter =
                DepartureListAdapter(
                    emptyList(),
                    this,
                    this,
                    this
                )
            binding.root.findNavController().navigateUp()
        }

        binding.homeServicesList.itemAnimator = DepartureListItemAnimtor(0)
            .withInterpolator(FastOutSlowInInterpolator())
            .withAddDuration(250)
            .withRemoveDuration(250)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.homeServicesList.layoutManager = layoutManager
        binding.homeServicesList.adapter =
            DepartureListAdapter(
                emptyList(),
                this,
                this,
                this
            )
        binding.homeServicesList.addItemDecoration(DividerItemDecoration(home_services_list.context, layoutManager.orientation))

        binding.btnShowNrcc.setOnClickListener {
            val messages = viewModel.nrccMessages.value

//            MaterialAlertDialogBuilder(requireContext())
//                .setView(R.layout.nrcc_messages_alert)
//                .setp

//            if (nrccList.isEmpty()) {
//                messages?.forEachIndexed { i, message ->
//                    Handler(Looper.getMainLooper()).postDelayed({
//                        val inflater = LayoutInflater.from(requireContext())
//                        val messageView: View =
//                            inflater.inflate(R.layout.dynamic_nrcc_message, binding.root, false)
//                        messageView.id = View.generateViewId()
//                        nrccList.add(messageView)
//                        binding.root.addView(messageView, 0)
//
//                        val messageBody: MaterialTextView = messageView.findViewById(R.id.message_body)
//                        messageBody.text = message.message
//
//                        val set = ConstraintSet().apply { clone(binding.rootLayout) }
//                        if (i > 0) {
//                            Log.i(TAG, "${nrccList[nrccList.lastIndex - 1].id} and ${messageView.id}")
//                            set.connect(messageView.id, ConstraintSet.TOP, nrccList[nrccList.lastIndex - 1].id, ConstraintSet.BOTTOM, 50.dp)
//                            set.connect(nrccList[nrccList.lastIndex - 1].id, ConstraintSet.BOTTOM, messageView.id, ConstraintSet.TOP, 0.dp)
//                        } else {
//                            Log.i(TAG, "${binding.depArrChip.id} and ${messageView.id}")
//                            set.connect(messageView.id, ConstraintSet.TOP, binding.depArrChip.id, ConstraintSet.BOTTOM, 8.dp)
//                        }
//                        if (i == messages.lastIndex) {
//                            Log.i(TAG, "Home services: ${binding.homeServicesList.id} and ${messageView.id}")
//                            set.connect(messageView.id, ConstraintSet.BOTTOM, binding.homeServicesList.id, ConstraintSet.TOP, 0.dp)
//                            set.connect(binding.homeServicesList.id, ConstraintSet.TOP, messageView.id, ConstraintSet.BOTTOM, 8.dp)
//                        }
//                        set.connect(messageView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 80.dp)
//                        set.connect(messageView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 80.dp)
//                        set.applyTo(binding.rootLayout)
//                    }, 200)
//                }
//            } else {
//                val set = ConstraintSet().apply { clone(binding.rootLayout) }
//                set.connect(binding.homeServicesList.id, ConstraintSet.TOP, binding.servicesHeader.id, ConstraintSet.BOTTOM, 80.dp)
//                set.applyTo(binding.rootLayout)
//                nrccList.forEach {
//                    (it.parent as ViewGroup).removeView(it)
//                }
//                nrccList.clear()
//            }

        }

        binding.btnFavourites.setOnClickListener {
            viewModel.saveFavouriteRoute()
            binding.btnFavourites.hide()
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dep_board_results, container, false)
    }

    override fun onClick(position: Int, itemBackground: View, destinationText: MaterialTextView) {

        isFirst = false

//        val bgName = "${getString(R.string.departure_background_transition)}-$position"
//
//        val navigateBundle = bundleOf("backgroundTransName" to bgName)
        viewModel.services.value?.let { services ->
            val service = services[position] as DepartureItem.DepartureServiceItem
            viewModel.setServiceId(service.service.rid)
        }

//        val extras = FragmentNavigatorExtras(
//            (itemBackground as ConstraintLayout) to bgName
//        )

        binding.root.findNavController().navigate(R.id.serviceDetailFragment,
            null)

    }

    override fun onPause() {
        viewModel.clearServices()
        super.onPause()
    }

    override fun onPreviousClick() {
        viewModel.getPreviousTrains()
    }

    override fun onNextClick() {

    }

}