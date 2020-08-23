package com.cniekirk.traintimes.utils.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * Extension function reducing bolierplate code needed to access network state
 */
val Context.networkInfo: NetworkInfo? get() =
     (this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo