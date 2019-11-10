package com.cniekirk.traintimes.ui.main

import androidx.lifecycle.MutableLiveData
import com.cniekirk.traintimes.domain.usecase.GetStationsUseCase
import com.cniekirk.traintimes.model.getdepboard.res.GetStationBoardResult
import com.cniekirk.traintimes.model.getdepboard.res.Service
import com.cniekirk.traintimes.vm.BaseViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeViewModel @Inject constructor(
    private val getStationsUseCase: GetStationsUseCase
) : BaseViewModel() {
    // TODO: Implement the ViewModel

    val services = MutableLiveData<List<Service>>()

    fun getDepartureBoard(station: String) {
        getStationsUseCase(station) { it.either(::handleFailure, ::handleResponse) }
    }

    private fun handleResponse(response: GetStationBoardResult) {
        services.value = response.trainServices.trainServices
    }
}
