package com.jammes.miauau

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginAuthViewModel(private val auth: FirebaseAuth): ViewModel() {

    private val _loginResult = MutableLiveData<Boolean>()
    private val loginResult: LiveData<Boolean> get() = _loginResult

    fun createAccount(email: String, password: String): LiveData<Boolean> {

        auth.createUserWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->
                _loginResult.value = task.isSuccessful

                if (task.isSuccessful) {

                    Log.d("createUser", "Criar usuario com email e senha: Sucesso!")

                } else {

                    Log.d("createUser", "Criar usuario com email e senha: Falhou!")

                }
            }

        return loginResult
    }

    fun signIn(email: String, password: String): LiveData<Boolean> {

        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->
                _loginResult.value = task.isSuccessful

                if (task.isSuccessful) {

                    Log.d("signIn", "Login do usuario com email e senha: Sucesso!")

                } else {

                    Log.d("signIn", "Login do usuario com email e senha: Falhou!")

                }
            }

        return loginResult
    }

    fun signInAnonymous(): LiveData<Boolean> {

        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                _loginResult.value = task.isSuccessful

                if (task.isSuccessful) {
                    Log.d("signInAnonymous", "Login Anonimo: Sucesso!")
                } else {
                    Log.d("signInAnonymous", "Login Anonimo: Falhou!")
                }
            }

        return loginResult
    }



    class Factory: ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val auth = Firebase.auth
            return LoginAuthViewModel(auth) as T
        }
    }
}
