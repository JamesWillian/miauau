package com.jammes.miauau.forms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jammes.miauau.collections.UserProfileViewModel
import com.jammes.miauau.core.repository.UsersRepositoryFirestore
import com.jammes.miauau.databinding.FragmentUserProfileBinding

class UserProfileFragment: Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserProfileViewModel by viewModels {
        UserProfileViewModel.Factory(UsersRepositoryFirestore())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.refreshUser(Firebase.auth.currentUser!!.uid)
        viewModel.stateOnceAndStream().observe(viewLifecycleOwner) {user ->
            binding.userNameTextView.text = user.user.name
            binding.userLocationTextView.text = user.user.location
            binding.userDescriptionTextView.text = user.user.about
            binding.userPhoneTextView.text = "Telefone: ${user.user.phone}"
            binding.userEmailTextView.text = "Email: ${user.user.email}"
        }

        binding.userDetailsButton.setOnClickListener {
            val action =
                UserProfileFragmentDirections.actionUserProfileFragmentToUserProfileEditFragment()
            findNavController().navigate(action)
        }

        binding.userLogoffButton.setOnClickListener {
            Firebase.auth.signOut()
            findNavController().navigateUp()
        }
    }
}