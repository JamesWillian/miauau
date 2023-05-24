package com.jammes.miauau.collections

import java.io.ByteArrayInputStream

data class Pet(
    val name: String,
    val description: String,
    val type: Int,
    val age: Int,
    val ageType: Int,
    val breed: String,
    val sex: Int,
    val vaccinated: Boolean,
    val size: Int,
    val castrated: Boolean,
    val image: ByteArrayInputStream
)
