package com.cniekirk.traintimes.domain.usecase

import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure
import kotlinx.coroutines.*

/**
 * Base UseCase class as described in the Clean Architecture by Uncle Bob
 */
abstract class BaseUseCase<out Type, in Params> where Type: Any {

    /**
     * Operation to be run on a background thread
     * @param params: The parameters of type [Params] to be used in the operation
     * @return An Either monad representing a [Failure] or data of type [Type]
     */
    abstract suspend fun run(params: Params): Either<Failure, Type>

    /**
     * This function allows us to invoke the class like a function
     * @param params: The parameters of type [Params] to be used in the operation
     * @param onResult: A function which takes an [Either] as its parameter and performs some operation
     */
    operator fun invoke(params: Params, onResult: (Either<Failure, Type>) -> Unit = {}) {

        // Execute background job on the IO thread
        val job = GlobalScope.async(Dispatchers.IO, CoroutineStart.DEFAULT) { run(params) }
        // Process result on the application Main thread
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) { onResult.invoke(job.await()) }

    }

}