package com.cniekirk.traintimes.base

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cniekirk.traintimes.domain.Failure

private const val TAG = "BaseViewModel"

/**
 * Base class fro all ViewModel instances to inherit from
 * It handles the failure case for all operations
 */
abstract class BaseViewModel: ViewModel() {

    val failure: SingleLiveEvent<Failure> = SingleLiveEvent()

    /**
     * @param failure: The failure associated with the failed operation
     * Logs the error to Logcat
     */
    protected fun handleFailure(failure: Failure) {
        Log.e(TAG, "Failure: $failure")
        this.failure.value = failure
    }

}