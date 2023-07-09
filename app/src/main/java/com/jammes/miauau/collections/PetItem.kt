package com.jammes.miauau.collections

import android.graphics.Bitmap

data class PetItem(
    val id: String? = null,
    val name: String = "",
    val petType: PetType = PetType.DOG,
    val description: String = "",
    val age: Int? = null,
    val ageType: AgeType = AgeType.YEARS,
    val breed: String = "",
    val sex: Sex = Sex.MALE,
    val vaccinated: String = "",
    val size: Size = Size.MEDIUM,
    val castrated: String = "",
    val imageURL: String? = null,
    val imageBitmap: Bitmap? = null,
    val tutorId: String = ""
)