package com.rndeep.fns_fantoo.ui.menu.editprofile.interest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.userinfo.Category
import com.rndeep.fns_fantoo.databinding.ListItemInterestBinding
import timber.log.Timber

class SelectInterestAdapter(
    private val onItemClicked: (Category) -> Unit
) : ListAdapter<Category, SelectInterestAdapter.InterestViewHolder>(InterestDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterestViewHolder {
        val binding =
            ListItemInterestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InterestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InterestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class InterestViewHolder(private val binding: ListItemInterestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        internal fun bind(item: Category) {
            Timber.d("bind item: $item")
            binding.apply {

                name.text = item.description

                if (item.selectYn) {
                    name.setTextColor(ContextCompat.getColor(root.context, R.color.primary_500))
                    name.setBackgroundResource(R.drawable.bg_club_create_category_active)
                } else {
                    name.setTextColor(ContextCompat.getColor(root.context, R.color.gray_800))
                    name.setBackgroundResource(R.drawable.bg_club_create_category)
                }

                name.setOnClickListener {
                    onItemClicked(item)
                }
            }
        }
    }

    companion object {
        private val InterestDiff = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
                return (oldItem.code == newItem.code) && (oldItem.selectYn == newItem.selectYn)
            }

            override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem == newItem
            }
        }
    }
}