package com.cniekirk.traintimes.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.cniekirk.traintimes.base.BaseViewModel
import com.cniekirk.traintimes.base.ViewModelFactory
import com.cniekirk.traintimes.domain.usecase.GetStationInformationUseCase
import com.cniekirk.traintimes.model.stationdetails.Station
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StationViewModel constructor(
    val handle: SavedStateHandle,
    private val stationInformationUseCase: GetStationInformationUseCase
) : BaseViewModel() {

    private val _stationInformation = MutableLiveData<Station>()

    val stationInformation: LiveData<Station>
        get() = _stationInformation

    fun getStationInformation(crsCode: String) {

        stationInformationUseCase(crsCode) {
            it.either(::handleFailure, ::handleStationInformation)
        }

    }

    private fun handleStationInformation(station: Station) {
        _stationInformation.postValue(station)
    }

}

@Singleton
class StationViewModelFactory @Inject constructor(
    val stationInformationUseCase: GetStationInformationUseCase
): ViewModelFactory<StationViewModel> {

    override fun create(handle: SavedStateHandle): StationViewModel {
        return StationViewModel(handle, stationInformationUseCase)
    }

}