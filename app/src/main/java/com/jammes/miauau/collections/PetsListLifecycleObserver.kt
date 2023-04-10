package com.jammes.miauau.collections

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jammes.miauau.LoginAuth

class PetsListLifecycleObserver(
    private val viewModel: PetsListViewModel
): DefaultLifecycleObserver {

    private val login = LoginAuth()

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)

        if (Firebase.auth.currentUser != null) {
            viewModel.onResume()
        } else {
            //Fazer login anonimo
            login.signInAnonymous()
        }
    }
}