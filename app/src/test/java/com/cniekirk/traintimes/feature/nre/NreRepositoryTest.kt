package com.cniekirk.traintimes.feature.nre

import com.cniekirk.traintimes.UnitTest
import com.cniekirk.traintimes.data.local.FavouritesDao
import com.cniekirk.traintimes.data.local.RecentQueriesDao
import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.data.prefs.PreferenceProvider
import com.cniekirk.traintimes.data.remote.NREService
import com.cniekirk.traintimes.data.remote.TrackTimesService
import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.repo.NreRepositoryImpl
import com.cniekirk.traintimes.utils.NetworkHandler
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.squareup.moshi.JsonAdapter
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.`when`
import org.mockito.BDDMockito.any
import org.mockito.Mock

class NreRepositoryTest: UnitTest() {

    private lateinit var nreRepository: NreRepositoryImpl

    @Mock private lateinit var networkHandler: NetworkHandler
    @Mock private lateinit var nreService: NREService
    @Mock private lateinit var trackTimesService: TrackTimesService
    @Mock private lateinit var preferenceProvider: PreferenceProvider
    @Mock private lateinit var recentQueriesDao: RecentQueriesDao
    @Mock private lateinit var favouritesDao: FavouritesDao
    @Mock private lateinit var adapter: JsonAdapter<com.cniekirk.traintimes.model.getdepboard.local.Query>

    @Before fun setup() {
        nreRepository = NreRepositoryImpl(
            networkHandler,
            nreService,
            trackTimesService,
            preferenceProvider,
            recentQueriesDao,
            favouritesDao,
            adapter
        )
    }

    @Test fun `national rail service should return network failure when no connection`() {

        `when`(adapter.toJson(any())).thenReturn("{}")
        `when`(networkHandler.isConnected).thenReturn(false)

        val depBoard = nreRepository.getDeparturesAtStation(CRS("London Waterloo", "WAT"), CRS("Salisbury", "SAL"))

        depBoard shouldBeInstanceOf Either::class.java
        depBoard.isLeft shouldBeEqualTo true
        depBoard.fold({ failure -> failure shouldBeInstanceOf Failure.NetworkConnectionError::class.java }, {})
        verifyZeroInteractions(nreService)

    }

    @Test fun `national rail service should return network failure when connection is undefined`() {

        `when`(adapter.toJson(any())).thenReturn("{}")
        `when`(networkHandler.isConnected).thenReturn(null)

        val depBoard = nreRepository.getDeparturesAtStation(CRS("London Waterloo", "WAT"), CRS("Salisbury", "SAL"))

        depBoard shouldBeInstanceOf Either::class.java
        depBoard.isLeft shouldBeEqualTo true
        depBoard.fold({ failure -> failure shouldBeInstanceOf Failure.NetworkConnectionError::class.java }, {})
        verifyZeroInteractions(nreService)

    }

    @Test fun `national rail service should return server error if no successful response`() {
        `when`(adapter.toJson(any())).thenReturn("{}")
        `when`(networkHandler.isConnected).thenReturn(true)

        val depBoard = nreRepository.getDeparturesAtStation(CRS("London Waterloo", "WAT"), CRS("Salisbury", "SAL"))

        depBoard shouldBeInstanceOf Either::class.java
        depBoard.isLeft shouldBeEqualTo true
        depBoard.fold({ failure -> failure shouldBeInstanceOf Failure.ServerError::class.java }, {})
    }


}