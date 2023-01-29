package com.rndeep.fns_fantoo.ui.club.detail.moneybox

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TabClubMoneyboxMonthRankingListBinding
import com.rndeep.fns_fantoo.data.remote.model.MoneyBoxRanking
import java.text.NumberFormat
import java.util.*

class MoneyBoxRankAdapter() : RecyclerView.Adapter<MoneyBoxRankAdapter.MoneyBoxRankVH>(){

    inner class MoneyBoxRankVH(private val binding : TabClubMoneyboxMonthRankingListBinding) :RecyclerView.ViewHolder(binding.root){

        fun itemBind(item : MoneyBoxRanking){
            binding.ivRankText.text =item.rank.toString()
            binding.tvNickName.text=item.rankingNickname
            when(bindingAdapterPosition){
                0->{
                    binding.ivRankText.text=""
                    binding.ivRankText.background=itemView.context.getDrawable(R.drawable.safe_ranking1)
                }
                1->{
                    binding.ivRankText.text=""
                    binding.ivRankText.background=itemView.context.getDrawable(R.drawable.safe_ranking2)
                }
                2->{
                    binding.ivRankText.text=""
                    binding.ivRankText.background=itemView.context.getDrawable(R.drawable.safe_ranking3)
                }
                else->{
                    binding.ivRankText.text=(bindingAdapterPosition+1).toString()
                }
            }
            binding.tvDonationAmount.text = NumberFormat.getNumberInstance(Locale.getDefault()).format(item.rankMoney)

        }
    }

    private var rankItem :List<MoneyBoxRanking> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoneyBoxRankVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tab_club_moneybox_month_ranking_list,parent,false)
        return MoneyBoxRankVH(TabClubMoneyboxMonthRankingListBinding.bind(view))
    }

    override fun onBindViewHolder(holder: MoneyBoxRankVH, position: Int) {
        holder.itemBind(rankItem[holder.bindingAdapterPosition])
    }

    override fun getItemCount()=rankItem.size

    fun setItem(item : List<MoneyBoxRanking>){
        this.rankItem=item
    }

}