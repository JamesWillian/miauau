package com.jammes.miauau.collections

import androidx.lifecycle.*
import com.jammes.miauau.core.model.PetItem
import com.jammes.miauau.core.model.User
import com.jammes.miauau.core.model.UserDomain
import com.jammes.miauau.core.repository.PetsRepository
import com.jammes.miauau.core.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UsersRepository,
    private val petRepository: PetsRepository
): ViewModel() {

    private val userUiState = MutableLiveData<UserUiState>()

    private val petListUiState: MutableLiveData<MyPetListUiState> by lazy {
        MutableLiveData<MyPetListUiState>(
            MyPetListUiState(
                petItemList = emptyList()
            )
        )
    }

    fun onResume() {
        viewModelScope.launch {
            refreshPetsList()
        }
    }

    private suspend fun refreshPetsList() {
        petListUiState.postValue(
            MyPetListUiState(petRepository.fetchMyPets()
                .map { pet ->
                    PetItem(
                        id = pet.id,
                        name = pet.name,
                        imageURL = pet.imageURL
                    )
                })
        )
    }

    fun deletePet(petId: String) {
        viewModelScope.launch {
            petRepository.deletePet(petId)
            refreshPetsList()
        }
    }

    fun adoptPet (petId: String) {
        viewModelScope.launch {
            petRepository.petAdopted(petId)
        }
    }

    fun statePetsOnce(): LiveData<MyPetListUiState> {
        return petListUiState
    }

    fun refreshUser(userId: String) {
        viewModelScope.launch {
            getUserById(userId)
        }
    }

    fun saveUserProfile(user: User) {
        viewModelScope.launch {
            setUser(user)
        }
    }

    private suspend fun getUserById(userId: String) {
        val userDomain = userRepository.fetchUserDetail(userId)
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
        userUiState.postValue(UserUiState(user))
    }

    private suspend fun setUser(user: User) {
        val newUser = user.let {
            UserDomain(
                uid = it.uid,
                name = it.name,
                location = it.location,
                about = it.about,
                phone = it.phone,
                email = it.email,
                showContact = it.showContact,
                photoUrl = it.photoUrl
            )
        }
        userRepository.addUser(newUser)
    }

    fun stateOnceAndStream(): LiveData<UserUiState> {
        return userUiState
    }

    data class UserUiState(val user: User)

    data class MyPetListUiState(val petItemList: List<PetItem>)

}