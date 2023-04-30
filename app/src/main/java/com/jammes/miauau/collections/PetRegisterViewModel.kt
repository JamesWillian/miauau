package com.jammes.miauau.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jammes.miauau.core.model.PetDomain
import com.jammes.miauau.core.repository.PetsRepository
import kotlinx.coroutines.launch

class PetRegisterViewModel(private val repository: PetsRepository) : ViewModel() {

    private fun isValid(pet: Pet): Boolean {
        if (pet.name.isBlank()) return false
        if (pet.description.isBlank()) return false
        if (pet.age <= 0) return false
        return true
    }

    fun addNewPet(pet: Pet): Boolean {
        val petValid = isValid(pet)

        if (petValid) {
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
                castrated = pet.castrated
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