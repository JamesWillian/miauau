package com.jammes.miauau.collections

data class PetItem(
    val id: String? = null,
    val name: String = "",
    val petType: PetType = PetType.DOG,
    val description: String = "",
    val age: Int = 0,
    val ageType: AgeType = AgeType.YEARS,
    val breed: String = "",
    val sex: Sex = Sex.MALE,
    val vaccinated: String = "",
    val size: Size = Size.MEDIUM,
    val castrated: String = "",
    val imageURL: String = ""
)