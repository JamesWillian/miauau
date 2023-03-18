package com.jammes.miauau.collections

import androidx.lifecycle.*
import com.jammes.miauau.R
import com.jammes.miauau.core.model.PetDomain
import com.jammes.miauau.core.repository.PetsRepository
import kotlinx.coroutines.launch

class PetsListViewModel(private val repository: PetsRepository): ViewModel() {

    private val uiState: MutableLiveData<UiState> by lazy {
        MutableLiveData<UiState>(UiState(petItemList = emptyList()))
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
        uiState.postValue(
            UiState(repository.fetchPets()
                .map {pet ->
                    PetItem(
                        id = pet.id,
                        name = pet.name,
                        petType =
                            when (pet.petType) {
                                1 -> PetType.DOG
                                2 -> PetType.CAT
                                else -> PetType.DOG
                            },
                        description = pet.description,
                        age = pet.age,
                        ageType =
                            when (pet.ageType) {
                                1 -> AgeType.YEARS
                                2 -> AgeType.MONTHS
                                3 -> AgeType.WEEKS
                                else -> AgeType.YEARS
                            },
                        breed = pet.breed,
                        sex =
                            when (pet.sex) {
                                1 -> Sex.MALE
                                2 -> Sex.FEMALE
                                else -> Sex.MALE
                            },
                        vaccinated = if (pet.vaccinated) "Vacinado" else "",
                        size =
                            when (pet.size) {
                                1 -> Size.SMALL
                                2 -> Size.MEDIUM
                                3 -> Size.LARGE
                                else -> Size.MEDIUM
                            },
                        castrated = if (pet.castrated) "Castrado" else "",
                        img = R.drawable.img_dog1,
                    )
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
            img = R.drawable.img_dog1,
        )
    }

    private suspend fun getPetById(petId: String) {
        val petDetail = repository.fetchPetDetail(petId)
        val pet: PetItem = petDetail.toPetItem()
        detailUiState.postValue(PetDetailUiState(pet))
    }

    fun stateOnceAndStream(): LiveData<UiState> {
        return uiState
    }

    data class UiState(val petItemList: List<PetItem>)

    data class PetDetailUiState(val petDetail: PetItem)

    class Factory(private val repository: PetsRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PetsListViewModel(repository) as T
        }
    }
}