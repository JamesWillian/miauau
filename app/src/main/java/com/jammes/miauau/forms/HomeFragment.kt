package com.jammes.miauau.forms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.jammes.miauau.R
import com.jammes.miauau.collections.HomeListAdapter
import com.jammes.miauau.collections.PetsListLifecycleObserver
import com.jammes.miauau.collections.PetsListViewModel
import com.jammes.miauau.core.repository.PetsRepositoryFirestore
import com.jammes.miauau.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: HomeListAdapter
    private lateinit var petListViewModel: PetsListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        petListViewModel = ViewModelProvider(requireActivity())[PetsListViewModel::class.java]

        lifecycle.addObserver(PetsListLifecycleObserver(petListViewModel))
        adapter = HomeListAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.homeRecyclerView.adapter = adapter

        addingDividerDecoration()

        binding.chipDog.setOnClickListener {
            petListViewModel.filterPet(1)
        }

        binding.chipCat.setOnClickListener {
            petListViewModel.filterPet(2)
        }

        petListViewModel.stateOnceAndStream().observe(viewLifecycleOwner){ petList ->
            bindUiState(petList)
        }
    }

    private fun bindUiState(petListUiState: PetsListViewModel.PetListUiState){
        adapter.updatePetList(petListUiState.petItemList)
    }

    private fun addingDividerDecoration() {
        val divider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        val resources = requireContext().resources

        divider.isLastItemDecorated = false
        divider.dividerThickness = resources.getDimensionPixelSize(R.dimen.vertical_margin)
        divider.dividerColor = ContextCompat.getColor(requireContext(), R.color.light)

        binding.homeRecyclerView.addItemDecoration(divider)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}