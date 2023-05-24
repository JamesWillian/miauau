package com.jammes.miauau.core.model

data class PetDomain(
    val id: String? = null,
    val petType: Int = 1,
    val name: String = "",
    val description: String = "",
    val age: Int = 0,
    val ageType: Int = 1,
    val breed: String = "",
    val sex: Int = 1,
    val vaccinated: Boolean = false,
    val size: Int = 1,
    val castrated: Boolean = false,
    val imageURL: String = "",
    val tutorId: Int? = null
)
