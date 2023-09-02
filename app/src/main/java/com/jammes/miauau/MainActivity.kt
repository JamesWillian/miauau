package com.jammes.miauau

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jammes.miauau.collections.AuthViewModel
import com.jammes.miauau.core.repository.UsersRepositoryFirestore
import com.jammes.miauau.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var viewModel: AuthViewModel

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        viewModel.handleSignInResult(result.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupNavigation()
        setupBottomNavigation()

        supportActionBar?.setDisplayShowTitleEnabled(false)

        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.userProfileFragment)
                    true
                }
                R.id.userProfileFragment -> {
                    if (Firebase.auth.currentUser!!.isAnonymous) {
                        login()
                    } else {
                        navController.navigate(R.id.userProfileFragment)
                    }
                    !Firebase.auth.currentUser!!.isAnonymous
                }
                else -> true
            }
        }

        /**
         * Tela de Cadastro de Novo Pet
         */
        binding.imageAddPetToolbar.setOnClickListener {
            if (Firebase.auth.currentUser!!.isAnonymous) {
                login()
            } else {
                if (navController.currentDestination != navController.findDestination(R.id.petRegisterFragment)) {
                    navController.navigate(R.id.petRegisterFragment)
                }
            }
        }

    }

    private fun setupBottomNavigation() {
        bottomNavigation = binding.bottomNavigation

        NavigationUI.setupWithNavController(bottomNavigation, navController)
    }

    private fun login() {
        viewModel.oneTapSignIn(
            getString(R.string.default_web_client_id),
            Identity.getSignInClient(this),
            signInLauncher
        )
    }

    private fun setupNavigation() {
        navController = findNavController(R.id.fragmentContainerView)

        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}