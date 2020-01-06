package com.cniekirk.traintimes.ui.main

import androidx.lifecycle.MutableLiveData
import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.domain.usecase.GetAllStationCodesUseCase
import com.cniekirk.traintimes.domain.usecase.GetDeparturesUseCase
import com.cniekirk.traintimes.domain.usecase.GetStationsUseCase
import com.cniekirk.traintimes.model.getdepboard.res.GetStationBoardResult
import com.cniekirk.traintimes.model.getdepboard.res.Service
import com.cniekirk.traintimes.vm.BaseViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeViewModel @Inject constructor(
    private val getStationsUseCase: GetStationsUseCase,
    private val getAllStationCodesUseCase: GetAllStationCodesUseCase,
    private val getDeparturesUseCase: GetDeparturesUseCase
) : BaseViewModel() {

    val services = MutableLiveData<List<Service>>()
    val crsStationCodes = MutableLiveData<List<CRS>>()
    val depStation = MutableLiveData<CRS>()
    val destStation = MutableLiveData<CRS>()

    @ExperimentalCoroutinesApi
    val queryChannel = BroadcastChannel<String>(Channel.CONFLATED)

    fun getCrsCodes() {
        getAllStationCodesUseCase(null) { it.either(::handleFailure, ::handleCrs) }
    }

    @FlowPreview
    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    fun listenForNewSearch() {
        GlobalScope.launch { queryChannel.asFlow().debounce(300).collect { query ->
                getStationsUseCase(query) { it.either(::handleFailure, ::handleCrs) }
            }
        }
    }

    fun getDepartures() {
        destStation.value?.let { crs ->
            getDeparturesUseCase(arrayOf(depStation.value!!.stationName, crs.stationName))
            { it.either(::handleFailure, ::handleResponse) }
        } ?: run {
            getDeparturesUseCase(arrayOf(depStation.value!!.stationName, ""))
            { it.either(::handleFailure, ::handleResponse) }
        }
    }

    private fun handleCrs(list: List<CRS>) {
        crsStationCodes.value = list
    }

    private fun handleResponse(response: GetStationBoardResult) {
        services.value = response.trainServices.trainServices
    }
}
