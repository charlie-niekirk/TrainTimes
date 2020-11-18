package com.cniekirk.traintimes.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.PorterDuff
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.base.withFactory
import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.databinding.FragmentDepBoardResultsBinding
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.domain.model.State
import com.cniekirk.traintimes.model.ui.Action
import com.cniekirk.traintimes.model.ui.DepartureItem
import com.cniekirk.traintimes.ui.adapter.DepartureListAdapter
import com.cniekirk.traintimes.ui.adapter.PopupActionAdapter
import com.cniekirk.traintimes.ui.behaviour.FabShrinkingOnScrollListener
import com.cniekirk.traintimes.ui.viewmodel.HomeViewModel
import com.cniekirk.traintimes.ui.viewmodel.HomeViewModelFactory
import com.cniekirk.traintimes.utils.Blur
import com.cniekirk.traintimes.utils.anim.DepartureListItemAnimtor
import com.cniekirk.traintimes.utils.anim.SwooshInterpolator
import com.cniekirk.traintimes.utils.extensions.cancel
import com.cniekirk.traintimes.utils.extensions.dp
import com.cniekirk.traintimes.utils.extensions.loop
import com.cniekirk.traintimes.utils.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_dep_board_results.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DepBoardResultsFragment: Fragment(R.layout.fragment_dep_board_results),
    DepartureListAdapter.DepartureItemClickListener,
    DepartureListAdapter.LoadPreviousItemClickListener,
    DepartureListAdapter.LoadNextItemClickListener {

    @Inject
    lateinit var viewModelFactory: HomeViewModelFactory

    private val binding by viewBinding(FragmentDepBoardResultsBinding::bind)
    private val viewModel: HomeViewModel by activityViewModels { withFactory(viewModelFactory, arguments) }

    private val animatedLoadingIndicator by lazy(LazyThreadSafetyMode.NONE) { binding.loadingIndicator.drawable as AnimatedVectorDrawable }
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
            viewModel.clearServices()
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
                    if (it.crs.equals("", true)) {
                        binding.routeDescription.text = dest.stationName
                    } else {
                        binding.routeDescription.text = "${it.crs} to ${dest.crs}"
                    }
                } ?: run {
                    binding.routeDescription.text = dest.stationName
                }
            } ?: run {
                dep?.let {
                    binding.routeDescription.text = it.stationName
                }
            }
        })

        viewModel.state.observe(viewLifecycleOwner, { state ->
            when(state) {
                is State.Loading -> animatedLoadingIndicator.loop(binding.loadingIndicator)
                is State.Idle -> animatedLoadingIndicator.cancel()
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

        })

        viewModel.nrccMessages.observe(viewLifecycleOwner, { messages ->

            if (!messages.isNullOrEmpty()) {
                binding.btnShowNrcc.visibility = View.VISIBLE
                binding.nrccBadge.text = "${messages.size}"
                binding.nrccBadge.visibility = View.VISIBLE
            }

        })

        viewModel.failure.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Failure.NoCrsFailure -> {
                    animatedLoadingIndicator.cancel()
                    Snackbar.make(binding.root, "No station selected!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(resources.getColor(R.color.colorRed, null))
                        .show()
                }
                else -> {
                    animatedLoadingIndicator.cancel()
                    Snackbar.make(binding.root, "Unknown error!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(resources.getColor(R.color.colorRed, null))
                        .show()
                }
            }
        })

        arguments?.let { isFirst = it.getBoolean("isFromSearch") }
        if (isFirst) {
            Timber.d( "Getting Trains...")
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

        binding.homeServicesList.addOnScrollListener(FabShrinkingOnScrollListener(binding.btnFavourites))

        binding.depArr.setOnClickListener {
            binding.depArr.text = if (binding.depArr.text.toString().equals(getString(R.string.departing), true)) {
                getString(R.string.arriving)
            } else {
                getString(R.string.departing)
            }

            viewModel.destStation.value?.let {
                if (it.crs.isEmpty()) {
                    Timber.i("Dest good, but empty, saving null to dest")
                    viewModel.saveDestStation(viewModel.depStation.value!!)
                    viewModel.clearDepStation()
                } else {
                    Timber.i("Good dest, saving to dep")
                    viewModel.saveDepStation(it)
                    viewModel.clearDestStation()
                }
            } ?: run {
                viewModel.depStation.value?.let {
                    if (it.crs.isEmpty()) {
                        Timber.i("Dep good, but empty, saving null to dep")
                        viewModel.saveDepStation(viewModel.destStation.value!!)
                        viewModel.clearDestStation()
                    } else {
                        Timber.i("Good dep, saving to dest")
                        viewModel.saveDestStation(it)
                        viewModel.clearDepStation()
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
            viewModel.clearServices()
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

            val alertMsg = messages?.map { it.message }?.joinToString(separator = "\n\n")

            if (!alertMsg.isNullOrEmpty()) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(alertMsg)
                    .setTitle(R.string.nrcc_alert_title)
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                    }.show()
            }

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

        viewModel.services.value?.let { services ->
            val service = services[position] as DepartureItem.DepartureServiceItem
            viewModel.setServiceId(service.service.rid)
        }

        binding.root.findNavController().navigate(R.id.serviceDetailFragment, null)

    }

    override fun onLongClick(itemView: View, height: Int, yPos: Int) {

        binding.btnFavourites.hide()
        binding.popupActionsMenu.layoutManager = LinearLayoutManager(requireContext())

        // Blur the bg
        binding.blurTarget.setImageDrawable(Blur.createBlur(requireActivity(), binding.rootLayout))

        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.rootLayout)

        val originalPopupY = binding.popupContainer.y
        val itemXY = IntArray(2)
        itemView.getLocationInWindow(itemXY)
        binding.popupContainer.y = itemXY[1].toFloat()
        binding.popupActionsMenu.y = itemXY[1].toFloat()

        val animatorSet = AnimatorSet()
        val boxAnim = AnimatorSet()

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.apply {
            interpolator = FastOutSlowInInterpolator()
            duration = 200
            addUpdateListener {
                val value = it.animatedValue as Float
                binding.blurTarget.alpha = value
                binding.popupContainer.alpha = value
                binding.popupActionsMenu.alpha = value
            }
        }

        val initialYAnimator = ValueAnimator.ofFloat(binding.popupContainer.y, originalPopupY)
        initialYAnimator.apply {
            interpolator = LinearInterpolator()
            duration = 100
            addUpdateListener {
                binding.popupContainer.y = it.animatedValue as Float
            }
        }

        val heightAnimator = ValueAnimator.ofInt((binding.popupContainer.bottom - binding.popupContainer.top),
            (binding.popupContainer.bottom - binding.popupContainer.top) * 2 + (originalPopupY - (binding.routeDescription.y + 16.dp)).toInt())
        val params = binding.popupContainer.layoutParams
        heightAnimator.apply {
            interpolator = OvershootInterpolator()
            duration = 200
            addUpdateListener {
                params.height = it.animatedValue as Int
                binding.popupContainer.layoutParams = params
            }
        }

        val yAnimator = ValueAnimator.ofFloat(originalPopupY, binding.routeDescription.y + 16.dp)
        yAnimator.interpolator = OvershootInterpolator()
        yAnimator.duration = 200
        yAnimator.addUpdateListener {
            binding.popupContainer.y = it.animatedValue as Float
        }

        val actionsAnimator = ValueAnimator.ofFloat(binding.popupActionsMenu.y,
            binding.popupContainer.bottom.toFloat() +
                    (binding.popupContainer.bottom - binding.popupContainer.top) + 50.dp)
        actionsAnimator.interpolator = OvershootInterpolator()
        actionsAnimator.duration = 200
        actionsAnimator.addUpdateListener {
            binding.popupActionsMenu.y = it.animatedValue as Float
        }

        boxAnim.playTogether(heightAnimator, yAnimator, actionsAnimator)
        animatorSet.playSequentially(animator, initialYAnimator, boxAnim)
        animatorSet.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                binding.popupActionsMenu.adapter = PopupActionAdapter(Action.actions)
            }
        })

        animatorSet.start()

        binding.popupContainer.isClickable = true
        binding.blurTarget.isClickable = true

        binding.blurTarget.setOnClickListener {
            val anim = ValueAnimator.ofFloat(1f, 0f)
            anim.interpolator = FastOutSlowInInterpolator()
            anim.duration = 200
            anim.addUpdateListener {
                binding.blurTarget.alpha = it.animatedValue as Float
                binding.popupContainer.alpha = it.animatedValue as Float
                binding.popupActionsMenu.alpha = it.animatedValue as Float
            }

            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    // Reset all views to initial layout
                    binding.popupActionsMenu.adapter = PopupActionAdapter(emptyList())
                    constraintSet.applyTo(binding.rootLayout)
                    Timber.i("New Y: ${binding.popupContainer.y}")
                    binding.popupContainer.isClickable = false
                    binding.blurTarget.isClickable = false
                    binding.blurTarget.setImageDrawable(null)
                    binding.btnFavourites.show()
                }
            })

            anim.start()
        }
        // Make the custom view visible


        // Animate the custom view into position

        // Load the data?

    }

    override fun onLongClickRelease() {

    }

    override fun onPreviousClick() {
        viewModel.getPreviousTrains()
    }

    override fun onNextClick() {

    }

}