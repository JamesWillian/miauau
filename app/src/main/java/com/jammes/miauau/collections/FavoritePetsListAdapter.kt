package com.jammes.miauau.collections

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jammes.miauau.R
import com.jammes.miauau.core.model.FavoritePet
import com.jammes.miauau.databinding.FavoritePetItemBinding
import com.jammes.miauau.forms.FavoritePetFragmentDirections
import com.squareup.picasso.Picasso

class FavoritePetsListAdapter(private val viewModel: PetsListViewModel) :
    RecyclerView.Adapter<FavoritePetsListAdapter.ViewHolder>() {

    private val asyncListDiffer: AsyncListDiffer<FavoritePet> = AsyncListDiffer(this, DiffCallBack)

    inner class ViewHolder(private val binding: FavoritePetItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favoritePet: FavoritePet, viewModel: PetsListViewModel) {

            binding.petNameTextView.text = favoritePet.name

            Picasso.get()
                .load(favoritePet.imageURL)
                .placeholder(R.drawable.dog_pixel_placeholder)
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.petImageView)

            binding.favoriteImageView.setOnClickListener {
                viewModel.removeFavoritePet(favoritePet.id ?: "")
                viewModel.listFavoritePets()
            }

            binding.root.setOnClickListener {
                val id = favoritePet.id ?: ""
                val action = FavoritePetFragmentDirections.actionFavoritePetFragmentToPetDetailFragment(id)
                itemView.findNavController().navigate(action)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FavoritePetItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    fun updateFavoritePetList(favoritePets: List<FavoritePet>) {
        asyncListDiffer.submitList(favoritePets)
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position], viewModel)
    }

    object DiffCallBack : DiffUtil.ItemCallback<FavoritePet>() {
        override fun areItemsTheSame(oldItem: FavoritePet, newItem: FavoritePet): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FavoritePet, newItem: FavoritePet): Boolean {
            return oldItem == newItem
        }
    }
}
