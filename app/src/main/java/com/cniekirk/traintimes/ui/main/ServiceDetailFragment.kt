package com.cniekirk.traintimes.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.transition.ChangeBounds
import androidx.transition.ChangeClipBounds
import androidx.transition.ChangeTransform
import androidx.transition.TransitionSet
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.di.Injectable
import com.cniekirk.traintimes.utils.anim.SwooshInterpolator
import kotlinx.android.synthetic.main.fragment_service_detail.*
import javax.inject.Inject

class ServiceDetailFragment: Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: ServiceDetailViewModel

    private var serviceId: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val interpolator = SwooshInterpolator(320f)
        val set = TransitionSet()
        //set.addTransition(ChangeBounds().setInterpolator(interpolator).setDuration(450))
        set.addTransition(ChangeTransform().apply { reparentWithOverlay = false }.setInterpolator(interpolator).setDuration(320))
        sharedElementEnterTransition = set

        return inflater.inflate(R.layout.fragment_service_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            serviceId = it.getString("serviceId")
            background_view.transitionName = it.getString("backgroundTransName")
            service_destination.transitionName = it.getString("destTransName")
        }

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(ServiceDetailViewModel::class.java)

        viewModel.serviceDetailsResult.observe(this, Observer { serviceDetailsResult ->
            val destinationIndex = serviceDetailsResult.subsequentCallingPoints?.subsequentCallingPoints?.get(0)?.callingPoints?.lastIndex
            val destination = serviceDetailsResult.subsequentCallingPoints?.subsequentCallingPoints?.get(0)?.callingPoints!![destinationIndex ?: 0]
            service_destination.text = destination.locationName
        })

        serviceId?.let {
            viewModel.getServiceDetails(it)
        }
    }

}