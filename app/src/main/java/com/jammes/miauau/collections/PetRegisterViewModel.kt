package com.jammes.miauau.collections

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.jammes.miauau.core.model.PetDomain
import com.jammes.miauau.core.repository.PetsRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayInputStream
import java.io.InputStream

class PetRegisterViewModel(private val repository: PetsRepository) : ViewModel() {

    fun isValid(pet: Pet): Boolean {
        if (pet.name.isBlank()) return false
        if (pet.description.isBlank()) return false
        if (pet.age <= 0) return false
        return true
    }

    private suspend fun sendImageToStore(petName: String, imageStream: ByteArrayInputStream): String? {
        val storage = Firebase.storage
        val storageRef = storage.reference

        val imageFileName = "${petName}-${System.currentTimeMillis()}.jpg "
        val imageRef = storageRef.child("imagesPets/$imageFileName")

        return try {
            imageRef.putStream(imageStream).await()
            imageRef.downloadUrl.await()?.toString()
        } catch (e: Exception) {
            null
        }
    }

    fun addNewPet(pet: Pet, petImage: ByteArrayInputStream) {

        viewModelScope.launch {
            val imageUrl = sendImageToStore(pet.name, petImage)

            val petComImg = PetDomain(
                    petType = pet.type,
                    name = pet.name,
                    description = pet.description,
                    age = pet.age,
                    ageType = pet.ageType,
                    breed = pet.breed,
                    sex = pet.sex,
                    vaccinated = pet.vaccinated,
                    size = pet.size,
                    castrated = pet.castrated
                )

            if (imageUrl != null) {
                petComImg.imageURL = imageUrl
                repository.addPet(petComImg)
            }
        }
    }

    class Factory(private val repository: PetsRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PetRegisterViewModel(repository) as T
        }
    }
}