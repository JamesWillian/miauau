package com.jammes.miauau.core.repository

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.jammes.miauau.core.model.UserDomain
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UsersRepositoryFirestore @Inject constructor(
    private val db: FirebaseFirestore
) : UsersRepository {

    override suspend fun fetchUserDetail(userId: String): UserDomain {
        val doc = db.collection(COLLECTION).document(userId).get().await()
        val userDetail = doc.toObject(UserDomain::class.java)!!

        if (doc.exists() && doc != null) {
            return userDetail.copy(
                uid = doc.id,
                name = doc.getString("name") ?: "Sem Nome",
                location = doc.getString("location") ?: "Sem Localização",
                about = doc.getString("about") ?: "Sem Informações",
                phone = doc.getString("phone") ?: "Sem Telefone",
                email = doc.getString("email") ?: "Sem Email",
                photoUrl = doc.getString("photoUrl") ?: "")
        } else {
            throw Exception("Usuário não localizado...")
        }
    }

    override suspend fun addUser(user: UserDomain) {
        db.collection(COLLECTION)
            .document(user.uid!!)
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("UsersRepoFirestore","Usuário Salvo com Sucesso! ID: ${user.uid}")
            }
            .addOnFailureListener {ex ->
                Log.w("UsersRepoFirestore","Não foi possível salvar os dados do Usuário!", ex)
            }
    }

    override suspend fun phoneIsEmpty(): Boolean {
        val userId = Firebase.auth.currentUser!!.uid
        val doc = db.collection(COLLECTION).document(userId).get().await()
        return doc.getString("phone").isNullOrEmpty()
    }

    companion object {
        private const val COLLECTION = "users"
    }
}