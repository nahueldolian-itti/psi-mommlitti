package com.psi.booking.infrastructure.adapters.inbound

import com.psi.booking.domain.entities.Psychologist
import com.psi.booking.domain.entities.AvailabilitySlot
import com.psi.booking.domain.usecases.*
import com.psi.booking.domain.enums.Theme
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@RestController
@RequestMapping("/api/v1/psychologists")
class PsychologistController(
    private val getPsychologistsByThemeUseCase: GetPsychologistsByThemeUseCase,
    private val getWeeklyAvailabilityUseCase: GetWeeklyAvailabilityUseCase,
    private val getAllThemesUseCase: GetAllThemesUseCase
) {

    @GetMapping("/themes")
    fun getAllThemes(): ResponseEntity<List<ThemeResponse>> {
        val themes = getAllThemesUseCase.execute()
        return ResponseEntity.ok(themes.map { ThemeResponse.from(it) })
    }

    @GetMapping("/by-theme/{theme}")
    fun getPsychologistsByTheme(@PathVariable theme: Theme): ResponseEntity<List<PsychologistResponse>> {
        val psychologists = getPsychologistsByThemeUseCase.execute(theme)
        return ResponseEntity.ok(psychologists.map { PsychologistResponse.from(it) })
    }

    @GetMapping("/{psychologistId}/weekly-availability")
    fun getWeeklyAvailability(
        @PathVariable psychologistId: String,
        @RequestParam weekStartDate: LocalDate,
        @RequestParam(defaultValue = "America/Argentina/Buenos_Aires") patientTimezone: String
    ): ResponseEntity<WeeklyAvailabilityResponse> {
        val timezone = ZoneId.of(patientTimezone)
        val availability = getWeeklyAvailabilityUseCase.execute(psychologistId, weekStartDate, timezone)

        val response = WeeklyAvailabilityResponse(
            psychologistId = psychologistId,
            weekStartDate = weekStartDate,
            patientTimezone = patientTimezone,
            availableSlots = availability.map { AvailabilitySlotResponse.from(it) }
        )

        return ResponseEntity.ok(response)
    }
}

data class ThemeResponse(
    val name: String,
    val displayName: String
) {
    companion object {
        fun from(theme: Theme): ThemeResponse {
            return ThemeResponse(
                name = theme.name,
                displayName = theme.displayName
            )
        }
    }
}

data class PsychologistResponse(
    val id: String,
    val name: String,
    val email: String,
    val themes: List<ThemeResponse>,
    val bio: String?,
    val experience: Int,
    val rating: Double?,
    val reviewCount: Int,
    val timezone: String
) {
    companion object {
        fun from(psychologist: Psychologist): PsychologistResponse {
            return PsychologistResponse(
                id = psychologist.id,
                name = psychologist.name,
                email = psychologist.email,
                themes = psychologist.themes.map { ThemeResponse.from(it) },
                bio = psychologist.bio,
                experience = psychologist.experience,
                rating = psychologist.rating,
                reviewCount = psychologist.reviewCount,
                timezone = psychologist.timezone.id
            )
        }
    }
}

data class AvailabilitySlotResponse(
    val id: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val isAvailable: Boolean
) {
    companion object {
        fun from(slot: AvailabilitySlot): AvailabilitySlotResponse {
            return AvailabilitySlotResponse(
                id = slot.id,
                startTime = slot.startTime,
                endTime = slot.endTime,
                isAvailable = slot.status == com.psi.booking.domain.enums.SlotStatus.AVAILABLE
            )
        }
    }
}

data class WeeklyAvailabilityResponse(
    val psychologistId: String,
    val weekStartDate: LocalDate,
    val patientTimezone: String,
    val availableSlots: List<AvailabilitySlotResponse>
)
