package com.jammes.miauau.core.model

data class UserDomain(
    val uid: String,
    val name: String,
    val location: String,
    val about: String,
    val phone: String,
    val email: String,
    val photoUrl: String
)
