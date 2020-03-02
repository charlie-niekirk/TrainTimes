package com.cniekirk.traintimes.ui.main

import android.content.res.Resources
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.databinding.FragmentHomeBinding
import com.cniekirk.traintimes.di.Injectable
import com.cniekirk.traintimes.utils.anim.DepartureListItemAnimtor
import com.cniekirk.traintimes.utils.extensions.dp
import com.cniekirk.traintimes.utils.viewBinding
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

class HomeFragment : Fragment(R.layout.fragment_home), Injectable,
    DepartureListAdapter.DepartureItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val binding by viewBinding(FragmentHomeBinding::bind)
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
            val depAdapter = DepartureListAdapter(service, this)
            binding.homeServicesList.adapter = depAdapter
            postponeEnterTransition()
            binding.homeServicesList.viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
            val avd = binding.loadingIndicator.drawable as AnimatedVectorDrawable
            avd.stop()
        })

        viewModel.depStation.observe(viewLifecycleOwner, Observer {
            binding.searchDepText.text = it.crs
        })

        viewModel.destStation.observe(viewLifecycleOwner, Observer {
            binding.searchDestText.text = it.crs
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val backward = MaterialSharedAxis.create(requireContext(), MaterialSharedAxis.Z, false)
//        enterTransition = backward
//
//        val forward = MaterialSharedAxis.create(requireContext(), MaterialSharedAxis.Z, true)
//        exitTransition = forward
        // TODO: Why is this not working?
        binding.homeServicesList.itemAnimator = DepartureListItemAnimtor(0)
            .withInterpolator(FastOutSlowInInterpolator())
            .withAddDuration(250)
            .withRemoveDuration(250)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.homeServicesList.layoutManager = layoutManager
        binding.homeServicesList.adapter = DepartureListAdapter(emptyList(), this)
        binding.homeServicesList.addItemDecoration(DividerItemDecoration(home_services_list.context, layoutManager.orientation))

        binding.searchSelectDepStation.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.searchSelectDepStation
                    to getString(R.string.dep_search_transition))
            view.findNavController().navigate(R.id.stationSearchFragment,
                bundleOf("isDeparture" to true), null, extras)
        }

        binding.searchSelectDestStation.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.searchSelectDestStation
                    to getString(R.string.dep_search_transition))
            view.findNavController().navigate(R.id.stationSearchFragment,
                bundleOf("isDeparture" to false), null, extras)
        }

        binding.searchButton.setOnClickListener {
            startLoadingAnim()
            // Remove old items to make the UX more seamless
            binding.homeServicesList.adapter = DepartureListAdapter(emptyList(), this)
            viewModel.getDepartures()
            //viewModel.getJourneyPlan()
        }

        binding.homeBtnSettings.setOnClickListener {
            view.findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
    }

    /**
     * Start the loading animation, looping it with the listeners
     */
    private fun startLoadingAnim() {
        if (loading_indicator.drawable is AnimatedVectorDrawable) {
            val avd = binding.loadingIndicator.drawable as AnimatedVectorDrawable
            avd.start()
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
