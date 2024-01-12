package com.jammes.miauau.collections

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jammes.miauau.core.model.UserDomain
import com.jammes.miauau.core.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UsersRepository
): ViewModel() {

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
            .addOnFailureListener {err ->
                Log.e(TAG, "Couldn't start Sign In: ${err.message}")
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

                        userRepository.addUser(
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

    private val userWithoutPhone = MutableLiveData<Boolean>()

    fun userWithoutPhone(): LiveData<Boolean> {
        return userWithoutPhone
    }

    fun checkUserPhone() {
        viewModelScope.launch {
            userWithoutPhone.postValue(phoneIsEmpty())
        }
    }

    private suspend fun phoneIsEmpty(): Boolean {
        return userRepository.phoneIsEmpty()
    }

    companion object {
        private const val TAG = "AuthViewModel"
    }
}