package com.rndeep.fns_fantoo.ui.menu.fantooclub.more

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.ListItemMoreMenuBinding
import timber.log.Timber

class MoreMenuListAdapter(
    private val onItemSelected: (MoreMenuItem) -> Unit
) : ListAdapter<MoreMenuItem, RecyclerView.ViewHolder>(ListDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ListItemMoreMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ListViewHolder).bind(getItem(position))
    }

    inner class ListViewHolder(
        private val binding: ListItemMoreMenuBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        internal fun bind(item: MoreMenuItem) {
            Timber.d("bind item : $item")
            binding.icon.setImageResource(item.icon)
            val color =
                if (item.selected) ColorStateList.valueOf(binding.root.context.getColor(R.color.primary_500))
                else ColorStateList.valueOf(binding.root.context.getColor(R.color.gray_870))
            val title =
                if (item.selected && item.title.size > 1) binding.root.context.getString(item.title[1])
                else binding.root.context.getString(item.title[0])
            binding.title.text = title
//            binding.title.setTextColor(color)

            binding.root.setOnClickListener {
                Timber.d("selected item : $item")
                onItemSelected(item)
            }
        }
    }

    companion object {
        private val ListDiff = object : DiffUtil.ItemCallback<MoreMenuItem>() {
            override fun areItemsTheSame(oldItem: MoreMenuItem, newItem: MoreMenuItem): Boolean {
                return oldItem.selected == newItem.selected
            }

            override fun areContentsTheSame(oldItem: MoreMenuItem, newItem: MoreMenuItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}