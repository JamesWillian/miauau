package com.jammes.miauau.collections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jammes.miauau.core.PetsRepository

class PetsListViewModel(private val repository: PetsRepository): ViewModel() {

    private val uiState: MutableLiveData<UiState> by lazy {
        MutableLiveData<UiState>(UiState(petItemList = repository.fetchPets()))
    }

    fun stateOnceAndStream(): LiveData<UiState> {
        return uiState
    }

    data class UiState(val petItemList: List<PetItem>)

    class Factory(private val repository: PetsRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PetsListViewModel(repository) as T
        }
    }
}