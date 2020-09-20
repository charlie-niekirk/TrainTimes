package com.cniekirk.traintimes.domain.usecase

import com.cniekirk.traintimes.repo.ProtoRepository
import javax.inject.Inject

class RemoveFavouriteUseCase @Inject constructor(
    private val protoRepository: ProtoRepository
): BaseUseCase<Boolean, Int>() {

    override suspend fun run(params: Int) = protoRepository.removeFavourite(params)

}