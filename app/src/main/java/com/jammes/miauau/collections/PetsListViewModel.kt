package com.jammes.miauau.collections

import androidx.lifecycle.*
import com.jammes.miauau.core.model.*
import com.jammes.miauau.core.repository.PetsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetsListViewModel @Inject constructor(
    private val repository: PetsRepository
): ViewModel() {

    init {
        //teste
    }

    var petFavorite = false

    private val petFilter: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(1)
    }

    private val petListUiState: MutableLiveData<PetListUiState> by lazy {
        MutableLiveData<PetListUiState>(PetListUiState(petItemList = emptyList()))
    }

    private val petFavoriteListUiState: MutableLiveData<FavoritePetsUiState> by lazy {
        MutableLiveData<FavoritePetsUiState>(FavoritePetsUiState(petFavoriteList = emptyList()))
    }

    val detailUiState = MutableLiveData<PetDetailUiState>()

    fun filterPet(petTypeFilter: Int) {
        petFilter.value = petTypeFilter
        fetchPets()
    }

    fun fetchPets() {
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
            PetListUiState(repository.fetchPets(petFilter.value ?: 1)
                .map {pet ->
                    pet.toPetItem()
                })
        )
    }

    fun listFavoritePets() {
        viewModelScope.launch {
            favoritePets()
        }
    }

    private suspend fun favoritePets() {
        petFavoriteListUiState.postValue(
            FavoritePetsUiState(repository.fetchFavoritePets())
        )
    }

    fun addFavoritePet(petId: String) {
        viewModelScope.launch {
            setFavoritePet(petId)
        }
    }
    fun removeFavoritePet(petId: String) {
        viewModelScope.launch {
            deleteFavoritePet(petId)
        }
    }

    private suspend fun setFavoritePet(petId: String) {
        repository.addFavoritePet(petId)
    }

    private suspend fun deleteFavoritePet(petId: String) {
        repository.removeFavoritePet(petId)
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
            tutorId = tutorId ?: "",
            favorite = petFavorite

        )
    }

    private suspend fun getPetById(petId: String) {
        repository.isPetFavorite(petId) {isFavorite ->
            petFavorite = isFavorite
        }

        val petDetail = repository.fetchPetDetail(petId)
        val pet: PetItem = petDetail.toPetItem()
        detailUiState.postValue(PetDetailUiState(pet))
    }

    fun stateOnceAndStream(): LiveData<PetListUiState> {
        return petListUiState
    }

    fun stateFavoritePets(): LiveData<FavoritePetsUiState> {
        return petFavoriteListUiState
    }

    data class PetListUiState(val petItemList: List<PetItem>)

    data class PetDetailUiState(val petDetail: PetItem)

    data class FavoritePetsUiState(val petFavoriteList: List<FavoritePet>)

}