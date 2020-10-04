package com.cniekirk.traintimes.ui.main

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
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
import com.cniekirk.traintimes.model.getdepboard.res.Location
import com.cniekirk.traintimes.model.ui.ServiceDetailsUiModel
import com.cniekirk.traintimes.ui.adapter.StationTimelineAdapter
import com.cniekirk.traintimes.ui.viewmodel.HomeViewModel
import com.cniekirk.traintimes.ui.viewmodel.HomeViewModelFactory
import com.cniekirk.traintimes.utils.anim.DepartureListItemAnimtor
import com.cniekirk.traintimes.utils.extensions.parseEncoded
import com.cniekirk.traintimes.utils.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

private const val TAG = "ServiceDetailFragment"

class ServiceDetailFragment: Fragment(R.layout.fragment_service_detail), Injectable, StationTimelineAdapter.OnStationItemClickedListener {

    @Inject
    lateinit var viewModelFactory: HomeViewModelFactory

    private val binding by viewBinding(FragmentServiceDetailBinding::bind)
    private val viewModel: HomeViewModel by activityViewModels { withFactory(viewModelFactory, arguments) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val backward =  MaterialSharedAxis(MaterialSharedAxis.Z,  false)
        returnTransition = backward

        val forward =  MaterialSharedAxis(MaterialSharedAxis.Z,  true)
        enterTransition = forward

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let {
            binding.backgroundView.transitionName = it.getString("backgroundTransName")
            //service_destination.transitionName = it.getString("destTransName")
        } ?:run {}
    }

    private fun processTimePill(serviceDetailsResult: ServiceDetailsUiModel) {
        Log.e(TAG, "Exec")

        serviceDetailsResult.subsequentLocations?.let { locations ->
            val sdf = SimpleDateFormat("YYYY-mm-DD'T'hh:mm", Locale.ENGLISH)
            val output = SimpleDateFormat("HH:mm", Locale.ENGLISH)
            locations[0].eta?.let {
                val estimated = sdf.parse(it)
                val scheduled = sdf.parse(locations[0].sta!!)
                if (estimated.after(scheduled)) {
                    Log.e(TAG, "AFTER")
                    binding.currentRunningTime.text = "Delayed, now departing at ${output.format(estimated)}"
                    binding.currentRunningTime.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.colorRed, null)))
                    binding.timeIndicatorDot.backgroundTintList = (ColorStateList.valueOf(resources.getColor(R.color.colorRed, null)))
                    binding.currentRunningTime.alpha = 1f
                } else {
                    Log.e(TAG, "ON_TIME")
                    binding.currentRunningTime.text = "Currently On Time"
                    binding.currentRunningTime.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.colorGreen, null)))
                    binding.timeIndicatorDot.backgroundTintList = (ColorStateList.valueOf(resources.getColor(R.color.colorGreen, null)))
                    binding.currentRunningTime.alpha = 1f
                }
            } ?: run {
                Log.e(TAG, "HIDDEN")
                locations[0].etd?.let {
                    val estimated = sdf.parse(it)
                    val scheduled = sdf.parse(locations[0].std!!)
                    if (estimated.after(scheduled)) {
                        Log.e(TAG, "AFTER")
                        binding.currentRunningTime.text = "Delayed, now departing at ${output.format(estimated)}"
                        binding.currentRunningTime.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.colorRed, null)))
                        binding.timeIndicatorDot.backgroundTintList = (ColorStateList.valueOf(resources.getColor(R.color.colorRed, null)))
                        binding.currentRunningTime.alpha = 1f
                    } else {
                    Log.e(TAG, "ON_TIME")
                    binding.currentRunningTime.text = "Currently On Time"
                    binding.currentRunningTime.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.colorGreen, null)))
                    binding.timeIndicatorDot.backgroundTintList = (ColorStateList.valueOf(resources.getColor(R.color.colorGreen, null)))
                    binding.currentRunningTime.alpha = 1f
                }
                }
            }
        } ?: run {
            Log.e(TAG, "HIDDEN")
            binding.currentRunningTime.visibility = View.INVISIBLE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backUpBtn.setOnClickListener { findNavController().popBackStack() }

        binding.serviceDestination.isSelected = true

        binding.stationStops.itemAnimator = DepartureListItemAnimtor(0)
            .withInterpolator(FastOutSlowInInterpolator())
            .withAddDuration(250)
            .withRemoveDuration(250)

        binding.stationStops.layoutManager = LinearLayoutManager(requireContext())
        binding.stationStops.adapter = StationTimelineAdapter(emptyList(), 0, this)

        viewModel.serviceDetailsResult.observe(viewLifecycleOwner, { serviceDetailsResult ->

            if (serviceDetailsResult.isCancelled) {

                serviceDetailsResult.cancelledCallingPoints?.let {
                    binding.stationStops.adapter = StationTimelineAdapter(it, 0, this)

                    val destination = it[it.lastIndex]
                    binding.serviceDestination.text = destination.locationName?.parseEncoded()
                    binding.operatorName.text = serviceDetailsResult.operator
                    changeTocBg()
                    processTimePill(serviceDetailsResult)
                }

            } else {

                val destinationIndex = serviceDetailsResult.subsequentLocations?.lastIndex ?: 0
                Log.d(TAG, serviceDetailsResult.subsequentLocations.toString())
                val destination = serviceDetailsResult.subsequentLocations!![destinationIndex]
                val previousCallingPoints = serviceDetailsResult.previousLocations
                val subsequentCallingPoints = serviceDetailsResult.subsequentLocations

                //serviceDetailsResult.

//            val current = listOf(CallingPoint(serviceDetailsResult.locationName!!, serviceDetailsResult.stationCode!!,
//                serviceDetailsResult.std!!, serviceDetailsResult.etd, serviceDetailsResult.atd))

                var previousWithCurrent = previousCallingPoints
                Log.e(TAG, "PREVIOUS: ${previousWithCurrent.toString()}")
                serviceDetailsResult.currentLocation?.let {
                    previousWithCurrent?.let { prevCur ->
                        Log.e(TAG, "PREVIOUS: ${prevCur.size}")
                        previousWithCurrent = prevCur.subList(0, prevCur.lastIndex - 1)
                        previousWithCurrent = previousWithCurrent?.plus(it)
                    }
                }
                Log.e(TAG, "PREVIOUS (with current): ${previousWithCurrent.toString()}")

                var allCallingPoints = previousWithCurrent?.plus(subsequentCallingPoints)
                    ?: subsequentCallingPoints
                val currentIndex = previousCallingPoints?.size ?: 0

                allCallingPoints = allCallingPoints.filter { location ->
                    location?.isOperational?.let {
                        !it
                    } ?: run {
                        true
                    }
                }

//                binding.btnWatch.setOnClickListener {
//                    MaterialAlertDialogBuilder(requireContext())
//                        .setTitle(R.string.live_updates_title)
//                        .setMultiChoiceItems(R.array.live_update_values, booleanArrayOf(true, true, true, false)) { dialog, which, isChecked ->
//
//                        }
//                        .setPositiveButton(R.string.live_updates_positive) { dialogInterface, _ ->
//                            val searchTiploc = allCallingPoints.find { location ->
//                                location.stationCode!!.equals(viewModel.depStation.value?.crs, true)
//                            }
//                            searchTiploc?.let { location ->
//                                location.tiploc?.let { tiploc ->
//                                    viewModel.trackService(serviceDetailsResult.rid!!, tiploc.replace(" ", ""),
//                                        PreferenceProvider(requireContext()).getFirebaseId(), serviceDetailsResult)
//                                }
//                            }
//                            dialogInterface.dismiss()
//                        }
//                        .setNegativeButton(R.string.live_updates_negative) { dialogInterface, _ ->
//                            dialogInterface.dismiss()
//                        }
//                        .show()
//                }

                binding.stationStops.adapter = StationTimelineAdapter(allCallingPoints, currentIndex, this)
                binding.stationStops.scrollToPosition(previousCallingPoints?.size ?: 0)

                binding.serviceDestination.text = destination.locationName?.parseEncoded()
                binding.operatorName.text = serviceDetailsResult.operator
                changeTocBg()
                processTimePill(serviceDetailsResult)

            }

        })

        viewModel.trackServiceSuccess.observe(viewLifecycleOwner, Observer {
            Log.e(TAG, "Got to here? $it")
            if (it) {
                val sb = Snackbar.make(binding.root, R.string.tracking, Snackbar.LENGTH_SHORT)
                sb.anchorView = binding.snackbarLocation
                sb.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorGreen))
                sb.show()
            } else {
                val sb = Snackbar.make(binding.root, R.string.tracking_error, Snackbar.LENGTH_SHORT)
                sb.anchorView = binding.snackbarLocation
                sb.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorRed))
                sb.show()
            }
        })

        viewModel.getServiceDetails()
    }

    override fun onPause() {
        viewModel.serviceDetailsResult.removeObservers(viewLifecycleOwner)
        super.onPause()
    }

    private fun changeTocBg() {
        binding.operatorName.setTextColor(resources.getColor(android.R.color.white, null))

        Log.e(TAG, "TOC: ${binding.operatorName.text}")

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

    override fun onStationItemClicked(station: Location) {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        view?.findNavController()?.navigate(R.id.stationDetailFragment, bundleOf("station" to station.stationCode))
    }

}