package com.psi.booking.infrastructure.adapters.outbound

import com.psi.booking.domain.entities.AvailabilitySlot
import com.psi.booking.business.ports.AvailabilitySlotRepository
import org.springframework.stereotype.Repository
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Repository
class DynamoDbAvailabilitySlotRepository(
    private val dynamoDbClient: DynamoDbClient
) : AvailabilitySlotRepository {

    companion object {
        private const val TABLE_NAME = "psi-sessions"
        private val DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }

    override fun findByPsychologistAndTimeRange(
        psychologistId: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): AvailabilitySlot? {
        val request = QueryRequest.builder()
            .tableName(TABLE_NAME)
            .keyConditionExpression("PK = :pk AND begins_with(SK, :sk)")
            .filterExpression("startTime = :startTime AND endTime = :endTime")
            .expressionAttributeValues(mapOf(
                ":pk" to AttributeValue.builder().s("PSYCHOLOGIST#$psychologistId").build(),
                ":sk" to AttributeValue.builder().s("SLOT#").build(),
                ":startTime" to AttributeValue.builder().s(startTime.format(DATE_FORMATTER)).build(),
                ":endTime" to AttributeValue.builder().s(endTime.format(DATE_FORMATTER)).build()
            ))
            .build()

        val response = dynamoDbClient.query(request)
        return if (response.hasItems() && response.items().isNotEmpty()) {
            mapItemToAvailabilitySlot(response.items()[0])
        } else null
    }

    override fun findByPsychologistAndDateRange(
        psychologistId: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<AvailabilitySlot> {
        val request = QueryRequest.builder()
            .tableName(TABLE_NAME)
            .keyConditionExpression("PK = :pk AND begins_with(SK, :sk)")
            .filterExpression("startTime BETWEEN :startTime AND :endTime")
            .expressionAttributeValues(mapOf(
                ":pk" to AttributeValue.builder().s("PSYCHOLOGIST#$psychologistId").build(),
                ":sk" to AttributeValue.builder().s("SLOT#").build(),
                ":startTime" to AttributeValue.builder().s(startTime.format(DATE_FORMATTER)).build(),
                ":endTime" to AttributeValue.builder().s(endTime.format(DATE_FORMATTER)).build()
            ))
            .build()

        val response = dynamoDbClient.query(request)
        return response.items().map { mapItemToAvailabilitySlot(it) }
    }

    override fun save(slot: AvailabilitySlot): AvailabilitySlot {
        val item = mapOf(
            "PK" to AttributeValue.builder().s("PSYCHOLOGIST#${slot.psychologistId}").build(),
            "SK" to AttributeValue.builder().s("SLOT#${slot.id}").build(),
            "id" to AttributeValue.builder().s(slot.id).build(),
            "psychologistId" to AttributeValue.builder().s(slot.psychologistId).build(),
            "startTime" to AttributeValue.builder().s(slot.startTime.format(DATE_FORMATTER)).build(),
            "endTime" to AttributeValue.builder().s(slot.endTime.format(DATE_FORMATTER)).build(),
            "status" to AttributeValue.builder().s(slot.status.name).build(),
            "sessionId" to AttributeValue.builder().s(slot.sessionId ?: "").build(),
            "createdAt" to AttributeValue.builder().s(slot.createdAt.format(DATE_FORMATTER)).build(),
            "updatedAt" to AttributeValue.builder().s(slot.updatedAt.format(DATE_FORMATTER)).build()
        )

        val request = PutItemRequest.builder()
            .tableName(TABLE_NAME)
            .item(item)
            .build()

        dynamoDbClient.putItem(request)
        return slot
    }

    override fun update(slot: AvailabilitySlot): AvailabilitySlot {
        return save(slot.copy(updatedAt = LocalDateTime.now()))
    }

    private fun mapItemToAvailabilitySlot(item: Map<String, AttributeValue>): AvailabilitySlot {
        return AvailabilitySlot(
            id = item["id"]?.s() ?: "",
            psychologistId = item["psychologistId"]?.s() ?: "",
            startTime = LocalDateTime.parse(item["startTime"]?.s(), DATE_FORMATTER),
            endTime = LocalDateTime.parse(item["endTime"]?.s(), DATE_FORMATTER),
            status = com.psi.booking.domain.enums.SlotStatus.valueOf(item["status"]?.s() ?: "AVAILABLE"),
            sessionId = item["sessionId"]?.s()?.takeIf { it.isNotEmpty() },
            createdAt = LocalDateTime.parse(item["createdAt"]?.s(), DATE_FORMATTER),
            updatedAt = LocalDateTime.parse(item["updatedAt"]?.s(), DATE_FORMATTER)
        )
    }
}
