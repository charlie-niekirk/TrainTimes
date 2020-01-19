package com.cniekirk.traintimes.ui.main

import androidx.lifecycle.MutableLiveData
import com.cniekirk.traintimes.domain.usecase.GetServiceDetailsUseCase
import com.cniekirk.traintimes.model.servicedetails.res.GetServiceDetailsResult
import com.cniekirk.traintimes.vm.BaseViewModel
import javax.inject.Inject

class ServiceDetailViewModel @Inject constructor(
    private val getServiceDetailsUseCase: GetServiceDetailsUseCase
): BaseViewModel() {

    val serviceDetailsResult = MutableLiveData<GetServiceDetailsResult>()

    fun getServiceDetails(serviceId: String) {
        getServiceDetailsUseCase(serviceId) { it.either(::handleFailure, ::handleServiceDetails)  }
    }

    private fun handleServiceDetails(serviceDetails: GetServiceDetailsResult) {
        serviceDetailsResult.value = serviceDetails
    }

}