package com.jammes.miauau.core.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.jammes.miauau.core.model.FavoritePet
import com.jammes.miauau.core.model.PetDomain
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PetsRepositoryFirestore @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : PetsRepository {

    private val storageRef = storage.reference

    override suspend fun addFavoritePet(petId: String) {

        val userId = Firebase.auth.currentUser!!.uid

        val favoritePetsRef = FirebaseFirestore.getInstance()
            .collection("users").document(userId)
            .collection("favoritePets").document(petId)

        favoritePetsRef.set(
            mapOf(
                "petId" to petId,
                "favorite" to true
            )
        )
    }

    override suspend fun removeFavoritePet(petId: String) {

        val userId = Firebase.auth.currentUser!!.uid

        val favoritePetsRef = FirebaseFirestore.getInstance()
            .collection("users").document(userId)
            .collection("favoritePets").document(petId)

        favoritePetsRef.update(
            "favorite",
            false
        )
    }

    override fun isPetFavorite(petId: String, callback: (Boolean) -> Unit) {

        val userId = Firebase.auth.currentUser!!.uid

        val favoritePetsRef = FirebaseFirestore.getInstance()
            .collection("users").document(userId)
            .collection("favoritePets").document(petId)

        favoritePetsRef.get()
            .addOnSuccessListener { documentSnapshot ->
                // Verifique se o documento existe
                if (documentSnapshot.exists()) {
                    val isFavorite = documentSnapshot.getBoolean("favorite") ?: false
                    callback(isFavorite)
                } else {
                    // O documento não existe, o pet não é favorito
                    callback(false)
                }
            }
            .addOnFailureListener { exception ->
                // Trate os erros aqui, por exemplo, log ou exiba uma mensagem de erro
                callback(false)
            }

    }

    private fun DocumentSnapshot.toFavoritePet(): FavoritePet {
        return FavoritePet(
            id = this.id,
            name = this.getString("name") ?: "",
            imageURL = this.getString("imageURL"),
            tutorName = this.getString("tutorName") ?: ""
        )
    }

    override suspend fun fetchFavoritePets(): List<FavoritePet> {
        val resultList = mutableListOf<FavoritePet>()
        val userId = Firebase.auth.currentUser!!.uid

        // Referência para a coleção "favoritePets" do usuário atual
        val favoritePetsRef = FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("favoritePets")

        // Consulta para obter os documentos na coleção "favoritePets"
        val pets = favoritePetsRef
            .whereEqualTo("favorite",true)
            .get()
            .await()

        val favoritePetIds = mutableListOf<String>()

        for (document in pets) {
            val petId = document.getString("petId")
            if (petId != null) {
                favoritePetIds.add(petId)
            }
        }

        for (pet in favoritePetIds) {
            val doc = db.collection(COLLECTION).document(pet).get().await()
            val petFav = doc.toFavoritePet()

            resultList.add(petFav)
        }

        return resultList
    }

    override suspend fun fetchPets(petType: Int): List<PetDomain> {
        val resultList = mutableListOf<PetDomain>()

        val petsList = db.collection(COLLECTION)
            .whereNotEqualTo("tutorId", Firebase.auth.currentUser!!.uid)
            .whereEqualTo("petType", petType)
            .whereEqualTo("adoptedAt", null)
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

    override suspend fun updatePet(petItem: PetDomain) {
        db.collection(COLLECTION)
            .document(petItem.id!!)
            .set(petItem)
            .addOnSuccessListener {
                Log.d("PetsRepositoryFirestore", "Pet Atualziado com Sucesso! ID: ${petItem.id}")
            }
            .addOnFailureListener { ex ->
                Log.w("PetsRepositoryFirestore", "Não foi possível atualziar o Pet!", ex)
            }
    }

    override suspend fun deletePet(petId: String) {
        db.collection(COLLECTION)
            .document(petId)
            .delete()
            .addOnSuccessListener {
                Log.d("PetsRepositoryFirestore", "Pet Excluído com Sucesso! ID: ${petId}")
            }
            .addOnFailureListener { ex ->
                Log.w("PetsRepositoryFirestore", "Não foi possível excluir o Pet!", ex)
            }
    }

    override suspend fun addPetWithImage(petItem: PetDomain, img: ByteArray) {
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

    private fun addPet(petItem: PetDomain) {
        db.collection(COLLECTION)
            .add(petItem)
            .addOnSuccessListener { docRef ->
                Log.d("PetsRepositoryFirestore", "Pet Salvo com Sucesso! ID: ${docRef.id}")
            }
            .addOnFailureListener { ex ->
                Log.w("PetsRepositoryFirestore", "Não foi possível salvar o Pet!", ex)
            }
    }

    override suspend fun petAdopted(petId: String) {
        db.collection(COLLECTION)
            .document(petId)
            .update("adoptedAt", Timestamp.now())
            .addOnSuccessListener {
                Log.d("PetsRepositoryFirestore", "Pet Adotado com Sucesso! ID: ${petId}")
            }
            .addOnFailureListener { ex ->
                Log.w("PetsRepositoryFirestore", "Não foi possível adotar o Pet!", ex)
            }
    }

    companion object {
        private const val COLLECTION = "pets"
    }
}