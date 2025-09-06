package com.psi.booking.domain.usecases

import com.psi.booking.domain.entities.Session

fun interface BookSessionUseCase {
    fun execute(session: Session): Session
}
