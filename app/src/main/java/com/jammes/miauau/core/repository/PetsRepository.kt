package com.jammes.miauau.core.repository

import com.jammes.miauau.collections.PetItem
import com.jammes.miauau.core.model.PetDomain

interface PetsRepository {

    suspend fun fetchPets():List<PetDomain>

    suspend fun fetchPetDetail(petId: String): PetDomain

    suspend fun addPet(petItem: PetDomain)
}