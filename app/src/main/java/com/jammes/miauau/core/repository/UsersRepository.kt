package com.jammes.miauau.core.repository

import com.jammes.miauau.core.model.UserDomain

interface UsersRepository {

    suspend fun fetchUserDetail(userId: String): UserDomain

    suspend fun addUser(user: UserDomain)

    suspend fun phoneIsEmpty(): Boolean
}