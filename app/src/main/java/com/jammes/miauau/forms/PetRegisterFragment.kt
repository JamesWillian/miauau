package com.jammes.miauau.forms


import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jammes.miauau.R
import com.jammes.miauau.collections.*
import com.jammes.miauau.core.model.*
import com.jammes.miauau.core.repository.PetsRepositoryFirestore
import com.jammes.miauau.databinding.FragmentPetRegisterBinding
import com.squareup.picasso.Picasso
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PetRegisterFragment : Fragment() {

    private var currentPicturePath: String? = null
    private var _binding: FragmentPetRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PetRegisterViewModel by viewModels {
        PetRegisterViewModel.Factory(PetsRepositoryFirestore())
    }

    private val storageDir = File(
        Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        ), "MiauAu"
    )

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
                startCamera()
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
        val pet = uiState.pet

        binding.petNameEditText.editText?.setText(pet.name)
        binding.petBreedEditText.editText?.setText(pet.breed)
        binding.petAgeEditText.editText?.setText((pet.age ?: "").toString())
        binding.petDescriptionEditText.editText?.setText(pet.description)
        binding.petVaccinatedCheckBox.isChecked = pet.vaccinated.isNotBlank()
        binding.petCastratedCheckBox.isChecked = pet.castrated.isNotBlank()
        if (pet.imageBitmap != null) {
            binding.imagePet.setImageBitmap(pet.imageBitmap)
        } else {
            Picasso.get()
                .load(pet.imageURL)
                .placeholder(R.drawable.camera)
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.imagePet)
        }
        binding.petTypeChipGroup.check(
            when (pet.petType) {
                PetType.DOG -> binding.chipDog.id
                PetType.CAT -> binding.chipCat.id
            }
        )
        binding.petAgeChipGroup.check(
            when (pet.ageType) {
                AgeType.YEARS -> binding.chipYears.id
                AgeType.MONTHS -> binding.chipMonths.id
                AgeType.WEEKS -> binding.chipWeeks.id
            }
        )
        when (pet.size) {
            Size.SMALL -> binding.chipSmall.id
            Size.MEDIUM -> binding.chipMedium.id
            Size.LARGE -> binding.chipLarge.id
        }
        when (pet.sex) {
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
            imageBitmap = binding.imagePet.drawable.toBitmap(),
            tutorId = Firebase.auth.currentUser!!.uid
        )
    }

    /**
     * Esta função inicia a câmera do dispositivo para capturar uma imagem.
     * Ela cria um arquivo de imagem, obtém a URI do arquivo, configura a URI
     * como uma saída para a câmera e inicia a atividade da câmera para capturar a imagem.
     *
     * Fluxo de Funcionamento:
     *
     * Uma nova instância de [Intent] é criada,
     * utilizando [MediaStore.ACTION_IMAGE_CAPTURE] como ação para
     * iniciar a atividade de captura de imagem.
     *
     * A função [createImageFile()] é chamada para criar um arquivo de imagem.
     * O retorno dessa função é armazenado na variável [picture].
     *
     * Utilizando a sintaxe de escopo [picture.also],
     * o código dentro do bloco é executado com a variável file apontando para o
     * arquivo de imagem recém-criado.
     *
     * A função [FileProvider.getUriForFile()] é usada para obter a URI do arquivo,
     * com base no contexto atual, no nome do provedor de arquivo
     * ("com.jammes.miauau.fileprovider") e no arquivo em si.
     *
     * A URI do arquivo é definida como uma saída para a câmera,
     * utilizando intent.putExtra([MediaStore.EXTRA_OUTPUT], fileUri).
     *
     * É realizada uma verificação para garantir que haja uma atividade disponível
     * para lidar com a [intent] da câmera.
     * Se existir uma atividade disponível, takePicture.launch(intent) é chamado
     * para iniciar a atividade da câmera com a intent de capturar a imagem.
     */
    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val picture = createImageFile()
        picture.also {file ->
            val fileUri = FileProvider.getUriForFile(
                requireContext(),
                "com.jammes.miauau.fileprovider",
                file
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                takePicture.launch(intent)
            }
        }
    }

    /**
     * Esta função registra um contrato de resultado para iniciar uma atividade de captura de imagem.
     * Quando a atividade é concluída com sucesso, a imagem capturada é exibida na ImageView "imagePet".
     */
    private val takePicture = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK)
            binding.imagePet.setImageURI(currentPicturePath?.toUri())

    }

    /**
     * Cria uma imagem temporaria para salvar a foto do pet na câmera
     */
    private fun createImageFile(): File {

        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())

        if (!storageDir.exists()) storageDir.mkdirs()

        return File.createTempFile(
            "PET_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPicturePath = path
        }
    }

    /**
     * Deleta todos os arquivos contidos na pasta passada no parâmetro
     * Utilizada para deletar as fotos dos pets capturadas pela câmera
     */
    private fun deleteTempFiles(dir: File): Boolean {
        if (dir.isDirectory) {
            val files = dir.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isDirectory) {
                        deleteTempFiles(file)
                    } else {
                        file.delete()
                    }
                }
            }
        }
        return dir.delete()
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

        if (!requireActivity().isChangingConfigurations) deleteTempFiles(storageDir)

        _binding = null
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 100
    }
}