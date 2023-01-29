package com.rndeep.fns_fantoo.ui.menu.fantooclub.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.databinding.ListItemRecentSearchBinding
import timber.log.Timber

class RecentSearchAdapter (
    private val onItemClicked: (RecentSearchClickType, String) -> Unit
) : ListAdapter<String, RecyclerView.ViewHolder>(RecentSearchDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Timber.d("onCreateViewHolder")
        val binding =
            ListItemRecentSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Timber.d("onBindViewHolder")
        (holder as RecentSearchViewHolder).bind(getItem(position) as String)
    }

    inner class RecentSearchViewHolder(
        private val binding: ListItemRecentSearchBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        internal fun bind(item: String) {
            Timber.d("bind item : $item")
            binding.recentSearch.text = item

            binding.recentSearch.setOnCloseIconClickListener {
                Timber.d("setOnCloseIconClickListener item : $item")
                onItemClicked(RecentSearchClickType.DELETE, item)
            }
            binding.recentSearch.setOnClickListener {
                Timber.d("setOnClickListener item : $item")
                onItemClicked(RecentSearchClickType.CLICK, item)
            }
        }
    }

    companion object {
        private val RecentSearchDiff = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }
}