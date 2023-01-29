package com.rndeep.fns_fantoo.ui.club.settings.tabs.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.dto.ClubStorageReplyListWithMeta
import com.rndeep.fns_fantoo.data.remote.model.CommentItem
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingMemberCommentItemBinding
import com.rndeep.fns_fantoo.databinding.TabClubSettingBoxPostEmptyItemBinding
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.TimeUtils
import com.rndeep.fns_fantoo.utils.setProfileAvatar

class CommentAdapter(val clickListener: (ClubStorageReplyListWithMeta) -> Unit) :
    PagingDataAdapter<ClubStorageReplyListWithMeta, RecyclerView.ViewHolder>(
        CommentItemDiffUtilCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_club_setting_member_comment_item,
            parent, false
        )
        return CommentVH(FragmentClubSettingMemberCommentItemBinding.bind(view))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            (holder as CommentVH).bind(it)
        }
    }

    inner class CommentVH(val binding: FragmentClubSettingMemberCommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(dataObj: ClubStorageReplyListWithMeta) {
            val replyData = dataObj.clubStorageReplyDto
            replyData?.let {
                val profileImgUrl = replyData.profileImg
                if (!profileImgUrl.isNullOrEmpty()) {
                    setProfileAvatar(binding.ivProfile, profileImgUrl)
                }
                binding.tvBoardName.text = replyData.categoryName1 ?: ""
                binding.tvNickName.text = replyData.nickname
                binding.tvPostDate.text = TimeUtils.diffTimeWithCurrentTime(replyData.createDate)
                when(ConstVariable.BlockType.compare(replyData.blockType)){
                    ConstVariable.BlockType.COMMENT ->{
                        binding.tvComment.apply {
                            text = context.getString(R.string.se_c_blocked_comment)
                        }
                    }
                    ConstVariable.BlockType.MEMBER ->{
                        binding.tvComment.apply {
                            text = context.getString(R.string.se_c_comment_of_blocked_user)
                        }
                    }
                    ConstVariable.BlockType.POST, ConstVariable.BlockType.NONE ->{
                        when(ConstVariable.CommentStatus.compare(replyData.status)){
                            ConstVariable.CommentStatus.REPORTED ->{
                                binding.tvComment.apply {
                                    text = context.getString(R.string.se_s_comment_hide_by_report)
                                }
                            }
                            ConstVariable.CommentStatus.DELETED ->{
                                binding.tvComment.apply {
                                    text = context.getString(R.string.se_j_deleted_comment_by_writer)
                                }
                            }
                            ConstVariable.CommentStatus.NONE ->{
                                binding.tvComment.text = replyData.content
                                binding.tvPostTitle.text = replyData.subject
                            }
                        }
                    }
                }
                binding.root.setOnClickListener {
                    clickListener(dataObj)
                }
            }
        }
    }

    class EmptyVH(val binding: TabClubSettingBoxPostEmptyItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.tvEmpty.text = binding.root.context.getString(R.string.se_j_no_write_comment)
        }
    }
}

class CommentItemDiffUtilCallback : DiffUtil.ItemCallback<ClubStorageReplyListWithMeta>() {
    override fun areContentsTheSame(
        oldItem: ClubStorageReplyListWithMeta,
        newItem: ClubStorageReplyListWithMeta
    ): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(
        oldItem: ClubStorageReplyListWithMeta,
        newItem: ClubStorageReplyListWithMeta
    ): Boolean {
        return oldItem.clubStorageReplyDto?.replyId == newItem.clubStorageReplyDto?.replyId
    }
}