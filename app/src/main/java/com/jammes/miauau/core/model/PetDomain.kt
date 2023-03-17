package com.jammes.miauau.core.model

data class PetDomain(
    val id: String,
    val petType: Int,
    val name: String,
    val description: String,
    val age: Int,
    val ageType: Int,
    val breed: String,
    val sex: Int,
    val vaccinated: Boolean,
    val size: Int,
    val castrated: Boolean
)
