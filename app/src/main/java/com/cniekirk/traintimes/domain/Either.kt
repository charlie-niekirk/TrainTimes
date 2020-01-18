package com.cniekirk.traintimes.domain

/**
 * An Either monad implemented in kotlin
 * @link https://github.com/android10/Android-CleanArchitecture-Kotlin/blob/master/app/src/main/kotlin/com/fernandocejas/sample/core/functional/Either.kt
 */
sealed class Either<out Failure, out Success> {

    /** * Represents the left side of [Either] class which by convention is a "Failure". */
    data class Left<out Failure>(val a: Failure) : Either<Failure, Nothing>()
    /** * Represents the right side of [Either] class which by convention is a "Success". */
    data class Right<out Success>(val b: Success) : Either<Nothing, Success>()

    val isRight get() = this is Right<Success>
    val isLeft get() = this is Left<Failure>

    fun <Failure> left(a: Failure) = Left(a)
    fun <Success> right(b: Success) = Right(b)

    fun either(fncL: (Failure) -> Any, fncR: (Success) -> Any): Any =
        when(this) {
            is Left -> fncL(a)
            is Right -> fncR(b)
        }

}

// Composes 2 functions
fun <A, B, C> ((A) -> B).c(f: (B) -> C): (A) -> C = {
    f(this(it))
}