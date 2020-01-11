package com.cniekirk.traintimes.ui.main

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.ChangeBounds
import androidx.transition.ChangeTransform
import androidx.transition.TransitionSet
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.di.Injectable
import com.cniekirk.traintimes.ui.favourites.StationListAdapter
import com.cniekirk.traintimes.utils.anim.SwooshInterpolator
import com.cniekirk.traintimes.utils.extensions.hideKeyboard
import kotlinx.android.synthetic.main.fragment_dep_search.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class DepStationSearchFragment: Fragment(), Injectable, StationListAdapter.OnStationItemSelected {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: HomeViewModel

    override fun onResume() {
        super.onResume()
        viewModel.getCrsCodes()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(HomeViewModel::class.java)
        viewModel.listenForNewSearch()
        viewModel.crsStationCodes.observe(this, Observer {
            it?.let {
                val adapter = StationListAdapter(it, this)
                station_list.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val interpolator = SwooshInterpolator(350f)
        val set = TransitionSet()
        set.addTransition(ChangeBounds().setInterpolator(interpolator).setDuration(350))
        set.addTransition(ChangeTransform().setInterpolator(interpolator).setDuration(350))
        sharedElementEnterTransition = set

        return inflater.inflate(R.layout.fragment_dep_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        station_list.layoutManager = LinearLayoutManager(requireContext()).apply { isAutoMeasureEnabled = false }
        station_list.adapter = StationListAdapter(emptyList(), this)
        btn_back.setOnClickListener { requireActivity().onBackPressed() }
        search_dep_stations.doAfterTextChanged {
            GlobalScope.launch {
                viewModel.queryChannel.send(it.toString())
            }
        }
    }

    override fun onStationItemClicked(crs: CRS) {
        hideKeyboard()
        viewModel.depStation.value = crs
        requireActivity().onBackPressed()
    }

}