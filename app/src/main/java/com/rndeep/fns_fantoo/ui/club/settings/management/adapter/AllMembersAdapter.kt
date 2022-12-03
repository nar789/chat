package com.rndeep.fns_fantoo.ui.club.settings.management.adapter

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.dto.ClubMemberInfoWithMeta
import com.rndeep.fns_fantoo.ui.common.RecyclerViewItemClickListener
import com.rndeep.fns_fantoo.databinding.TabClubSettingAllMemberItemBinding
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ImageUtils
import com.rndeep.fns_fantoo.utils.setProfileAvatar
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.Instant
import timber.log.Timber

class AllMembersAdapter :
    PagingDataAdapter<ClubMemberInfoWithMeta, RecyclerView.ViewHolder>(ClubMemberInfoDiffCallback()) {

    var itemClickListener: RecyclerViewItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHoder: RecyclerView.ViewHolder
        viewHoder = MemberVH(
            TabClubSettingAllMemberItemBinding.bind(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.tab_club_setting_all_member_item, parent, false)
            )
        )
        return viewHoder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            (holder as MemberVH).bind(it)
        }
    }

    inner class MemberVH(val binding: TabClubSettingAllMemberItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(memberInfoMeta: ClubMemberInfoWithMeta) {
            val memberInfo = memberInfoMeta.clubMemberInfo
            if (!memberInfo.profileImg.isNullOrEmpty()) {
                setProfileAvatar(
                    binding.ivProfile,
                    ImageUtils.getImageFullUriForCloudFlare(memberInfo.profileImg, true)
                )
            }
            binding.tvNick.text = memberInfo.nickname
            binding.tvMemberGrade.let {
                it.text =
                    if (memberInfo.memberLevel == ConstVariable.ClubDef.CLUB_MEMBERSHIP_LV_MANAGER) it.context.getString(
                        R.string.k_club_president
                    ) else it.context.getString(R.string.a_general_membership)
            }
            try {
                binding.tvMemberJoinDate.text = SimpleDateFormat("yyyy.MM.dd").format(
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(memberInfo.createDate)
                )
            } catch (e: Exception) {
                Timber.e("TimeFormat Exception ${e.printStackTrace()}")
                binding.tvMemberJoinDate.text = memberInfo.createDate
            }
            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(memberInfo)
            }
        }
    }
}

class ClubMemberInfoDiffCallback : DiffUtil.ItemCallback<ClubMemberInfoWithMeta>() {
    override fun areItemsTheSame(
        oldItem: ClubMemberInfoWithMeta,
        newItem: ClubMemberInfoWithMeta
    ): Boolean {
        return oldItem.clubMemberInfo.memberId == newItem.clubMemberInfo.memberId
    }

    override fun areContentsTheSame(
        oldItem: ClubMemberInfoWithMeta,
        newItem: ClubMemberInfoWithMeta
    ): Boolean {
        return oldItem == newItem
    }
}

