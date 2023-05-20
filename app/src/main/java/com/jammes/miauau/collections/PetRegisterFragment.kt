package com.jammes.miauau.collections


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jammes.miauau.R
import com.jammes.miauau.core.repository.PetsRepositoryFirestore
import com.jammes.miauau.databinding.FragmentPetRegisterBinding

class PetRegisterFragment : Fragment() {

    private var _binding: FragmentPetRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PetRegisterViewModel by viewModels {
        PetRegisterViewModel.Factory(PetsRepositoryFirestore())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPetRegisterBinding.inflate(inflater, container, false)

        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.SaveButton.setOnClickListener {

            val pet = Pet(
                name = binding.petNameEditText.editText?.text.toString(),
                description = binding.petDescriptionEditText.editText?.text.toString(),
                type = petOptionsChip[binding.petTypeChipGroup.checkedChipId]?.find { it.chipId == 1 }?.value ?: 1,
                age = try {binding.petAgeEditText.editText?.text.toString().toInt()}
                      catch (ex: NumberFormatException) { 0 },
                ageType = petOptionsChip[binding.petAgeChipGroup.checkedChipId]?.find { it.chipId == 1 }?.value ?: 1,
                breed = binding.petBreedEditText.editText?.text.toString(),
                sex = petOptionsChip[binding.petSexChipGroup.checkedChipId]?.find { it.chipId == 1 }?.value ?: 1,
                vaccinated = binding.petVaccinatedCheckBox.isChecked,
                size = petOptionsChip[binding.petSizeChipGroup.checkedChipId]?.find { it.chipId == 1 }?.value ?: 2,
                castrated = binding.petCastratedCheckBox.isChecked
            )

            if (viewModel.addNewPet(pet)) {
                findNavController().navigateUp()
            } else {
                Toast.makeText(
                    context,
                    "Preencha todos os campos obrigatórios",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }

    private val petOptionsChip = mapOf(
        R.id.chipDog to listOf(PetRegisterChipInfo(1, "Dog", 1)),
        R.id.chipCat to listOf(PetRegisterChipInfo(1, "Cat", 2)),
        R.id.chipYears to listOf(PetRegisterChipInfo(1, "Years", 1)),
        R.id.chipMonths to listOf(PetRegisterChipInfo(1, "Months", 2)),
        R.id.chipWeeks to listOf(PetRegisterChipInfo(1, "Weeks", 3)),
        R.id.chipMale to listOf(PetRegisterChipInfo(1, "Male", 1)),
        R.id.chipFemale to listOf(PetRegisterChipInfo(1, "Female", 2)),
        R.id.chipSmall to listOf(PetRegisterChipInfo(1, "Small", 1)),
        R.id.chipMedium to listOf(PetRegisterChipInfo(1, "Medium", 2)),
        R.id.chipLarge to listOf(PetRegisterChipInfo(1, "Large", 3)),
    )

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}