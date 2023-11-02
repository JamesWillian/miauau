package com.jammes.miauau.collections

import androidx.lifecycle.*
import com.jammes.miauau.core.model.*
import com.jammes.miauau.core.repository.PetsRepository
import com.jammes.miauau.core.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetsListViewModel @Inject constructor(
    private val petsRepository: PetsRepository,
    private val userRepository: UsersRepository
): ViewModel() {

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

    private val tutorUiState = MutableLiveData<TutorUiState>()

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
            PetListUiState(petsRepository.fetchPets(petFilter.value ?: 1)
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
            FavoritePetsUiState(petsRepository.fetchFavoritePets())
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

    fun fetchTutorById(tutorId: String) {
        viewModelScope.launch {
            getTutorById(tutorId)
        }
    }

    private suspend fun setFavoritePet(petId: String) {
        petsRepository.addFavoritePet(petId)
    }

    private suspend fun deleteFavoritePet(petId: String) {
        petsRepository.removeFavoritePet(petId)
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
        petsRepository.isPetFavorite(petId) {isFavorite ->
            petFavorite = isFavorite
        }

        val petDetail = petsRepository.fetchPetDetail(petId)
        val pet: PetItem = petDetail.toPetItem()
        detailUiState.postValue(PetDetailUiState(pet))
    }

    private suspend fun getTutorById(tutorId: String) {
        val userDomain = userRepository.fetchUserDetail(tutorId)
        val user = userDomain.let {
            User(
                uid = it.uid,
                name = it.name,
                location = it.location,
                about = it.about,
                phone = it.phone,
                email = it.email,
                photoUrl = it.photoUrl,
                showContact = it.showContact
            )
        }
        tutorUiState.postValue(TutorUiState(user))
    }

    fun stateOnceAndStream(): LiveData<PetListUiState> {
        return petListUiState
    }

    fun stateFavoritePets(): LiveData<FavoritePetsUiState> {
        return petFavoriteListUiState
    }

    fun stateTutorOnceAndStream(): LiveData<TutorUiState> {
        return tutorUiState
    }

    data class PetListUiState(val petItemList: List<PetItem>)

    data class PetDetailUiState(val petDetail: PetItem)

    data class FavoritePetsUiState(val petFavoriteList: List<FavoritePet>)

    data class TutorUiState(val tutor: User)

}