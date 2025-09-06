package com.psi.booking.domain.usecases

import com.psi.booking.domain.entities.Session

fun interface GetSessionUseCase {
    fun execute(sessionId: String): Session?
}

fun interface RescheduleSessionUseCase {
    fun execute(sessionId: String, newStartTime: java.time.LocalDateTime, newEndTime: java.time.LocalDateTime): Session
}
