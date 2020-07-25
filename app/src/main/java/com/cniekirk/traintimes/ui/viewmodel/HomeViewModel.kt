package com.cniekirk.traintimes.ui.viewmodel

import androidx.lifecycle.LiveData
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
import com.cniekirk.traintimes.utils.ConnectionStateEmitter
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
    private val getArrivalsUseCase: GetArrivalsUseCase,
    private val connectionState: ConnectionStateEmitter
) : BaseViewModel() {

    val services: LiveData<List<Service>>
        get() = _services
    val crsStationCodes: LiveData<List<CRS>>
        get() = _crsStationCodes
    val depStation: LiveData<CRS>
        get() = _depStation
    val destStation: LiveData<CRS>
        get() = _destStation
    val serviceDetailsResult: LiveData<GetServiceDetailsResult>
        get() = _serviceDetailsResult
    val serviceDetailId: LiveData<String>
        get() = _serviceDetailId
    val connectionStateEmitter: LiveData<Boolean>
        get() = connectionState

    private val _services = MutableLiveData<List<Service>>()
    private val _crsStationCodes = MutableLiveData<List<CRS>>()
    private val _depStation = MutableLiveData<CRS>()
    private val _destStation = MutableLiveData<CRS>()
    private val _serviceDetailsResult = MutableLiveData<GetServiceDetailsResult>()
    private val _serviceDetailId = MutableLiveData<String>()

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

    fun getCrsCodes() {
        getAllStationCodesUseCase(null) { it.either(::handleFailure, ::handleCrs) }
    }

    fun getTrains() {
        _destStation.value?.let { crs ->
            _depStation.value?.let { depCrs ->
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
            _depStation.value?.let {
                // Get all departures
                getDeparturesUseCase(arrayOf(_depStation.value!!.stationName, ""))
                { it.either(::handleFailure, ::handleResponse) }
            } ?: run {
                // Needed so old value isn't cached and reloaded which looks odd to the user
                _services.postValue(emptyList())
                handleFailure(Failure.NoCrsFailure())
            }
        }
    }

    fun getServiceDetails() {
        _serviceDetailId.value?.let { serviceId ->
            getServiceDetailsUseCase(serviceId) { it.either(::handleFailure, ::handleServiceDetails) }
        }
    }

    fun setServiceId(serviceId: String) {
        _serviceDetailId.postValue(serviceId)
    }

    fun saveDepStation(crs: CRS) {
        _depStation.postValue(crs)
        handle.set("depStation", crs.crs)
    }

    fun saveDestStation(crs: CRS) {
        _destStation.postValue(crs)
        handle.set("destStation", crs.crs)
    }

    fun clearDepStation() {
        _depStation.postValue(null)
        handle.remove<String>("depStation")
    }

    fun clearDestStation() {
        _destStation.postValue(null)
        handle.remove<String>("destStation")
    }

    private fun handleCrs(list: List<CRS>) {
        _crsStationCodes.value = list
    }

    private fun handleResponse(response: GetStationBoardResult) {
        response.trainServices?.let {
            _services.value = it.trainServices
        } ?: run {
            _services.value = emptyList()
        }
    }

    private fun handleServiceDetails(serviceDetails: GetServiceDetailsResult) {
        _serviceDetailsResult.value = serviceDetails
    }

    @ExperimentalCoroutinesApi
    override fun onCleared() {
        queryChannel.cancel()
        super.onCleared()
    }
}

@Singleton
class HomeViewModelFactory @Inject constructor(
    private val getStationsUseCase: GetStationsUseCase,
    private val getAllStationCodesUseCase: GetAllStationCodesUseCase,
    private val getDeparturesUseCase: GetDeparturesUseCase,
    private val getServiceDetailsUseCase: GetServiceDetailsUseCase,
    private val getArrivalsUseCase: GetArrivalsUseCase,
    private val connectionStateEmitter: ConnectionStateEmitter
) : ViewModelFactory<HomeViewModel> {

    override fun create(handle: SavedStateHandle): HomeViewModel {
        return HomeViewModel(handle, getStationsUseCase, getAllStationCodesUseCase,
                getDeparturesUseCase, getServiceDetailsUseCase, getArrivalsUseCase, connectionStateEmitter)
    }
}
