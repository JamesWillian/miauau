package com.jammes.miauau.forms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jammes.miauau.R
import com.jammes.miauau.collections.MyPetsListAdapter
import com.jammes.miauau.collections.UserProfileViewModel
import com.jammes.miauau.core.repository.PetsRepositoryFirestore
import com.jammes.miauau.core.repository.UsersRepositoryFirestore
import com.jammes.miauau.databinding.FragmentUserProfileBinding
import com.squareup.picasso.Picasso

class UserProfileFragment: Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MyPetsListAdapter
    private val viewModel: UserProfileViewModel by activityViewModels {
        UserProfileViewModel.Factory(UsersRepositoryFirestore(), PetsRepositoryFirestore())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = MyPetsListAdapter(viewModel)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
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
        binding.userPetsList.layoutManager = LinearLayoutManager(requireContext())
        binding.userPetsList.adapter = adapter

        viewModel.refreshUser(Firebase.auth.currentUser!!.uid)
        viewModel.stateOnceAndStream().observe(viewLifecycleOwner) {user ->
            updateUi(user)
        }

        viewModel.statePetsOnce().observe(viewLifecycleOwner) {pet ->
            bindPetItem(pet)
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

    private fun updateUi(uiState: UserProfileViewModel.UserUiState) {
        binding.userNameTextView.text = uiState.user.name
        binding.userLocationTextView.text = uiState.user.location
        binding.userDescriptionTextView.text = uiState.user.about
        binding.userPhoneTextView.text = "Telefone: ${uiState.user.phone}"
        binding.userEmailTextView.text = "Email: ${uiState.user.email}"

        Picasso.get()
            .load(uiState.user.photoUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(binding.imageView)

        if (uiState.user.uid != Firebase.auth.currentUser!!.uid) {
            if (!uiState.user.showContact) binding.userinfoCard.visibility = View.GONE
            binding.userDetailsButton.visibility = View.GONE
            binding.userLogoffButton.visibility = View.GONE
        }
    }

    private fun bindPetItem(petListUiState: UserProfileViewModel.MyPetListUiState){
        adapter.updateMyPetList(petListUiState.petItemList)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}