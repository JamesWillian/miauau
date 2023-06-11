package com.jammes.miauau.core.repository

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jammes.miauau.core.model.PetDomain
import com.jammes.miauau.core.model.UserDomain
import kotlinx.coroutines.tasks.await

class UsersRepositoryFirestore: UsersRepository {

    private val db = Firebase.firestore

    override suspend fun fetchUserDetail(userId: String): UserDomain {
        val doc = db.collection(COLLECTION).document(userId).get().await()
        val userDetail = doc.toObject(UserDomain::class.java)!!

        if (doc.exists() && doc != null) {
            return userDetail.copy(
                uid = doc.id,
                name = doc.getString("name")!!,
                location = doc.getString("location")!!,
                about = doc.getString("about")!!,
                phone = doc.getString("phone")!!,
                email = doc.getString("email")!!,
                photoUrl = doc.getString("photoUrl")!!)
        } else {
            throw Exception("Usuário não localizado...")
        }
    }

    override suspend fun addUser(user: UserDomain) {
        db.collection(COLLECTION)
            .add(user)
            .addOnSuccessListener {docRef ->
                Log.d("UsersRepoFirestore","Usuário Salvo com Sucesso! ID: ${docRef.id}")
            }
            .addOnFailureListener {ex ->
                Log.w("UsersRepoFirestore","Não foi possível salvar os dados do Usuário!", ex)
            }
    }

    companion object {
        private const val COLLECTION = "users"
    }
}