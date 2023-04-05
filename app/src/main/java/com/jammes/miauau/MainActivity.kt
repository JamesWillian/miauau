package com.jammes.miauau

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jammes.miauau.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        auth = Firebase.auth

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupNavigation()

        binding.imageAddPetToolbar.setOnClickListener {
            findNavController(R.id.fragmentContainerView).navigate(R.id.petRegisterFragment)
        }

        binding.imageUserToolbar.setOnClickListener {
            login()
        }
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser == null) {
            val signInLauncher = registerForActivityResult(
                FirebaseAuthUIActivityResultContract()
            ) { res ->
                this.onSignInResult(res)
            }

            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
            )

            val signInItent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()

            signInLauncher.launch(signInItent)
        }

    }

    private fun login() {
        val loginDialog = LoginDialogFragment()
        loginDialog.show(supportFragmentManager, "loginFragment")
    }

    private fun onSignInResult(res: FirebaseAuthUIAuthenticationResult?) {
        val response = res?.idpResponse

        if (res?.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser

            Toast.makeText(this, "Sucesso! Bem-vindo ${user?.displayName}", Toast.LENGTH_SHORT).show()

        } else {
            login()
            Toast.makeText(this, "Não foi possível fazer login. Tente Novamente...", Toast.LENGTH_SHORT).show()
        }
    }

    //Codigo para navegacao entre fragment foi extraido do projeto HabitList do Lucas
    //**revisar, ainda nao sei ao certo o que faz cada comando
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