package com.jammes.miauau.collections

import androidx.lifecycle.*
import com.jammes.miauau.core.model.UserDomain
import com.jammes.miauau.core.repository.UsersRepository
import kotlinx.coroutines.launch

class UserProfileViewModel(private val repository: UsersRepository): ViewModel() {

    private val userUiState = MutableLiveData<UserUiState>()

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
        val userDomain = repository.fetchUserDetail(userId)
//        val user = userDomain.toUser()
        val user = userDomain.let {
            User(
                uid = it.uid,
                name = it.name,
                location = it.location,
                about = it.about,
                phone = it.phone,
                email = it.email,
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
                showContact = it.showContact
            )
        }
        repository.addUser(newUser)
    }

    fun stateOnceAndStream(): LiveData<UserUiState> {
        return userUiState
    }

//    private fun UserDomain.toUser(): User {
//        return User(
//            uid,
//            name,
//            location,
//            about,
//            phone,
//            email
//        )
//    }

    data class UserUiState(val user: User)

    class Factory(private val repository: UsersRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UserProfileViewModel(repository) as T
        }
    }
}