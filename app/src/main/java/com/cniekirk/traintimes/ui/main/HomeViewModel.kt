package com.cniekirk.traintimes.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.domain.usecase.*
import com.cniekirk.traintimes.model.getdepboard.res.GetStationBoardResult
import com.cniekirk.traintimes.model.getdepboard.res.Service
import com.cniekirk.traintimes.model.journeyplanner.res.JourneyPlanResponse
import com.cniekirk.traintimes.model.servicedetails.res.GetServiceDetailsResult
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
    private val getDeparturesUseCase: GetDeparturesUseCase,
    private val getJourneyPlanUseCase: GetJourneyPlanUseCase,
    private val getServiceDetailsUseCase: GetServiceDetailsUseCase
) : BaseViewModel() {

    val services = MutableLiveData<List<Service>>()
    val crsStationCodes = MutableLiveData<List<CRS>>()
    val depStation = MutableLiveData<CRS>()
    val destStation = MutableLiveData<CRS>()
    val serviceDetailsResult = MutableLiveData<GetServiceDetailsResult>()
    val serviceDetailId = MutableLiveData<String>()

    @ExperimentalCoroutinesApi
    val queryChannel = BroadcastChannel<String>(Channel.CONFLATED)

    @FlowPreview
    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    fun listenForNewSearch() {
        GlobalScope.launch { queryChannel.asFlow().debounce(300).collect { query ->
                getStationsUseCase(query) { it.either(::handleFailure, ::handleCrs) }
            }
        }
    }

    fun setServiceId(serviceId: String) {
        serviceDetailId.value = serviceId
    }

    fun getCrsCodes() {
        getAllStationCodesUseCase(null) { it.either(::handleFailure, ::handleCrs) }
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

    fun getJourneyPlan() {
        destStation.value?.let { crs ->
            getJourneyPlanUseCase(arrayOf(depStation.value!!.stationName, crs.stationName))
            { it.either(::handleFailure, ::handleJorneyPlanResponse) }
        } ?: run {
            getJourneyPlanUseCase(arrayOf(depStation.value!!.stationName, ""))
            { it.either(::handleFailure, ::handleJorneyPlanResponse) }
        }
    }

    fun getServiceDetails() {
        serviceDetailId.value?.let { serviceId ->
            getServiceDetailsUseCase(serviceId) { it.either(::handleFailure, ::handleServiceDetails) }
        }
    }

    fun clearDepStation() { depStation.value = null }
    fun clearDestStation() { destStation.value = null }

    private fun handleCrs(list: List<CRS>) {
        crsStationCodes.value = list
    }

    private fun handleResponse(response: GetStationBoardResult) {
        response.trainServices?.let {
            services.value = it.trainServices
        } ?: run {
            services.value = emptyList()
        }
    }

    private fun handleServiceDetails(serviceDetails: GetServiceDetailsResult) {
        serviceDetailsResult.value = serviceDetails
    }

    private fun handleJorneyPlanResponse(journeyPlanResponse: JourneyPlanResponse) {
        Log.d("VM", "JourneyPlanner: ${journeyPlanResponse.outwardJourney[0].destination}")
    }
}
