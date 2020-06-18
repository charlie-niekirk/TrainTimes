package com.cniekirk.traintimes.ui.planner

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.base.withFactory
import com.cniekirk.traintimes.databinding.FragmentPlannerResultsBinding
import com.cniekirk.traintimes.di.Injectable
import com.cniekirk.traintimes.ui.adapter.JourneyPlanAdapter
import com.cniekirk.traintimes.ui.viewmodel.JourneyPlannerViewModel
import com.cniekirk.traintimes.ui.viewmodel.JourneyPlannerViewModelFactory
import com.cniekirk.traintimes.utils.anim.DepartureListItemAnimtor
import com.cniekirk.traintimes.utils.viewBinding
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.android.synthetic.main.fragment_home.loading_indicator
import kotlinx.android.synthetic.main.fragment_planner_results.*
import javax.inject.Inject

class JourneyPlannerResultsFragment: Fragment(R.layout.fragment_planner_results), Injectable {

    @Inject
    lateinit var viewModelFactory: JourneyPlannerViewModelFactory

    private val binding by viewBinding(FragmentPlannerResultsBinding::bind)
    private val viewModel: JourneyPlannerViewModel by activityViewModels { withFactory(viewModelFactory, arguments) }

    private val avd by lazy(LazyThreadSafetyMode.NONE) { binding.loadingIndicator.drawable as AnimatedVectorDrawable }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startLoadingAnim()

        binding.btnBack.setOnClickListener { it.findNavController().navigateUp() }
        binding.routeDescription.text = getString(R.string.route_desc_template,
            viewModel.depStation.value?.stationName,
            viewModel.destStation.value?.stationName)
        binding.journeyList.itemAnimator = DepartureListItemAnimtor(0)
            .withInterpolator(FastOutSlowInInterpolator())
            .withAddDuration(250)
            .withRemoveDuration(250)
        binding.journeyList.layoutManager = LinearLayoutManager(requireContext())
        binding.journeyList.adapter = JourneyPlanAdapter(mutableListOf(), false)
        binding.journeyList.addItemDecoration(
            DividerItemDecoration(journey_list.context, (binding.journeyList.layoutManager as LinearLayoutManager).orientation))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.failure.observe(viewLifecycleOwner, Observer {
            avd.clearAnimationCallbacks()
            avd.stop()
        })

        viewModel.journeyPlannerResponse.observe(viewLifecycleOwner, Observer {
            avd.clearAnimationCallbacks()
            avd.stop()
            it?.let { response ->
                response.outwardJourney?.let { journeys ->
                    Log.e("RESULTS", "Preferences should show price: ${viewModel.shouldShowPrice()}")
                    binding.journeyList.adapter = JourneyPlanAdapter(journeys.toMutableList(), viewModel.shouldShowPrice())
                }
            }
        })
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

    override fun onPause() {
        viewModel.journeyPlannerResponse.call()
        super.onPause()
    }

}