package com.jammes.miauau.collections

import android.annotation.SuppressLint
import android.content.res.Resources.Theme
import android.graphics.Color
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.jammes.miauau.R
import com.jammes.miauau.databinding.FragmentPetRegisterBinding

class PetRegisterFragment : Fragment() {

    private var _binding: FragmentPetRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PetRegisterViewModel

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPetRegisterBinding.inflate(inflater, container, false)

        return (binding.root)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PetRegisterViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}