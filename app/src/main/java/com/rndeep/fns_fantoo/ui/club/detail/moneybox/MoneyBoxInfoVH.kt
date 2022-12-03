package com.rndeep.fns_fantoo.ui.club.detail.moneybox

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TabClubMoneyboxInfoLayoutBinding
import com.rndeep.fns_fantoo.data.remote.model.MoneyBoxTopInfo
import com.rndeep.fns_fantoo.utils.setProfileAvatar
import java.text.NumberFormat
import java.util.*

class MoneyBoxInfoVH(private val binding: TabClubMoneyboxInfoLayoutBinding) :RecyclerView.ViewHolder(binding.root) {

    fun infoBind(infoItem : MoneyBoxTopInfo?){
        if(infoItem!=null){
            binding.tvClubName.text=itemView.context.getString(R.string.p_coffer_of_fanclub_with_arg,infoItem.clubName)
            setProfileAvatar(binding.ivClubThumbnail,infoItem.clubThumbnail)
            binding.clubMoneyAccount.text=NumberFormat.getNumberInstance(Locale.getDefault()).format(infoItem.accountAmount)
        }else{
            binding.tvClubName.text=itemView.context.getString(R.string.b_loading_data)
            binding.clubMoneyAccount.visibility= View.GONE
            binding.clubMoneyAccountUnit.visibility=View.GONE
        }
    }

}