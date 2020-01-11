package com.cniekirk.traintimes.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.di.Injectable
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
            val depAdapter = DepatureListAdapter(service)
            home_services_list.adapter = depAdapter
            depAdapter.notifyDataSetChanged()
        })
        viewModel.depStation.observe(this, Observer {
            search_dep_text.text = it.crs
        })
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(requireContext())
        home_services_list.layoutManager = layoutManager
        home_services_list.adapter = DepatureListAdapter(emptyList())
        home_services_list.addItemDecoration(DividerItemDecoration(home_services_list.context, layoutManager.orientation))
        search_select_dep_station.setOnClickListener {
            val extras = FragmentNavigatorExtras(search_select_dep_station
                    to getString(R.string.dep_search_transition))
            view.findNavController().navigate(R.id.depStationSearchFragment, null, null, extras)
        }
        search_select_dest_station.setOnClickListener {
            // Go to detail view
        }
        search_button.setOnClickListener {
            no_train_services_placeholder_text.visibility = View.GONE
            viewModel.getDepartures()
        }
    }

}
