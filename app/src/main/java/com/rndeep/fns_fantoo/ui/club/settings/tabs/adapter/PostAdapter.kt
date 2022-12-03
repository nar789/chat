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
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingMemberPostItemBinding
import com.rndeep.fns_fantoo.databinding.TabClubSettingBoxPostEmptyItemBinding
import com.rndeep.fns_fantoo.data.remote.dto.ClubStoragePostListWithMeta
import com.rndeep.fns_fantoo.utils.*

private const val ATTACH_ICON_WIDTH_WITH_MARGIN = 24f // 4+16+4
private const val NON_ATTACH_ICON_MARGIN = 4f

class PostAdapter(val clickListener: (ClubStoragePostListWithMeta) -> Unit) :
    PagingDataAdapter<ClubStoragePostListWithMeta, RecyclerView.ViewHolder>(PostInfoDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_club_setting_member_post_item,
            parent, false
        )
        return PostVH(FragmentClubSettingMemberPostItemBinding.bind(view))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            (holder as PostVH).bind(it)
        }
    }


    inner class PostVH(val binding: FragmentClubSettingMemberPostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dataObj: ClubStoragePostListWithMeta) {
            val postInfo = dataObj.postDto
            postInfo?.let {
                val profileUrl = postInfo.profileImg
                if (profileUrl.isNotEmpty()) {
                    setProfileAvatar(binding.ivProfile, profileUrl)
                }
                binding.tvBoardName.text = postInfo.categoryName1
                binding.tvNickName.text = postInfo.nickname
                binding.tvPostDate.text = TimeUtils.diffTimeWithCurrentTime(postInfo.createDate)

                postInfo.subject?.let {
                    when(ConstVariable.BlockType.compare(postInfo.blockType)){//(0:없음, 1:멤버, 2:게시글)
                        ConstVariable.BlockType.MEMBER->{
                            binding.tvPostContent.apply {
                                text = context.getString(R.string.se_c_post_of_blocked_user)
                            }
                        }
                        ConstVariable.BlockType.POST->{
                            binding.tvPostContent.apply {
                                text = context.getString(R.string.se_c_blocked_post)
                            }
                        }
                        ConstVariable.BlockType.COMMENT, ConstVariable.BlockType.NONE -> {
                            if (postInfo.status == 1) {//상태 (0:정상, 1:신고, 2:삭제, 3:클럽장 삭제)
                                binding.tvPostContent.apply {
                                    text = context.getString(R.string.se_s_post_hide_by_report)
                                }
                            } else {
                                binding.tvHeadWord.apply {
                                    visibility = View.VISIBLE
                                    text = postInfo.hashtagList?.toString()//"[말머리]"
                                }
                                val hasAttachContent: Boolean =
                                    postInfo.attachList?.isNotEmpty() == true
                                binding.ivAttachIcon.visibility =
                                    if (hasAttachContent) View.VISIBLE else View.GONE
                                val headWordTextWidth =
                                    binding.tvHeadWord.paint.measureText(binding.tvHeadWord.text.toString())
                                        .toInt()
                                val attachIconWidth = SizeUtils.getDpValue(
                                    if (hasAttachContent) ATTACH_ICON_WIDTH_WITH_MARGIN else NON_ATTACH_ICON_MARGIN,
                                    binding.root.context
                                ).toInt()
                                val leadingMargin = headWordTextWidth + attachIconWidth
                                binding.tvPostContent.text = SpannableString(it)
                                    .apply {
                                        setSpan(
                                            LeadingMarginSpan.Standard(leadingMargin, 1),
                                            0,
                                            1,
                                            0
                                        )
                                    }
                            }
                        }
                    }
                }
                binding.tvHonorCount.text = postInfo.honorCount.toString()
                binding.tvCommentCount.text = postInfo.replyCount.toString()
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
            binding.tvEmpty.text = binding.root.context.getString(R.string.se_j_no_write_post)
        }
    }

}

class PostInfoDiffUtilCallback : DiffUtil.ItemCallback<ClubStoragePostListWithMeta>() {
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
