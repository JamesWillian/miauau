package com.jammes.miauau.core.repository

import com.jammes.miauau.core.model.PetDomain

interface PetsRepository {

    suspend fun fetchPets():List<PetDomain>
    suspend fun fetchOwnPets():List<PetDomain>
    suspend fun fetchPetDetail(petId: String): PetDomain
    fun addPet(petItem: PetDomain)
    fun updatePet(petItem: PetDomain)

}