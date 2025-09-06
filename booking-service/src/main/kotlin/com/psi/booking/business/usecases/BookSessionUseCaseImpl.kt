package com.psi.booking.business.usecases

import com.psi.booking.domain.entities.Session
import com.psi.booking.domain.usecases.BookSessionUseCase
import com.psi.booking.domain.enums.SessionStatus
import com.psi.booking.domain.enums.SlotStatus
import com.psi.booking.business.ports.SessionRepository
import com.psi.booking.business.ports.AvailabilitySlotRepository
import com.psi.booking.business.ports.EventPublisher
import org.springframework.stereotype.Service

@Service
class BookSessionUseCaseImpl(
    private val sessionRepository: SessionRepository,
    private val availabilitySlotRepository: AvailabilitySlotRepository,
    private val eventPublisher: EventPublisher
) : BookSessionUseCase {

    override fun execute(session: Session): Session {
        // Check idempotency
        sessionRepository.findByIdempotencyKey(session.idempotencyKey)?.let {
            return it
        }

        // Find and validate availability slot
        val availabilitySlot = availabilitySlotRepository.findByPsychologistAndTimeRange(
            session.psychologistId,
            session.startTime,
            session.endTime
        ) ?: throw IllegalArgumentException("No availability slot found for the specified time")

        if (availabilitySlot.status != SlotStatus.AVAILABLE) {
            throw IllegalStateException("Time slot is not available")
        }

        // Save session
        val savedSession = sessionRepository.save(session.copy(status = SessionStatus.BOOKED))

        // Update availability slot
        availabilitySlotRepository.update(
            availabilitySlot.copy(
                status = SlotStatus.BOOKED,
                sessionId = savedSession.id
            )
        )

        // Publish event
        eventPublisher.publishSessionBooked(savedSession)

        return savedSession
    }
}
