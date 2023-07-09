package com.jammes.miauau.core.model

import com.google.firebase.Timestamp

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
    var imageURL: String? = null,
    val tutorId: String? = null,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp? = null
)
