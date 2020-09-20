package com.cniekirk.traintimes.domain.usecase

import com.cniekirk.traintimes.model.Favourites
import com.cniekirk.traintimes.repo.ProtoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavouritesUseCase @Inject constructor(
    private val protoRepository: ProtoRepository
): BaseUseCase<Flow<Favourites>, Unit?>() {

    override suspend fun run(params: Unit?) = protoRepository.getFavourites()

}