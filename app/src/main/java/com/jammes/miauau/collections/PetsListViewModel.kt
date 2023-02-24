package com.jammes.miauau.collections

import androidx.lifecycle.*
import com.jammes.miauau.core.repository.PetsRepository
import kotlinx.coroutines.launch

class PetsListViewModel(private val repository: PetsRepository): ViewModel() {

    private val uiState: MutableLiveData<UiState> by lazy {
        MutableLiveData<UiState>(UiState(petItemList = emptyList()))
    }

    fun onResume() {
        viewModelScope.launch {
            refreshPetsList()
        }
    }

    private suspend fun refreshPetsList() {
        uiState.postValue(UiState(repository.fetchPets()))
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