package com.psi.booking.infrastructure.adapters.outbound

import com.psi.booking.domain.entities.Session
import com.psi.booking.domain.entities.AvailabilitySlot
import com.psi.booking.business.ports.SessionRepository
import com.psi.booking.business.ports.AvailabilitySlotRepository
import org.springframework.stereotype.Repository
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Repository
class DynamoDbSessionRepository(
    private val dynamoDbClient: DynamoDbClient
) : SessionRepository {

    companion object {
        private const val TABLE_NAME = "psi-sessions"
        private const val GSI_IDEMPOTENCY = "idempotency-index"
        private val DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }

    override fun save(session: Session): Session {
        val item = mapOf(
            "PK" to AttributeValue.builder().s("SESSION#${session.id}").build(),
            "SK" to AttributeValue.builder().s("METADATA").build(),
            "id" to AttributeValue.builder().s(session.id).build(),
            "psychologistId" to AttributeValue.builder().s(session.psychologistId).build(),
            "patientId" to AttributeValue.builder().s(session.patientId).build(),
            "startTime" to AttributeValue.builder().s(session.startTime.format(DATE_FORMATTER)).build(),
            "endTime" to AttributeValue.builder().s(session.endTime.format(DATE_FORMATTER)).build(),
            "status" to AttributeValue.builder().s(session.status.name).build(),
            "idempotencyKey" to AttributeValue.builder().s(session.idempotencyKey).build(),
            "createdAt" to AttributeValue.builder().s(session.createdAt.format(DATE_FORMATTER)).build(),
            "updatedAt" to AttributeValue.builder().s(session.updatedAt.format(DATE_FORMATTER)).build()
        )

        val request = PutItemRequest.builder()
            .tableName(TABLE_NAME)
            .item(item)
            .build()

        dynamoDbClient.putItem(request)
        return session
    }

    override fun findById(sessionId: String): Session? {
        val key = mapOf(
            "PK" to AttributeValue.builder().s("SESSION#$sessionId").build(),
            "SK" to AttributeValue.builder().s("METADATA").build()
        )

        val request = GetItemRequest.builder()
            .tableName(TABLE_NAME)
            .key(key)
            .build()

        val response = dynamoDbClient.getItem(request)
        return if (response.hasItem()) {
            mapItemToSession(response.item())
        } else null
    }

    override fun findByIdempotencyKey(idempotencyKey: String): Session? {
        val request = QueryRequest.builder()
            .tableName(TABLE_NAME)
            .indexName(GSI_IDEMPOTENCY)
            .keyConditionExpression("idempotencyKey = :key")
            .expressionAttributeValues(mapOf(
                ":key" to AttributeValue.builder().s(idempotencyKey).build()
            ))
            .build()

        val response = dynamoDbClient.query(request)
        return if (response.hasItems() && response.items().isNotEmpty()) {
            mapItemToSession(response.items()[0])
        } else null
    }

    override fun update(session: Session): Session {
        return save(session.copy(updatedAt = LocalDateTime.now()))
    }

    private fun mapItemToSession(item: Map<String, AttributeValue>): Session {
        return Session(
            id = item["id"]?.s() ?: "",
            psychologistId = item["psychologistId"]?.s() ?: "",
            patientId = item["patientId"]?.s() ?: "",
            startTime = LocalDateTime.parse(item["startTime"]?.s(), DATE_FORMATTER),
            endTime = LocalDateTime.parse(item["endTime"]?.s(), DATE_FORMATTER),
            status = com.psi.booking.domain.enums.SessionStatus.valueOf(item["status"]?.s() ?: "BOOKED"),
            idempotencyKey = item["idempotencyKey"]?.s() ?: "",
            createdAt = LocalDateTime.parse(item["createdAt"]?.s(), DATE_FORMATTER),
            updatedAt = LocalDateTime.parse(item["updatedAt"]?.s(), DATE_FORMATTER)
        )
    }
}
