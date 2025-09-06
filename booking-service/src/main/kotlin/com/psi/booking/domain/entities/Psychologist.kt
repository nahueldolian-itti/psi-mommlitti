package com.psi.booking.domain.entities

import com.psi.booking.domain.enums.DayOfWeek
import com.psi.booking.domain.enums.SlotStatus
import com.psi.booking.domain.enums.Theme
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

data class Psychologist(
    val id: String,
    val name: String,
    val email: String,
    val themes: List<Theme>,
    val bio: String? = null,
    val experience: Int = 0, // years of experience
    val rating: Double? = null,
    val reviewCount: Int = 0,
    val weeklySchedule: List<WeeklyTimeSlot> = emptyList(),
    val timezone: ZoneId = ZoneId.of("America/Argentina/Buenos_Aires"),
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class WeeklyTimeSlot(
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val isAvailable: Boolean = true
)
