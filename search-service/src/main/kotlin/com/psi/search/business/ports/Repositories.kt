package com.psi.search.business.ports

import com.psi.search.domain.entities.PsychologistSearchModel
import com.psi.search.domain.entities.SessionSearchModel
import com.psi.search.domain.criteria.PsychologistSearchCriteria
import java.time.LocalDateTime

interface PsychologistSearchRepository {
    fun search(criteria: PsychologistSearchCriteria): List<PsychologistSearchModel>
    fun findById(psychologistId: String): PsychologistSearchModel?
    fun save(psychologist: PsychologistSearchModel): PsychologistSearchModel
    fun updateAvailability(psychologistId: String, slotId: String, isAvailable: Boolean, sessionId: String?): Boolean
}

interface SessionSearchRepository {
    fun save(session: SessionSearchModel): SessionSearchModel
    fun findById(sessionId: String): SessionSearchModel?
    fun update(session: SessionSearchModel): SessionSearchModel
}

interface EventConsumer {
    fun processSessionBookedEvent(event: SessionBookedEvent)
    fun processSessionCancelledEvent(event: SessionCancelledEvent)
    fun processSessionRescheduledEvent(event: SessionRescheduledEvent)
}

// Event DTOs
data class SessionBookedEvent(
    val sessionId: String,
    val psychologistId: String,
    val patientId: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val slotId: String
)

data class SessionCancelledEvent(
    val sessionId: String,
    val psychologistId: String,
    val slotId: String
)

data class SessionRescheduledEvent(
    val sessionId: String,
    val psychologistId: String,
    val oldSlotId: String,
    val newSlotId: String,
    val newStartTime: LocalDateTime,
    val newEndTime: LocalDateTime
)
