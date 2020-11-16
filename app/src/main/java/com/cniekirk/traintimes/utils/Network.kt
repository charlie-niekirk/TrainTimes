package com.cniekirk.traintimes.utils

import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure
import retrofit2.Call
import timber.log.Timber

/**
 * @param call: The retrofit call
 * @param transform: The transformation function to apply if needed
 * @return An [Either] monad representing a [Failure] or an [R] response
 */
fun <T, R> request(call: Call<T>, transform: (T) -> R): Either<Failure, R> {
    return try {
        val response = call.execute()
        Timber.d("Res: ${response.body()}")
        when (response.isSuccessful) {
            true -> {
                response.body()?.let {
                    Timber.d("Result: $it")
                    Either.Right(transform(it))
                } ?: Either.Left(Failure.ServerError(response.message()))
            }
            false -> {
                Either.Left(Failure.ServerError(response.message()))
            }
        }
    } catch (exception: Throwable) {
        Timber.e("Error: ${exception.localizedMessage}")
        Either.Left(Failure.ServerError(exception.stackTraceToString()))
    }
}