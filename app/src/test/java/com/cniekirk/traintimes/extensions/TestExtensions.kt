package com.cniekirk.traintimes.extensions

import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure

fun <T: Any> getEitherSuccess(value: T): Either<Failure, T> =
    Either.Right(value)

fun <T: List<Any>> getEitherListSuccess(value: T): Either<Failure, T> =
    Either.Right(value)