package com.psi.booking.domain.entities

import com.psi.booking.domain.enums.SlotStatus
import java.time.LocalDateTime

data class AvailabilitySlot(
    val id: String,
    val psychologistId: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val status: SlotStatus = SlotStatus.AVAILABLE,
    val sessionId: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
