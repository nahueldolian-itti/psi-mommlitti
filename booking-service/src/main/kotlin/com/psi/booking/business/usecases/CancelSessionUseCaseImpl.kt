package com.psi.booking.business.usecases

import com.psi.booking.domain.entities.Session
import com.psi.booking.domain.usecases.CancelSessionUseCase
import com.psi.booking.domain.usecases.GetSessionUseCase
import com.psi.booking.domain.enums.SessionStatus
import com.psi.booking.domain.enums.SlotStatus
import com.psi.booking.business.ports.SessionRepository
import com.psi.booking.business.ports.AvailabilitySlotRepository
import com.psi.booking.business.ports.EventPublisher
import org.springframework.stereotype.Service

@Service
class CancelSessionUseCaseImpl(
    private val sessionRepository: SessionRepository,
    private val availabilitySlotRepository: AvailabilitySlotRepository,
    private val eventPublisher: EventPublisher
) : CancelSessionUseCase {

    override fun execute(sessionId: String): Session {
        val session = sessionRepository.findById(sessionId)
            ?: throw IllegalArgumentException("Session not found")

        if (session.status != SessionStatus.BOOKED) {
            throw IllegalStateException("Session cannot be cancelled in current status: ${session.status}")
        }

        val updatedSession = sessionRepository.update(
            session.copy(status = SessionStatus.CANCELLED)
        )

        // Release the availability slot
        val availabilitySlot = availabilitySlotRepository.findByPsychologistAndTimeRange(
            session.psychologistId,
            session.startTime,
            session.endTime
        )

        availabilitySlot?.let {
            availabilitySlotRepository.update(
                it.copy(
                    status = SlotStatus.AVAILABLE,
                    sessionId = null
                )
            )
        }

        eventPublisher.publishSessionCancelled(updatedSession)

        return updatedSession
    }
}

@Service
class GetSessionUseCaseImpl(
    private val sessionRepository: SessionRepository
) : GetSessionUseCase {

    override fun execute(sessionId: String): Session? {
        return sessionRepository.findById(sessionId)
    }
}
