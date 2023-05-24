package com.jammes.miauau.collections


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.jammes.miauau.R
import com.jammes.miauau.core.repository.PetsRepositoryFirestore
import com.jammes.miauau.databinding.FragmentPetRegisterBinding
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

private const val REQUEST_CAMERA_PERMISSION = 100

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



        binding.SaveButton.setOnClickListener {

            val imageStream = binding.imagePet.toInputStream(binding.imagePet)



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
                castrated = binding.petCastratedCheckBox.isChecked,
                image = imageStream
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

    fun ImageView.toInputStream(image: ImageView): ByteArrayInputStream {
        val drawable = image.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        return ByteArrayInputStream(data)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}