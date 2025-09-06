package com.psi.booking.business.usecases

import com.psi.booking.domain.entities.Psychologist
import com.psi.booking.domain.entities.AvailabilitySlot
import com.psi.booking.domain.entities.Session
import com.psi.booking.domain.usecases.*
import com.psi.booking.domain.enums.Theme
import com.psi.booking.domain.enums.SlotStatus
import com.psi.booking.business.ports.SessionRepository
import com.psi.booking.business.ports.AvailabilitySlotRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Service
class GetPsychologistsByThemeUseCaseImpl(
    private val psychologistRepository: PsychologistRepository
) : GetPsychologistsByThemeUseCase {

    override fun execute(theme: Theme): List<Psychologist> {
        return psychologistRepository.findByTheme(theme)
            .filter { it.isActive }
            .sortedByDescending { it.rating ?: 0.0 }
    }
}

@Service
class GetWeeklyAvailabilityUseCaseImpl(
    private val psychologistRepository: PsychologistRepository,
    private val availabilitySlotRepository: AvailabilitySlotRepository
) : GetWeeklyAvailabilityUseCase {

    override fun execute(psychologistId: String, weekStartDate: LocalDate, patientTimezone: ZoneId): List<AvailabilitySlot> {
        val psychologist = psychologistRepository.findById(psychologistId)
            ?: return emptyList()

        val weekEndDate = weekStartDate.plusDays(6)

        return availabilitySlotRepository.findByPsychologistAndDateRange(
            psychologistId,
            weekStartDate.atStartOfDay(),
            weekEndDate.atTime(23, 59)
        ).filter { it.status == SlotStatus.AVAILABLE }
         .map { slot ->
            // Convert times to patient's timezone
            val psychologistZone = psychologist.timezone
            val patientZone = patientTimezone

            val psychologistStart = ZonedDateTime.of(slot.startTime, psychologistZone)
            val psychologistEnd = ZonedDateTime.of(slot.endTime, psychologistZone)

            val patientStart = psychologistStart.withZoneSameInstant(patientZone).toLocalDateTime()
            val patientEnd = psychologistEnd.withZoneSameInstant(patientZone).toLocalDateTime()

            slot.copy(
                startTime = patientStart,
                endTime = patientEnd
            )
        }
    }
}

@Service
class GetAllThemesUseCaseImpl : GetAllThemesUseCase {
    override fun execute(): List<Theme> {
        return Theme.values().toList()
    }
}

@Service
class ConfirmSessionUseCaseImpl(
    private val sessionRepository: SessionRepository,
    private val psychologistRepository: PsychologistRepository
) : ConfirmSessionUseCase {

    override fun execute(sessionId: String, patientTimezone: ZoneId): SessionConfirmation {
        val session = sessionRepository.findById(sessionId)
            ?: throw IllegalArgumentException("Session not found")

        val psychologist = psychologistRepository.findById(session.psychologistId)
            ?: throw IllegalArgumentException("Psychologist not found")

        // Convert session times to both timezones
        val psychologistZone = psychologist.timezone
        val sessionInPsychZone = ZonedDateTime.of(session.startTime, psychologistZone)
        val sessionInPatientZone = sessionInPsychZone.withZoneSameInstant(patientTimezone)

        val confirmationCode = generateConfirmationCode()

        return SessionConfirmation(
            sessionId = session.id,
            psychologistName = psychologist.name,
            patientLocalTime = sessionInPatientZone.toLocalDateTime(),
            psychologistLocalTime = session.startTime,
            theme = psychologist.themes.firstOrNull()?.displayName ?: "Consulta General",
            confirmationCode = confirmationCode
        )
    }

    private fun generateConfirmationCode(): String {
        return "PSI-${UUID.randomUUID().toString().substring(0, 8).uppercase()}"
    }
}

// New repository interface needed
interface PsychologistRepository {
    fun findById(psychologistId: String): Psychologist?
    fun findByTheme(theme: Theme): List<Psychologist>
    fun findAll(): List<Psychologist>
    fun save(psychologist: Psychologist): Psychologist
}
