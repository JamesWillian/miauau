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
import com.jammes.miauau.core.model.PetDomain
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

            val name = binding.petNameEditText.editText?.text.toString()

            val description = binding.petDescriptionEditText.editText?.text.toString()

            val petType = when (binding.petTypeChipGroup.checkedChipId) {
                R.id.chipDog -> 1
                R.id.chipCat -> 2
                else -> 1
            }

            val age = try {
                binding.petAgeEditText.editText?.text.toString().toInt()
            } catch (ex: NumberFormatException) { 0 }

            val ageType = when (binding.petAgeChipGroup.checkedChipId) {
                R.id.chipYears -> 1
                R.id.chipMonths -> 2
                R.id.chipWeeks -> 3
                else -> 1
            }

            val breed = binding.petBreedEditText.editText?.text.toString()

            val sex = when (binding.petSexChipGroup.checkedChipId) {
                R.id.chipMale -> 1
                R.id.chipFemale -> 2
                else -> 1
            }

            val vaccinated = binding.petVaccinatedCheckBox.isChecked

            val size = when (binding.petSizeChipGroup.checkedChipId) {
                R.id.chipSmall -> 1
                R.id.chipMedium -> 2
                R.id.chipLarge -> 3
                else -> 2
            }

            val castrated = binding.petCastratedCheckBox.isChecked

            if (name == "") Toast.makeText(context, "Informe o Nome do Pet", Toast.LENGTH_LONG).show() else
            if (description == "") Toast.makeText(context, "Informe uma Descriçã do Pet", Toast.LENGTH_LONG).show() else
            if (age <= 0) Toast.makeText(context, "Informe a Idade do Pet", Toast.LENGTH_LONG).show()
            else {

                viewModel.addNewPet(
                    PetDomain(
                        petType = petType,
                        name = name,
                        description = description,
                        age = age,
                        ageType = ageType,
                        breed = breed,
                        sex = sex,
                        vaccinated = vaccinated,
                        size = size,
                        castrated = castrated
                    )
                )

                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}