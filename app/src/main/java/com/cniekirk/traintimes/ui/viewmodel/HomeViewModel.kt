package com.cniekirk.traintimes.ui.viewmodel

import android.util.Log
import androidx.datastore.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.cniekirk.traintimes.base.BaseViewModel
import com.cniekirk.traintimes.base.SingleLiveEvent
import com.cniekirk.traintimes.base.ViewModelFactory
import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.domain.usecase.*
import com.cniekirk.traintimes.model.Favourites
import com.cniekirk.traintimes.model.getdepboard.local.Query
import com.cniekirk.traintimes.model.getdepboard.res.GetBoardWithDetailsResult
import com.cniekirk.traintimes.model.getdepboard.res.Message
import com.cniekirk.traintimes.model.getdepboard.res.NrccMessages
import com.cniekirk.traintimes.model.getdepboard.res.Service
import com.cniekirk.traintimes.model.track.req.TrackServiceRequest
import com.cniekirk.traintimes.model.track.res.TrackServiceResponse
import com.cniekirk.traintimes.model.ui.ServiceDetailsUiModel
import com.cniekirk.traintimes.utils.ConnectionStateEmitter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "HomeViewModel"

@Singleton
class HomeViewModel constructor(
    val handle: SavedStateHandle,
    private val getStationsUseCase: GetStationsUseCase,
    private val getAllStationCodesUseCase: GetAllStationCodesUseCase,
    private val getDeparturesUseCase: GetDeparturesUseCase,
    private val getServiceDetailsUseCase: GetServiceDetailsUseCase,
    private val getArrivalsUseCase: GetArrivalsUseCase,
    private val trackServiceUseCase: TrackServiceUseCase,
    private val getRecentQueriesUseCase: GetRecentQueriesUseCase,
    private val saveFavouriteUseCase: SaveFavouriteUseCase,
    private val connectionState: ConnectionStateEmitter,
    private val favouritesDataStore: DataStore<Favourites>
) : BaseViewModel() {

    val services: LiveData<List<Service>>
        get() = _services
    val crsStationCodes: LiveData<List<CRS>>
        get() = _crsStationCodes
    val depStation: LiveData<CRS>
        get() = _depStation
    val destStation: LiveData<CRS>
        get() = _destStation
    val serviceDetailsResult: LiveData<ServiceDetailsUiModel>
        get() = _serviceDetailsResult
    val serviceDetailId: LiveData<String>
        get() = _serviceDetailId
    val connectionStateEmitter: LiveData<Boolean>
        get() = connectionState
    val trackServiceSuccess: LiveData<Boolean>
        get() = _trackServiceSuccess
    val canProceedToSearch: LiveData<Boolean>
        get() = _canProceedToSearch
    val recentQueries: LiveData<List<Query>>
        get() = _recentQueries
    val nrccMessages: LiveData<List<Message>>
        get() = _nrccMessages

    private val _services = MutableLiveData<List<Service>>()
    private val _crsStationCodes = MutableLiveData<List<CRS>>()
    private val _depStation = MutableLiveData<CRS>()
    private val _destStation = MutableLiveData<CRS>()
    private val _serviceDetailsResult = MutableLiveData<ServiceDetailsUiModel>()
    private val _serviceDetailId = MutableLiveData<String>()
    private val _trackServiceSuccess = SingleLiveEvent<Boolean>()
    private val _recentQueries = MutableLiveData<List<Query>>()
    private val _canProceedToSearch = SingleLiveEvent<Boolean>()
    private val _nrccMessages = MutableLiveData<List<Message>>()

    private val favouritesFlow: Flow<Favourites> = favouritesDataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(TAG, "Error reading sort order preferences.", exception)
                emit(Favourites.getDefaultInstance())
            } else {
                throw exception
            }
        }

    @ExperimentalCoroutinesApi
    val queryChannel = BroadcastChannel<String>(Channel.CONFLATED)
    fun depStationText(): String? = handle.get<String>("depStation")

    @FlowPreview
    @ExperimentalCoroutinesApi
    fun listenForNewSearch() {
        viewModelScope.launch { queryChannel.asFlow().debounce(300).collect { query ->
                getStationsUseCase(query) { it.either(::handleFailure, ::handleCrs) }
            }
        }
    }

    fun getCrsCodes() {
        getAllStationCodesUseCase(null) { it.either(::handleFailure, ::handleCrs) }
    }

    fun getTrains() {
        Log.e(TAG, "EXEC - getTrains()")
        _destStation.value?.let { crs ->
            _depStation.value?.let { depCrs ->
                // Get specific departures
                getDeparturesUseCase(arrayOf(depCrs, crs))
                { it.either(::handleFailure, ::handleResponse) }
            } ?: run {
                // Get all arrivals
                getArrivalsUseCase(arrayOf(crs.crs))
                { it.either(::handleFailure, ::handleResponse) }
            }
        } ?: run {
            // Check if empty
            _depStation.value?.let {
                // Get all departures
                getDeparturesUseCase(arrayOf(_depStation.value!!, CRS("", "")))
                { it.either(::handleFailure, ::handleResponse) }
            } ?: run {
                // Needed so old value isn't cached and reloaded which looks odd to the user
                _services.postValue(emptyList())
                handleFailure(Failure.NoCrsFailure())
            }
        }
    }

    fun getRecentSearches() {
        getRecentQueriesUseCase(null) { it.either(::handleFailure, ::handleRecentQueries) }
    }

    private fun handleRecentQueries(queries: List<Query>) {
        _recentQueries.postValue(queries)
    }

    // Checks that there are valid search parameters
    fun attemptServiceSearch() {
        _depStation.value?.let {
            _canProceedToSearch.postValue(true)
        } ?: run {
            // Check for dest
            _destStation.value?.let {
                _canProceedToSearch.postValue(true)
            } ?: run {
                // If both are null then emit failure
                _canProceedToSearch.postValue(false)
            }
        }
    }

    private fun handleInsert(msg: String) {
        //
    }

    fun performRecentQuery(position: Int) {
        _depStation.value = null
        handle.remove<String>("depStation")
        _destStation.value = null
        handle.remove<String>("destStation")
        val from = CRS(_recentQueries.value?.get(position)?.fromName!!, _recentQueries.value?.get(position)?.fromCrs!!)
        val to = _recentQueries.value?.get(position)?.toCrs
        to?.let {
            _depStation.value = from
            handle.set("depStation", from.stationName)
            val toCrs = CRS(_recentQueries.value?.get(position)?.toName!!, it)
            _destStation.value = toCrs
            handle.set("destStation", toCrs.stationName)
        } ?: run {
            Log.e(TAG, "StationName: ${from.stationName}")
            _depStation.value = from
            handle.set("depStation", from.stationName)
        }
    }

    fun trackService(rid: String, tiploc: String, fbId: String, serviceDetailsUiModel: ServiceDetailsUiModel) {

        val request = TrackServiceRequest(rid, tiploc, fbId, serviceDetailsUiModel)
        trackServiceUseCase(request) { it.either(::handleFailure, ::handleTrackResult) }

    }

    private fun handleTrackResult(trackServiceResponse: TrackServiceResponse) {
        Log.d("VM", "Tracking? ${trackServiceResponse.tracking}")
        _trackServiceSuccess.postValue(trackServiceResponse.tracking)
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
        handle.set("depStation", crs.stationName)
    }

    fun saveDestStation(crs: CRS) {
        _destStation.postValue(crs)
        handle.set("destStation", crs.stationName)
    }

    fun clearDepStation() {
        _depStation.postValue(null)
        handle.remove<String>("depStation")
    }

    fun clearDestStation() {
        _destStation.postValue(null)
        handle.remove<String>("destStation")
    }

    fun clearNrcc() {
        _nrccMessages.postValue(emptyList())
    }

    fun saveFavouriteRoute() {

        viewModelScope.launch {
            favouritesFlow.collect {

            }
        }

    }

    private fun handleFavouritesSuccess(success: Boolean) {
        if (success) {

        }
    }

    private fun handleCrs(list: List<CRS>) {
        _crsStationCodes.value = list
    }

    private fun handleResponse(response: GetBoardWithDetailsResult) {
        response.trainServices?.let {
            _services.value = it.trainServices
        } ?: run {
            _services.value = emptyList()
        }
        response.nrccMessages?.let { nrcc ->
            nrcc.messages?.let {
                _nrccMessages.postValue(it)
            }
        }
    }

    private fun handleServiceDetails(serviceDetails: ServiceDetailsUiModel) {
        _serviceDetailsResult.value = serviceDetails
    }

    @ExperimentalCoroutinesApi
    override fun onCleared() {
        viewModelScope.cancel()
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
    private val trackServiceUseCase: TrackServiceUseCase,
    private val getRecentQueriesUseCase: GetRecentQueriesUseCase,
    private val saveFavouriteUseCase: SaveFavouriteUseCase,
    private val connectionStateEmitter: ConnectionStateEmitter,
    private val favouritesDataStore: DataStore<Favourites>
) : ViewModelFactory<HomeViewModel> {

    override fun create(handle: SavedStateHandle): HomeViewModel {
        return HomeViewModel(handle, getStationsUseCase, getAllStationCodesUseCase,
                getDeparturesUseCase, getServiceDetailsUseCase, getArrivalsUseCase,
                trackServiceUseCase, getRecentQueriesUseCase, saveFavouriteUseCase,
                connectionStateEmitter, favouritesDataStore)
    }
}
