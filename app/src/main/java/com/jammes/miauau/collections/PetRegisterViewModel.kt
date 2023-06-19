package com.jammes.miauau.collections

import android.graphics.Bitmap
import android.util.Log
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
import java.io.InputStream

class PetRegisterViewModel(private val repository: PetsRepository) : ViewModel() {

    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private val petScope = viewModelScope
    private val imageScope = viewModelScope
    private val urlImage = MutableLiveData("")

    private val uiState = MutableLiveData<UiState>()

    fun stateOnceAndStream(): LiveData<UiState> {
        return uiState
    }

    fun isValid(pet: Pet): Boolean {
        if (pet.name.isBlank()) return false
        if (pet.description.isBlank()) return false
        if (pet.age <= 0) return false
        return true
    }

    private suspend fun sendImageToStore(petItem: PetDomain, imageStream: ByteArrayInputStream) {
        Log.i("ImagemPet", "Chamou sendImageToStore")
        val imageFileName = "${petItem.name}-${System.currentTimeMillis()}.png "
        val imageRef = storageRef.child("imagesPets/$imageFileName")

        Log.i("ImagemPet", "Antes do PutStream")
        val uploadImg = imageRef.putStream(imageStream).await()

        Log.i("ImagemPet", "Antes do If")
        if (uploadImg.task.isSuccessful) {
            Log.i("ImagemPet", "Sucess antes da url")
            imageRef.downloadUrl
                .addOnSuccessListener {
                    urlImage.postValue(it.toString())
                    Log.i("ImagemPet", "Sucess $it")

                }
                .addOnFailureListener {
                    Log.e("ImagemPet", "Erro ao obter URL ${it.message}")
                }

        }
    }

    fun addNewPet(pet: Pet) {
        val idUsr = Firebase.auth.currentUser!!.uid

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
            tutorId = idUsr
        )

        petScope.launch {

            repository.addPet(petComImg)

        }

        imageScope.launch {

//            sendImageToStore(petComImg, pet.image)

        }

//        viewModelScope.launch {
//            Log.i("ImagemPet", "URL da Imagem: $urlImage")
//            repository.addImagePet(petComImg.id!!, urlImage.value.toString())
//        }
    }

    data class UiState(val pet: Pet)

    class Factory(private val repository: PetsRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PetRegisterViewModel(repository) as T
        }
    }
}