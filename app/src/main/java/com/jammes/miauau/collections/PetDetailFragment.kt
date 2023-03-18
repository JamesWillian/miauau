package com.jammes.miauau.collections

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jammes.miauau.core.repository.PetsRepositoryFirestore
import com.jammes.miauau.databinding.FragmentPetDetailBinding

class PetDetailFragment : Fragment() {

    private val viewModel: PetsListViewModel by viewModels {
        PetsListViewModel.Factory(PetsRepositoryFirestore())
    }

    private var _binding: FragmentPetDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPetDetailBinding.inflate(inflater,  container, false)

        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: PetDetailFragmentArgs by navArgs()

        viewModel.onPetItemClicked(args.petId)
        viewModel.detailUiState.observe(viewLifecycleOwner) { pet ->
            if (pet != null) {
//                binding.petNameTextView.text = pet.petDetail.name //Name
//                binding.petDescriptionTextView.text = pet.petDetail.description //Description
//                binding.petBreedTextView.text = pet.petDetail.breed //Breed
//                binding.petAgeTextView.text = pet.petDetail.age //Age
//                binding.petSexTextView.text = pet.petDetail.sex //Sex
//                binding.petCastratedTextView.text = pet.petDetail.castrated
//                binding.petSizeTextView.text = pet.petDetail.size
//                binding.petVaccinatedTextView.text = pet.petDetail.vaccinated
//                binding.petImageView.setImageResource(pet.petDetail.img)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
