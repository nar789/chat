package com.rndeep.fns_fantoo.ui.editor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.databinding.ListItemHashtagBinding
import timber.log.Timber

class HashtagAdapter(
    private val onDeleteClicked: (String) -> Unit
) : ListAdapter<String, RecyclerView.ViewHolder>(HashtagDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Timber.d("onCreateViewHolder")
        val binding =
            ListItemHashtagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HashtagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Timber.d("onBindViewHolder")
        (holder as HashtagViewHolder).bind(getItem(position) as String)
    }

    inner class HashtagViewHolder(
        private val binding: ListItemHashtagBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        internal fun bind(item: String) {
            Timber.d("bind item : $item")
            binding.hashtag.text = item

            binding.hashtag.setOnCloseIconClickListener {
                Timber.d("click end icon hashtag : $item")
                onDeleteClicked(item)
            }
        }
    }

    companion object {
        private val HashtagDiff = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

}