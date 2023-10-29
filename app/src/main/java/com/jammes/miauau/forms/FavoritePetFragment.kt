package com.jammes.miauau.forms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.jammes.miauau.R
import com.jammes.miauau.collections.FavoritePetsListAdapter
import com.jammes.miauau.collections.HomeListAdapter
import com.jammes.miauau.collections.PetsListViewModel
import com.jammes.miauau.databinding.FragmentPetFavoriteBinding

class FavoritePetFragment: Fragment() {

    private var _binding: FragmentPetFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FavoritePetsListAdapter

    private lateinit var viewModel: PetsListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[PetsListViewModel::class.java]
        adapter = FavoritePetsListAdapter(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPetFavoriteBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.favoritePetsList.layoutManager = LinearLayoutManager(requireContext())
        binding.favoritePetsList.adapter = adapter

        addingDividerDecoration()

        viewModel.stateFavoritePets().observe(viewLifecycleOwner){petList ->
            bindUiState(petList)
        }
    }

    private fun bindUiState(petList: PetsListViewModel.FavoritePetsUiState) {
        adapter.updateFavoritePetList(petList.petFavoriteList)
    }

    private fun addingDividerDecoration() {
        val divider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        val resources = requireContext().resources

        divider.isLastItemDecorated = true
        divider.dividerThickness = resources.getDimensionPixelSize(R.dimen.vertical_margin)
        divider.dividerColor = ContextCompat.getColor(requireContext(), R.color.light)

        binding.favoritePetsList.addItemDecoration(divider)
    }

    override fun onResume() {
        super.onResume()
        viewModel.listFavoritePets()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}