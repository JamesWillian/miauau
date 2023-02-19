package com.jammes.miauau.collections

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jammes.miauau.databinding.PetItemBinding
import com.jammes.miauau.R

class HomeListAdapter: RecyclerView.Adapter<HomeListAdapter.ViewHolder>() {

    class ViewHolder(private val binding: PetItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(petItem: PetItem) {
            binding.petNameTextView.text = petItem.name
            binding.petDescriptionTextView.text = petItem.description
            binding.petBreedTextView.text = petItem.breed
            binding.petAgeTextView.text = petItem.age
            binding.petSexTextView.text = petItem.sex

            binding.root.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToPetDetailFragment(petItem.id)
                itemView.findNavController().navigate(action)
            }
        }
    }

    fun updatePetList(pet: List<PetItem>) {
        asyncListDiffer.submitList(pet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PetItemBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
    }

    private val asyncListDiffer: AsyncListDiffer<PetItem> = AsyncListDiffer(this, DiffCallBack)

    object DiffCallBack: DiffUtil.ItemCallback<PetItem>() {
        override fun areItemsTheSame(oldItem: PetItem, newItem: PetItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PetItem, newItem: PetItem): Boolean {
            return oldItem == newItem
        }

    }
}