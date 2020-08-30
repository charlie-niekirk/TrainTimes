package com.cniekirk.traintimes.ui.main

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.base.withFactory
import com.cniekirk.traintimes.databinding.FragmentDepBoardResultsBinding
import com.cniekirk.traintimes.di.Injectable
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.ui.adapter.DepartureListAdapter
import com.cniekirk.traintimes.ui.viewmodel.HomeViewModel
import com.cniekirk.traintimes.ui.viewmodel.HomeViewModelFactory
import com.cniekirk.traintimes.utils.anim.DepartureListItemAnimtor
import com.cniekirk.traintimes.utils.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.android.synthetic.main.fragment_dep_board_results.*
import javax.inject.Inject

private const val TAG = "DepBoardResultsFragment"

class DepBoardResultsFragment: Fragment(R.layout.fragment_dep_board_results), Injectable,
    DepartureListAdapter.DepartureItemClickListener {

    @Inject
    lateinit var viewModelFactory: HomeViewModelFactory

    private val binding by viewBinding(FragmentDepBoardResultsBinding::bind)
    private val viewModel: HomeViewModel by activityViewModels { withFactory(viewModelFactory, arguments) }

    private val avd by lazy(LazyThreadSafetyMode.NONE) { binding.loadingIndicator.drawable as AnimatedVectorDrawable }
    private var isFirst = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val backward =  MaterialSharedAxis(MaterialSharedAxis.Z,  false)
        exitTransition = backward

        val forward =  MaterialSharedAxis(MaterialSharedAxis.Z,  true)
        enterTransition = forward
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // VM stuff
        viewModel.connectionStateEmitter.observe(viewLifecycleOwner, { connected ->
            connected?.let {
                if (connected) {
                    val sb = Snackbar.make(binding.root, R.string.reconnected, Snackbar.LENGTH_SHORT)
                    sb.anchorView = binding.snackbarLocation
                    sb.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorGreen))
                    sb.show()
                } else {
                    val sb = Snackbar.make(binding.root, R.string.disconnected, Snackbar.LENGTH_LONG)
                    sb.anchorView = binding.snackbarLocation
                    sb.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorRed))
                    sb.show()
                }
            }
        })

        arguments?.let { bundle ->
            val position = bundle.getInt("recentQueries", 999)
            if (position != 999) {
                viewModel.performRecentQuery(position)
            }
        }

        viewModel.depStation.observe(viewLifecycleOwner, { dep ->
            viewModel.destStation.value?.let { dest ->
                dep?.let {
                    binding.routeDescription.text = "${it.crs} to ${dest.crs}"
                }
            } ?: run {
                dep?.let {
                    binding.routeDescription.text = "${it.stationName}"
                }
            }
        })

        viewModel.services.observe(viewLifecycleOwner, { service ->
            val depAdapter =
                DepartureListAdapter(
                    service,
                    this
                )
            binding.homeServicesList.adapter = depAdapter
            postponeEnterTransition()
            binding.homeServicesList.viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
            avd.apply {
                clearAnimationCallbacks()
                stop()
                reset()
            }
        })

        viewModel.failure.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Failure.NoCrsFailure -> {
                    avd.clearAnimationCallbacks()
                    avd.stop()
                    Snackbar.make(binding.root, "No station selected!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(resources.getColor(R.color.colorRed, null))
                        .show()
                }
            }
        })

        arguments?.let { isFirst = it.getBoolean("isFromSearch") }
        if (isFirst) {
            viewModel.getTrains()
        }

        arguments?.clear()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Binding stuff
        startLoadingAnim()

        binding.btnBack.setOnClickListener {
            binding.homeServicesList.adapter =
                DepartureListAdapter(
                    emptyList(),
                    this
                )
            it.findNavController().navigateUp()
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
                this
            )
        binding.homeServicesList.addItemDecoration(DividerItemDecoration(home_services_list.context, layoutManager.orientation))

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dep_board_results, container, false)
    }

    /**
     * Start the loading animation, looping it with the listeners
     */
    private fun startLoadingAnim() {
        if (loading_indicator.drawable is AnimatedVectorDrawable) {
            avd.registerAnimationCallback(object: Animatable2.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    avd.start()
                }
            })
            avd.start()
        }
    }

    override fun onClick(position: Int, itemBackground: View, destinationText: MaterialTextView) {

        isFirst = false

        val bgName = "${getString(R.string.departure_background_transition)}-$position"

        val navigateBundle = bundleOf("backgroundTransName" to bgName)
        viewModel.services.value?.let { services ->
            viewModel.setServiceId(services[position].rid)
        }

        val extras = FragmentNavigatorExtras(
            (itemBackground as ConstraintLayout) to bgName
        )

        view?.findNavController()?.navigate(R.id.serviceDetailFragment,
            navigateBundle, null, extras)

    }

}