package com.rndeep.fns_fantoo.ui.club.search

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.club.ClubSearchItem
import com.rndeep.fns_fantoo.databinding.TabClubSearchHotClubLlistBinding
import com.rndeep.fns_fantoo.utils.setProfileAvatar
import timber.log.Timber

class ClubSearchHotClubVH(private val binding: TabClubSearchHotClubLlistBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun hotClubBind(item: ClubSearchItem, listener : ClubSearchListAdapter.OnClubClickListener?) {
        setProfileAvatar(binding.ivClubImage,itemView.context.getString(R.string.imageThumbnailUrl,item.profileImg))

        binding.tvClubName.text = item.clubName
        var tags = ""
        item.hashtagList?.let {
            for (a in it) {
                tags += "#${a} "
            }
        }
        binding.tvClubTag.text = tags
        if (item.memberCountOpenYn) {
            binding.llMemberCountContainer.visibility = View.VISIBLE
            binding.tvMemberCount.text = itemView.context.getString(
                R.string.club_search_member_count,
                item.memberCount
            )
        } else {
            binding.llMemberCountContainer.visibility = View.GONE
        }

        itemView.setOnClickListener{
            listener?.onClubClick(itemView,item.clubId)
        }
    }
}