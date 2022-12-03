package com.rndeep.fns_fantoo.ui.menu.editprofile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.databinding.ListItemEditProfileBinding
import timber.log.Timber

class EditProfileAdapter (
    private val onItemClicked: (EditProfileItem) -> Unit
) : ListAdapter<EditProfileItem, EditProfileAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemEditProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ListItemEditProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val itemClicked = onItemClicked
        fun bind(item: EditProfileItem) {
            binding.apply {
                val visibility = if (item.isIcon) View.VISIBLE else View.GONE
                itemMenuMoreIc.visibility = visibility
                itemMenuTitle.text = root.context.getString(item.itemType.stringRes)
                if (item.itemType != EditProfileItemType.CONCERN) itemValue.text = item.value
                root.setOnClickListener {
                    itemClicked(item)
                }
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<EditProfileItem>() {
            override fun areItemsTheSame(oldItem: EditProfileItem, newItem: EditProfileItem): Boolean {
                return oldItem.value == newItem.value
            }

            override fun areContentsTheSame(oldItem: EditProfileItem, newItem: EditProfileItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}