package com.jammes.miauau.core.repository

import com.google.firebase.inject.Deferred
import com.jammes.miauau.collections.PetItem
import com.jammes.miauau.core.model.PetDomain
import kotlinx.coroutines.coroutineScope

interface PetsRepository {

    suspend fun fetchPets():List<PetDomain>
    suspend fun fetchOwnPets():List<PetDomain>
    suspend fun fetchPetDetail(petId: String): PetDomain
    fun addPet(petItem: PetDomain)
    fun updatePet(petItem: PetDomain)

}