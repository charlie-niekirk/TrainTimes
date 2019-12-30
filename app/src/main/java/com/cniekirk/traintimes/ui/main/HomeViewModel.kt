package com.cniekirk.traintimes.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.domain.usecase.GetAllStationCodesUseCase
import com.cniekirk.traintimes.domain.usecase.GetStationsUseCase
import com.cniekirk.traintimes.model.getdepboard.res.GetStationBoardResult
import com.cniekirk.traintimes.model.getdepboard.res.Service
import com.cniekirk.traintimes.vm.BaseViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeViewModel @Inject constructor(
    private val getStationsUseCase: GetStationsUseCase,
    private val getAllStationCodesUseCase: GetAllStationCodesUseCase
) : BaseViewModel() {
    // TODO: Implement the ViewModel

    val services = MutableLiveData<List<Service>>()

    fun getDepartureBoard(station: String) {
        getStationsUseCase(station) { it.either(::handleFailure, ::handleResponse) }
    }

    fun getCrsCodes() {
        getAllStationCodesUseCase(Any()) { it.either(::handleFailure, ::handleCrs) }
    }

    private fun handleCrs(list: List<CRS>): Any {
        Log.e("VM", "DONE")
        return Any()
    }

    private fun handleResponse(response: GetStationBoardResult) {
        services.value = response.trainServices.trainServices
    }
}
