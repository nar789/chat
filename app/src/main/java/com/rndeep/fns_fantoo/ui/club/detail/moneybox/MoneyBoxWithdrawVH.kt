package com.rndeep.fns_fantoo.ui.club.detail.moneybox

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TabClubMoneyboxWithdrawLayoutBinding
import com.rndeep.fns_fantoo.data.remote.model.MoneyBoxWithDrawItem
import com.rndeep.fns_fantoo.utils.CustomDividerDecoration
import com.rndeep.fns_fantoo.utils.addSingleItemDecoRation

class MoneyBoxWithdrawVH(private val binding : TabClubMoneyboxWithdrawLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

    private val moneyBoxWithdrawAdapter = MoneyBoxWithdrawAdapter()
    private val customDividerDecoration = CustomDividerDecoration(0.5f,0f,itemView.context.getColor(R.color.gray_400_opacity12),false)


    fun withdrawBind(withDrawItem: MoneyBoxWithDrawItem?){
        withDrawItem?: return
        binding.tvCurrentMonth.text =itemView.context.getString(R.string.a_bank_transactions_of_month_with_arg,withDrawItem.month)
        binding.rcWithDrawList.layoutManager=LinearLayoutManager(itemView.context)
        moneyBoxWithdrawAdapter.setWithdrawItem(withDrawItem.items)
        binding.rcWithDrawList.addSingleItemDecoRation(customDividerDecoration)
        binding.rcWithDrawList.adapter=moneyBoxWithdrawAdapter

    }
}