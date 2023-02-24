package com.jammes.miauau.core.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jammes.miauau.R
import com.jammes.miauau.collections.PetItem
import kotlinx.coroutines.tasks.await

class PetsRepositoryFirestore: PetsRepository {

    private val db = Firebase.firestore

    override suspend fun fetchPets(): List<PetItem> {
        val resultList = mutableListOf<PetItem>()

        val petsList = db.collection(COLLECTION)
            .get()
            .await()

        for (pet in petsList) {
            resultList.add(
                PetItem(
                    id = pet.id,
                    name = pet.getString("name")!!,
                    description = pet.getString("description")!!,
                    age = pet.getString("age")!!,
                    breed = pet.getString("breed")!!,
                    sex = pet.getString("sex")!!,
                    vaccinated = pet.getString("vaccinated")!!,
                    size = pet.getString("size")!!,
                    castrated = pet.getString("castrated")!!,
                    img = R.drawable.img_dog1
                )
            )
        }

        return resultList
    }

    override suspend fun fetchPetDetail(petId: Int): PetItem {
        TODO("Not yet implemented")
    }

    companion object {
        private const val COLLECTION = "pets"
    }
}