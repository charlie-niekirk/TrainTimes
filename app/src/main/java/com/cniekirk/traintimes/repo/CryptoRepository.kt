package com.cniekirk.traintimes.repo

import java.io.InputStream
import java.io.OutputStream

interface CryptoRepository {
    fun encrypt(rawBytes: ByteArray, outputStream: OutputStream)
    fun decrypt(inputStream: InputStream): ByteArray
}