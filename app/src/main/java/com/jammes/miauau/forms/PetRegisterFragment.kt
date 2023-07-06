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
import androidx.core.view.drawToBitmap
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jammes.miauau.R
import com.jammes.miauau.collections.*
import com.jammes.miauau.core.repository.PetsRepositoryFirestore
import com.jammes.miauau.databinding.FragmentPetRegisterBinding
import com.squareup.picasso.Picasso
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class PetRegisterFragment : Fragment() {


    private var _binding: FragmentPetRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PetRegisterViewModel by viewModels {
        PetRegisterViewModel.Factory(PetsRepositoryFirestore())
    }

    private var imageBitmap: Bitmap? = null

    val takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageBitmap = result.data?.extras?.get("data") as Bitmap

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

        //Verificando se foi passado o id do Pet para buscar as informações para a UI
        if (arguments?.containsKey("petId") == true) {
            if (!args.petId.isNullOrEmpty()) {
                viewModel.fetchPet(args.petId!!)
            }
        }

        val observador = Observer<PetRegisterViewModel.UiState> {uiState ->
            updateUI(uiState)
        }
        viewModel.stateOnceAndStream().observe(viewLifecycleOwner, observador)

        binding.SaveButton.setOnClickListener {

            onSave()

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

    private fun onSave() {
        //Atualiza o UiState para utilizar os dados no momento de enviar o pet para o Firebase
        viewModel.updateUiState(petData())

        if (viewModel.petIsValid()) {
            viewModel.addPet()
            findNavController().navigateUp()
        } else {
            Toast.makeText(
                context,
                "Preencha todos os campos obrigatórios",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onPause() {
        super.onPause()

        viewModel.updateUiState(petData())
    }

    private fun updateUI(uiState: PetRegisterViewModel.UiState) {
        binding.petNameEditText.editText?.setText(uiState.pet.name)
        binding.petBreedEditText.editText?.setText(uiState.pet.breed)
        binding.petAgeEditText.editText?.setText((uiState.pet.age ?: "").toString())
        binding.petDescriptionEditText.editText?.setText(uiState.pet.description)
        if (uiState.pet.imageBitmap != null) {
            binding.imagePet.setImageBitmap(uiState.pet.imageBitmap)
        } else {
            if (uiState.pet.imageURL != "") {
                Picasso.get()
                    .load(uiState.pet.imageURL)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(binding.imagePet)
            }
        }
        binding.petTypeChipGroup.check(
            when (uiState.pet.petType) {
                PetType.DOG -> binding.chipDog.id
                PetType.CAT -> binding.chipCat.id
            }
        )
        binding.petAgeChipGroup.check(
            when (uiState.pet.ageType) {
                AgeType.YEARS -> binding.chipYears.id
                AgeType.MONTHS -> binding.chipMonths.id
                AgeType.WEEKS -> binding.chipWeeks.id
            }
        )
        when (uiState.pet.size) {
            Size.SMALL -> binding.chipSmall.id
            Size.MEDIUM -> binding.chipMedium.id
            Size.LARGE -> binding.chipLarge.id
        }
        when (uiState.pet.sex) {
            Sex.MALE -> binding.chipMale.id
            Sex.FEMALE -> binding.chipFemale.id
        }

    }

    private fun petData(): PetItem {
        return PetItem(
            name = binding.petNameEditText.editText?.text.toString(),
            description = binding.petDescriptionEditText.editText?.text.toString(),
            petType = (petOptionsChip[binding.petTypeChipGroup.checkedChipId]?.find { it.chipId == 1 }?.value ?: PetType.DOG) as PetType,
            age = try {binding.petAgeEditText.editText?.text.toString().toInt()}
                  catch (ex: NumberFormatException) { null },
            ageType = (petOptionsChip[binding.petAgeChipGroup.checkedChipId]?.find { it.chipId == 1 }?.value ?: 1) as AgeType,
            breed = binding.petBreedEditText.editText?.text.toString(),
            sex = (petOptionsChip[binding.petSexChipGroup.checkedChipId]?.find { it.chipId == 1 }?.value ?: 1) as Sex,
            vaccinated = if (binding.petVaccinatedCheckBox.isChecked) "Vacinado" else "",
            size = (petOptionsChip[binding.petSizeChipGroup.checkedChipId]?.find { it.chipId == 1 }?.value ?: 2) as Size,
            castrated = if (binding.petCastratedCheckBox.isChecked) "Castrado" else "",
            imageBitmap = imageBitmap,
            tutorId = Firebase.auth.currentUser!!.uid
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
        R.id.chipDog to listOf(PetRegisterChipInfo(1, "Dog", PetType.DOG)),
        R.id.chipCat to listOf(PetRegisterChipInfo(1, "Cat", PetType.CAT)),
        R.id.chipYears to listOf(PetRegisterChipInfo(1, "Years", AgeType.YEARS)),
        R.id.chipMonths to listOf(PetRegisterChipInfo(1, "Months", AgeType.MONTHS)),
        R.id.chipWeeks to listOf(PetRegisterChipInfo(1, "Weeks", AgeType.WEEKS)),
        R.id.chipMale to listOf(PetRegisterChipInfo(1, "Male", Sex.MALE)),
        R.id.chipFemale to listOf(PetRegisterChipInfo(1, "Female", Sex.FEMALE)),
        R.id.chipSmall to listOf(PetRegisterChipInfo(1, "Small", Size.SMALL)),
        R.id.chipMedium to listOf(PetRegisterChipInfo(1, "Medium", Size.MEDIUM)),
        R.id.chipLarge to listOf(PetRegisterChipInfo(1, "Large", Size.LARGE)),
    )

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 100
    }
}