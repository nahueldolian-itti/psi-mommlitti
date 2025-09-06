package com.psi.booking.business.ports

import com.psi.booking.domain.entities.Session
import com.psi.booking.domain.entities.AvailabilitySlot
import com.psi.booking.domain.entities.Psychologist
import com.psi.booking.domain.enums.Theme

interface SessionRepository {
    fun save(session: Session): Session
    fun findById(sessionId: String): Session?
    fun findByIdempotencyKey(idempotencyKey: String): Session?
    fun update(session: Session): Session
}

interface AvailabilitySlotRepository {
    fun findByPsychologistAndTimeRange(
        psychologistId: String,
        startTime: java.time.LocalDateTime,
        endTime: java.time.LocalDateTime
    ): AvailabilitySlot?
    fun findByPsychologistAndDateRange(
        psychologistId: String,
        startTime: java.time.LocalDateTime,
        endTime: java.time.LocalDateTime
    ): List<AvailabilitySlot>
    fun save(slot: AvailabilitySlot): AvailabilitySlot
    fun update(slot: AvailabilitySlot): AvailabilitySlot
}

interface PsychologistRepository {
    fun findById(psychologistId: String): Psychologist?
    fun findByTheme(theme: Theme): List<Psychologist>
    fun findAll(): List<Psychologist>
    fun save(psychologist: Psychologist): Psychologist
}

interface EventPublisher {
    fun publishSessionBooked(session: Session)
    fun publishSessionCancelled(session: Session)
    fun publishSessionRescheduled(session: Session)
}
