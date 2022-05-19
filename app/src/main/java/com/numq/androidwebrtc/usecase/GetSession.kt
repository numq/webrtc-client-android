package com.numq.androidwebrtc.usecase

import com.numq.androidwebrtc.domain.entity.Session
import com.numq.androidwebrtc.domain.repository.SessionRepository
import com.numq.androidwebrtc.platform.usecase.UseCase
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetSession
@Inject constructor(private val repository: SessionRepository) :
    UseCase<Unit, StateFlow<Session?>>() {

    override fun execute(args: Unit) = repository.session
}