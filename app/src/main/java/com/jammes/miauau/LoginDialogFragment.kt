package com.jammes.miauau

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jammes.miauau.databinding.FragmentDialogLoginBinding
import com.jammes.miauau.databinding.FragmentPetRegisterBinding

class LoginDialogFragment: DialogFragment() {

    private var _binding: FragmentDialogLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDialogLoginBinding.inflate(inflater, container, false)
//        return inflater.inflate(R.layout.fragment_dialog_login, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        val email = binding.usernameText
        val password = binding.passwordText

        binding.login.setOnClickListener {

            signIn(email.text.toString().trim(), password.text.toString().trim())

        }
    }

    private fun createAccount(email: String, password: String) {

        if ((email.isEmpty()) || (password.isEmpty())) {

            Toast.makeText(this.context, "Informe o Email e Senha", Toast.LENGTH_LONG).show()

        } else {

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {

                        Log.d("createUser", "Criar usuario com email e senha: Sucesso!")
                        Toast.makeText(this.context, "usuario Criado com Sucesso!", Toast.LENGTH_LONG).show()

                        signIn(email, password)

                    } else {

                        Log.d("createUser", "Criar usuario com email e senha: Falhou!")
                        Toast.makeText(this.context, "Nao foi possivel criar o usuario", Toast.LENGTH_LONG).show()

                    }
                }
        }
    }

    fun signIn(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("signIn", "Login do usuario com email e senha: Sucesso!")
                    dismiss()
                } else {
                    Log.d("signIn", "Login do usuario com email e senha: Falhou!")

                    createAccount(email, password)
                }
            }
    }
}