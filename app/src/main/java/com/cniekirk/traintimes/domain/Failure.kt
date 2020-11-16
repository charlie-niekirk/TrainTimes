package com.cniekirk.traintimes.domain

/**
 * Class representing a Failure
 */
sealed class Failure {

    class NetworkConnectionError: Failure()
    class ServerError(message: String): Failure()
    class NoCrsFailure(): Failure()
    class NoDestinationFailure(): Failure()
    class MoreRailcardsThanPassengersError: Failure()
    class NoRecentQueriesFailure(): Failure()
    class FavouriteNotSavedFailure : Failure()

    abstract class FeatureFailure: Failure()

}