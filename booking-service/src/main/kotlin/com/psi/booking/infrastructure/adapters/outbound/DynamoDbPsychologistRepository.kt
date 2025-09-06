package com.psi.booking.infrastructure.adapters.outbound

import com.psi.booking.domain.entities.Psychologist
import com.psi.booking.domain.entities.WeeklyTimeSlot
import com.psi.booking.domain.enums.Theme
import com.psi.booking.domain.enums.DayOfWeek
import com.psi.booking.business.ports.PsychologistRepository
import org.springframework.stereotype.Repository
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Repository
class DynamoDbPsychologistRepository(
    private val dynamoDbClient: DynamoDbClient
) : PsychologistRepository {

    companion object {
        private const val TABLE_NAME = "psi-sessions"
        private const val GSI_THEME = "theme-index"
        private val DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        private val TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME
    }

    override fun findById(psychologistId: String): Psychologist? {
        val key = mapOf(
            "PK" to AttributeValue.builder().s("PSYCHOLOGIST#$psychologistId").build(),
            "SK" to AttributeValue.builder().s("METADATA").build()
        )

        val request = GetItemRequest.builder()
            .tableName(TABLE_NAME)
            .key(key)
            .build()

        val response = dynamoDbClient.getItem(request)
        return if (response.hasItem()) {
            mapItemToPsychologist(response.item())
        } else null
    }

    override fun findByTheme(theme: Theme): List<Psychologist> {
        val request = QueryRequest.builder()
            .tableName(TABLE_NAME)
            .indexName(GSI_THEME)
            .keyConditionExpression("theme = :theme")
            .expressionAttributeValues(mapOf(
                ":theme" to AttributeValue.builder().s(theme.name).build()
            ))
            .build()

        val response = dynamoDbClient.query(request)
        return response.items().map { mapItemToPsychologist(it) }
    }

    override fun findAll(): List<Psychologist> {
        val request = ScanRequest.builder()
            .tableName(TABLE_NAME)
            .filterExpression("begins_with(PK, :pk) AND SK = :sk")
            .expressionAttributeValues(mapOf(
                ":pk" to AttributeValue.builder().s("PSYCHOLOGIST#").build(),
                ":sk" to AttributeValue.builder().s("METADATA").build()
            ))
            .build()

        val response = dynamoDbClient.scan(request)
        return response.items().map { mapItemToPsychologist(it) }
    }

    override fun save(psychologist: Psychologist): Psychologist {
        val mainItem = createMainItem(psychologist)
        val themeItems = createThemeItems(psychologist)

        val transactItems = mutableListOf<TransactWriteItem>()

        // Add main psychologist item
        transactItems.add(
            TransactWriteItem.builder()
                .put(Put.builder().tableName(TABLE_NAME).item(mainItem).build())
                .build()
        )

        // Add theme index items
        themeItems.forEach { themeItem ->
            transactItems.add(
                TransactWriteItem.builder()
                    .put(Put.builder().tableName(TABLE_NAME).item(themeItem).build())
                    .build()
            )
        }

        val request = TransactWriteItemsRequest.builder()
            .transactItems(transactItems)
            .build()

        dynamoDbClient.transactWriteItems(request)
        return psychologist
    }

    private fun createMainItem(psychologist: Psychologist): Map<String, AttributeValue> {
        return mapOf(
            "PK" to AttributeValue.builder().s("PSYCHOLOGIST#${psychologist.id}").build(),
            "SK" to AttributeValue.builder().s("METADATA").build(),
            "id" to AttributeValue.builder().s(psychologist.id).build(),
            "name" to AttributeValue.builder().s(psychologist.name).build(),
            "email" to AttributeValue.builder().s(psychologist.email).build(),
            "themes" to AttributeValue.builder().ss(psychologist.themes.map { it.name }).build(),
            "bio" to AttributeValue.builder().s(psychologist.bio ?: "").build(),
            "experience" to AttributeValue.builder().n(psychologist.experience.toString()).build(),
            "rating" to AttributeValue.builder().n((psychologist.rating ?: 0.0).toString()).build(),
            "reviewCount" to AttributeValue.builder().n(psychologist.reviewCount.toString()).build(),
            "timezone" to AttributeValue.builder().s(psychologist.timezone.id).build(),
            "isActive" to AttributeValue.builder().bool(psychologist.isActive).build(),
            "weeklySchedule" to AttributeValue.builder().s(serializeWeeklySchedule(psychologist.weeklySchedule)).build(),
            "createdAt" to AttributeValue.builder().s(psychologist.createdAt.format(DATE_FORMATTER)).build()
        )
    }

    private fun createThemeItems(psychologist: Psychologist): List<Map<String, AttributeValue>> {
        return psychologist.themes.map { theme ->
            mapOf(
                "PK" to AttributeValue.builder().s("THEME#${theme.name}").build(),
                "SK" to AttributeValue.builder().s("PSYCHOLOGIST#${psychologist.id}").build(),
                "theme" to AttributeValue.builder().s(theme.name).build(),
                "psychologistId" to AttributeValue.builder().s(psychologist.id).build(),
                "psychologistName" to AttributeValue.builder().s(psychologist.name).build(),
                "rating" to AttributeValue.builder().n((psychologist.rating ?: 0.0).toString()).build(),
                "experience" to AttributeValue.builder().n(psychologist.experience.toString()).build()
            )
        }
    }

    private fun serializeWeeklySchedule(schedule: List<WeeklyTimeSlot>): String {
        return schedule.joinToString("|") { slot ->
            "${slot.dayOfWeek.name}:${slot.startTime.format(TIME_FORMATTER)}-${slot.endTime.format(TIME_FORMATTER)}:${slot.isAvailable}"
        }
    }

    private fun deserializeWeeklySchedule(scheduleString: String): List<WeeklyTimeSlot> {
        if (scheduleString.isEmpty()) return emptyList()

        return scheduleString.split("|").map { slotString ->
            val parts = slotString.split(":")
            val dayOfWeek = DayOfWeek.valueOf(parts[0])
            val times = parts[1].split("-")
            val startTime = LocalTime.parse(times[0], TIME_FORMATTER)
            val endTime = LocalTime.parse(times[1], TIME_FORMATTER)
            val isAvailable = parts[2].toBoolean()

            WeeklyTimeSlot(dayOfWeek, startTime, endTime, isAvailable)
        }
    }

    private fun mapItemToPsychologist(item: Map<String, AttributeValue>): Psychologist {
        return Psychologist(
            id = item["id"]?.s() ?: "",
            name = item["name"]?.s() ?: "",
            email = item["email"]?.s() ?: "",
            themes = item["themes"]?.ss()?.map { Theme.valueOf(it) } ?: emptyList(),
            bio = item["bio"]?.s()?.takeIf { it.isNotEmpty() },
            experience = item["experience"]?.n()?.toInt() ?: 0,
            rating = item["rating"]?.n()?.toDouble()?.takeIf { it > 0.0 },
            reviewCount = item["reviewCount"]?.n()?.toInt() ?: 0,
            weeklySchedule = deserializeWeeklySchedule(item["weeklySchedule"]?.s() ?: ""),
            timezone = ZoneId.of(item["timezone"]?.s() ?: "America/Argentina/Buenos_Aires"),
            isActive = item["isActive"]?.bool() ?: true,
            createdAt = LocalDateTime.parse(item["createdAt"]?.s(), DATE_FORMATTER)
        )
    }
}
