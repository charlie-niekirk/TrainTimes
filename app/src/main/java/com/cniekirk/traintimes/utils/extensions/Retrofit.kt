package com.cniekirk.traintimes.utils.extensions

import okhttp3.Call
import okhttp3.Request
import retrofit2.Retrofit

internal inline fun Retrofit.Builder.callFactory(
    crossinline body: (Request) -> Call
) = callFactory(object : Call.Factory {
    override fun newCall(request: Request): Call = body(request)
})