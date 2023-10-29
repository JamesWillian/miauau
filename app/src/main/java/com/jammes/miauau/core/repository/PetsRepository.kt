package com.jammes.miauau.core.repository

import com.jammes.miauau.core.model.FavoritePet
import com.jammes.miauau.core.model.PetDomain

interface PetsRepository {

    suspend fun fetchPets(petType: Int):List<PetDomain>
    suspend fun fetchMyPets():List<PetDomain>
    suspend fun fetchPetDetail(petId: String): PetDomain
    suspend fun updatePet(petItem: PetDomain)
    suspend fun deletePet(petId: String)
    suspend fun addPetWithImage(petItem: PetDomain, img: ByteArray)
    suspend fun petAdopted(petId: String)
    suspend fun addFavoritePet(petId: String)
    suspend fun removeFavoritePet(petId: String)
    fun isPetFavorite(petId: String, callback: (Boolean) -> Unit)
    suspend fun fetchFavoritePets(): List<FavoritePet>

}