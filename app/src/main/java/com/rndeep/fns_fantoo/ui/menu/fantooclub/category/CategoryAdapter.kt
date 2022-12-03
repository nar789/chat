package com.rndeep.fns_fantoo.ui.menu.fantooclub.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.FantooClubCategory
import com.rndeep.fns_fantoo.databinding.ListItemCategoryBinding

class CategoryAdapter(
    private val onItemClicked: (FantooClubCategory) -> Unit
) : ListAdapter<FantooClubCategory, RecyclerView.ViewHolder>(CategoryDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ListItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        (holder as CategoryViewHolder).bind(item)
    }

    inner class CategoryViewHolder(private val binding: ListItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FantooClubCategory) {
            binding.apply {
                title.text = item.categoryName
                number.text = item.postCount.toString()
                root.setOnClickListener {
                    onItemClicked(item)
                }
            }
        }
    }

    companion object {
        private val CategoryDiff = object : DiffUtil.ItemCallback<FantooClubCategory>() {
            override fun areItemsTheSame(oldItem: FantooClubCategory, newItem: FantooClubCategory): Boolean {
                return oldItem.categoryId == newItem.categoryId
            }

            override fun areContentsTheSame(oldItem: FantooClubCategory, newItem: FantooClubCategory): Boolean {
                return oldItem == newItem
            }
        }
    }
}