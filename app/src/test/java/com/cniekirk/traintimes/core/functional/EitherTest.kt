package com.cniekirk.traintimes.core.functional

import com.cniekirk.traintimes.UnitTest
import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.getOrElse
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Test

class EitherTest : UnitTest() {

    @Test
    fun `Either Right should return correct type`() {
        val result = Either.Right("waterloo")

        result shouldBeInstanceOf Either::class.java
        result.isRight shouldBe true
        result.isLeft shouldBe false
        result.fold({},
            { right ->
                right shouldBeInstanceOf String::class.java
                right shouldBeEqualTo "waterloo"
            })
    }

    @Test fun `Either Left should return correct type`() {
        val result = Either.Left("waterloo")

        result shouldBeInstanceOf Either::class.java
        result.isLeft shouldBe true
        result.isRight shouldBe false
        result.fold(
            { left ->
                left shouldBeInstanceOf String::class.java
                left shouldBeEqualTo "waterloo"
            }, {})
    }

    @Test fun `Either fold should ignore passed argument if it is Right type`() {
        val result = Either.Right("Right").getOrElse("Other")

        result shouldBeEqualTo "Right"
    }

    @Test fun `Either fold should return argument if it is Left type`() {
        val result = Either.Left("Left").getOrElse("Other")

        result shouldBeEqualTo "Other"
    }
}