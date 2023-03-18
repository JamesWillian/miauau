package com.jammes.miauau.core.repository

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jammes.miauau.R
import com.jammes.miauau.collections.*
import com.jammes.miauau.core.model.PetDomain
import kotlinx.coroutines.tasks.await

class PetsRepositoryFirestore: PetsRepository {

    private val db = Firebase.firestore

    override suspend fun fetchPets(): List<PetDomain> {
        val resultList = mutableListOf<PetDomain>()

        val petsList = db.collection(COLLECTION)
            .get()
            .await()

        for (pet in petsList) {
            resultList.add(
                PetDomain(
                    id = pet.id,
                    petType = 1,
                    name = pet.getString("name")!!,
                    description = pet.getString("description")!!,
                    age = pet.getLong("age")!!.toInt(),
                    ageType = 1,
                    breed = pet.getString("breed")!!,
                    sex = pet.getLong("sex")!!.toInt(),
                    vaccinated = pet.getBoolean("vaccinated")!!,
                    size = pet.getLong("size")!!.toInt(),
                    castrated = pet.getBoolean("castrated")!!
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
                petType = PetType.DOG,
                name = doc.getString("name")!!,
                description = doc.getString("description")!!,
                age = doc.getLong("age")!!.toInt(),
                ageType = AgeType.YEARS,
                breed = doc.getString("breed")!!,
                sex = Sex.MALE,
                vaccinated = doc.getString("vaccinated")!!,
                size = Size.MEDIUM,
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