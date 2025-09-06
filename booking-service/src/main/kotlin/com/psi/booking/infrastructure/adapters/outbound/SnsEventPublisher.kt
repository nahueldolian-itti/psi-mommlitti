package com.psi.booking.infrastructure.adapters.outbound

import com.psi.booking.domain.entities.Session
import com.psi.booking.business.ports.EventPublisher
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.PublishRequest

@Component
class SnsEventPublisher(
    private val snsClient: SnsClient,
    private val objectMapper: ObjectMapper
) : EventPublisher {

    companion object {
        private const val SESSION_EVENTS_TOPIC_ARN = "arn:aws:sns:us-east-1:123456789012:session-events"
    }

    override fun publishSessionBooked(session: Session) {
        val event = SessionBookedEvent(
            sessionId = session.id,
            psychologistId = session.psychologistId,
            patientId = session.patientId,
            startTime = session.startTime,
            endTime = session.endTime,
            idempotencyKey = session.idempotencyKey
        )

        publishEvent("SessionBooked", event)
    }

    override fun publishSessionCancelled(session: Session) {
        val event = SessionCancelledEvent(
            sessionId = session.id,
            psychologistId = session.psychologistId,
            patientId = session.patientId,
            startTime = session.startTime,
            endTime = session.endTime
        )

        publishEvent("SessionCancelled", event)
    }

    override fun publishSessionRescheduled(session: Session) {
        val event = SessionRescheduledEvent(
            sessionId = session.id,
            psychologistId = session.psychologistId,
            patientId = session.patientId,
            newStartTime = session.startTime,
            newEndTime = session.endTime
        )

        publishEvent("SessionRescheduled", event)
    }

    private fun publishEvent(eventType: String, event: Any) {
        try {
            val message = objectMapper.writeValueAsString(event)

            val request = PublishRequest.builder()
                .topicArn(SESSION_EVENTS_TOPIC_ARN)
                .message(message)
                .messageAttributes(mapOf(
                    "eventType" to software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue(eventType)
                        .build()
                ))
                .build()

            snsClient.publish(request)
        } catch (e: Exception) {
            // Log error but don't fail the transaction
            println("Failed to publish event: ${e.message}")
        }
    }
}

// Event DTOs for SNS publishing
data class SessionBookedEvent(
    val sessionId: String,
    val psychologistId: String,
    val patientId: String,
    val startTime: java.time.LocalDateTime,
    val endTime: java.time.LocalDateTime,
    val idempotencyKey: String
)

data class SessionCancelledEvent(
    val sessionId: String,
    val psychologistId: String,
    val patientId: String,
    val startTime: java.time.LocalDateTime,
    val endTime: java.time.LocalDateTime
)

data class SessionRescheduledEvent(
    val sessionId: String,
    val psychologistId: String,
    val patientId: String,
    val newStartTime: java.time.LocalDateTime,
    val newEndTime: java.time.LocalDateTime
)
