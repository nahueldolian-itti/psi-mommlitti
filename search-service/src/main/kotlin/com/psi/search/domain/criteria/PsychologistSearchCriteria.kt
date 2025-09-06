package com.psi.search.domain.criteria

import java.time.LocalDate
import java.time.LocalDateTime

data class PsychologistSearchCriteria(
    val name: String? = null,
    val themes: List<String> = emptyList(), // Theme enum names
    val isActive: Boolean = true,
    val availableFrom: LocalDateTime? = null,
    val availableTo: LocalDateTime? = null,
    val availableOnDate: LocalDate? = null,
    val minRating: Double? = null,
    val minExperience: Int? = null,
    val patientTimezone: String = "America/Argentina/Buenos_Aires",
    val sortBy: SortBy = SortBy.RATING,
    val sortDirection: SortDirection = SortDirection.DESC,
    val page: Int = 0,
    val size: Int = 20
)

enum class SortBy {
    RATING,
    EXPERIENCE,
    NAME,
    AVAILABILITY_COUNT
}

enum class SortDirection {
    ASC,
    DESC
}
