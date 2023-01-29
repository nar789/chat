package com.rndeep.fns_fantoo.ui.menu.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityMyReply
import com.rndeep.fns_fantoo.databinding.ListItemLibraryCommentBinding
import com.rndeep.fns_fantoo.utils.TimeUtils
import com.rndeep.fns_fantoo.utils.getImageUrlFromCDN
import com.rndeep.fns_fantoo.utils.setProfileAvatar

class LibraryCommunityCommentAdapter(
    private val onItemClicked: (CommunityMyReply) -> Unit
) : ListAdapter<CommunityMyReply, RecyclerView.ViewHolder>(CommentDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ListItemLibraryCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LibraryCommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        (holder as LibraryCommentViewHolder).bind(item)
    }

    inner class LibraryCommentViewHolder(private val binding: ListItemLibraryCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CommunityMyReply) {
            binding.apply {

                setProfileAvatar(img, getImageUrlFromCDN(root.context, item.userPhoto))

                binding.title.text = item.code
                binding.nickname.text = item.userNick
                binding.time.text = TimeUtils.diffTimeWithCurrentTime(item.createDate)
                binding.comment.text = item.content

                if(item.attachList.isNotEmpty()) {
                    binding.commentImg.visibility = View.VISIBLE
                    Glide.with(binding.root.context)
                        .load(getImageUrlFromCDN(root.context, item.attachList[0].id))
                        .into(commentImg)
                }
                binding.originalContents.text = item.postTitle

                root.setOnClickListener {
                    onItemClicked(item)
                }
            }
        }
    }

    companion object {
        private val CommentDiff = object : DiffUtil.ItemCallback<CommunityMyReply>() {
            override fun areItemsTheSame(oldItem: CommunityMyReply, newItem: CommunityMyReply): Boolean {
                return oldItem.replyId == newItem.replyId
            }

            override fun areContentsTheSame(oldItem: CommunityMyReply, newItem: CommunityMyReply): Boolean {
                return oldItem == newItem
            }
        }
    }
}