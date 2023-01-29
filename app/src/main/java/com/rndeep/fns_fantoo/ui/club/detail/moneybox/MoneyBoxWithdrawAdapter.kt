package com.rndeep.fns_fantoo.ui.club.detail.moneybox

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TabClubMoneyboxWithdrawListBinding
import com.rndeep.fns_fantoo.data.remote.model.MoneyBoxWithDrawListItem
import java.text.SimpleDateFormat
import java.util.*

class MoneyBoxWithdrawAdapter :RecyclerView.Adapter<MoneyBoxWithdrawAdapter.MoneyBoxWithDrawVH>() {

    private var withdrawItems = listOf<MoneyBoxWithDrawListItem>()

    inner class MoneyBoxWithDrawVH(private val binding: TabClubMoneyboxWithdrawListBinding) : RecyclerView.ViewHolder(binding.root){
        private val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")
        fun itemBind(withdrawItem : MoneyBoxWithDrawListItem){
            binding.tvDonatorNickName.text=withdrawItem.donateNickname
            val calendar = Calendar.getInstance()
            calendar.timeInMillis=withdrawItem.donateDate
            binding.tvDonateDate.text=dateFormat.format(calendar.time)
            binding.tvAccountAmount.text=withdrawItem.donateAmount.toString()
            binding.tvDonateMoney.text=withdrawItem.donateMoney.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoneyBoxWithDrawVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tab_club_moneybox_withdraw_list,parent,false)
        return MoneyBoxWithDrawVH(TabClubMoneyboxWithdrawListBinding.bind(view))
    }

    override fun onBindViewHolder(holder: MoneyBoxWithDrawVH, position: Int) {
        holder.itemBind(withdrawItems[position])
    }

    override fun getItemCount()=withdrawItems.size

    fun setWithdrawItem(items :List<MoneyBoxWithDrawListItem>){
        this.withdrawItems=items
    }

}