package com.psi.search.infrastructure.adapters.inbound

import com.psi.search.domain.usecases.SearchPsychologistUseCase
import com.psi.search.domain.usecases.GetPsychologistAvailabilityUseCase
import com.psi.search.domain.usecases.GetPsychologistByIdUseCase
import com.psi.search.domain.criteria.PsychologistSearchCriteria
import com.psi.search.domain.entities.PsychologistSearchModel
import com.psi.search.domain.entities.AvailabilitySlotSearchModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/psychologists")
class PsychologistController(
    private val searchPsychologistUseCase: SearchPsychologistUseCase,
    private val getPsychologistAvailabilityUseCase: GetPsychologistAvailabilityUseCase,
    private val getPsychologistByIdUseCase: GetPsychologistByIdUseCase
) {

    @GetMapping
    fun searchPsychologists(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) themes: List<String>?,
        @RequestParam(required = false) availableFrom: LocalDateTime?,
        @RequestParam(required = false) availableTo: LocalDateTime?,
        @RequestParam(required = false) minRating: Double?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<PsychologistResponse>> {
        val criteria = PsychologistSearchCriteria(
            name = name,
            themes = themes ?: emptyList(),
            availableFrom = availableFrom,
            availableTo = availableTo,
            minRating = minRating,
            page = page,
            size = size
        )

        val psychologists = searchPsychologistUseCase.execute(criteria)
        return ResponseEntity.ok(psychologists.map { PsychologistResponse.from(it) })
    }

    @GetMapping("/{psychologistId}")
    fun getPsychologist(@PathVariable psychologistId: String): ResponseEntity<PsychologistResponse> {
        val psychologist = getPsychologistByIdUseCase.execute(psychologistId)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(PsychologistResponse.from(psychologist))
    }

    @GetMapping("/{psychologistId}/availability")
    fun getAvailability(
        @PathVariable psychologistId: String,
        @RequestParam from: LocalDateTime,
        @RequestParam to: LocalDateTime
    ): ResponseEntity<List<AvailabilitySlotResponse>> {
        val availability = getPsychologistAvailabilityUseCase.execute(psychologistId, from, to)
        return ResponseEntity.ok(availability.map { AvailabilitySlotResponse.from(it) })
    }
}

data class PsychologistResponse(
    val id: String,
    val name: String,
    val email: String,
    val themes: List<String>,
    val themeDisplayNames: List<String>,
    val isActive: Boolean,
    val rating: Double?,
    val reviewCount: Int,
    val availability: List<AvailabilitySlotResponse>
) {
    companion object {
        fun from(psychologist: PsychologistSearchModel): PsychologistResponse {
            return PsychologistResponse(
                id = psychologist.id,
                name = psychologist.name,
                email = psychologist.email,
                themes = psychologist.themes,
                themeDisplayNames = psychologist.themeDisplayNames,
                isActive = psychologist.isActive,
                rating = psychologist.rating,
                reviewCount = psychologist.reviewCount,
                availability = psychologist.availability.map { AvailabilitySlotResponse.from(it) }
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
        fun from(slot: AvailabilitySlotSearchModel): AvailabilitySlotResponse {
            return AvailabilitySlotResponse(
                id = slot.id,
                startTime = slot.startTime,
                endTime = slot.endTime,
                isAvailable = slot.isAvailable
            )
        }
    }
}
