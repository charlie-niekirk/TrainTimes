package com.cniekirk.traintimes.ui.main

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.base.withFactory
import com.cniekirk.traintimes.databinding.FragmentServiceDetailBinding
import com.cniekirk.traintimes.di.Injectable
import com.cniekirk.traintimes.model.getdepboard.res.CallingPoint
import com.cniekirk.traintimes.model.servicedetails.res.GetServiceDetailsResult
import com.cniekirk.traintimes.ui.adapter.StationTimelineAdapter
import com.cniekirk.traintimes.utils.anim.DepartureListItemAnimtor
import com.cniekirk.traintimes.utils.anim.SwooshInterpolator
import com.cniekirk.traintimes.utils.extensions.parseEncoded
import com.cniekirk.traintimes.utils.viewBinding
import com.cniekirk.traintimes.ui.viewmodel.HomeViewModel
import com.cniekirk.traintimes.ui.viewmodel.HomeViewModelFactory
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialContainerTransform.FADE_MODE_CROSS
import com.google.android.material.transition.MaterialSharedAxis
import javax.inject.Inject

class ServiceDetailFragment: Fragment(R.layout.fragment_service_detail), Injectable, StationTimelineAdapter.OnStationItemClickedListener {

    @Inject
    lateinit var viewModelFactory: HomeViewModelFactory

    private val binding by viewBinding(FragmentServiceDetailBinding::bind)
    private val viewModel: HomeViewModel by activityViewModels { withFactory(viewModelFactory, arguments) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            fadeMode = FADE_MODE_CROSS
            interpolator = SwooshInterpolator(270f)
            duration = 270
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.serviceDetailsResult.observe(viewLifecycleOwner, Observer { serviceDetailsResult ->
            val destinationIndex = serviceDetailsResult.subsequentCallingPoints?.subsequentCallingPoints?.get(0)?.callingPoints?.lastIndex
            val destination = serviceDetailsResult.subsequentCallingPoints?.subsequentCallingPoints?.get(0)?.callingPoints!![destinationIndex ?: 0]
            val previousCallingPoints = serviceDetailsResult.previousCallingPoints?.previousCallingPoints?.get(0)?.callingPoints
            val subsequentCallingPoints = serviceDetailsResult.subsequentCallingPoints.subsequentCallingPoints[0].callingPoints

            //serviceDetailsResult.

//            val current = listOf(CallingPoint(serviceDetailsResult.locationName!!, serviceDetailsResult.stationCode!!,
//                serviceDetailsResult.std!!, serviceDetailsResult.etd, serviceDetailsResult.atd))

            val allCallingPoints = previousCallingPoints?.plus(subsequentCallingPoints)
                ?: subsequentCallingPoints
            val currentIndex = if (previousCallingPoints != null)  previousCallingPoints.size - 1 else 0

            binding.stationStops.adapter = StationTimelineAdapter(allCallingPoints, currentIndex, this)
            binding.stationStops.scrollToPosition(previousCallingPoints?.size ?: 0)

            binding.serviceDestination.text = destination.locationName.parseEncoded()
            binding.operatorName.text = serviceDetailsResult.operator
            binding.operatorName.alpha = 1f
            changeTocBg()
            processTimePill(serviceDetailsResult)
        })

        viewModel.getServiceDetails()

        arguments?.let {
            binding.backgroundView.transitionName = it.getString("backgroundTransName")
            //service_destination.transitionName = it.getString("destTransName")
        } ?:run {}
    }

    private fun processTimePill(serviceDetailsResult: GetServiceDetailsResult) {
        Log.e("Detail", "Exec")
        serviceDetailsResult.etd?.let {
            if (it.equals(getString(R.string.on_time), true)) {
                binding.currentRunningTime.text = it
                binding.currentRunningTime.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.colorGreen, null)))
                binding.currentRunningTime.alpha = 1f
            } else {
                binding.currentRunningTime.text = it
                binding.currentRunningTime.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.colorRed, null)))
                binding.currentRunningTime.alpha = 1f
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backUpBtn.setOnClickListener { findNavController().popBackStack() }

        binding.stationStops.itemAnimator = DepartureListItemAnimtor(0)
            .withInterpolator(FastOutSlowInInterpolator())
            .withAddDuration(250)
            .withRemoveDuration(250)

        binding.stationStops.layoutManager = LinearLayoutManager(requireContext())
        binding.stationStops.adapter = StationTimelineAdapter(emptyList(), 0, this)
    }

    override fun onPause() {
        binding.operatorName.alpha = 0f
        binding.currentRunningTime.alpha = 0f
        super.onPause()
    }

    private fun changeTocBg() {
        binding.operatorName.setTextColor(resources.getColor(android.R.color.white, null))

        Log.e("Details", "TOC: ${binding.operatorName.text}")

        when (binding.operatorName.text.toString().toLowerCase()) {
            "tfl rail" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocTflRail, null))
            }
            "great western railway" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocGwr, null))
            }
            "northern" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocNorthern, null))
            }
            "south western railway" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocSwr, null))
            }
            "london overground" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocLondOverground, null))
            }
            "london north eastern railway" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocLner, null))
            }
            "hull trains" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocHullTrains, null))
            }
            "great northern" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocGreatNorthern, null))
            }
            "thameslink" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocThameslink, null))
            }
            "greater anglia" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocGreaterAnglia, null))
            }
            "crosscountry" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocXC, null))
            }
            "gatwick express" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocGatwick, null))
            }
            "southern" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocSouthern, null))
            }
            "southeastern" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocSoutheastern, null))
            }
            "c2c" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocC2c, null))
            }
            "avanti west coast" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocAvantiWest, null))
            }
            "west midlands trains" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocWestmidlands, null))
            }
            "chiltern railways" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocChiltern, null))
            }
            "east midlands railway" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocEastMid, null))
            }
            "transpennine express" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocTranspenine, null))
            }
            "eurostar" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocEuStar, null))
            }
            "heathrow express" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocHeathrow, null))
            }
            "grand central" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocGrandCentral, null))
            }
            "transport for wales" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocTfw, null))
            }
            "scotrail" -> binding.operatorName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocScotrail, null))
            }
            "merseyrail" -> binding.operatorName.apply {
                binding.operatorName.setTextColor(binding.operatorName.resources.getColor(R.color.colorBackground, null))
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocMerseyRail, null))
            }
            else -> binding.operatorName.apply {
                binding.operatorName.setTextColor(binding.operatorName.resources.getColor(android.R.color.black, null))
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.colorAccent, null))
            }
        }
    }

    override fun onStationItemClicked(station: CallingPoint) {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        view?.findNavController()?.navigate(R.id.stationDetailFragment, bundleOf("station" to station.stationCode))
    }

}