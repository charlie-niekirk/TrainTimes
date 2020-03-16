package com.cniekirk.traintimes.ui.main

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.databinding.FragmentStationSearchBinding
import com.cniekirk.traintimes.di.Injectable
import com.cniekirk.traintimes.ui.adapter.StationListAdapter
import com.cniekirk.traintimes.utils.extensions.hideKeyboard
import com.cniekirk.traintimes.utils.extensions.onFocusChange
import com.cniekirk.traintimes.utils.viewBinding
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class StationSearchFragment: Fragment(R.layout.fragment_station_search), Injectable, StationListAdapter.OnStationItemSelected {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val binding by viewBinding(FragmentStationSearchBinding::bind)
    private lateinit var viewModel: HomeViewModel

    private var isDeparture: Boolean = false

    override fun onResume() {
        super.onResume()
        viewModel.getCrsCodes()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(HomeViewModel::class.java)
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
        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.searchDepStations.doAfterTextChanged {
            GlobalScope.launch {
                viewModel.queryChannel.send(it.toString())
            }
        }
    }

    override fun onStationItemClicked(crs: CRS) {
        hideKeyboard()
        if (isDeparture) {
            viewModel.depStation.value = crs
        } else {
            viewModel.destStation.value = crs
        }
        requireActivity().onBackPressed()
    }

}