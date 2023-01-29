package com.rndeep.fns_fantoo.ui.club.detail.moneybox

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.ui.common.post.BlankErrorVH
import com.rndeep.fns_fantoo.databinding.BlankLayoutForErrorBinding
import com.rndeep.fns_fantoo.databinding.TabClubMoneyboxInfoLayoutBinding
import com.rndeep.fns_fantoo.databinding.TabClubMoneyboxMonthRankingLayoutBinding
import com.rndeep.fns_fantoo.databinding.TabClubMoneyboxWithdrawLayoutBinding
import com.rndeep.fns_fantoo.data.remote.model.MoneyBoxItem
import com.rndeep.fns_fantoo.data.remote.model.MoneyBoxRanking
import com.rndeep.fns_fantoo.data.remote.model.MoneyBoxTopInfo
import com.rndeep.fns_fantoo.data.remote.model.MoneyBoxWithDrawItem
import com.rndeep.fns_fantoo.utils.ConstVariable

class MoneyBoxAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val CLUBMONEYBOXINFO = 0
    private val MONEYBOXRANKING = 1
    private val WITHDRAWLIST = 2


    private var moneyBoxTypeList = listOf<MoneyBoxItem>()
    //상단 정보
    private var moneyBoxInfo : MoneyBoxTopInfo? = null
    //랭킹 정보
    private var moneyBoxRankItem :List<MoneyBoxRanking>? =null
    //입출금 정보
    private var moneyBoxWithDrawItem : MoneyBoxWithDrawItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CLUBMONEYBOXINFO -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tab_club_moneybox_info_layout, parent, false)
                MoneyBoxInfoVH(TabClubMoneyboxInfoLayoutBinding.bind(view))
            }
            MONEYBOXRANKING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tab_club_moneybox_month_ranking_layout, parent, false)
                MoneyBoxMonthRankingVH(TabClubMoneyboxMonthRankingLayoutBinding.bind(view))
            }
            WITHDRAWLIST -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tab_club_moneybox_withdraw_layout, parent, false)
                MoneyBoxWithdrawVH(TabClubMoneyboxWithdrawLayoutBinding.bind(view))
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.blank_layout_for_error, parent, false)
                BlankErrorVH(BlankLayoutForErrorBinding.bind(view))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MoneyBoxInfoVH -> {
                holder.infoBind(moneyBoxInfo)
            }
            is MoneyBoxMonthRankingVH -> {
                holder.rankingBind(moneyBoxRankItem)
            }
            is MoneyBoxWithdrawVH -> {
                holder.withdrawBind(moneyBoxWithDrawItem)
            }

        }
    }

    override fun getItemCount() = moneyBoxTypeList.size

    override fun getItemViewType(position: Int): Int {
        when (moneyBoxTypeList[position].type) {
            ConstVariable.MONEYBOXINFO -> {
                return CLUBMONEYBOXINFO
            }
            ConstVariable.MONTHLYRANKING -> {
                return MONEYBOXRANKING
            }
            ConstVariable.WITHDRAWLIST -> {
                return WITHDRAWLIST
            }
        }
        return super.getItemViewType(position)
    }

    fun setMoneyboxItem(items : List<MoneyBoxItem>){
        this.moneyBoxTypeList=items
    }

    fun setMoneyboxInfo(item : MoneyBoxTopInfo){
        moneyBoxInfo=item
    }

    fun setMoneyBoxRanking(items : List<MoneyBoxRanking>){
        this.moneyBoxRankItem=items
    }

    fun setMoneyBoxWithDrawItem(item : MoneyBoxWithDrawItem){
        this.moneyBoxWithDrawItem=item
    }
}