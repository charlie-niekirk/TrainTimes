package com.cniekirk.traintimes.domain

/**
 * Class representing a Failure
 */
sealed class Failure {

    class NetworkConnectionError: Failure()
    class ServerError(): Failure()
    class NoCrsFailure(): Failure()

    abstract class FeatureFailure: Failure()

}