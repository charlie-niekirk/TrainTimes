package com.cniekirk.traintimes.domain.usecase

import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.repo.NreRepository
import javax.inject.Inject

class SaveFavouriteUseCase @Inject constructor(private val nreRepository: NreRepository)
    :BaseUseCase<Boolean, Array<CRS>>() {

    override suspend fun run(params: Array<CRS>) = nreRepository.saveFavouriteQuery(params[0], params[1])

}