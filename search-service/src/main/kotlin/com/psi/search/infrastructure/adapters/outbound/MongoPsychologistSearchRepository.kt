package com.psi.search.infrastructure.adapters.outbound

import com.psi.search.domain.entities.PsychologistSearchModel
import com.psi.search.domain.criteria.PsychologistSearchCriteria
import com.psi.search.business.ports.PsychologistSearchRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class MongoPsychologistSearchRepository(
    private val mongoTemplate: MongoTemplate
) : PsychologistSearchRepository {

    override fun search(criteria: PsychologistSearchCriteria): List<PsychologistSearchModel> {
        val query = Query()

        // Add search criteria
        if (!criteria.name.isNullOrBlank()) {
            query.addCriteria(Criteria.where("name").regex(criteria.name, "i"))
        }

        if (criteria.themes.isNotEmpty()) {
            query.addCriteria(Criteria.where("themes").`in`(criteria.themes))
        }

        query.addCriteria(Criteria.where("isActive").`is`(criteria.isActive))

        criteria.minRating?.let { minRating ->
            query.addCriteria(Criteria.where("rating").gte(minRating))
        }

        // Add availability filter if specified
        if (criteria.availableFrom != null && criteria.availableTo != null) {
            query.addCriteria(
                Criteria.where("availability").elemMatch(
                    Criteria.where("isAvailable").`is`(true)
                        .and("startTime").gte(criteria.availableFrom)
                        .and("endTime").lte(criteria.availableTo)
                )
            )
        }

        // Add pagination
        query.skip((criteria.page * criteria.size).toLong())
        query.limit(criteria.size)

        return mongoTemplate.find(query, PsychologistSearchModel::class.java)
    }

    override fun findById(psychologistId: String): PsychologistSearchModel? {
        return mongoTemplate.findById(psychologistId, PsychologistSearchModel::class.java)
    }

    override fun save(psychologist: PsychologistSearchModel): PsychologistSearchModel {
        return mongoTemplate.save(psychologist)
    }

    override fun updateAvailability(
        psychologistId: String,
        slotId: String,
        isAvailable: Boolean,
        sessionId: String?
    ): Boolean {
        val query = Query(Criteria.where("id").`is`(psychologistId).and("availability.id").`is`(slotId))
        val update = Update()
            .set("availability.$.isAvailable", isAvailable)
            .set("availability.$.sessionId", sessionId)
            .set("updatedAt", java.time.LocalDateTime.now())

        val result = mongoTemplate.updateFirst(query, update, PsychologistSearchModel::class.java)
        return result.modifiedCount > 0
    }
}
