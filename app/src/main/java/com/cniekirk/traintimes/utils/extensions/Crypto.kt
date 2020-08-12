package com.cniekirk.traintimes.utils.extensions

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@ExperimentalUnsignedTypes
fun ByteArray.toHexString() = asUByteArray().joinToString("") { it.toString(16).padStart(2, '0') }

fun String.hmac(key: String): String {

    val algo = "HmacSHA256"

    val mac = Mac.getInstance(algo)
    mac.init(SecretKeySpec(key.toByteArray(), algo))

    return mac.doFinal(this.toByteArray()).toHexString()

}