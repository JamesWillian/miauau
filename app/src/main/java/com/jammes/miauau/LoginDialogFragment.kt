package com.jammes.miauau

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.jammes.miauau.databinding.FragmentDialogLoginBinding

class LoginDialogFragment: DialogFragment() {

    private var _binding: FragmentDialogLoginBinding? = null
    private val binding get() = _binding!!
    private val login = LoginAuth()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDialogLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = binding.usernameText
        val password = binding.passwordText

        binding.login.setOnClickListener {

            if ( login.signIn(email.text.toString(), password.text.toString()) ) {
                dismiss()
            } else {
                Toast.makeText(this.context, "Email ou Senha Incorretos.", Toast.LENGTH_LONG).show()
            }

        }
    }

}