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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupNavigation()

        //Chamar a tela de registro de novo pet
        binding.imageAddPetToolbar.setOnClickListener {
            findNavController(R.id.fragmentContainerView).navigate(R.id.petRegisterFragment)
        }

        //Chamar a tela de Login caso o usuario ainda não estiver autenticado
        binding.imageUserToolbar.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val loginDialog = LoginDialogFragment()
        loginDialog.show(supportFragmentManager, "loginFragment")
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