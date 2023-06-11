package com.jammes.miauau

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jammes.miauau.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var oneTapClient: SignInClient
    private lateinit var signUpRequest: BeginSignInRequest
    private lateinit var auth: FirebaseAuth

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        handleSignInResult(result.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupNavigation()

        auth = Firebase.auth

        /**
         * Tela de Cadastro de Novo Pet
         */
        binding.imageAddPetToolbar.setOnClickListener {
            findNavController(R.id.fragmentContainerView).navigate(R.id.petRegisterFragment)
        }

        /**
         * Se autenticado > Tela de Perfil
         * Não autenticado > Login One Tap
         */
        binding.imageUserToolbar.setOnClickListener {
            if (Firebase.auth.currentUser != null) {
                findNavController(R.id.fragmentContainerView).navigate(R.id.userProfileFragment)
            } else {
                oneTapSignIn()
            }
        }

    }

    private fun oneTapSignIn() {
        oneTapClient = Identity.getSignInClient(this)
        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build()

        oneTapClient.beginSignIn(signUpRequest)
            .addOnSuccessListener { result ->
                launchSignIn(result.pendingIntent)
            }
    }

    private fun launchSignIn(pendingIntent: PendingIntent) {
        try {
            val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent)
                .build()
            //Chama a activity de login
            signInLauncher.launch(intentSenderRequest)
        } catch (e: IntentSender.SendIntentException) {
            Log.e(TAG, "Couldn't start Sign In: ${e.localizedMessage}")
        }
    }

    private fun handleSignInResult(data: Intent?) {
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
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    Toast.makeText(this, "Bem-vindo ${user!!.displayName}", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }

            }
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.fragmentContainerView)

        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}