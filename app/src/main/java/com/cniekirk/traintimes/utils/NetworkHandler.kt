package com.cniekirk.traintimes.utils

import android.content.Context
import com.cniekirk.traintimes.utils.extensions.networkInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Class to make determining network state easy in the backend
 */
@Singleton
class NetworkHandler
@Inject constructor(@ApplicationContext private val context: Context) {
    val isConnected get() = context.networkInfo?.isConnected
}