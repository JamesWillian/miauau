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
        adapter = MyPetsListAdapter()
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
            binding.userNameTextView.text = user.user.name
            binding.userLocationTextView.text = user.user.location
            binding.userDescriptionTextView.text = user.user.about
            binding.userPhoneTextView.text = "Telefone: ${user.user.phone}"
            binding.userEmailTextView.text = "Email: ${user.user.email}"
            if (user.user.photoUrl != "") {
                Picasso.get()
                    .load(user.user.photoUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(binding.imageView)
            }
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

    private fun bindPetItem(petListUiState: UserProfileViewModel.MyPetListUiState){
        adapter.updateOwnPetList(petListUiState.petItemList)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}