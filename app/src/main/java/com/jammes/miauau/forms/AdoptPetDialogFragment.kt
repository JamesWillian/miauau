package com.jammes.miauau.forms

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.jammes.miauau.R
import com.jammes.miauau.collections.PetsListViewModel
import com.jammes.miauau.core.model.User
import com.jammes.miauau.databinding.FragmentDialogPetAdoptBinding
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder

class AdoptPetDialogFragment: DialogFragment() {

    private lateinit var viewModel: PetsListViewModel

    private var _binding: FragmentDialogPetAdoptBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[PetsListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogPetAdoptBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.detailUiState.value?.petDetail?.let {item ->
            viewModel.fetchTutorById(
                item.tutorId
            )
        }

        viewModel.stateTutorOnceAndStream().observe(viewLifecycleOwner){uiState ->
            updateUi(uiState.tutor)
        }
    }

    private fun updateUi(tutor: User) {
        binding.nameTextView.text = tutor.name
        binding.phoneTextView.text = tutor.phone

        if (tutor.photoUrl != "") {
            Picasso.get()
                .load(tutor.photoUrl)
                .placeholder(R.drawable.user_avatar)
                .error(R.drawable.user_avatar)
                .into(binding.tutorImageView)
        }

        binding.tutorProfileButton.setOnClickListener {

            val action =
                    PetDetailFragmentDirections.actionPetDetailFragmentToUserProfileFragment(tutor.uid)
                findNavController().navigate(action)

            dismiss()
        }

        binding.sendMessageButton.setOnClickListener{
            viewModel.detailUiState.value?.petDetail?.let { pet ->
                messageTutorByWhatsApp(tutor.phone, pet.name, tutor.name)
            }
        }
    }

    private fun messageTutorByWhatsApp(phone: String, petName: String, tutorName: String) {
        val sendIntent = Intent(Intent.ACTION_VIEW)
        val tutorPhone = "55${phone.trim()}"

        try {
            val url = "https://api.whatsapp.com/send?phone=$tutorPhone&text=" +
                    URLEncoder.encode(
                        "Olá $tutorName, me interessei em adotar *$petName* através do aplicativo *Miauau*!\n\n" +
                                "Gostaria de saber mais sobre o processo de adoção e conhecer melhor o pet.",
                        "UTF-8"
                    )

            sendIntent.setPackage("com.whatsapp")
            sendIntent.data = Uri.parse(url)
            startActivity(sendIntent)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}