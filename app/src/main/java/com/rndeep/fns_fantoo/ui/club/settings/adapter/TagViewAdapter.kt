package com.rndeep.fns_fantoo.ui.club.settings.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TagviewLayoutBinding
import com.rndeep.fns_fantoo.ui.club.settings.data.TagItem
import timber.log.Timber

class TagViewAdapter(val clickListener: (TagItem) -> Unit) :
    ListAdapter<TagItem, TagViewAdapter.TagViewVH>(TagItemDiffUtilCallback()) {

    /*companion object {
        private const val MAX_ITEM_COUNT = 10
    }
*/
    /*@SuppressLint("NotifyDataSetChanged")
    fun addSearchWord(word: TagItem): AddResult {
        if (currentList.size >= MAX_ITEM_COUNT) {
            return AddResult.AddResultMaxItem
        }
        for (wordItem in currentList) {
            if (word.name == wordItem.name) {
                return AddResult.AddResultDuplicateItem
            }
        }

        val itemList = currentList.toMutableList()
        itemList.add(word)
        submitList(itemList)
        return AddResult.AddResultOk
    }*/

    /*fun isMaxItemCount(): Boolean {
        return currentList.size >= MAX_ITEM_COUNT
    }*/

    inner class TagViewVH(val binding: TagviewLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(item: TagItem) {
            binding.tvTag.text = "#" + item.name
            if(item.isFixedItem){
                binding.ivDelete.visibility = View.GONE
            }else{
                binding.tvTag.setPadding(binding.tvTag.paddingLeft, binding.tvTag.paddingTop, 0, binding.tvTag.paddingBottom)
            }
            binding.root.setOnClickListener {
                /*if (!item.isFixedItem) {
                    val itemsList = currentList.toMutableList()
                    itemsList.remove(item)
                    submitList(itemsList)
                }*/
                clickListener(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewVH {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.tagview_layout, parent, false)
        return TagViewVH(TagviewLayoutBinding.bind(view))
    }

    override fun onBindViewHolder(holder: TagViewVH, position: Int) {
        try {
            holder.bind(getItem(position))
        } catch (e: IndexOutOfBoundsException) {
            Timber.e("TagViewAdapter IndexOutOfBoundsException.")
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }
}

class TagItemDiffUtilCallback : DiffUtil.ItemCallback<TagItem>() {
    override fun areContentsTheSame(oldItem: TagItem, newItem: TagItem): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: TagItem, newItem: TagItem): Boolean {
        return oldItem.name == newItem.name
    }
}
