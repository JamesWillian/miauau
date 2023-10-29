package com.jammes.miauau.collections

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jammes.miauau.R
import com.jammes.miauau.core.model.AgeType
import com.jammes.miauau.core.model.PetItem
import com.jammes.miauau.core.model.PetType
import com.jammes.miauau.core.model.Sex
import com.jammes.miauau.databinding.PetItemBinding
import com.jammes.miauau.forms.HomeFragmentDirections
import com.squareup.picasso.Picasso

class HomeListAdapter(private val viewModel: PetsListViewModel) : RecyclerView.Adapter<HomeListAdapter.ViewHolder>() {

    class ViewHolder(private val binding: PetItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(petItem: PetItem, viewModel: PetsListViewModel) {
            binding.petNameTextView.text = petItem.name
            binding.petDescriptionTextView.text = petItem.description

            fun setPlaceHolderImage(petType: PetType): Int {
                return when (petType) {
                    PetType.DOG -> R.drawable.dog_pixel_placeholder
                    PetType.CAT -> R.drawable.cat_pixel_placeholder
                }
            }

            Picasso.get()
                .load(petItem.imageURL)
                .placeholder(setPlaceHolderImage(petItem.petType))
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.petPhotoImageView)

            binding.root.setOnClickListener {
                val id = petItem.id ?: ""
                val action = HomeFragmentDirections.actionHomeFragmentToPetDetailFragment(id)
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
        holder.bind(asyncListDiffer.currentList[position], viewModel)
    }

    private val asyncListDiffer: AsyncListDiffer<PetItem> = AsyncListDiffer(this, DiffCallBack)

    object DiffCallBack : DiffUtil.ItemCallback<PetItem>() {
        override fun areItemsTheSame(oldItem: PetItem, newItem: PetItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PetItem, newItem: PetItem): Boolean {
            return oldItem == newItem
        }

    }
}