package com.jammes.miauau.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.jammes.miauau.core.model.PetDomain
import com.jammes.miauau.core.repository.PetsRepository
import kotlinx.coroutines.launch

class PetRegisterViewModel(private val repository: PetsRepository) : ViewModel() {

    lateinit var storage: FirebaseStorage

    private fun isValid(pet: Pet): Boolean {
        if (pet.name.isBlank()) return false
        if (pet.description.isBlank()) return false
        if (pet.age <= 0) return false
        return true
    }

    fun addNewPet(pet: Pet): Boolean {

        val petValid = isValid(pet)

        if (petValid) {
            storage = Firebase.storage
            val storageRef = storage.reference

            val imageFileName = "${pet.name}-${System.currentTimeMillis()}.jpg "
            val imageRef = storageRef.child("imagesPets/$imageFileName")
            var imageUrl = ""

            val uploadTask = imageRef.putStream(pet.image)
            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener {uri ->
                    imageUrl = uri.toString() // Verificar! Não está capturando a url da imagem para gravar no firestore
                }
            }

            Thread.sleep(5000)

            val pet = PetDomain(
                petType = pet.type,
                name = pet.name,
                description = pet.description,
                age = pet.age,
                ageType = pet.ageType,
                breed = pet.breed,
                sex = pet.sex,
                vaccinated = pet.vaccinated,
                size = pet.size,
                castrated = pet.castrated,
                imageURL = imageUrl
            )

            viewModelScope.launch {
                repository.addPet(pet)
            }
        }
        return petValid
    }

    class Factory(private val repository: PetsRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PetRegisterViewModel(repository) as T
        }
    }
}