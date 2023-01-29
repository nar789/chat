package com.rndeep.fns_fantoo.ui.editor

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.ListItemBottomSheetBinding
import timber.log.Timber

class SelectListAdapter(
    private val onItemSelected: (BoardItem) -> Unit
) : ListAdapter<BoardItem, RecyclerView.ViewHolder>(ListDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ListItemBottomSheetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ListViewHolder).bind(getItem(position))
    }

    inner class ListViewHolder(
        private val binding: ListItemBottomSheetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        internal fun bind(item: BoardItem) {
            Timber.d("bind item : $item")
            val visibility = if (item.selected) View.VISIBLE else View.INVISIBLE
            val color =
                if (item.selected) ColorStateList.valueOf(binding.root.context.getColor(R.color.primary_500))
                else ColorStateList.valueOf(binding.root.context.getColor(R.color.gray_850))
            binding.name.text = item.name
            binding.name.setTextColor(color)
            binding.icon.visibility = visibility

            binding.name.setOnClickListener {
                Timber.d("selected item : $item")
                onItemSelected(item)
            }
        }
    }

    companion object {
        private val ListDiff = object : DiffUtil.ItemCallback<BoardItem>() {
            override fun areItemsTheSame(oldItem: BoardItem, newItem: BoardItem): Boolean {
                return oldItem.selected == newItem.selected
            }

            override fun areContentsTheSame(oldItem: BoardItem, newItem: BoardItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}