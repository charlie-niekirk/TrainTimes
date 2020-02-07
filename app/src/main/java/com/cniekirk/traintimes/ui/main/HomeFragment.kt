package com.cniekirk.traintimes.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.di.Injectable
import com.cniekirk.traintimes.utils.anim.DepartureListItemAnimtor
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

class HomeFragment : Fragment(), Injectable, DepartureListAdapter.DepartureItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(HomeViewModel::class.java)

        viewModel.services.observe(viewLifecycleOwner, Observer { service ->
            root_motion.setTransitionListener(object : MotionLayout.TransitionListener {
                override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
                override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
                override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                    loading_bar.alpha = 0f
                }
            })
            val depAdapter = DepartureListAdapter(service, this)
            home_services_list.adapter = depAdapter
            postponeEnterTransition()
            home_services_list.viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        })

        viewModel.depStation.observe(viewLifecycleOwner, Observer {
            search_dep_text.text = it.crs
            search_arrow_dep.apply {
                setImageDrawable(requireContext().getDrawable(R.drawable.ic_clear))
                // TODO: Replace with dimen
                layoutParams.width = 30
            }
        })

        viewModel.destStation.observe(viewLifecycleOwner, Observer {
            search_dest_text.text = it.crs
            search_arrow_dest.apply {
                setImageDrawable(requireContext().getDrawable(R.drawable.ic_clear))
                // TODO: Replace with dimen
                val lp = layoutParams as ConstraintLayout.LayoutParams
                lp.width = 30
                lp.height = 30
                layoutParams = lp
                requestLayout()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Why is this not working?
        home_services_list.itemAnimator = DepartureListItemAnimtor(0)
            .withInterpolator(FastOutSlowInInterpolator())
            .withAddDuration(250)
            .withRemoveDuration(250)

        val layoutManager = LinearLayoutManager(requireContext())
        home_services_list.layoutManager = layoutManager
        home_services_list.adapter = DepartureListAdapter(emptyList(), this)
        home_services_list.addItemDecoration(DividerItemDecoration(home_services_list.context, layoutManager.orientation))

        search_select_dep_station.setOnClickListener {
            val extras = FragmentNavigatorExtras(search_select_dep_station
                    to getString(R.string.dep_search_transition))
            view.findNavController().navigate(R.id.stationSearchFragment,
                bundleOf("isDeparture" to true), null, extras)
        }

        search_select_dest_station.setOnClickListener {
            val extras = FragmentNavigatorExtras(search_select_dest_station
                    to getString(R.string.dep_search_transition))
            view.findNavController().navigate(R.id.stationSearchFragment,
                bundleOf("isDeparture" to false), null, extras)
        }

        search_button.setOnClickListener {
            startLoadingAnim()
            // Remove old items to make the UX more seamless
            home_services_list.adapter = DepartureListAdapter(emptyList(), this)
            viewModel.getDepartures()
            viewModel.getJourneyPlan()
        }
    }

    /**
     * Start the loading animation, looping it with the listeners
     */
    private fun startLoadingAnim() {
        root_motion.setTransition(R.id.start, R.id.middle)
        root_motion.setTransitionListener(listener)
        root_motion.transitionToEnd()
    }

    private fun resetAnim() {
        root_motion.rebuildScene()
        root_motion.setTransition(R.id.start, R.id.middle)
        root_motion.setTransitionListener(listener)
        root_motion.transitionToEnd()
    }

    private val listener = object : MotionLayout.TransitionListener {
        override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
        override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

        override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
            root_motion.setTransition(R.id.middle, R.id.end)
            root_motion.setTransitionListener(object : MotionLayout.TransitionListener {

                override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
                override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
                override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                    resetAnim()
                }
            })

            root_motion.transitionToEnd()
        }
    }

    override fun onClick(position: Int, itemBackground: View, destinationText: MaterialTextView) {

        val bgName = "${getString(R.string.departure_background_transition)}-$position"
        val destTransName = "${getString(R.string.departure_text_transition)}-$position"

        val navigateBundle = bundleOf("backgroundTransName" to bgName, "destTransName" to destTransName)
        viewModel.services.value?.let { services ->
            viewModel.serviceDetailId.value = services[position].serviceID
        }

        val extras = FragmentNavigatorExtras(
            (itemBackground as ConstraintLayout) to bgName,
            destinationText to destTransName
        )

        view?.findNavController()?.navigate(R.id.serviceDetailFragment,
            navigateBundle, null, extras)
    }

}
