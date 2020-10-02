package com.cniekirk.traintimes.ui.station

import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.base.withFactory
import com.cniekirk.traintimes.databinding.FragmentStationDetailBinding
import com.cniekirk.traintimes.di.Injectable
import com.cniekirk.traintimes.ui.viewmodel.StationViewModel
import com.cniekirk.traintimes.ui.viewmodel.StationViewModelFactory
import com.cniekirk.traintimes.utils.viewBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.transition.MaterialSharedAxis
import javax.inject.Inject

/**
 * Fragment that displays train station information
 *
 * @author Charles Niekirk 2020
 */
class StationDetailFragment: Fragment(R.layout.fragment_station_detail), Injectable, OnMapReadyCallback {

    @Inject
    lateinit var viewModelFactory: StationViewModelFactory

    private lateinit var stationLocation: LatLng
    private lateinit var crsCode: String

    private var map: GoogleMap? = null

    private val binding by viewBinding(FragmentStationDetailBinding::bind)
    private val viewModel: StationViewModel by viewModels { withFactory(viewModelFactory, arguments) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val backward =  MaterialSharedAxis(MaterialSharedAxis.Z,  false)
        returnTransition = backward

        val forward =  MaterialSharedAxis(MaterialSharedAxis.Z,  true)
        enterTransition = forward
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_station_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let { crsCode = it.getString("station", "WAT") }

        viewModel.stationInformation.observe(viewLifecycleOwner, Observer {
            it?.let {
                stationLocation = LatLng(it.latitude!!, it.longitude!!)
                binding.stationName.text = it.stationName
                binding.map.clipToOutline = true

                Log.e(StationDetailFragment::class.java.simpleName, "Toilets ${it.stationFacilities?.toilets}")

                it.stationFacilities?.toilets?.let { toilets -> toilets.available?.let { available ->
                    Log.e(StationDetailFragment::class.java.simpleName, "Are toilets available? $available")
                    Glide.with(requireContext()).load(if (available) R.drawable.ic_baseline_check else R.drawable.ic_baseline_cancel).into(binding.isToiletAvailable)
                    if (!available) {
                        binding.isToiletAvailable.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.colorRed))
                    } else {
                        binding.isToiletAvailable.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.colorGreen))
                    }
                } }
                it.stationFacilities?.babyChange?.let { babyChange -> babyChange.available?.let { available ->
                    Glide.with(requireContext()).load(if (available) R.drawable.ic_baseline_check else R.drawable.ic_baseline_cancel).into(binding.isBabyChangeAvailable)
                    if (!available) {
                        binding.isBabyChangeAvailable.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.colorRed))
                    } else {
                        binding.isBabyChangeAvailable.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.colorGreen))
                    }
                } }
                it.stationFacilities?.stationBuffet?.let { buffet -> buffet.available?.let { available ->
                    Glide.with(requireContext()).load(if (available) R.drawable.ic_baseline_check else R.drawable.ic_baseline_cancel).into(binding.isFoodAvailable)
                    if (!available) {
                        binding.isFoodAvailable.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.colorRed))
                    } else {
                        binding.isFoodAvailable.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.colorGreen))
                    }
                } }
                it.stationFacilities?.seatedArea?.let { seatedArea -> seatedArea.available?.let { available ->
                    Glide.with(requireContext()).load(if (available) R.drawable.ic_baseline_check else R.drawable.ic_baseline_cancel).into(binding.isSeatingAvailable)
                    if (!available) {
                        binding.isSeatingAvailable.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.colorRed))
                    } else {
                        binding.isSeatingAvailable.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.colorGreen))
                    }
                } }
                it.stationFacilities?.showers?.let { showers -> showers.available?.let { available ->
                    Glide.with(requireContext()).load(if (available) R.drawable.ic_baseline_check else R.drawable.ic_baseline_cancel).into(binding.isShowersAvailable)
                    if (!available) {
                        binding.isShowersAvailable.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.colorRed))
                    } else {
                        binding.isShowersAvailable.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.colorGreen))
                    }
                } }
                it.staffing?.let { staffing -> staffing.staffingLevel?.let { staffingLevel ->
                    when {
                        staffingLevel.equals(getString(R.string.full_time), true) -> {
                            binding.staffingAvailable.text = getString(R.string.full_time_description)
                        }
                        staffingLevel.equals(getString(R.string.part_time), true) -> {
                            binding.staffingAvailable.text = getString(R.string.part_time_description)
                        }
                        else -> {
                            binding.staffingAvailable.text = getString(R.string.no_time_description)
                        }
                    }
                } }

                it.address?.let { address -> address.postalAddress?.let { postalAddress -> postalAddress.fiveLineAddress?.let { fiveLineAddress ->
                    binding.address.text = ""
                    fiveLineAddress.lines?.forEach { addressLine ->
                        binding.address.text = "${binding.address.text}${addressLine.line}\n"
                    }
                    binding.address.text = "${binding.address.text}${fiveLineAddress.postalCode}"
                } } }

                val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        })

        viewModel.getStationInformation(crsCode)

        binding.isBabyChangeAvailable.clipToOutline = true
        binding.isFoodAvailable.clipToOutline = true
        binding.isSeatingAvailable.clipToOutline = true
        binding.isShowersAvailable.clipToOutline = true
        binding.isToiletAvailable.clipToOutline = true

        binding.openInMaps.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:0,0?q=${binding.address.text.toString().replace("\n", "%2C").replace(" ", "+")}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            mapIntent.resolveActivity(requireActivity().packageManager)?.let {
                startActivity(mapIntent)
            } ?: run { Toast.makeText(requireContext(), R.string.no_maps_installed, Toast.LENGTH_SHORT).show() }
        }

        binding.backUpBtn.setOnClickListener {
            view?.findNavController()?.popBackStack()
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        map = googleMap

        when ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                try {
                    if (!googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.dark_style))) {
                        Log.e(StationDetailFragment::class.java.simpleName, getString(R.string.map_parse_error))
                    }
                } catch (exception: Resources.NotFoundException) {
                    Log.e(StationDetailFragment::class.java.simpleName, exception.toString())
                }
            }
            Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                try {
                    if (!googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.light_style))) {
                        Log.e(StationDetailFragment::class.java.simpleName, getString(R.string.map_parse_error))
                    }
                } catch (exception: Resources.NotFoundException) {
                    Log.e(StationDetailFragment::class.java.simpleName, exception.toString())
                }
            }
        }

        //val waterloo = LatLng(51.503518,-0.1132977)
        googleMap.addMarker(MarkerOptions().position(stationLocation).title("Waterloo"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(stationLocation, 14.0f))
    }

    override fun onPause() {
        map?.clear()
        map = null
        super.onPause()
    }

}