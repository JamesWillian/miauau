package com.jammes.miauau.core.model

import android.graphics.Bitmap

data class User (
    val uid: String? = null,
    val name: String = "",
    val location: String = "",
    val about: String = "",
    val phone: String = "",
    val email: String = "",
    val showContact: Boolean = true,
    val photoUrl: String? = null
)