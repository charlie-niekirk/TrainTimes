package com.cniekirk.traintimes.utils

class Sign {

    init {
        System.loadLibrary("contour")
    }

    external fun e(plaintext: ByteArray): ByteArray

    fun sign(plaintext: ByteArray): String {

        val signedBytes = e(plaintext)
        return ""

    }

}