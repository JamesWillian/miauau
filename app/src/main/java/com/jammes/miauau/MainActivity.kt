package com.jammes.miauau

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
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
import com.jammes.miauau.collections.AuthViewModel
import com.jammes.miauau.core.repository.UsersRepositoryFirestore
import com.jammes.miauau.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModel.Factory(UsersRepositoryFirestore())
    }

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        viewModel.handleSignInResult(result.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupNavigation()

        val navController = findNavController(R.id.fragmentContainerView)

        /**
         * Tela de Cadastro de Novo Pet
         */
        binding.imageAddPetToolbar.setOnClickListener {
            if (Firebase.auth.currentUser!!.isAnonymous) {
                login()
            } else {
                navController.navigate(R.id.petRegisterFragment)
            }
        }

        /**
         * Se autenticado > Tela de Perfil
         * Não autenticado > Login One Tap
         */
        binding.imageUserToolbar.setOnClickListener {
            if (Firebase.auth.currentUser!!.isAnonymous) {
                login()
            } else {
                navController.navigate(R.id.userProfileFragment)
            }
        }
    }

    private fun login() {
        viewModel.oneTapSignIn(
            getString(R.string.default_web_client_id),
            Identity.getSignInClient(this),
            signInLauncher
        )
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


}