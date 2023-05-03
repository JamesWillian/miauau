package com.jammes.miauau.collections

import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jammes.miauau.LoginAuthViewModel

class PetsListLifecycleObserver(
    private val viewModel: PetsListViewModel,
    private val loginViewModel: LoginAuthViewModel
): DefaultLifecycleObserver {

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)

        //Se não houver usuario autenticado, faz login anonimo
        if (Firebase.auth.currentUser == null) {
            loginViewModel.signInAnonymous().observe(owner) { loginSucess ->
                if (loginSucess) {
                    viewModel.onResume()
                }
            }
        } else {
            viewModel.onResume()
        }
    }
}