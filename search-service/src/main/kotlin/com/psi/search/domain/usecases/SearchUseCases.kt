package com.psi.search.domain.usecases

import com.psi.search.domain.entities.PsychologistSearchModel
import com.psi.search.domain.entities.AvailabilitySlotSearchModel
import com.psi.search.domain.criteria.PsychologistSearchCriteria
import java.time.LocalDateTime

fun interface SearchPsychologistUseCase {
    fun execute(criteria: PsychologistSearchCriteria): List<PsychologistSearchModel>
}

fun interface GetPsychologistAvailabilityUseCase {
    fun execute(psychologistId: String, from: LocalDateTime, to: LocalDateTime): List<AvailabilitySlotSearchModel>
}

fun interface GetPsychologistByIdUseCase {
    fun execute(psychologistId: String): PsychologistSearchModel?
}
