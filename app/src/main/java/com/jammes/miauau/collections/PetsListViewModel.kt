package com.jammes.miauau.collections

import androidx.lifecycle.*
import com.jammes.miauau.core.model.*
import com.jammes.miauau.core.repository.PetsRepository
import kotlinx.coroutines.launch

class PetsListViewModel(private val repository: PetsRepository): ViewModel() {

    private val petListUiState: MutableLiveData<PetListUiState> by lazy {
        MutableLiveData<PetListUiState>(PetListUiState(petItemList = emptyList()))
    }

    val detailUiState = MutableLiveData<PetDetailUiState>()

    fun onResume() {
        viewModelScope.launch {
            refreshPetsList()
        }
    }

    fun onPetItemClicked(petId: String) {
        viewModelScope.launch {
            getPetById(petId)
        }
    }

    private suspend fun refreshPetsList() {
        petListUiState.postValue(
            PetListUiState(repository.fetchPets()
                .map {pet ->
                    pet.toPetItem()
                })
        )
    }

    private fun PetDomain.toPetItem(): PetItem {
        return PetItem(
            id,
            name,
            petType =
            when (petType) {
                1 -> PetType.DOG
                2 -> PetType.CAT
                else -> PetType.DOG
            },
            description,
            age,
            ageType =
            when (ageType) {
                1 -> AgeType.YEARS
                2 -> AgeType.MONTHS
                3 -> AgeType.WEEKS
                else -> AgeType.YEARS
            },
            breed,
            sex =
            when (sex) {
                1 -> Sex.MALE
                2 -> Sex.FEMALE
                else -> Sex.MALE
            },
            vaccinated = if (vaccinated) "Vacinado" else "",
            size =
            when (size) {
                1 -> Size.SMALL
                2 -> Size.MEDIUM
                3 -> Size.LARGE
                else -> Size.MEDIUM
            },
            castrated = if (castrated) "Castrado" else "",
            imageURL = imageURL,
            tutorId = tutorId ?: ""
        )
    }

    private suspend fun getPetById(petId: String) {
        val petDetail = repository.fetchPetDetail(petId)
        val pet: PetItem = petDetail.toPetItem()
        detailUiState.postValue(PetDetailUiState(pet))
    }

    fun stateOnceAndStream(): LiveData<PetListUiState> {
        return petListUiState
    }

    data class PetListUiState(val petItemList: List<PetItem>)

    data class PetDetailUiState(val petDetail: PetItem)

    class Factory(private val repository: PetsRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PetsListViewModel(repository) as T
        }
    }
}