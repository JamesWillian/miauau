package com.jammes.miauau.collections

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PetsListLifecycleObserver(
    private val viewModel: PetsListViewModel
): DefaultLifecycleObserver {

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)

        //Se não houver usuario autenticado, faz login anonimo
        if (Firebase.auth.currentUser == null) {

            val auth = Firebase.auth

            //Faz login anonimo
            auth.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("signInAnonymous", "Login Anonimo: Sucesso!")
                        viewModel.onResume()
                    } else {
                        Log.d("signInAnonymous", "Login Anonimo: Falhou!")
                    }
                }

        } else {
            viewModel.onResume()
        }
    }

}