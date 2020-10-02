package com.cniekirk.traintimes.domain.usecase

import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.repo.ProtoRepository
import javax.inject.Inject

class SaveFavouriteUseCase @Inject constructor(private val protoRepository: ProtoRepository)
    :BaseUseCase<Boolean, Array<CRS>>() {

    override suspend fun run(params: Array<CRS>) = protoRepository.addFavourite(params[0], params[1])

}