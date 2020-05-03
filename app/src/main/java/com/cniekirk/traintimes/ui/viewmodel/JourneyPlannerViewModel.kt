package com.cniekirk.traintimes.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.cniekirk.traintimes.base.BaseViewModel
import com.cniekirk.traintimes.base.SingleLiveEvent
import com.cniekirk.traintimes.base.ViewModelFactory
import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.data.prefs.PreferenceProvider
import com.cniekirk.traintimes.domain.usecase.*
import com.cniekirk.traintimes.model.journeyplanner.req.JourneyPlanRepoRequest
import com.cniekirk.traintimes.model.journeyplanner.req.JourneyPlanRequest
import com.cniekirk.traintimes.model.journeyplanner.req.Railcard
import com.cniekirk.traintimes.model.journeyplanner.res.JourneyPlannerResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JourneyPlannerViewModel(
    val handle: SavedStateHandle,
    private val getStationsUseCase: GetStationsUseCase,
    private val getAllStationCodesUseCase: GetAllStationCodesUseCase,
    private val getJourneyPlanUseCase: GetJourneyPlanUseCase,
    private val preferenceProvider: PreferenceProvider
): BaseViewModel() {

    val crsStationCodes = MutableLiveData<List<CRS>>()
    val adults = MutableLiveData(1)
    val children = MutableLiveData(0)
    val depStation = MutableLiveData<CRS>()
    val destStation = MutableLiveData<CRS>()
    val journeyPlannerResponse = SingleLiveEvent<JourneyPlannerResponse>()
    val directTrainsOnly = MutableLiveData<Boolean>()
    val railcards = MutableLiveData<MutableList<Railcard>>()

    val chipDateTime = MutableLiveData<String>()
    val returnChipDateTime = MutableLiveData<String>()

    private val datetimeLiveData = MutableLiveData<String>()
    private val returnDateTimeLiveData = MutableLiveData<String>()
    fun depStationText(): String? = handle.get<String>("depStation")

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

    fun getJourneyPlan() {
        depStation.value?.let { depStation ->
            destStation.value?.let { destStation ->
                datetimeLiveData.value?.let { datetime ->
                    returnDateTimeLiveData.value?.let { returnDatetime ->
                        directTrainsOnly.value?.let { direct ->
                            val request = JourneyPlanRequest(datetime, adults.value, children.value, railcards.value, returnDatetime, direct)
                            val query = JourneyPlanRepoRequest(depStation.stationName, destStation.stationName, request)
                            getJourneyPlanUseCase(query) { it.either(::handleFailure, ::handleJourneyPlanResponse) }
                        } ?: run {
                            val request = JourneyPlanRequest(datetime, adults.value, children.value, railcards.value, returnDatetime)
                            val query = JourneyPlanRepoRequest(depStation.stationName, destStation.stationName, request)
                            getJourneyPlanUseCase(query) { it.either(::handleFailure, ::handleJourneyPlanResponse) }
                        }
                    } ?: run {
                        directTrainsOnly.value?.let { direct ->
                            val request = JourneyPlanRequest(datetime, adults.value, children.value, railcards.value, directOnly = direct)
                            val query = JourneyPlanRepoRequest(depStation.stationName, destStation.stationName, request)
                            getJourneyPlanUseCase(query) { it.either(::handleFailure, ::handleJourneyPlanResponse) }
                        } ?: run {
                            val request = JourneyPlanRequest(datetime, adults.value, children.value, railcards.value)
                            val query = JourneyPlanRepoRequest(depStation.stationName, destStation.stationName, request)
                            getJourneyPlanUseCase(query) { it.either(::handleFailure, ::handleJourneyPlanResponse) }
                        }
                    }
                } ?: run { Log.e("Vm", "No date for some reason") }
            } ?: run { Log.e("Vm", "PLANNING - Fail dest") }
        } ?: run { Log.e("Vm", "PLANNING - Fail dep") }
    }

    fun getCrsCodes() {
        getAllStationCodesUseCase(null) { it.either(::handleFailure, ::handleCrs) }
    }

    private fun handleJourneyPlanResponse(journeyPlanResponse: JourneyPlannerResponse) =
        journeyPlannerResponse.postValue(journeyPlanResponse)

    fun saveDepStation(crs: CRS) {
        depStation.postValue(crs)
        handle.set("depStation", crs.crs)
    }

    fun saveDestStation(crs: CRS) {
        destStation.postValue(crs)
        handle.set("destStation", crs.crs)
    }

    fun saveDatetime(date: Calendar) {
        // Parse string here
        datetimeLiveData.postValue(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.ENGLISH).format(date.time))

        // Use the selected dateTime
        val yrmonth = "${date[Calendar.DAY_OF_MONTH]}/${date[Calendar.MONTH]}"
        val min = when {
            date[Calendar.MINUTE] == 0 -> {
                "00"
            }
            date[Calendar.MINUTE] < 10 -> {
                "0${date[Calendar.MINUTE]}"
            }
            else -> {
                date[Calendar.MINUTE].toString()
            }
        }
        val time = "${date[Calendar.HOUR_OF_DAY]}:$min"
        val dateString = "$yrmonth at $time"
        chipDateTime.postValue(dateString)
        handle.set("date_string", dateString)
    }

    fun saveReturnDatetime(date: Calendar) {
        // Parse string here
        returnDateTimeLiveData.postValue(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.ENGLISH).format(date.time))

        // Use the selected dateTime
        val yrmonth = "${date[Calendar.DAY_OF_MONTH]}/${date[Calendar.MONTH]}"
        val min = when {
            date[Calendar.MINUTE] == 0 -> {
                "00"
            }
            date[Calendar.MINUTE] < 10 -> {
                "0${date[Calendar.MINUTE]}"
            }
            else -> {
                date[Calendar.MINUTE].toString()
            }
        }
        val time = "${date[Calendar.HOUR_OF_DAY]}:$min"
        val dateString = "$yrmonth at $time"
        returnChipDateTime.postValue(dateString)
        handle.set("return_date_string", dateString)
    }

    fun clearPassengers() {
        adults.postValue(1)
        children.postValue(0)
    }

    fun updatePassengers(type: String, isDecrement: Boolean) {
        if (type.equals("Adults", true)) {
            if (isDecrement) {
                adults.value?.let {
                    val count = it - 1
                    if (count >= 0) adults.postValue(count)
                }
            } else {
                adults.value?.let {
                    val count = it + 1
                    adults.postValue(count)
                }
            }
        } else {
            if (isDecrement) {
                children.value?.let {
                    val count = it - 1
                    if (count >= 0) children.postValue(count)
                }
            } else {
                children.value?.let {
                    val count = it + 1
                    children.postValue(count)
                }
            }
        }
    }

    fun clearRailcards() {
        railcards.postValue(mutableListOf())
    }

    fun updateRailcards(code: String, count: Int) {
        if (railcards.value!!.isEmpty()) {
            Log.e("VM", "Empty!")
            railcards.postValue(mutableListOf(Railcard(code, count)))
        } else if (railcards.value!!.find{ code.equals(it.code, true) } != null) {
            railcards.value!!.find{ code.equals(it.code, true) }?.count =
                railcards.value!!.find{ code.equals(it.code, true) }?.count?.plus(count)!!
            Log.e("VM", "Count: ${railcards.value!!.find{ code.equals(it.code, true) }?.count}")
        } else {
            // List exists and Railcard does not
            railcards.value!!.add(Railcard(code, count))
        }
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

    fun shouldShowPrice() = preferenceProvider.getShouldShowPrices()

}

@Singleton
class JourneyPlannerViewModelFactory @Inject constructor(
    private val getStationsUseCase: GetStationsUseCase,
    private val getAllStationCodesUseCase: GetAllStationCodesUseCase,
    private val getJourneyPlanUseCase: GetJourneyPlanUseCase,
    private val preferenceProvider: PreferenceProvider
) : ViewModelFactory<JourneyPlannerViewModel> {

    override fun create(handle: SavedStateHandle): JourneyPlannerViewModel {
        return JourneyPlannerViewModel(handle, getStationsUseCase, getAllStationCodesUseCase, getJourneyPlanUseCase, preferenceProvider)
    }
}