package com.cniekirk.traintimes.repo

import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class CryptoRepositoryImpl @Inject constructor(private val cipherProvider: CipherRepository) : CryptoRepository {

    override fun encrypt(rawBytes: ByteArray, outputStream: OutputStream) {
        val cipher = cipherProvider.encryptCipher
        val encryptedBytes = cipher.doFinal(rawBytes)
        with(outputStream) {
            write(cipher.iv.size)
            write(cipher.iv)
            write(encryptedBytes.size)
            write(encryptedBytes)
        }
    }

    override fun decrypt(inputStream: InputStream): ByteArray {
        val ivSize = inputStream.read()
        val iv = ByteArray(ivSize)
        inputStream.read(iv)
        val encryptedDataSize = inputStream.read()
        val encryptedData = ByteArray(encryptedDataSize)
        inputStream.read(encryptedData)
        val cipher = cipherProvider.decryptCipher(iv)
        return cipher.doFinal(encryptedData)
    }
}