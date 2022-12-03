package com.rndeep.fns_fantoo.ui.club.settings.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.dto.ClubMemberInfoWithMeta
import com.rndeep.fns_fantoo.ui.common.RecyclerViewItemClickListener
import com.rndeep.fns_fantoo.databinding.TabClubSettingMembersItemBinding
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ImageUtils
import com.rndeep.fns_fantoo.utils.setProfileAvatar
import timber.log.Timber
import java.lang.Exception

class ClubMembersAdapter :
    PagingDataAdapter<ClubMemberInfoWithMeta, RecyclerView.ViewHolder>(UserInfoDiffUtilCallback()) {

    private var itemClickListener: RecyclerViewItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHoder: RecyclerView.ViewHolder
        viewHoder = MembersViewHolder(
            TabClubSettingMembersItemBinding.bind(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.tab_club_setting_members_item, parent, false)
            )
        )
        return viewHoder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            getItem(position)?.let {
                (holder as MembersViewHolder).bind(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return snapshot().items.size
    }

    inner class MembersViewHolder(val binding: TabClubSettingMembersItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clubMemberObj: ClubMemberInfoWithMeta) {
            val clubMemberInfo = clubMemberObj.clubMemberInfo
            if (!clubMemberInfo.profileImg.isNullOrEmpty()) {
                setProfileAvatar(
                    binding.ivProfile,
                    ImageUtils.getImageFullUriForCloudFlare(clubMemberInfo.profileImg, true)
                )
            }
            binding.tvMemberNick.text = clubMemberInfo.nickname
            binding.tvMemberGrade.let {
                it.text =
                    if (clubMemberInfo.memberLevel == ConstVariable.ClubDef.CLUB_MEMBERSHIP_LV_MANAGER) it.context.getString(
                        R.string.k_club_president
                    ) else it.context.getString(R.string.a_general_membership)
            }
            binding.clRoot.setOnClickListener {
                itemClickListener?.onItemClick(clubMemberInfo)
            }
        }
    }

    fun setOnItemClickListener(listener: RecyclerViewItemClickListener) {
        itemClickListener = listener
    }

}

class UserInfoDiffUtilCallback : DiffUtil.ItemCallback<ClubMemberInfoWithMeta>() {
    override fun areContentsTheSame(
        oldItem: ClubMemberInfoWithMeta,
        newItem: ClubMemberInfoWithMeta
    ): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(
        oldItem: ClubMemberInfoWithMeta,
        newItem: ClubMemberInfoWithMeta
    ): Boolean {
        return oldItem.clubMemberInfo.memberId == newItem.clubMemberInfo.memberId
    }
}