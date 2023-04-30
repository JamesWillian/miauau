package com.jammes.miauau.collections

sealed class PetType {
    object DOG: PetType()
    object CAT: PetType()
}

sealed class AgeType {
    object YEARS: AgeType()
    object MONTHS: AgeType()
    object WEEKS: AgeType()
}

sealed class Sex {
    object MALE: Sex()
    object FEMALE: Sex()
}

sealed class Size {
    object SMALL: Size()
    object MEDIUM: Size()
    object LARGE: Size()
}
