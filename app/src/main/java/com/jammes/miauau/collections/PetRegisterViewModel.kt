package com.jammes.miauau.collections

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.ImageView
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.jammes.miauau.core.model.PetDomain
import com.jammes.miauau.core.repository.PetsRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

class PetRegisterViewModel(private val repository: PetsRepository) : ViewModel() {

    private val storage = Firebase.storage
    private val storageRef = storage.reference

    private val uiState : MutableLiveData<UiState> by lazy {
        MutableLiveData<UiState>()
    }

    fun stateOnceAndStream(): LiveData<UiState> {
        return uiState
    }

    fun isValid(pet: Pet): Boolean {
        if (pet.name.isBlank()) return false
        if (pet.description.isBlank()) return false
        if (pet.age <= 0) return false
        return true
    }

    private fun toInputStream(image: Bitmap): ByteArray {

        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()

    }

    fun addNewPet(pet: Pet) {
        val idUsr = Firebase.auth.currentUser!!.uid
        val img = toInputStream(pet.image)
        val imageFileName = "${pet.name}-${System.currentTimeMillis()}.png "
        val imageRef = storageRef.child("imagesPets/$imageFileName")

        val uploadTask = imageRef.putBytes(img)
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

                val petComImg = PetDomain(
                    id = "${pet.name}-${System.currentTimeMillis()}",
                    petType = pet.type,
                    name = pet.name,
                    description = pet.description,
                    age = pet.age,
                    ageType = pet.ageType,
                    breed = pet.breed,
                    sex = pet.sex,
                    vaccinated = pet.vaccinated,
                    size = pet.size,
                    castrated = pet.castrated,
                    tutorId = idUsr,
                    imageURL = downloadUrl
                )
                repository.addPet(petComImg)
            }
        }

    }

    data class UiState(val pet: Pet)

    class Factory(private val repository: PetsRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PetRegisterViewModel(repository) as T
        }
    }
}