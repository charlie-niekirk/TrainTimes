package com.cniekirk.traintimes.ui.main

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.base.withFactory
import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.databinding.FragmentStationSearchBinding
import com.cniekirk.traintimes.di.Injectable
import com.cniekirk.traintimes.ui.adapter.StationListAdapter
import com.cniekirk.traintimes.utils.extensions.hideKeyboard
import com.cniekirk.traintimes.utils.viewBinding
import com.cniekirk.traintimes.ui.viewmodel.HomeViewModel
import com.cniekirk.traintimes.ui.viewmodel.HomeViewModelFactory
import com.cniekirk.traintimes.utils.extensions.onFocusChange
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class StationSearchFragment: Fragment(R.layout.fragment_station_search), Injectable, StationListAdapter.OnStationItemSelected {

    @Inject
    lateinit var viewModelFactory: HomeViewModelFactory

    private val binding by viewBinding(FragmentStationSearchBinding::bind)
    private val viewModel: HomeViewModel by activityViewModels { withFactory(viewModelFactory, arguments) }

    private var isDeparture: Boolean = false

    override fun onResume() {
        super.onResume()
        viewModel.getCrsCodes()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.listenForNewSearch()
        viewModel.crsStationCodes.observe(viewLifecycleOwner, {
            // Hide animation
            binding.loadingAnimation.visibility = View.GONE
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

        val backward =  MaterialSharedAxis(MaterialSharedAxis.Z,  false)
        returnTransition = backward

        val forward =  MaterialSharedAxis(MaterialSharedAxis.Z,  true)
        enterTransition = forward

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.stationList.layoutManager = LinearLayoutManager(requireContext())
        binding.stationList.adapter =
            StationListAdapter(
                emptyList(),
                this
            )
        binding.btnBack.setOnClickListener { it.findNavController().popBackStack() }
        binding.searchDepStations.requestFocus()
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
            viewModel.saveDepStation(crs)
        } else {
            //viewModel.destStation.value = crs
            viewModel.saveDestStation(crs)
        }
        binding.root.findNavController().popBackStack()
    }

}