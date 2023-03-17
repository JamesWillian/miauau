package com.jammes.miauau.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jammes.miauau.core.model.PetDomain
import com.jammes.miauau.core.repository.PetsRepository
import kotlinx.coroutines.launch

class PetRegisterViewModel(private val repository: PetsRepository) : ViewModel() {

    fun addNewPet(pet: PetDomain) {
        viewModelScope.launch {
            repository.addPet(pet)
        }
    }

    class Factory(private val repository: PetsRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PetsListViewModel(repository) as T
        }
    }
}