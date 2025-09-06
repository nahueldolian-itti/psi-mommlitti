package com.psi.booking.domain.entities

import java.time.ZoneId

data class Patient(
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val timezone: ZoneId = ZoneId.systemDefault(),
    val createdAt: java.time.LocalDateTime = java.time.LocalDateTime.now()
)
