package com.cniekirk.traintimes.utils

import android.util.Log
import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure
import retrofit2.Call

/**
 * @param call: The retrofit call
 * @param transform: The transformation function to apply if needed
 * @return An [Either] monad representing a [Failure] or an [R] response
 */
fun <T, R> request(call: Call<T>, transform: (T) -> R): Either<Failure, R> {
    return try {
        val response = call.execute()
        Log.d("REPO", "Res: ${response.body()}")
        when (response.isSuccessful) {
            true -> {
                response.body()?.let {
                    Log.d("REPO", "Result: ${it}")
                    Either.Right(transform(it))
                } ?: Either.Left(Failure.ServerError())
            }
            false -> {
                Either.Left(Failure.ServerError())
            }
        }
    } catch (exception: Throwable) {
        Log.e("REPO", "Error: ${exception.localizedMessage}")
        Either.Left(Failure.ServerError())
    }
}