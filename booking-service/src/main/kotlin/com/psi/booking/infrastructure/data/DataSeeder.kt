package com.psi.booking.infrastructure.data

import com.psi.booking.domain.entities.Psychologist
import com.psi.booking.domain.entities.AvailabilitySlot
import com.psi.booking.domain.entities.WeeklyTimeSlot
import com.psi.booking.domain.enums.Theme
import com.psi.booking.domain.enums.DayOfWeek
import com.psi.booking.domain.enums.SlotStatus
import com.psi.booking.business.ports.PsychologistRepository
import com.psi.booking.business.ports.AvailabilitySlotRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

@Component
class DataSeeder(
    private val psychologistRepository: PsychologistRepository,
    private val availabilitySlotRepository: AvailabilitySlotRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        if (psychologistRepository.findAll().isEmpty()) {
            seedPsychologists()
            seedAvailabilitySlots()
            println("✅ Database seeded with sample data")
        } else {
            println("ℹ️ Database already contains data, skipping seed")
        }
    }

    private fun seedPsychologists() {
        val psychologists = listOf(
            createPsychologist(
                "1", "Dra. María Elena Rodríguez", "maria.rodriguez@psimammoliti.com",
                listOf(Theme.ANXIETY, Theme.DEPRESSION, Theme.STRESS),
                "Especialista en trastornos de ansiedad y depresión con 15 años de experiencia. Enfoque cognitivo-conductual.",
                15, 4.8, 127,
                createWeeklySchedule(listOf(
                    DayOfWeek.MONDAY to (9 to 17),
                    DayOfWeek.TUESDAY to (9 to 17),
                    DayOfWeek.WEDNESDAY to (9 to 17),
                    DayOfWeek.THURSDAY to (9 to 17),
                    DayOfWeek.FRIDAY to (9 to 15)
                ))
            ),
            createPsychologist(
                "2", "Lic. Carlos Mendoza", "carlos.mendoza@psimammoliti.com",
                listOf(Theme.RELATIONSHIPS, Theme.COUPLE_THERAPY, Theme.FAMILY_THERAPY),
                "Terapeuta familiar y de pareja. Especializado en conflictos relacionales y comunicación.",
                12, 4.7, 89,
                createWeeklySchedule(listOf(
                    DayOfWeek.MONDAY to (14 to 20),
                    DayOfWeek.TUESDAY to (14 to 20),
                    DayOfWeek.WEDNESDAY to (14 to 20),
                    DayOfWeek.THURSDAY to (14 to 20),
                    DayOfWeek.SATURDAY to (9 to 13)
                ))
            ),
            createPsychologist(
                "3", "Dra. Ana Sofía Morales", "ana.morales@psimammoliti.com",
                listOf(Theme.PHOBIAS, Theme.TRAUMA, Theme.STRESS),
                "Especialista en trastornos de estrés postraumático y fobias. Técnicas de exposición y EMDR.",
                8, 4.9, 156,
                createWeeklySchedule(listOf(
                    DayOfWeek.TUESDAY to (10 to 18),
                    DayOfWeek.WEDNESDAY to (10 to 18),
                    DayOfWeek.THURSDAY to (10 to 18),
                    DayOfWeek.FRIDAY to (10 to 18),
                    DayOfWeek.SATURDAY to (10 to 14)
                ))
            ),
            createPsychologist(
                "4", "Lic. Roberto Silva", "roberto.silva@psimammoliti.com",
                listOf(Theme.SELF_ESTEEM, Theme.CAREER_COUNSELING, Theme.STRESS),
                "Coach psicológico enfocado en desarrollo personal y orientación vocacional.",
                6, 4.6, 73,
                createWeeklySchedule(listOf(
                    DayOfWeek.MONDAY to (8 to 16),
                    DayOfWeek.WEDNESDAY to (8 to 16),
                    DayOfWeek.FRIDAY to (8 to 16),
                    DayOfWeek.SATURDAY to (9 to 13)
                ))
            ),
            createPsychologist(
                "5", "Dra. Laura Vega", "laura.vega@psimammoliti.com",
                listOf(Theme.CHILD_THERAPY, Theme.ADOLESCENT_THERAPY, Theme.FAMILY_THERAPY),
                "Psicóloga infantil y adolescente. Especializada en trastornos del desarrollo y conducta.",
                10, 4.8, 94,
                createWeeklySchedule(listOf(
                    DayOfWeek.MONDAY to (9 to 17),
                    DayOfWeek.TUESDAY to (9 to 17),
                    DayOfWeek.THURSDAY to (9 to 17),
                    DayOfWeek.FRIDAY to (9 to 17),
                    DayOfWeek.SATURDAY to (9 to 12)
                ))
            ),
            createPsychologist(
                "6", "Lic. Diego Herrera", "diego.herrera@psimammoliti.com",
                listOf(Theme.ADDICTION, Theme.DEPRESSION, Theme.ANXIETY),
                "Especialista en adicciones y trastornos duales. Enfoque integral de recuperación.",
                14, 4.7, 112,
                createWeeklySchedule(listOf(
                    DayOfWeek.MONDAY to (11 to 19),
                    DayOfWeek.TUESDAY to (11 to 19),
                    DayOfWeek.WEDNESDAY to (11 to 19),
                    DayOfWeek.THURSDAY to (11 to 19),
                    DayOfWeek.FRIDAY to (11 to 16)
                ))
            ),
            createPsychologist(
                "7", "Dra. Patricia Ramos", "patricia.ramos@psimammoliti.com",
                listOf(Theme.EATING_DISORDERS, Theme.SELF_ESTEEM, Theme.ANXIETY),
                "Especialista en trastornos alimentarios y imagen corporal. Terapia cognitivo-conductual.",
                9, 4.9, 78,
                createWeeklySchedule(listOf(
                    DayOfWeek.TUESDAY to (8 to 16),
                    DayOfWeek.WEDNESDAY to (8 to 16),
                    DayOfWeek.THURSDAY to (8 to 16),
                    DayOfWeek.FRIDAY to (8 to 16)
                ))
            ),
            createPsychologist(
                "8", "Lic. Andrés Torres", "andres.torres@psimammoliti.com",
                listOf(Theme.GRIEF, Theme.DEPRESSION, Theme.TRAUMA),
                "Especialista en duelo y pérdidas. Acompañamiento en procesos de elaboración del duelo.",
                11, 4.8, 65,
                createWeeklySchedule(listOf(
                    DayOfWeek.MONDAY to (15 to 21),
                    DayOfWeek.WEDNESDAY to (15 to 21),
                    DayOfWeek.FRIDAY to (15 to 21),
                    DayOfWeek.SATURDAY to (10 to 14)
                ))
            )
        )

        psychologists.forEach { psychologistRepository.save(it) }
    }

    private fun createPsychologist(
        id: String, name: String, email: String, themes: List<Theme>,
        bio: String, experience: Int, rating: Double, reviewCount: Int,
        weeklySchedule: List<WeeklyTimeSlot>
    ): Psychologist {
        return Psychologist(
            id = id,
            name = name,
            email = email,
            themes = themes,
            bio = bio,
            experience = experience,
            rating = rating,
            reviewCount = reviewCount,
            weeklySchedule = weeklySchedule,
            timezone = ZoneId.of("America/Argentina/Buenos_Aires"),
            isActive = true,
            createdAt = LocalDateTime.now()
        )
    }

    private fun createWeeklySchedule(schedule: List<Pair<DayOfWeek, Pair<Int, Int>>>): List<WeeklyTimeSlot> {
        return schedule.map { (day, hours) ->
            WeeklyTimeSlot(
                dayOfWeek = day,
                startTime = LocalTime.of(hours.first, 0),
                endTime = LocalTime.of(hours.second, 0),
                isAvailable = true
            )
        }
    }

    private fun seedAvailabilitySlots() {
        val today = LocalDate.now()
        val psychologists = psychologistRepository.findAll()

        psychologists.forEach { psychologist ->
            // Generate availability for next 4 weeks
            (0..27).forEach { dayOffset ->
                val date = today.plusDays(dayOffset.toLong())
                val dayOfWeek = when (date.dayOfWeek.value) {
                    1 -> DayOfWeek.MONDAY
                    2 -> DayOfWeek.TUESDAY
                    3 -> DayOfWeek.WEDNESDAY
                    4 -> DayOfWeek.THURSDAY
                    5 -> DayOfWeek.FRIDAY
                    6 -> DayOfWeek.SATURDAY
                    7 -> DayOfWeek.SUNDAY
                    else -> DayOfWeek.MONDAY
                }

                val weeklySlot = psychologist.weeklySchedule.find { it.dayOfWeek == dayOfWeek }
                weeklySlot?.let { slot ->
                    generateSlotsForDay(psychologist.id, date, slot.startTime, slot.endTime)
                }
            }
        }
    }

    private fun generateSlotsForDay(psychologistId: String, date: LocalDate, startTime: LocalTime, endTime: LocalTime) {
        var currentTime = startTime
        while (currentTime.isBefore(endTime)) {
            val slotEnd = currentTime.plusHours(1)
            if (slotEnd.isAfter(endTime)) break

            val availabilitySlot = AvailabilitySlot(
                id = UUID.randomUUID().toString(),
                psychologistId = psychologistId,
                startTime = LocalDateTime.of(date, currentTime),
                endTime = LocalDateTime.of(date, slotEnd),
                status = SlotStatus.AVAILABLE,
                sessionId = null,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            availabilitySlotRepository.save(availabilitySlot)
            currentTime = slotEnd
        }
    }
}
