package com.cniekirk.traintimes.view.planner

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.base.withFactory
import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.databinding.FragmentPlannerStationSearchBinding
import com.cniekirk.traintimes.databinding.FragmentStationSearchBinding
import com.cniekirk.traintimes.di.Injectable
import com.cniekirk.traintimes.view.adapter.StationListAdapter
import com.cniekirk.traintimes.utils.extensions.hideKeyboard
import com.cniekirk.traintimes.utils.extensions.onFocusChange
import com.cniekirk.traintimes.utils.viewBinding
import com.cniekirk.traintimes.view.viewmodel.HomeViewModel
import com.cniekirk.traintimes.view.viewmodel.HomeViewModelFactory
import com.cniekirk.traintimes.view.viewmodel.JourneyPlannerViewModel
import com.cniekirk.traintimes.view.viewmodel.JourneyPlannerViewModelFactory
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class PlannerStationSearchFragment: Fragment(R.layout.fragment_planner_station_search), Injectable, StationListAdapter.OnStationItemSelected {

    @Inject
    lateinit var viewModelFactory: JourneyPlannerViewModelFactory

    private val binding by viewBinding(FragmentPlannerStationSearchBinding::bind)
    private val viewModel: JourneyPlannerViewModel by activityViewModels { withFactory(viewModelFactory, arguments) }

    private var isDeparture: Boolean = false

    override fun onResume() {
        super.onResume()
        viewModel.getCrsCodes()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.listenForNewSearch()
        viewModel.crsStationCodes.observe(viewLifecycleOwner, Observer {
            it?.let {
                val adapter =
                    StationListAdapter(
                        it,
                        this
                    )
                binding.stationList.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        })
        arguments?.let { isDeparture = it.getBoolean("isDeparture") }
    }

    override fun onPause() {
        // To reset the search screen, horrible I know
        GlobalScope.launch { viewModel.queryChannel.send("") }
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform(requireContext()).apply {
            this.interpolator = interpolator
            this.duration = 350
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchDepStations.onFocusChange { hasFocus ->
            if (hasFocus)
                binding.searchDepStations.setHint(R.string.search_hint_focused)
            else
                binding.searchDepStations.setHint(R.string.station_search_hint)
        }

        binding.stationList.layoutManager = LinearLayoutManager(requireContext())
        binding.stationList.adapter =
            StationListAdapter(
                emptyList(),
                this
            )
        binding.btnBack.setOnClickListener { it.findNavController().popBackStack() }
        binding.searchDepStations.doAfterTextChanged {
            GlobalScope.launch {
                viewModel.queryChannel.send(it.toString())
            }
        }
    }

    override fun onStationItemClicked(crs: CRS) {
        hideKeyboard()
        if (isDeparture) {
            //viewModel.depStation.value = crs
            Log.d("FR", "ACTUALLY SAVED")
            viewModel.saveDepStation(crs)
        } else {
            //viewModel.destStation.value = crs
            Log.d("FR", "ACTUALLY SAVED")
            viewModel.saveDestStation(crs)
        }
        binding.root.findNavController().popBackStack()
    }

}