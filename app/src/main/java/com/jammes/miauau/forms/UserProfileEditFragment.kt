package com.jammes.miauau.forms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jammes.miauau.core.model.User
import com.jammes.miauau.collections.UserProfileViewModel
import com.jammes.miauau.core.repository.PetsRepositoryFirestore
import com.jammes.miauau.core.repository.UsersRepositoryFirestore
import com.jammes.miauau.databinding.FragmentUserProfileEditBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileEditFragment: Fragment() {

    private var _binding: FragmentUserProfileEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UserProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[UserProfileViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileEditBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userInfo()?.let { updateUI(it.user) }

        binding.userSaveButton.setOnClickListener {
            val user = User(
                uid = Firebase.auth.currentUser!!.uid,
                name = binding.userNameEditText.editText?.text.toString(),
                location = binding.userLocationEditText.editText?.text.toString(),
                email = binding.userEmailEditText.editText?.text.toString(),
                phone = binding.userPhoneEditText.editText?.text.toString(),
                about = binding.userDescriptionEditText.editText?.text.toString(),
                showContact = binding.showUserContactCheckBox.isChecked,
                photoUrl = Firebase.auth.currentUser!!.photoUrl.toString()
            )
            viewModel.saveUserProfile(user)
            findNavController().navigateUp()
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.onResume()
    }

    private fun updateUI(user: User) {

        binding.userNameEditText.editText?.setText(user.name, TextView.BufferType.NORMAL)
        binding.userLocationEditText.editText?.setText(user.location, TextView.BufferType.NORMAL)
        binding.userEmailEditText.editText?.setText(user.email, TextView.BufferType.NORMAL)
        binding.userPhoneEditText.editText?.setText(user.phone, TextView.BufferType.NORMAL)
        binding.userDescriptionEditText.editText?.setText(user.about, TextView.BufferType.NORMAL)
        binding.showUserContactCheckBox.isChecked = user.showContact

    }

}