package com.jammes.miauau.core.repository

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.jammes.miauau.core.model.PetDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PetsRepositoryFirestore : PetsRepository {

    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val storageRef = storage.reference

    override suspend fun fetchPets(petType: Int): List<PetDomain> {
        val resultList = mutableListOf<PetDomain>()

        val petsList = db.collection(COLLECTION)
            .whereNotEqualTo("tutorId", Firebase.auth.currentUser!!.uid)
            .whereEqualTo("petType", petType)
            .get()
            .await()

        for (pet in petsList) {
            resultList.add(
                PetDomain(
                    id = pet.id,
                    petType = pet.getLong("petType")!!.toInt(),
                    name = pet.getString("name")!!,
                    description = pet.getString("description")!!,
                    age = pet.getLong("age")!!.toInt(),
                    ageType = pet.getLong("ageType")!!.toInt(),
                    breed = pet.getString("breed")!!,
                    sex = pet.getLong("sex")!!.toInt(),
                    vaccinated = pet.getBoolean("vaccinated")!!,
                    size = pet.getLong("size")!!.toInt(),
                    castrated = pet.getBoolean("castrated")!!,
                    imageURL = pet.getString("imageURL")
                )
            )
        }

        return resultList
    }

    override suspend fun fetchMyPets(): List<PetDomain> {
        val resultList = mutableListOf<PetDomain>()

        val petsList = db.collection(COLLECTION)
            .whereEqualTo("tutorId", Firebase.auth.currentUser!!.uid)
            .get()
            .await()

        for (pet in petsList) {
            resultList.add(
                PetDomain(
                    id = pet.id,
                    name = pet.getString("name")!!,
                    imageURL = pet.getString("imageURL")
                )
            )
        }

        return resultList
    }

    override suspend fun fetchPetDetail(petId: String): PetDomain {

        val doc = db.collection(COLLECTION).document(petId).get().await()
        val petDetail = doc.toObject(PetDomain::class.java)!!

        if (doc.exists() && doc != null) {
            return petDetail.copy(
                id = doc.id,
                petType = doc.getLong("petType")!!.toInt(),
                name = doc.getString("name")!!,
                description = doc.getString("description")!!,
                age = doc.getLong("age")!!.toInt(),
                ageType = doc.getLong("ageType")!!.toInt(),
                breed = doc.getString("breed")!!,
                sex = doc.getLong("sex")!!.toInt(),
                vaccinated = doc.getBoolean("vaccinated")!!,
                size = doc.getLong("size")!!.toInt(),
                castrated = doc.getBoolean("castrated")!!,
                imageURL = doc.getString("imageURL"),
                tutorId = doc.getString("tutorId")
            )
        } else {
            throw Exception("Desculpe! Não consegui encontrar o seu pet... :(")
        }

    }

    override fun addPet(petItem: PetDomain) {
        db.collection(COLLECTION)
            .add(petItem)
            .addOnSuccessListener {docRef ->
                Log.d("PetsRepositoryFirestore", "Pet Salvo com Sucesso! ID: ${docRef.id}")
            }
            .addOnFailureListener { ex ->
                Log.w("PetsRepositoryFirestore", "Não foi possível salvar o Pet!", ex)
            }
    }

    override suspend fun updatePet(petItem: PetDomain) {
        db.collection(COLLECTION)
            .document(petItem.id!!)
            .set(petItem)
            .addOnSuccessListener {
                Log.d("PetsRepositoryFirestore", "Pet Atualziado com Sucesso! ID: ${petItem.id}")
            }
            .addOnFailureListener {ex ->
                Log.w("PetsRepositoryFirestore", "Não foi possível atualziar o Pet!", ex)
            }
    }

    override suspend fun addPetImage(petItem: PetDomain, img: ByteArray) {
        val imageFileName = "${petItem.name}-${System.currentTimeMillis()}.png "
        val imageRef = storageRef.child("imagesPets/$imageFileName")

        val uploadTask = img.let { imageRef.putBytes(it) }
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result.toString()
                val newPet = petItem.copy(imageURL = downloadUrl)

                addPet(newPet)

            }
        }
    }

    companion object {
        private const val COLLECTION = "pets"
    }
}