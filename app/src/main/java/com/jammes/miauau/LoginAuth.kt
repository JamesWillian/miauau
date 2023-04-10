package com.jammes.miauau

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginAuth {

    private lateinit var auth: FirebaseAuth

    fun createAccount(email: String, password: String): Boolean {

        var result = true

        auth = Firebase.auth

        auth.createUserWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    Log.d("createUser", "Criar usuario com email e senha: Sucesso!")
                    signIn(email, password)

                } else {

                    Log.d("createUser", "Criar usuario com email e senha: Falhou!")
                    result = false

                }
            }

        return result
    }

    fun signIn(email: String, password: String): Boolean {

        var result = true
        auth = Firebase.auth

        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    Log.d("signIn", "Login do usuario com email e senha: Sucesso!")

                } else {

                    Log.d("signIn", "Login do usuario com email e senha: Falhou!")
                    result = false

                }
            }

        return result
    }

    fun signInAnonymous() {
        auth = Firebase.auth

        auth.signInAnonymously()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("signInAnonymous", "Login Anonimo: Sucesso!")
                } else {
                    Log.d("signInAnonymous", "Login Anonimo: Falhou!")
                }
            }
    }
}
