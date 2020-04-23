package com.cniekirk.traintimes.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.domain.usecase.*
import com.cniekirk.traintimes.model.getdepboard.res.GetStationBoardResult
import com.cniekirk.traintimes.model.getdepboard.res.Service
import com.cniekirk.traintimes.model.servicedetails.res.GetServiceDetailsResult
import com.cniekirk.traintimes.base.BaseViewModel
import com.cniekirk.traintimes.base.ViewModelFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeViewModel constructor(
    val handle: SavedStateHandle,
    private val getStationsUseCase: GetStationsUseCase,
    private val getAllStationCodesUseCase: GetAllStationCodesUseCase,
    private val getDeparturesUseCase: GetDeparturesUseCase,
    private val getServiceDetailsUseCase: GetServiceDetailsUseCase,
    private val getArrivalsUseCase: GetArrivalsUseCase
) : BaseViewModel() {

    val services = MutableLiveData<List<Service>>()
    val crsStationCodes = MutableLiveData<List<CRS>>()
    val depStation = MutableLiveData<CRS>()
    val destStation = MutableLiveData<CRS>()
    val serviceDetailsResult = MutableLiveData<GetServiceDetailsResult>()
    val serviceDetailId = MutableLiveData<String>()

    @ExperimentalCoroutinesApi
    val queryChannel = BroadcastChannel<String>(Channel.CONFLATED)
    fun depStationText(): String? = handle.get<String>("depStation")

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

    fun getTrains() {
        destStation.value?.let { crs ->
            depStation.value?.let { depCrs ->
                // Get specific departures
                getDeparturesUseCase(arrayOf(depCrs.stationName, crs.stationName))
                { it.either(::handleFailure, ::handleResponse) }
            } ?: run {
                // Get all arrivals
                getArrivalsUseCase(arrayOf(crs.stationName))
                { it.either(::handleFailure, ::handleResponse) }
            }
        } ?: run {
            // Check if empty
            depStation.value?.let {
                // Get all departures
                getDeparturesUseCase(arrayOf(depStation.value!!.stationName, ""))
                { it.either(::handleFailure, ::handleResponse) }
            } ?: run {
                // Needed so old value isn't cached and reloaded which looks odd to the user
                services.postValue(emptyList())
                handleFailure(Failure.NoCrsFailure())
            }
        }
    }

    fun getServiceDetails() {
        serviceDetailId.value?.let { serviceId ->
            getServiceDetailsUseCase(serviceId) { it.either(::handleFailure, ::handleServiceDetails) }
        }
    }

    fun saveDepStation(crs: CRS) {
        depStation.postValue(crs)
        handle.set("depStation", crs.crs)
    }

    fun saveDestStation(crs: CRS) {
        destStation.postValue(crs)
        handle.set("destStation", crs.crs)
    }

    fun clearDepStation() {
        depStation.postValue(null)
        handle.remove<String>("depStation")
    }

    fun clearDestStation() {
        destStation.postValue(null)
        handle.remove<String>("destStation")
    }

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
}

@Singleton
class HomeViewModelFactory @Inject constructor(
    private val getStationsUseCase: GetStationsUseCase,
    private val getAllStationCodesUseCase: GetAllStationCodesUseCase,
    private val getDeparturesUseCase: GetDeparturesUseCase,
    private val getServiceDetailsUseCase: GetServiceDetailsUseCase,
    private val getArrivalsUseCase: GetArrivalsUseCase
) : ViewModelFactory<HomeViewModel> {

    override fun create(handle: SavedStateHandle): HomeViewModel {
        return HomeViewModel(handle, getStationsUseCase, getAllStationCodesUseCase,
                getDeparturesUseCase, getServiceDetailsUseCase, getArrivalsUseCase)
    }
}
