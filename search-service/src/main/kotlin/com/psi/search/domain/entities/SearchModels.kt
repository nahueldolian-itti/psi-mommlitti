package com.psi.search.domain.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.index.Indexed
import java.time.LocalDateTime

@Document(collection = "psychologists")
data class PsychologistSearchModel(
    @Id
    val id: String,
    val name: String,
    val email: String,
    @Indexed
    val themes: List<String>, // Theme enum names for indexing
    val themeDisplayNames: List<String>, // Human-readable theme names
    val bio: String? = null,
    val experience: Int = 0,
    val rating: Double? = null,
    val reviewCount: Int = 0,
    val timezone: String = "America/Argentina/Buenos_Aires",
    val isActive: Boolean = true,
    val availability: List<AvailabilitySlotSearchModel> = emptyList(),
    val weeklySchedule: List<WeeklyTimeSlotSearchModel> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class AvailabilitySlotSearchModel(
    val id: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val isAvailable: Boolean = true,
    val sessionId: String? = null
)

data class WeeklyTimeSlotSearchModel(
    val dayOfWeek: String, // DayOfWeek enum name
    val startTime: String, // LocalTime as string (HH:mm)
    val endTime: String,   // LocalTime as string (HH:mm)
    val isAvailable: Boolean = true
)

@Document(collection = "sessions")
data class SessionSearchModel(
    @Id
    val id: String,
    val psychologistId: String,
    val psychologistName: String,
    val patientId: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val status: String,
    val theme: String? = null,
    val confirmationCode: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

@Document(collection = "themes")
data class ThemeSearchModel(
    @Id
    val name: String,
    val displayName: String,
    val description: String? = null,
    val psychologistCount: Int = 0
)
