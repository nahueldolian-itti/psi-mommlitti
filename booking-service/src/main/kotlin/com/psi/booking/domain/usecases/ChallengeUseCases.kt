package com.psi.booking.domain.usecases

import com.psi.booking.domain.entities.Psychologist
import com.psi.booking.domain.entities.AvailabilitySlot
import com.psi.booking.domain.enums.Theme
import java.time.LocalDate
import java.time.ZoneId

fun interface GetPsychologistsByThemeUseCase {
    fun execute(theme: Theme): List<Psychologist>
}

fun interface GetWeeklyAvailabilityUseCase {
    fun execute(psychologistId: String, weekStartDate: LocalDate, patientTimezone: ZoneId): List<AvailabilitySlot>
}

fun interface GetAllThemesUseCase {
    fun execute(): List<Theme>
}

fun interface ConfirmSessionUseCase {
    fun execute(sessionId: String, patientTimezone: ZoneId): SessionConfirmation
}

data class SessionConfirmation(
    val sessionId: String,
    val psychologistName: String,
    val patientLocalTime: java.time.LocalDateTime,
    val psychologistLocalTime: java.time.LocalDateTime,
    val theme: String,
    val confirmationCode: String
)
