package com.jammes.miauau.collections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jammes.miauau.core.repository.PetsRepositoryFirestore
import com.jammes.miauau.databinding.FragmentHomeBinding

class HomeFragment: Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: HomeListAdapter
    private val viewModel: PetsListViewModel by activityViewModels {
        val petsRepository = PetsRepositoryFirestore()
        PetsListViewModel.Factory(petsRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(PetsListLifecycleObserver(viewModel))
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

        viewModel.stateOnceAndStream().observe(viewLifecycleOwner){
            bindUiState(it)
        }
    }

    private fun bindUiState(uiState: PetsListViewModel.UiState){
        adapter.updatePetList(uiState.petItemList)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}