package com.psi.booking.infrastructure.adapters.inbound

import com.psi.booking.domain.entities.Session
import com.psi.booking.domain.usecases.*
import com.psi.booking.domain.enums.Theme
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@RestController
@RequestMapping("/api/v1/sessions")
class SessionController(
    private val bookSessionUseCase: BookSessionUseCase,
    private val cancelSessionUseCase: CancelSessionUseCase,
    private val getSessionUseCase: GetSessionUseCase,
    private val confirmSessionUseCase: ConfirmSessionUseCase
) {

    @PostMapping
    fun bookSession(
        @RequestHeader("Idempotency-Key") idempotencyKey: String,
        @RequestBody request: BookSessionRequest
    ): ResponseEntity<SessionResponse> {
        val session = Session(
            id = UUID.randomUUID().toString(),
            psychologistId = request.psychologistId,
            patientId = request.patientId,
            startTime = request.startTime,
            endTime = request.endTime,
            status = com.psi.booking.domain.enums.SessionStatus.BOOKED,
            idempotencyKey = idempotencyKey
        )

        val bookedSession = bookSessionUseCase.execute(session)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SessionResponse.from(bookedSession))
    }

    @GetMapping("/{sessionId}")
    fun getSession(@PathVariable sessionId: String): ResponseEntity<SessionResponse> {
        val session = getSessionUseCase.execute(sessionId)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(SessionResponse.from(session))
    }

    @PutMapping("/{sessionId}/cancel")
    fun cancelSession(
        @PathVariable sessionId: String,
        @RequestBody request: CancelSessionRequest? = null
    ): ResponseEntity<SessionResponse> {
        val cancelledSession = cancelSessionUseCase.execute(sessionId)
        return ResponseEntity.ok(SessionResponse.from(cancelledSession))
    }

    @PostMapping("/{sessionId}/confirm")
    fun confirmSession(
        @PathVariable sessionId: String,
        @RequestParam(defaultValue = "America/Argentina/Buenos_Aires") patientTimezone: String
    ): ResponseEntity<SessionConfirmationResponse> {
        val timezone = ZoneId.of(patientTimezone)
        val confirmation = confirmSessionUseCase.execute(sessionId, timezone)
        return ResponseEntity.ok(SessionConfirmationResponse.from(confirmation))
    }
}

data class BookSessionRequest(
    val psychologistId: String,
    val patientId: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)

data class CancelSessionRequest(
    val reason: String?
)

data class SessionResponse(
    val id: String,
    val psychologistId: String,
    val patientId: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val status: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(session: Session): SessionResponse {
            return SessionResponse(
                id = session.id,
                psychologistId = session.psychologistId,
                patientId = session.patientId,
                startTime = session.startTime,
                endTime = session.endTime,
                status = session.status.name,
                createdAt = session.createdAt
            )
        }
    }
}

data class SessionConfirmationResponse(
    val sessionId: String,
    val psychologistName: String,
    val patientLocalTime: LocalDateTime,
    val psychologistLocalTime: LocalDateTime,
    val theme: String,
    val confirmationCode: String
) {
    companion object {
        fun from(confirmation: SessionConfirmation): SessionConfirmationResponse {
            return SessionConfirmationResponse(
                sessionId = confirmation.sessionId,
                psychologistName = confirmation.psychologistName,
                patientLocalTime = confirmation.patientLocalTime,
                psychologistLocalTime = confirmation.psychologistLocalTime,
                theme = confirmation.theme,
                confirmationCode = confirmation.confirmationCode
            )
        }
    }
}
