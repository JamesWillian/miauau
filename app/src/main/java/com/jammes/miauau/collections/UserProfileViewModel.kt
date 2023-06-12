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

    private suspend fun getUserById(userId: String) {
        val userDomain = repository.fetchUserDetail(userId)
        val user = userDomain.toUser()
        userUiState.postValue(UserUiState(user))
    }

    fun stateOnceAndStream(): LiveData<UserUiState> {
        return userUiState
    }

    private fun UserDomain.toUser(): User {
        return User(
            uid,
            name,
            location,
            about,
            phone,
            email
        )
    }

    data class UserUiState(val user: User)

    class Factory(private val repository: UsersRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UserProfileViewModel(repository) as T
        }
    }
}