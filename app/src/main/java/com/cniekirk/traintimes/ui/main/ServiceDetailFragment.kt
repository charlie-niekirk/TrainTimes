package com.cniekirk.traintimes.ui.main

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.transition.ChangeTransform
import androidx.transition.TransitionSet
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.databinding.FragmentServiceDetailBinding
import com.cniekirk.traintimes.di.Injectable
import com.cniekirk.traintimes.utils.anim.SwooshInterpolator
import com.cniekirk.traintimes.utils.viewBinding
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.android.synthetic.main.fragment_service_detail.*
import javax.inject.Inject

class ServiceDetailFragment: Fragment(R.layout.fragment_service_detail), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val binding by viewBinding(FragmentServiceDetailBinding::bind)
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform(requireContext()).apply {
            interpolator = SwooshInterpolator(270f)
            duration = 270
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(HomeViewModel::class.java)

        viewModel.serviceDetailsResult.observe(viewLifecycleOwner, Observer { serviceDetailsResult ->
            val destinationIndex = serviceDetailsResult.subsequentCallingPoints?.subsequentCallingPoints?.get(0)?.callingPoints?.lastIndex
            val destination = serviceDetailsResult.subsequentCallingPoints?.subsequentCallingPoints?.get(0)?.callingPoints!![destinationIndex ?: 0]
            binding.serviceDestination.text = destination.locationName
            binding.operatorName.text = serviceDetailsResult.operator
            changeTocBg()
            binding.operatorName.alpha = 1f
        })

        viewModel.getServiceDetails()

        arguments?.let {
            binding.backgroundView.transitionName = it.getString("backgroundTransName")
            //service_destination.transitionName = it.getString("destTransName")
        } ?:run {  }
    }

    override fun onPause() {
        binding.operatorName.alpha = 0f
        super.onPause()
    }

    private fun changeTocBg() {
        binding.operatorName.setTextColor(resources.getColor(R.color.colorAccent, null))

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
            else -> binding.operatorName.apply {
                binding.operatorName.setTextColor(binding.operatorName.resources.getColor(android.R.color.black, null))
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.colorAccent, null))
            }
        }
    }

}