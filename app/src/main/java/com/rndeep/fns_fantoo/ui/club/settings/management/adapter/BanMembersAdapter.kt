package com.rndeep.fns_fantoo.ui.club.settings.management.adapter

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.dto.ClubWithDrawMemberInfoWithMeta
import com.rndeep.fns_fantoo.ui.common.RecyclerViewItemClickListener
import com.rndeep.fns_fantoo.databinding.*
import com.rndeep.fns_fantoo.utils.setProfileAvatar
import timber.log.Timber

class BanMembersAdapter :
    PagingDataAdapter<ClubWithDrawMemberInfoWithMeta, RecyclerView.ViewHolder>(
        BanMemberInfoDiffCallback()
    ) {

    private var itemClickListener: RecyclerViewItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHoder: RecyclerView.ViewHolder
        viewHoder = MemberVH(
            TabClubSettingBanMemberItemBinding.bind(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.tab_club_setting_ban_member_item, parent, false)
            )
        )
        return viewHoder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            (holder as MemberVH).bind(it)
        }
    }

    inner class MemberVH(val binding: TabClubSettingBanMemberItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clubWithDrawMemberInfoWithMeta: ClubWithDrawMemberInfoWithMeta) {
            val memberInfo = clubWithDrawMemberInfoWithMeta.clubWithDrawMemberInfo
            if (!memberInfo.profileImg.isNullOrEmpty()) {
                setProfileAvatar(binding.ivProfile, memberInfo.profileImg)
            }
            binding.tvNick.text = memberInfo.nickname
            try {
                binding.tvBanDate.text =
                    binding.root.context.getString(R.string.g_forced_leave_date) + " " + SimpleDateFormat(
                        "yyyy. MM. dd"
                    ).format(
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(memberInfo.createDate)
                    )
            }catch (e:Exception){
                Timber.e("${e.printStackTrace()}")
            }

            if (memberInfo.joinYn) {
                binding.tvReJoin.text = binding.root.context.getString(R.string.j_allow_rejoin)
                binding.tvReJoin.setTextColor(binding.root.context.getColor(R.color.primary_default))
            } else {
                binding.tvReJoin.text = binding.root.context.getString(R.string.j_no_rejoin)
                binding.tvReJoin.setTextColor(binding.root.context.getColor(R.color.btn_danger))
            }
            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(memberInfo)
            }
        }
    }

    fun setItemClickListener(itemClickListener: RecyclerViewItemClickListener) {
        this.itemClickListener = itemClickListener
    }
}

class BanMemberInfoDiffCallback : DiffUtil.ItemCallback<ClubWithDrawMemberInfoWithMeta>() {
    override fun areItemsTheSame(
        oldItem: ClubWithDrawMemberInfoWithMeta,
        newItem: ClubWithDrawMemberInfoWithMeta
    ): Boolean {
        return oldItem.clubWithDrawMemberInfo.clubWithdrawId == newItem.clubWithDrawMemberInfo.clubWithdrawId
    }

    override fun areContentsTheSame(
        oldItem: ClubWithDrawMemberInfoWithMeta,
        newItem: ClubWithDrawMemberInfoWithMeta
    ): Boolean {
        return oldItem == newItem
    }
}