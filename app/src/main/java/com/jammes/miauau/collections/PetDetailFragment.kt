package com.jammes.miauau.collections

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jammes.miauau.databinding.FragmentPetDetailBinding

class PetDetailFragment : Fragment() {

    private var _binding: FragmentPetDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPetDetailBinding.inflate(inflater,  container, false)

//        val args: PetDetailFragmentArgs by navArgs()

        return (binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
