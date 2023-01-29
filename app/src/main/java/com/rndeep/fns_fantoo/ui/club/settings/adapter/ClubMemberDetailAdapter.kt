package com.rndeep.fns_fantoo.ui.club.settings.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TabClubSettingMemberDetailTabitemBinding
import com.rndeep.fns_fantoo.databinding.TabClubSettingMemberDetailUserinfoBinding

class ClubMemberDetailAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER_INFO = 0
        private const val VIEW_TYPE_TAB_HEADER = 1
        private const val VIEW_TYPE_LIST_ITEM = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHoder:RecyclerView.ViewHolder
        when(viewType){
            VIEW_TYPE_USER_INFO ->{
                viewHoder = UserInfoVH(TabClubSettingMemberDetailUserinfoBinding.bind(
                    LayoutInflater.from(parent.context).inflate(R.layout.tab_club_setting_member_detail_userinfo, parent, false)))
            }
            VIEW_TYPE_TAB_HEADER ->{
                viewHoder = TabItemVH(TabClubSettingMemberDetailTabitemBinding.bind(
                    LayoutInflater.from(parent.context).inflate(R.layout.tab_club_setting_member_detail_tabitem, parent, false)
                ))
            }
            VIEW_TYPE_LIST_ITEM ->{
                viewHoder = TabItemVH(TabClubSettingMemberDetailTabitemBinding.bind(
                    LayoutInflater.from(parent.context).inflate(R.layout.tab_club_setting_member_detail_tabitem, parent, false)
                ))
            }
            else ->{
                viewHoder = TabItemVH(TabClubSettingMemberDetailTabitemBinding.bind(
                    LayoutInflater.from(parent.context).inflate(R.layout.tab_club_setting_member_detail_tabitem, parent, false)
                ))
            }
        }
        return viewHoder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as UserInfoVH).bind()
    }

    override fun getItemCount(): Int {
        return 0
    }


    inner class UserInfoVH(val binding:TabClubSettingMemberDetailUserinfoBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(){
            //binding.ivMemberProfile
            binding.tvMemberNick.setText("사용자 닉네임")
            binding.tvMemberGrade.setText("일반회원")
            binding.tvGivingAmount.setText("365 KDG")
        }
    }

    inner class TabItemVH(val binding: TabClubSettingMemberDetailTabitemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(){

        }
    }
}