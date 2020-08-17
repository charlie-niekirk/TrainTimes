package com.cniekirk.traintimes.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import javax.inject.Inject


/**
 * LiveData that emits internet connection state to registered observers
 * Emits a successful connection when connected to a 3G or better cellular
 * connection, or WiFi.
 *
 * @author Charles Niekirk
 */
class ConnectionStateEmitter @Inject constructor(
    private val applicationContext: Context
) : LiveData<Boolean>() {

    private var isFirstEvent = true

    // Request with connection parameters to observe
    private val networkRequest: NetworkRequest by lazy(LazyThreadSafetyMode.NONE) {
        NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
    }

    /**
     * Called when observers register
     */
    override fun onActive() {
        super.onActive()
        getConnectionDetails()
    }

    /**
     * Registers a [ConnectivityManager.NetworkCallback] with the [ConnectivityManager]
     * and triggers a [LiveData] value update when the network state is observed or changes
     */
    private fun getConnectionDetails() {
        val manager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (value == null && !isNetworkAvailable) {
            postValue(isNetworkAvailable)
        }
        manager?.requestNetwork(networkRequest, callback)
    }

    /**
     * Separate callback definition as the listener needs to be unregistered later
     */
    private val callback: NetworkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            // If there isn't a value currently set or if it's false
            if (value == null || !value!!) {
                if (isFirstEvent) {
                    isFirstEvent = false
                } else {
                    postValue(true)
                }
            }
        }

        override fun onUnavailable() {
            super.onUnavailable()
            postValue(false)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(false)
        }
    }

    private val isNetworkAvailable: Boolean
        get() {
            val manager =
                applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = manager.activeNetworkInfo
            return info != null && info.isConnected
        }

    /**
     * When all observers have unregistered, unregister the [ConnectivityManager.NetworkCallback]
     */
    override fun onInactive() {
        super.onInactive()
        val manager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        manager?.unregisterNetworkCallback(callback)
        isFirstEvent = true
    }
}