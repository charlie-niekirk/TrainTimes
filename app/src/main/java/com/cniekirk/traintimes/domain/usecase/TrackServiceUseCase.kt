package com.cniekirk.traintimes.domain.usecase

import com.cniekirk.traintimes.model.track.req.TrackServiceRequest
import com.cniekirk.traintimes.model.track.res.TrackServiceResponse
import com.cniekirk.traintimes.repo.NreRepository
import javax.inject.Inject

class TrackServiceUseCase @Inject constructor(private val nreRepository: NreRepository)
    :BaseUseCase<TrackServiceResponse, TrackServiceRequest>() {

    override suspend fun run(params: TrackServiceRequest) = nreRepository.trackService(params)

}