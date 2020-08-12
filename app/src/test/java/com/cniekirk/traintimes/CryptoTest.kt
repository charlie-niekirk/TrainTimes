package com.cniekirk.traintimes

import com.cniekirk.traintimes.utils.Sign
import com.cniekirk.traintimes.utils.extensions.toHexString
import org.junit.Test
import org.junit.internal.runners.JUnit4ClassRunner
import org.junit.runner.RunWith

@RunWith(JUnit4ClassRunner::class)
class CryptoTest {

    private val payload = "Payload to Sign!"

    @Test
    fun signPayload() {

        val signer = Sign()
        val bytes = signer.e(payload.toByteArray())
        println(bytes.toHexString())

    }

}