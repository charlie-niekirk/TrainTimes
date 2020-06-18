package com.cniekirk.traintimes

import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.domain.usecase.GetStationsUseCase
import com.cniekirk.traintimes.entity.NREFactory
import com.cniekirk.traintimes.extensions.getEitherSuccess
import com.cniekirk.traintimes.repo.CrsRepository
import com.cniekirk.traintimes.repo.NreRepository
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    private lateinit var getStationsUseCase: GetStationsUseCase

    @Mock
    private lateinit var crsRepository: CrsRepository

    @Before
    fun setup() {
        getStationsUseCase = GetStationsUseCase(crsRepository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Invoke should return crs list`() {
        val query = "WAT"
        whenever(crsRepository.getCrsCodes(anyString()))
            .doReturn(getEitherSuccess(NREFactory.providesCRS()))

        val result = runBlocking { getStationsUseCase.run(query) }
        result.right { value: List<CRS> ->
            value[0].crs shouldBeEqualTo query
        }
    }

}