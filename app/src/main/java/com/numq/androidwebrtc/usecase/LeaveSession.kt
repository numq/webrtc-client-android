package com.numq.androidwebrtc.usecase

import com.numq.androidwebrtc.domain.repository.SessionRepository
import com.numq.androidwebrtc.platform.usecase.UseCase
import javax.inject.Inject

class LeaveSession
@Inject constructor(private val repository: SessionRepository) : UseCase<Unit, Unit>() {

    override fun execute(args: Unit) = repository.leaveSession()
}