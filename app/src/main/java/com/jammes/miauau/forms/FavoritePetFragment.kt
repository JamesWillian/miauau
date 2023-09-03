package com.jammes.miauau.forms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jammes.miauau.databinding.FragmentPetFavoriteBinding

class FavoritePetFragment: Fragment() {

    private var _binding: FragmentPetFavoriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPetFavoriteBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}