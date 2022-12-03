package com.rndeep.fns_fantoo.ui.club.detail.moneybox

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TabClubMoneyboxMonthRankingLayoutBinding
import com.rndeep.fns_fantoo.data.remote.model.MoneyBoxRanking
import com.rndeep.fns_fantoo.utils.CustomDividerDecoration
import java.text.SimpleDateFormat
import java.util.*

class MoneyBoxMonthRankingVH(private val binding : TabClubMoneyboxMonthRankingLayoutBinding) :RecyclerView.ViewHolder(binding.root) {
    private val rankAdapter = MoneyBoxRankAdapter()
    private val dividerDecoration = CustomDividerDecoration(0.5f,20f,itemView.context.getColor(R.color.gray_400),false)
    fun rankingBind(rankList : List<MoneyBoxRanking>?){
        settingDate()
        binding.rankList.layoutManager=LinearLayoutManager(itemView.context)
        rankList?.let {
            rankAdapter.setItem(it)
        }
        binding.rankList.adapter=rankAdapter
        binding.rankList.removeItemDecoration(dividerDecoration)
        binding.rankList.addItemDecoration(dividerDecoration)

    }

    private fun settingDate(){
        val date = Calendar.getInstance()
        val dateFormat= SimpleDateFormat("yyyy.MM.dd")
        val currentDate = dateFormat.format(date.time)
        date.set(Calendar.DAY_OF_MONTH,1)
        val firstOfMonthDate =dateFormat.format(date.time)
        binding.tvRankDateRange.text="${firstOfMonthDate}  ~ ${currentDate}"
    }

}