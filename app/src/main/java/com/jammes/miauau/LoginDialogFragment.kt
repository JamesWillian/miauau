package com.jammes.miauau

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.jammes.miauau.databinding.FragmentDialogLoginBinding

class LoginDialogFragment: DialogFragment() {

    private var _binding: FragmentDialogLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginAuthViewModel by viewModels {
        LoginAuthViewModel.Factory()
    }

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

        binding.signIn.setOnClickListener {

            val email = binding.usernameText.text.toString()
            val password = binding.passwordText.text.toString()
            loginViewModel.signIn(email, password)
                .observe(viewLifecycleOwner) { result ->

                if (result) {
                    dismiss()
                }  else {
                    Toast.makeText(this.context, "Email ou Senha Incorretos.", Toast.LENGTH_LONG).show()
                }

            }

        }

        binding.signUpNew.setOnClickListener {
            val signupDialog = SignUpDialogFragment()
            signupDialog.show(childFragmentManager, "signupFragment")
        }
    }

}