package com.psi.booking.domain.enums

enum class Theme(val displayName: String) {
    ANXIETY("Ansiedad"),
    DEPRESSION("Depresión"),
    PHOBIAS("Fobias"),
    RELATIONSHIPS("Relaciones Personales"),
    SELF_ESTEEM("Autoestima"),
    STRESS("Estrés"),
    FAMILY_THERAPY("Terapia Familiar"),
    COUPLE_THERAPY("Terapia de Pareja"),
    GRIEF("Duelo"),
    TRAUMA("Trauma"),
    EATING_DISORDERS("Trastornos Alimentarios"),
    SLEEP_DISORDERS("Trastornos del Sueño"),
    ADDICTION("Adicciones"),
    CAREER_COUNSELING("Orientación Vocacional"),
    CHILD_THERAPY("Terapia Infantil"),
    ADOLESCENT_THERAPY("Terapia de Adolescentes")
}

enum class DayOfWeek(val displayName: String) {
    MONDAY("Lunes"),
    TUESDAY("Martes"),
    WEDNESDAY("Miércoles"),
    THURSDAY("Jueves"),
    FRIDAY("Viernes"),
    SATURDAY("Sábado"),
    SUNDAY("Domingo")
}
