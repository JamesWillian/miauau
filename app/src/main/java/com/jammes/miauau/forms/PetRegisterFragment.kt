package com.jammes.miauau.forms


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jammes.miauau.R
import com.jammes.miauau.collections.Pet
import com.jammes.miauau.collections.PetRegisterChipInfo
import com.jammes.miauau.collections.PetRegisterViewModel
import com.jammes.miauau.core.repository.PetsRepositoryFirestore
import com.jammes.miauau.databinding.FragmentPetRegisterBinding
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class PetRegisterFragment : Fragment() {


    private var _binding: FragmentPetRegisterBinding? = null
    private val binding get() = _binding!!


    private val viewModel: PetRegisterViewModel by viewModels {
        PetRegisterViewModel.Factory(PetsRepositoryFirestore())
    }

    val takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as Bitmap

            binding.imagePet.setImageBitmap(imageBitmap)

        }
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

        val args: PetRegisterFragmentArgs by navArgs()

        viewModel.stateOnceAndStream().observe(viewLifecycleOwner) {pet ->
            updateUI(pet)
        }

        binding.SaveButton.setOnClickListener {

            val pet = petData()

            if (viewModel.isValid(pet)) {
                viewModel.addNewPet(pet)
                findNavController().navigateUp()
            } else {
                Toast.makeText(
                    context,
                    "Preencha todos os campos obrigatórios",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        binding.imagePet.setOnClickListener {

            val activity = this.requireActivity()
            if (ContextCompat.checkSelfPermission(
                    activity,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startCamera(activity)
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(android.Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
            }
        }
    }

    private fun updateUI(uiState: PetRegisterViewModel.UiState) {
        binding.petNameEditText.editText?.setText(uiState.pet.name)
        binding.petBreedEditText.editText?.setText(uiState.pet.breed)
        binding.petAgeEditText.editText?.setText(uiState.pet.age)
        binding.petDescriptionEditText.editText?.setText(uiState.pet.description)
        binding.imagePet.setImageBitmap(uiState.pet.image)

        binding.petTypeChipGroup.check(getChipIdByValue(uiState.pet.type) ?: binding.chipDog.id)
        binding.petAgeChipGroup.check(getChipIdByValue(uiState.pet.ageType) ?: binding.chipYears.id)
        binding.petSizeChipGroup.check(getChipIdByValue(uiState.pet.size) ?: binding.chipMedium.id)
        binding.petSexChipGroup.check(getChipIdByValue(uiState.pet.sex) ?: binding.chipMale.id)
    }

    fun petData(): Pet {
        return Pet(
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
            castrated = binding.petCastratedCheckBox.isChecked,
            image = binding.imagePet.drawable.toBitmap()
        )
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startCamera(activity: FragmentActivity) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            takePicture.launch(intent)
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

    private val petOptionsChipValues = petOptionsChip.flatMap { entry ->
        entry.value.map { chipInfo -> chipInfo.value to entry.key }
    }.toMap()

    fun getChipIdByValue(value: Int): Int? {
        return petOptionsChipValues[value]
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 100
    }
}