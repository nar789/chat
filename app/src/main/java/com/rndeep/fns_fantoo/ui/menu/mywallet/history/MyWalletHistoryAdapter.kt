package com.rndeep.fns_fantoo.ui.menu.mywallet.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.userinfo.WalletHistory
import com.rndeep.fns_fantoo.databinding.ListItemWalletHistoryBinding
import timber.log.Timber

class MyWalletHistoryAdapter :
    ListAdapter<WalletHistory, MyWalletHistoryAdapter.MyWalletHistoryViewHolder>(WalletHistoryDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyWalletHistoryViewHolder {
        val binding =
            ListItemWalletHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyWalletHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyWalletHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MyWalletHistoryViewHolder(private val binding: ListItemWalletHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        internal fun bind(item: WalletHistory) {
            Timber.d("bind item: $item")
            binding.apply {
                date.text = item.monthAndDate
                title.text = item.title
                type.text = item.comment
                time.text = item.createDate
                reward.text = item.value.toString()
                if(item.value > 0) {
                    reward.setTextColor(binding.root.context.getColor(R.color.primary_default))
                    reward.text = "+${item.value}"
                } else {
                    reward.setTextColor(binding.root.context.getColor(R.color.gray_800))
                }
            }
        }
    }

    companion object {
        private val WalletHistoryDiff = object : DiffUtil.ItemCallback<WalletHistory>() {
            override fun areItemsTheSame(oldItem: WalletHistory, newItem: WalletHistory): Boolean {
                return oldItem.historyId == newItem.historyId
            }

            override fun areContentsTheSame(oldItem: WalletHistory, newItem: WalletHistory): Boolean {
                return oldItem == newItem
            }
        }
    }
}