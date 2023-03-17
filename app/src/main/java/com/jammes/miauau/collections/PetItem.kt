package com.jammes.miauau.collections

data class PetItem(
    val id: String = "",
    val name: String = "",
    val petType: PetType = PetType.DOG,
    val description: String = "",
    val age: String = "",
    val ageType: AgeType = AgeType.YEARS,
    val breed: String = "",
    val sex: Sex = Sex.MALE,
    val vaccinated: String = "",
    val size: Size = Size.MEDIUM,
    val castrated: String = "",
    val img: Int = 0
)

enum class PetType {
    DOG, CAT
}

enum class AgeType {
    YEARS, MONTHS, WEEKS
}

enum class Sex {
    MALE, FEMALE
}

enum class Size {
    SMALL, MEDIUM, LARGE
}