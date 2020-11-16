package com.cniekirk.traintimes.ui.viewmodel

//import com.cniekirk.traintimes.model.Favourites
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.cniekirk.traintimes.base.BaseViewModel
import com.cniekirk.traintimes.base.SingleLiveEvent
import com.cniekirk.traintimes.base.ViewModelFactory
import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.domain.model.DepartureAtTimeRequest
import com.cniekirk.traintimes.domain.model.State
import com.cniekirk.traintimes.domain.usecase.*
import com.cniekirk.traintimes.model.Favourites
import com.cniekirk.traintimes.model.getdepboard.local.Query
import com.cniekirk.traintimes.model.getdepboard.res.GetBoardWithDetailsResult
import com.cniekirk.traintimes.model.getdepboard.res.Message
import com.cniekirk.traintimes.model.track.req.TrackServiceRequest
import com.cniekirk.traintimes.model.track.res.TrackServiceResponse
import com.cniekirk.traintimes.model.ui.DepartureItem
import com.cniekirk.traintimes.model.ui.ServiceDetailsUiModel
import com.cniekirk.traintimes.utils.ConnectionStateEmitter
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeViewModel constructor(
    val handle: SavedStateHandle,
    private val getStationsUseCase: GetStationsUseCase,
    private val getAllStationCodesUseCase: GetAllStationCodesUseCase,
    private val getDeparturesUseCase: GetDeparturesUseCase,
    private val getDeparturesAtTime: GetDeparturesAtTimeUseCase,
    private val getServiceDetailsUseCase: GetServiceDetailsUseCase,
    private val getArrivalsUseCase: GetArrivalsUseCase,
    private val trackServiceUseCase: TrackServiceUseCase,
    private val getRecentQueriesUseCase: GetRecentQueriesUseCase,
    private val saveFavouriteUseCase: SaveFavouriteUseCase,
    private val getFavouritesUseCase: GetFavouritesUseCase,
    private val removeFavouriteUseCase: RemoveFavouriteUseCase,
    private val connectionState: ConnectionStateEmitter
) : BaseViewModel() {

    val services: LiveData<List<DepartureItem>>
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
    val saveFavouriteSuccess: LiveData<Boolean>
        get() = _saveFavouriteSuccess
    val favourites: LiveData<List<Query>>
        get() = _favourites
    val state: LiveData<State>
        get() = _state

    private val _services = MutableLiveData<List<DepartureItem>>()
    private val _crsStationCodes = MutableLiveData<List<CRS>>()
    private val _depStation = MutableLiveData<CRS>()
    private val _destStation = MutableLiveData<CRS>()
    private val _serviceDetailsResult = MutableLiveData<ServiceDetailsUiModel>()
    private val _serviceDetailId = MutableLiveData<String>()
    private val _trackServiceSuccess = SingleLiveEvent<Boolean>()
    private val _recentQueries = MutableLiveData<List<Query>>()
    private val _canProceedToSearch = SingleLiveEvent<Boolean>()
    private val _nrccMessages = MutableLiveData<List<Message>>()
    private val _saveFavouriteSuccess = SingleLiveEvent<Boolean>()
    private val _favourites = MutableLiveData<List<Query>>()
    private val _state = MutableLiveData<State>()

    private val scopedIOContext = viewModelScope + Dispatchers.IO

    private var requestDate: Instant = Instant.now()

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

    private fun getTrainsInternal(time: String) {
        _state.postValue(State.Loading)
        Timber.i("EXEC - getTrains()")
        _destStation.value?.let { crs ->
            Timber.i("Dest should be good!")
            _depStation.value?.let { depCrs ->
                if (!depCrs.crs.equals("", true)) {
                    // Get specific departures
                    getDeparturesAtTime(DepartureAtTimeRequest(depCrs, crs, time))
                    { it.either(::handleFailure, ::handleResponse) }
                } else {
                    // Get all arrivals
                    getArrivalsUseCase(arrayOf(crs.crs, ""))
                    { it.either(::handleFailure, ::handleResponse) }
                }
            } ?: run {
                Timber.i("Dep should be empty!")
                // Get all arrivals
                getArrivalsUseCase(arrayOf(crs.crs, ""))
                { it.either(::handleFailure, ::handleResponse) }
            }
        } ?: run {
            // Check if empty
            _depStation.value?.let { depCrs ->
                if (!depCrs.crs.equals("", true)) {
                    // Get all departures
                    getDeparturesAtTime(DepartureAtTimeRequest(_depStation.value!!, CRS("", ""), time))
                    { it.either(::handleFailure, ::handleResponse) }
                } else {
                    // Needed so old value isn't cached and reloaded which looks odd to the user
                    _services.postValue(emptyList())
                    handleFailure(Failure.NoCrsFailure())
                }
            } ?: run {
                // Needed so old value isn't cached and reloaded which looks odd to the user
                _services.postValue(emptyList())
                handleFailure(Failure.NoCrsFailure())
            }
        }
    }

    fun getTrains() {

        requestDate = Instant.now()
        val time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.ENGLISH).format(Date.from(requestDate))
        getTrainsInternal(time)

    }

    fun getPreviousTrains() {

        requestDate = requestDate.minus(1, ChronoUnit.HOURS)
        val previousTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.ENGLISH)
            .format(Date.from(requestDate))
        getTrainsInternal(previousTime)

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
            Timber.i("StationName: ${from.stationName}")
            _depStation.value = from
            handle.set("depStation", from.stationName)
        }
    }

    fun performFavouriteQuery(position: Int) {
        _depStation.value = null
        handle.remove<String>("depStation")
        _destStation.value = null
        handle.remove<String>("destStation")
        val from = CRS(_favourites.value?.get(position)?.fromName!!, _favourites.value?.get(position)?.fromCrs!!)
        val to = _favourites.value?.get(position)?.toCrs
        to?.let {
            if (to.isEmpty()) {
                Timber.i("StationName: ${from.stationName}")
                _depStation.value = from
                handle.set("depStation", from.stationName)
            } else {
                _depStation.value = from
                handle.set("depStation", from.stationName)
                val toCrs = CRS(_favourites.value?.get(position)?.toName!!, it)
                _destStation.value = toCrs
                handle.set("destStation", toCrs.stationName)
            }
        } ?: run {
            Timber.i("StationName: ${from.stationName}")
            _depStation.value = from
            handle.set("depStation", from.stationName)
        }
    }

    fun trackService(rid: String, tiploc: String, fbId: String, serviceDetailsUiModel: ServiceDetailsUiModel) {

        val request = TrackServiceRequest(rid, tiploc, fbId, serviceDetailsUiModel)
        trackServiceUseCase(request) { it.either(::handleFailure, ::handleTrackResult) }

    }

    private fun handleTrackResult(trackServiceResponse: TrackServiceResponse) {
        Timber.d("Tracking? ${trackServiceResponse.tracking}")
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
        _depStation.value = crs
        handle.set("depStation", crs.stationName)
    }

    fun saveDestStation(crs: CRS) {
        _destStation.value = crs
        handle.set("destStation", crs.stationName)
    }

    fun clearDepStation() {
        _depStation.value = null
        handle.remove<String>("depStation")
    }

    fun clearDestStation() {
        _destStation.value = null
        handle.remove<String>("destStation")
    }

    fun clearNrcc() {
        _nrccMessages.postValue(emptyList())
    }

    fun saveFavouriteRoute() {

        Timber.d("Saving!")

        _depStation.value?.let { dep ->
            Timber.d("Saving 1")
            _destStation.value?.let { dest ->
                Timber.d("Saving 2")
                saveFavouriteUseCase(arrayOf(dep, dest)) {
                    Timber.d("Saving 3")
                    it.either(::handleFailure, ::handleFavouritesSuccess)
                }
            } ?: run {
                saveFavouriteUseCase(arrayOf(dep, CRS("", ""))) {
                    Timber.d("Saving 3")
                    it.either(::handleFailure, ::handleFavouritesSuccess)
                }
            }
        } ?: run {
            _saveFavouriteSuccess.postValue(false)
        }

    }

    fun getFavourites() {

        getFavouritesUseCase(null) {
            it.either(::handleFailure, ::handleFavouritesFlow)
        }

    }

    fun clearServices() {
        _services.postValue(emptyList())
    }

    fun removeFavourite() {

        val favs = _favourites.value
        val match = favs?.filter { query ->
            query.toCrs?.let {
                (query.fromCrs.equals(_depStation.value?.crs, true))
                    .and(it.equals(_destStation.value?.crs, true))
            } ?: run {
                query.fromCrs.equals(_depStation.value?.crs, true)
            }
        }?.get(0)

        favs?.let { favourites ->
            removeFavouriteUseCase(favourites.indexOf(match)) {
                it.either(::handleFailure, ::handleFavouriteRemoved)
            }
        }

    }

    private fun handleFavouriteRemoved(success: Boolean) {
        Timber.i("Favourite removed successfully: $success")
    }

    private fun handleFavouritesFlow(favourites: Flow<Favourites>) {

        scopedIOContext.launch {
            favourites.collect {
                // Post to LiveData
                val queryList = it.favourite.map { fav ->
                    Query(fav.fromCrs, fav.fromStationName, fav.toCrs, fav.toStationName)
                }
                _favourites.postValue(queryList)
            }
        }

    }

    private fun handleFavouritesSuccess(success: Boolean) =
        _saveFavouriteSuccess.postValue(success)

    private fun handleCrs(list: List<CRS>) {
        _crsStationCodes.value = list
    }

    private fun handleResponse(response: GetBoardWithDetailsResult) {
        response.trainServices?.let {
            if (_services.value.isNullOrEmpty()) {
                val initialServicesList = mutableListOf(DepartureItem.LoadBeforeItem, DepartureItem.LoadAfterItem)
                val allServices = it.trainServices?.map { service -> DepartureItem.DepartureServiceItem(service) }
                allServices?.forEach { depItem ->
                    val subLoc = depItem.service.subsequentLocations?.locations
                    if (depItem.service.origin.location.crs.equals(subLoc!![subLoc.size - 1].stationCode, true)) {
                        depItem.isCircular = true
                    }
                }
                initialServicesList.addAll(1, allServices!!)
                _services.value = initialServicesList
            } else {
                _services.value?.let { current ->
                    val deps = current.toMutableList()
                    deps.addAll(1, it.trainServices?.map { service -> DepartureItem.DepartureServiceItem(service) }!!)
                    _services.value = deps
                }
            }

        } ?: run {
            _services.value = emptyList()
        }
        response.nrccMessages?.let { nrcc ->
            nrcc.messages?.let {
                _nrccMessages.postValue(it)
            }
        }
        _state.postValue(State.Idle)
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
    private val getDeparturesAtTimeUseCase: GetDeparturesAtTimeUseCase,
    private val getServiceDetailsUseCase: GetServiceDetailsUseCase,
    private val getArrivalsUseCase: GetArrivalsUseCase,
    private val trackServiceUseCase: TrackServiceUseCase,
    private val getRecentQueriesUseCase: GetRecentQueriesUseCase,
    private val saveFavouriteUseCase: SaveFavouriteUseCase,
    private val getFavouritesUseCase: GetFavouritesUseCase,
    private val removeFavouriteUseCase: RemoveFavouriteUseCase,
    private val connectionStateEmitter: ConnectionStateEmitter
) : ViewModelFactory<HomeViewModel> {

    override fun create(handle: SavedStateHandle): HomeViewModel {
        return HomeViewModel(handle, getStationsUseCase, getAllStationCodesUseCase,
                getDeparturesUseCase, getDeparturesAtTimeUseCase, getServiceDetailsUseCase,
                getArrivalsUseCase, trackServiceUseCase, getRecentQueriesUseCase, saveFavouriteUseCase,
                getFavouritesUseCase, removeFavouriteUseCase, connectionStateEmitter)
    }
}
