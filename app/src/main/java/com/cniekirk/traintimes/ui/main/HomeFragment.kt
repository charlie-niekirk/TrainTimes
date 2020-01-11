package com.cniekirk.traintimes.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
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
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

class HomeFragment : Fragment(), Injectable {

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
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(HomeViewModel::class.java)
        viewModel.services.observe(this, Observer { service ->
            root_motion.setTransitionListener(object : MotionLayout.TransitionListener {
                override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
                override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
                override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                    loading_bar.alpha = 0f
                }
            })
            val depAdapter = DepartureListAdapter(service)
            home_services_list.adapter = depAdapter
        })
        viewModel.depStation.observe(this, Observer {
            search_dep_text.text = it.crs
        })
        viewModel.destStation.observe(this, Observer {
            search_dest_text.text = it.crs
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireContext())
        home_services_list.layoutManager = layoutManager
        home_services_list.adapter = DepartureListAdapter(emptyList())
        home_services_list.addItemDecoration(DividerItemDecoration(home_services_list.context, layoutManager.orientation))
        home_services_list.itemAnimator = DepartureListItemAnimtor(0)
            .withInterpolator(FastOutSlowInInterpolator())
            .withAddDuration(250)
            .withRemoveDuration(250)

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
            viewModel.getDepartures()
        }
    }

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
            Log.d("LOL", "HELLO WANKER 2")
            root_motion.setTransition(R.id.middle, R.id.end)
            root_motion.setTransitionListener(object : MotionLayout.TransitionListener {

                override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
                override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
                override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                    Log.d("LOL", "HELLO WANKER 3")
                    resetAnim()
                }
            })

            root_motion.transitionToEnd()
        }
    }

}
