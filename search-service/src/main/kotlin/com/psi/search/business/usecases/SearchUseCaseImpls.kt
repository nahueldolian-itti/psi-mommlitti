package com.psi.search.business.usecases

import com.psi.search.domain.entities.PsychologistSearchModel
import com.psi.search.domain.entities.AvailabilitySlotSearchModel
import com.psi.search.domain.usecases.SearchPsychologistUseCase
import com.psi.search.domain.usecases.GetPsychologistAvailabilityUseCase
import com.psi.search.domain.usecases.GetPsychologistByIdUseCase
import com.psi.search.domain.criteria.PsychologistSearchCriteria
import com.psi.search.business.ports.PsychologistSearchRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class SearchPsychologistUseCaseImpl(
    private val psychologistSearchRepository: PsychologistSearchRepository
) : SearchPsychologistUseCase {

    override fun execute(criteria: PsychologistSearchCriteria): List<PsychologistSearchModel> {
        return psychologistSearchRepository.search(criteria)
    }
}

@Service
class GetPsychologistAvailabilityUseCaseImpl(
    private val psychologistSearchRepository: PsychologistSearchRepository
) : GetPsychologistAvailabilityUseCase {

    override fun execute(psychologistId: String, from: LocalDateTime, to: LocalDateTime): List<AvailabilitySlotSearchModel> {
        val psychologist = psychologistSearchRepository.findById(psychologistId)
            ?: return emptyList()

        return psychologist.availability.filter { slot ->
            slot.isAvailable &&
            !slot.startTime.isBefore(from) &&
            !slot.endTime.isAfter(to)
        }
    }
}

@Service
class GetPsychologistByIdUseCaseImpl(
    private val psychologistSearchRepository: PsychologistSearchRepository
) : GetPsychologistByIdUseCase {

    override fun execute(psychologistId: String): PsychologistSearchModel? {
        return psychologistSearchRepository.findById(psychologistId)
    }
}
