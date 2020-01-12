package com.cniekirk.traintimes.ui.main

import com.cniekirk.traintimes.domain.usecase.GetServiceDetailsUseCase
import com.cniekirk.traintimes.vm.BaseViewModel
import javax.inject.Inject

class ServiceDetailViewModel @Inject constructor(
    private val getServiceDetailsUseCase: GetServiceDetailsUseCase
): BaseViewModel() {



}