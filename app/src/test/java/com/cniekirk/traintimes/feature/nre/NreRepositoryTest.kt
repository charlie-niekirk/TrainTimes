package com.cniekirk.traintimes.feature.nre

import com.cniekirk.traintimes.UnitTest
import com.cniekirk.traintimes.repo.NreRepositoryImpl
import com.cniekirk.traintimes.utils.NetworkHandler
import org.junit.Before
import org.mockito.Mock

class NreRepositoryTest: UnitTest() {

    private lateinit var nreRepository: NreRepositoryImpl

    @Mock private lateinit var networkHandler: NetworkHandler

    @Before fun setup() {
        nreRepository = NreRepositoryImpl()
    }

}