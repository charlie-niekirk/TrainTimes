package com.cniekirk.traintimes.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cniekirk.traintimes.domain.Failure

/**
 * Base class fro all ViewModel instances to inherit from
 * It handles the failure case for all operations
 */
abstract class BaseViewModel: ViewModel() {

    private val failure: MutableLiveData<Failure> = MutableLiveData()

    /**
     * @param failure: The failure associated with the failed operation
     * Logs the error to Logcat
     */
    protected fun handleFailure(failure: Failure) {
        Log.d("FAILURE", "Failure: $failure")
        this.failure.value = failure
    }

}