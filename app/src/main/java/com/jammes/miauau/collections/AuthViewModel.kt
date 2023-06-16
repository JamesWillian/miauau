package com.jammes.miauau.collections

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jammes.miauau.MainActivity
import com.jammes.miauau.R
import com.jammes.miauau.core.model.UserDomain
import com.jammes.miauau.core.repository.UsersRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UsersRepository): ViewModel() {

    private lateinit var oneTapClient: SignInClient
    private lateinit var signUpRequest: BeginSignInRequest
    private lateinit var auth: FirebaseAuth

    fun oneTapSignIn(serverClientId: String, signInClient: SignInClient, resultLauncher: ActivityResultLauncher<IntentSenderRequest>) {
        auth = Firebase.auth
        oneTapClient = signInClient
        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(serverClientId)
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build()

        oneTapClient.beginSignIn(signUpRequest)
            .addOnSuccessListener { result ->
                launchSignIn(result.pendingIntent, resultLauncher)
            }
    }

    private fun launchSignIn(pendingIntent: PendingIntent, resultLauncher: ActivityResultLauncher<IntentSenderRequest>) {
        try {
            val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent)
                .build()
            //Chama a activity de login
            resultLauncher.launch(intentSenderRequest)
        } catch (e: IntentSender.SendIntentException) {
            Log.e(TAG, "Couldn't start Sign In: ${e.localizedMessage}")
        }
    }

    fun handleSignInResult(data: Intent?) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken

            //Se obteve o token, inicia processo de autenticação no Firebase
            if (idToken != null) {
                Log.d(TAG, "firebaseAuthWithGoogle: ${credential.id}")
                firebaseAuthWithGoogle(idToken)
            } else {
                Log.d(TAG, "No ID token!")
            }
        } catch (e: ApiException) {
            Log.w(TAG, "Google sign in failed", e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        //Faz login na Firebase com as credenciais obtidas
        auth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->

                viewModelScope.launch {
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser

                        repository.addUser(
                            UserDomain(
                                uid = user!!.uid,
                                name = user.displayName ?: "Sem Nome",
                                about = "",
                                phone = user.phoneNumber ?: "",
                                email = user.email ?: "",
                                location = "",
                                photoUrl = user.photoUrl.toString()
                            )
                        )
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                    }
                }

            }
    }

    class Factory(private val repository: UsersRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(repository) as T
        }
    }

    companion object {
        private const val TAG = "AuthViewModel"
    }
}