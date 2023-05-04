package com.jammes.miauau

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.jammes.miauau.databinding.FragmentDialogSignupBinding

class SignUpDialogFragment: DialogFragment() {

    private var _binding: FragmentDialogSignupBinding? = null
    private val binding get() = _binding!!
    private val signUpViewModel: LoginAuthViewModel by viewModels {
        LoginAuthViewModel.Factory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDialogSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUp.setOnClickListener {

            val email = binding.emailSignUpText.text.toString()
            val password = binding.passwordSignUpText.text.toString()
            val passwordConfirm = binding.passwordConfirmationSignUpText.text.toString()

            if (password != passwordConfirm) {
                Toast.makeText(this.context, "As senhas não correspondem!", Toast.LENGTH_LONG).show()
            } else {

                signUpViewModel.createAccount(email, password)
                    .observe(viewLifecycleOwner) { result ->

                        if (result) {
                            Toast.makeText(this.context, "Usuário criado com sucesso!", Toast.LENGTH_LONG).show()
                            dismiss()
                        } else {
                            Toast.makeText(
                                this.context,
                                "Não foi possível criar seu usuário. Verifique seus dados!",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }
            }

        }

        binding.alreadyHaveUserTextView.setOnClickListener {
            dismiss()
        }
    }

}