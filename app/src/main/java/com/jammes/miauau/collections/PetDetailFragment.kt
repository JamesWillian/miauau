package com.jammes.miauau.collections

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jammes.miauau.R
import com.jammes.miauau.databinding.FragmentPetDetailBinding
import com.jammes.miauau.dummy.MockPets

class PetDetailFragment : Fragment() {

    private var _binding: FragmentPetDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPetDetailBinding.inflate(inflater,  container, false)

        val args: PetDetailFragmentArgs by navArgs()

        val pet = MockPets.petItemList.find { it.id == args.petId }

        if (pet != null) {
            binding.petNameTextView.text = pet.name //Name
            binding.petDescriptionTextView.text = pet.description //Description
            binding.petBreedTextView.text = pet.breed //Breed
            binding.petAgeTextView.text = pet.age //Age
            binding.petSexTextView.text = pet.sex //Sex
            binding.petCastratedTextView.text = pet.castrated
            binding.petSizeTextView.text = pet.size
            binding.petVaccinatedTextView.text = pet.vaccinated
            binding.petImageView.setImageResource(pet.img)
        }

        return (binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
