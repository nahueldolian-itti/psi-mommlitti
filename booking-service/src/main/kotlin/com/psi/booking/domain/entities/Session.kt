package com.psi.booking.domain.entities

import com.psi.booking.domain.enums.SessionStatus
import java.time.LocalDateTime

data class Session(
    val id: String,
    val psychologistId: String,
    val patientId: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val status: SessionStatus,
    val idempotencyKey: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
