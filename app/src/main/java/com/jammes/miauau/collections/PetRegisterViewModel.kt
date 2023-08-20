package com.jammes.miauau.collections

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.jammes.miauau.core.model.*
import com.jammes.miauau.core.repository.PetsRepository
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream

class PetRegisterViewModel(private val repository: PetsRepository) : ViewModel() {

    private val storage = Firebase.storage
    private val storageRef = storage.reference

    private val uiState : MutableLiveData<UiState> by lazy {
        MutableLiveData<UiState>(
            UiState(
                PetItem(
                    id = null,
                    name = "",
                    petType = PetType.DOG,
                    description = "",
                    age = null,
                    ageType = AgeType.YEARS,
                    breed = "",
                    sex = Sex.MALE,
                    vaccinated = "",
                    size = Size.MEDIUM,
                    castrated = "",
                    imageURL = null,
                    tutorId = ""
                )
            )
        )
    }

    fun fetchPet(petId: String) {
        viewModelScope.launch {
            getPetById(petId)
        }
    }

    fun stateOnceAndStream(): LiveData<UiState> {
        return uiState
    }

    fun updateUiState(newPet: PetItem) {
        uiState.value?.let {currentUiState ->
            uiState.value = currentUiState.copy(
                PetItem(
                    id = currentUiState.pet.id,
                    name = newPet.name,
                    petType = newPet.petType,
                    description = newPet.description,
                    age = newPet.age,
                    ageType = newPet.ageType,
                    breed = newPet.breed,
                    sex = newPet.sex,
                    vaccinated = newPet.vaccinated,
                    size = newPet.size,
                    castrated = newPet.castrated,
                    imageURL = currentUiState.pet.imageURL,
                    imageBitmap = newPet.imageBitmap,
                    tutorId = newPet.tutorId
                )
            )
        }

    }

    fun petIsValid(): Boolean {
        val pet = uiState.value?.pet ?: return false

        if (pet.name.isBlank()) return false
//        if (pet.imageBitmap == null) return false
        if (pet.description.isBlank()) return false
        if ((pet.age ?: 0) <= 0) return false
        return true
    }

    private fun toInputStream(image: Bitmap): ByteArray {

        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 80, baos)
        return baos.toByteArray()

    }

    fun addPet() {
        viewModelScope.launch {

            if (uiState.value?.pet!!.id.isNullOrEmpty()) {

                createPet()

            } else {

                val pet = uiState.value?.pet!!.toPetDomain()
                repository.updatePet(pet)

            }
        }
    }

    private suspend fun createPet() {
        val pet = uiState.value?.pet
        val img = pet?.imageBitmap?.let { toInputStream(it) }
        val pet2 = pet?.toPetDomain()

        if ((pet2 != null) && (img != null)) {
            repository.addPetImage(pet2, img)
        }
    }

    private suspend fun getPetById(petId: String) {
        val petDetail = repository.fetchPetDetail(petId)
        val pet: PetItem = petDetail.toPetItem()
        uiState.postValue(UiState(pet))
    }

    data class UiState(val pet: PetItem)

    private fun PetDomain.toPetItem(): PetItem {
        return PetItem(
            id = id,
            name = name,
            description = description,
            age = age,
            breed = breed,
            vaccinated = if (vaccinated) "Vacinado" else "",
            castrated = if (castrated) "Castrado" else "",
            imageURL = imageURL,
            tutorId = tutorId ?: "",
            petType =
            when (petType) {
                1 -> PetType.DOG
                2 -> PetType.CAT
                else -> {
                    PetType.DOG}
            },
            ageType =
            when (ageType) {
                1 -> AgeType.YEARS
                2 -> AgeType.MONTHS
                3 -> AgeType.WEEKS
                else -> (AgeType.YEARS)
            },
            sex =
            when (sex) {
                1 -> Sex.MALE
                2 -> Sex.FEMALE
                else -> (Sex.MALE)
            },
            size =
            when (size) {
                1 -> Size.SMALL
                2 -> Size.MEDIUM
                3 -> Size.LARGE
                else -> (Size.MEDIUM)
            }
        )
    }

    private fun PetItem.toPetDomain(): PetDomain {
        return PetDomain(
            id = id,
            name = name,
            description = description,
            age = age ?: 0,
            breed = breed,
            vaccinated = (vaccinated != ""),
            castrated = (castrated != ""),
            imageURL = imageURL,
            tutorId = tutorId,
            petType =
            when (petType) {
                PetType.DOG -> 1
                PetType.CAT -> 2
            },
            ageType =
            when (ageType) {
                AgeType.YEARS -> 1
                AgeType.MONTHS -> 2
                AgeType.WEEKS -> 3
            },
            sex =
            when (sex) {
                Sex.MALE -> 1
                Sex.FEMALE -> 2
            },
            size =
            when (size) {
                Size.SMALL -> 1
                Size.MEDIUM -> 2
                Size.LARGE -> 3
            },
            updatedAt = if (!id.isNullOrEmpty()) Timestamp.now() else null
        )
    }

    class Factory(private val repository: PetsRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PetRegisterViewModel(repository) as T
        }
    }
}