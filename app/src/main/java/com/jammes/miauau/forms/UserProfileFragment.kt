package com.jammes.miauau.forms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jammes.miauau.R
import com.jammes.miauau.collections.MyPetsListAdapter
import com.jammes.miauau.collections.UserProfileViewModel
import com.jammes.miauau.databinding.FragmentUserProfileBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileFragment: Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MyPetsListAdapter
    private lateinit var viewModel: UserProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[UserProfileViewModel::class.java]
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

        val args: UserProfileFragmentArgs by navArgs()

        if (arguments?.containsKey("userId") == true) {
            if (!args.userId.isNullOrEmpty()) {
                viewModel.refreshUser(args.userId!!)
            }
        } else {
            viewModel.refreshUser(Firebase.auth.currentUser!!.uid)
            viewModel.statePetsOnce().observe(viewLifecycleOwner) {pet ->
                bindPetItem(pet)
            }
        }

        viewModel.stateOnceAndStream().observe(viewLifecycleOwner) {user ->
            updateUi(user)
        }

        binding.userEditImageButton.setOnClickListener {
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
            binding.userEditImageButton.visibility = View.GONE
            binding.userLogoffButton.visibility = View.GONE
            binding.petsListCard.visibility = View.GONE
        } else {
            binding.userinfoCard.visibility = View.VISIBLE
            binding.userEditImageButton.visibility = View.VISIBLE
            binding.userLogoffButton.visibility = View.VISIBLE
            binding.petsListCard.visibility = View.VISIBLE

            if (uiState.user.phone.isEmpty()) {
                Toast.makeText(requireContext(),"Informe seu Telefone para continuar", Toast.LENGTH_SHORT).show()

                findNavController().navigate(R.id.userProfileEditFragment)
            }
        }


    }

    private fun bindPetItem(petListUiState: UserProfileViewModel.MyPetListUiState){
        adapter.updateMyPetList(petListUiState.petItemList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}