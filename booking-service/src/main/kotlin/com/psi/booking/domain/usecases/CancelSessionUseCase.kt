package com.psi.booking.domain.usecases

import com.psi.booking.domain.entities.Session

fun interface CancelSessionUseCase {
    fun execute(sessionId: String): Session
}
