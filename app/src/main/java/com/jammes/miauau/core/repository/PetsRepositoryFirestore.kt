package com.jammes.miauau.core.repository

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jammes.miauau.R
import com.jammes.miauau.collections.PetItem
import com.jammes.miauau.core.model.PetDomain
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

    override suspend fun fetchPetDetail(petId: String): PetItem {

        val doc = db.collection(COLLECTION).document(petId).get().await()
        val petDetail = doc.toObject(PetItem::class.java)!!

        if (doc.exists() && doc != null) {
            return petDetail.copy(id = doc.id,
                name = doc.getString("name")!!,
                description = doc.getString("description")!!,
                age = doc.getString("age")!!,
                breed = doc.getString("breed")!!,
                sex = doc.getString("sex")!!,
                vaccinated = doc.getString("vaccinated")!!,
                size = doc.getString("size")!!,
                castrated = doc.getString("castrated")!!,
                img = R.drawable.img_dog1)
        } else {
            throw Exception("Desculpe! Não consegui encontrar o seu pet... :(")
        }

    }

    override suspend fun addPet(petItem: PetDomain) {
        db.collection(COLLECTION)
            .add(petItem)
            .addOnSuccessListener {docRef ->
                Log.d("PetsRepositoryFirestore","Pet Salvo com Sucesso! ID: ${docRef.id}")
            }
            .addOnFailureListener {ex ->
                Log.w("PetsRepositoryFirestore","Não foi possível salvar o Pet!", ex)
            }
    }

    companion object {
        private const val COLLECTION = "pets"
    }
}