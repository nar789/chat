package com.rndeep.fns_fantoo.ui.club.settings.tabs.adapter

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.style.LeadingMarginSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingMemberSaveItemBinding
import com.rndeep.fns_fantoo.databinding.TabClubSettingBoxPostEmptyItemBinding
import com.rndeep.fns_fantoo.data.remote.dto.ClubStoragePostListWithMeta
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.SizeUtils
import com.rndeep.fns_fantoo.utils.TimeUtils
import com.rndeep.fns_fantoo.utils.setProfileAvatar

private const val ATTACH_ICON_WIDTH_WITH_MARGIN = 24f // 4+16+4
private const val NON_ATTACH_ICON_MARGIN = 4f

class SaveAdapter(val clickListener: (ClubStoragePostListWithMeta) -> Unit) :
    PagingDataAdapter<ClubStoragePostListWithMeta, RecyclerView.ViewHolder>(SaveItemDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_club_setting_member_save_item,
            parent, false
        )
        return SaveVH(FragmentClubSettingMemberSaveItemBinding.bind(view))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            (holder as SaveVH).bind(it)
        }
    }


    inner class SaveVH(val binding: FragmentClubSettingMemberSaveItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(dataObj: ClubStoragePostListWithMeta) {
            val postInfo = dataObj.postDto
            postInfo?.let {
                val profileImgUrl = postInfo.profileImg
                if (profileImgUrl.isNotEmpty()) {
                    setProfileAvatar(binding.ivProfile, profileImgUrl)
                }
                binding.tvBoardName.text = postInfo.categoryName1
                binding.tvNickName.text = postInfo.nickname
                binding.tvPostDate.text = TimeUtils.diffTimeWithCurrentTime(postInfo.createDate)
                binding.tvHeadWord.text = postInfo.hashtagList?.toString()
                val hasAttachContent = postInfo.attachList?.isNotEmpty() == true
                binding.ivAttachIcon.visibility = if (hasAttachContent) View.VISIBLE else View.GONE
                val headWordTextWidth =
                    binding.tvHeadWord.paint.measureText(binding.tvHeadWord.text.toString()).toInt()
                val attachIconWidth = SizeUtils.getDpValue(
                    if (hasAttachContent) ATTACH_ICON_WIDTH_WITH_MARGIN else NON_ATTACH_ICON_MARGIN,
                    binding.root.context
                ).toInt()
                val leadingMargin = headWordTextWidth + attachIconWidth
                postInfo.subject?.let{
                    binding.tvPostContent.text = SpannableString(it)
                        .apply {
                            setSpan(LeadingMarginSpan.Standard(leadingMargin, 1), 0, 1, 0)
                        }
                }
                binding.tvHonorCount.text = postInfo.honorCount?.let { it.toString() }
                binding.tvCommentCount.text = postInfo.replyCount?.let { it.toString() }
                binding.root.setOnClickListener {
                    clickListener(dataObj)
                }
                if (!ConstVariable.Config.FEATURE_HONOR) {
                    binding.ivHonorIcon.visibility = View.INVISIBLE
                    binding.tvHonorCount.visibility = View.INVISIBLE
                }
            }
        }
    }

    class EmptyVH(val binding: TabClubSettingBoxPostEmptyItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.tvEmpty.text = binding.root.context.getString(R.string.se_j_no_save_post)
        }
    }
}

class SaveItemDiffUtilCallback : DiffUtil.ItemCallback<ClubStoragePostListWithMeta>() {
    override fun areContentsTheSame(
        oldItem: ClubStoragePostListWithMeta,
        newItem: ClubStoragePostListWithMeta
    ): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(
        oldItem: ClubStoragePostListWithMeta,
        newItem: ClubStoragePostListWithMeta
    ): Boolean {
        return oldItem.postDto?.postId == newItem.postDto?.postId
    }
}