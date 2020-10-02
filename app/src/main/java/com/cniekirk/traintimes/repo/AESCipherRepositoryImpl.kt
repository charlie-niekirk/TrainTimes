package com.cniekirk.traintimes.repo

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.cniekirk.traintimes.di.SecurityModule
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject
import javax.inject.Named

class AESCipherRepositoryImpl @Inject constructor(
    @Named(SecurityModule.KEY_NAME) private val keyName: String,
    private val keyStore: KeyStore,
    @Named(SecurityModule.KEY_STORE_NAME) private val keyStoreName: String
): CipherRepository {

    override val encryptCipher: Cipher
        get() = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        }

    override fun decryptCipher(iv: ByteArray): Cipher =
        Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv))
        }

    private fun getOrCreateKey(): SecretKey =
        (keyStore.getEntry(keyName, null) as? KeyStore.SecretKeyEntry)?.secretKey
            ?: generateKey()

    private fun generateKey(): SecretKey =
        KeyGenerator.getInstance(ALGORITHM, keyStoreName)
            .apply { init(keyGenParams) }
            .generateKey()

    private val keyGenParams =
        KeyGenParameterSpec.Builder(
            keyName,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setBlockModes(BLOCK_MODE)
            setEncryptionPaddings(PADDING)
            setUserAuthenticationRequired(false)
            setRandomizedEncryptionRequired(true)
        }.build()

    companion object {
        const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
        const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }

}