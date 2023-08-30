package com.jammes.miauau.forms

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jammes.miauau.R
import com.jammes.miauau.collections.*
import com.jammes.miauau.core.model.AgeType
import com.jammes.miauau.core.model.Sex
import com.jammes.miauau.core.model.Size
import com.jammes.miauau.core.repository.PetsRepositoryFirestore
import com.jammes.miauau.databinding.FragmentPetDetailBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder

@AndroidEntryPoint
class PetDetailFragment : Fragment() {

    private lateinit var viewModel: PetsListViewModel

    private var _binding: FragmentPetDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[PetsListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPetDetailBinding.inflate(inflater, container, false)

        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: PetDetailFragmentArgs by navArgs()

        viewModel.onPetItemClicked(args.petId)
        viewModel.detailUiState.observe(viewLifecycleOwner) { pet ->
            updateUI(pet)
        }

    }

    private fun updateUI(pet: PetsListViewModel.PetDetailUiState) {

        val ageType = when (pet.petDetail.ageType) {
            AgeType.YEARS -> resources.getString(R.string.years)
            AgeType.MONTHS -> resources.getString(R.string.months)
            AgeType.WEEKS -> resources.getString(R.string.weeks)
        }

        binding.petNameTextView.text = pet.petDetail.name //Name
        binding.petDescriptionTextView.text = pet.petDetail.description //Description
        binding.petBreedTextView.text = pet.petDetail.breed //Breed
        binding.petAgeTextView.text = "${pet.petDetail.age} $ageType" //Age
        binding.petSexTextView.text =
            when (pet.petDetail.sex) {
                Sex.MALE -> resources.getString(R.string.male)
                Sex.FEMALE -> resources.getString(R.string.female)
            } //Sex
        binding.petCastratedTextView.text = pet.petDetail.castrated
        binding.petSizeTextView.text =
            when (pet.petDetail.size) {
                Size.SMALL -> resources.getString(R.string.small)
                Size.MEDIUM -> resources.getString(R.string.medium)
                Size.LARGE -> resources.getString(R.string.large)
            } //size
        binding.petVaccinatedTextView.text = pet.petDetail.vaccinated

        if (pet.petDetail.imageURL != "") {
            Picasso.get()
                .load(pet.petDetail.imageURL)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.petImageView)
        }

        if (pet.petDetail.tutorId == Firebase.auth.currentUser!!.uid) {
            binding.adoptButton.text = "Editar meu Pet"
            binding.adoptButton.setOnClickListener {
                val action =
                    PetDetailFragmentDirections.actionPetDetailFragmentToPetRegisterFragment(pet.petDetail.id)
                findNavController().navigate(action)
            }
        } else {
            binding.adoptButton.text = "Adotar esse Pet"
            binding.adoptButton.setOnClickListener {
                val action =
                    PetDetailFragmentDirections.actionPetDetailFragmentToUserProfileFragment(pet.petDetail.tutorId)
                findNavController().navigate(action)
            }
        }

        binding.saveImageView2.visibility = View.GONE
        binding.shareImageView2.visibility = View.GONE

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
