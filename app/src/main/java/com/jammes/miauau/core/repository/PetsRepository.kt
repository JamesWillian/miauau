package com.jammes.miauau.core.repository

import com.jammes.miauau.collections.PetItem
import com.jammes.miauau.core.model.PetDomain

interface PetsRepository {

    suspend fun fetchPets():List<PetItem>

    suspend fun fetchPetDetail(petId: String): PetItem

    suspend fun addPet(petItem: PetDomain)
}