package com.rndeep.fns_fantoo.ui.menu.fantooclub.more.report

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.databinding.ListItemMoreMenuBinding
import timber.log.Timber

class ReportMessageListAdapter (
    private val onItemSelected: (ReportMessageItem) -> Unit
) : ListAdapter<ReportMessageItem, RecyclerView.ViewHolder>(ListDiff) {

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

        internal fun bind(item: ReportMessageItem) {
            Timber.d("bind item : $item")
            binding.icon.visibility = View.GONE
            binding.title.text = item.message
            binding.root.setOnClickListener {
                Timber.d("selected item : $item")
                onItemSelected(item)
            }
        }
    }

    companion object {
        private val ListDiff = object : DiffUtil.ItemCallback<ReportMessageItem>() {
            override fun areItemsTheSame(oldItem: ReportMessageItem, newItem: ReportMessageItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ReportMessageItem, newItem: ReportMessageItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}