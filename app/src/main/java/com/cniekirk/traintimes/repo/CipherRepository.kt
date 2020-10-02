package com.cniekirk.traintimes.repo

import javax.crypto.Cipher

interface CipherRepository {
    val encryptCipher: Cipher
    fun decryptCipher(iv: ByteArray): Cipher
}