package com.jammes.miauau.collections

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jammes.miauau.R
import com.jammes.miauau.core.model.PetItem
import com.jammes.miauau.databinding.MyPetItemBinding
import com.jammes.miauau.forms.UserProfileFragmentDirections
import com.squareup.picasso.Picasso

class MyPetsListAdapter: RecyclerView.Adapter<MyPetsListAdapter.ViewHolder>() {

    class ViewHolder(private val binding: MyPetItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(petItem: PetItem) {
            binding.petNameTextView.text = petItem.name

            Picasso.get()
                .load(petItem.imageURL)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.petImageView)

            binding.root.setOnClickListener {
                val id = petItem.id ?: ""
                val action = UserProfileFragmentDirections.actionUserProfileFragmentToPetDetailFragment(id)
                itemView.findNavController().navigate(action)
            }

            binding.button.setOnClickListener {
                PopupMenu(it.context, binding.button).apply {
                    menuInflater.inflate(R.menu.popup_menu_my_pet, this.menu)

                    setOnMenuItemClickListener {menu ->
                        when (menu.itemId) {
                            R.id.option_delete_pet -> Toast.makeText(it.context, "Deletar", Toast.LENGTH_SHORT).show()
                            R.id.option_adopted_pet -> Toast.makeText(it.context, "Adotado", Toast.LENGTH_SHORT).show()
                        }
                        true
                    }
                    show()

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = MyPetItemBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(binding)
    }

    fun updateMyPetList(pet: List<PetItem>) {
        asyncListDiffer.submitList(pet)
    }

    private val asyncListDiffer: AsyncListDiffer<PetItem> = AsyncListDiffer(this, DiffCallBack)

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
    }

    object DiffCallBack : DiffUtil.ItemCallback<PetItem>() {
        override fun areItemsTheSame(oldItem: PetItem, newItem: PetItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PetItem, newItem: PetItem): Boolean {
            return oldItem == newItem
        }

    }
}