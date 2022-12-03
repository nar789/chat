package com.rndeep.fns_fantoo.ui.menu.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rndeep.fns_fantoo.data.remote.dto.ClubStorageReplyDto
import com.rndeep.fns_fantoo.databinding.ListItemLibraryCommentBinding
import com.rndeep.fns_fantoo.utils.TimeUtils
import com.rndeep.fns_fantoo.utils.getImageUrlFromCDN
import com.rndeep.fns_fantoo.utils.setProfileAvatar

class LibraryClubCommentAdapter(
    private val onItemClicked: (ClubStorageReplyDto) -> Unit
) : ListAdapter<ClubStorageReplyDto, RecyclerView.ViewHolder>(CommentDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ListItemLibraryCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return LibraryCommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        (holder as LibraryCommentViewHolder).bind(item)
    }

    inner class LibraryCommentViewHolder(private val binding: ListItemLibraryCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ClubStorageReplyDto) {
            binding.apply {

                setProfileAvatar(img, getImageUrlFromCDN(root.context, item.profileImg))

                binding.title.text = item.clubName
                binding.nickname.text = item.nickname
                binding.time.text = TimeUtils.diffTimeWithCurrentTime(item.createDate)
                binding.comment.text = item.content

                if (item.attachList.isNotEmpty()) {
                    binding.commentImg.visibility = View.VISIBLE
                    Glide.with(binding.root.context)
                        .load(item.attachList[0].attach)
                        .into(commentImg)

                }

                binding.originalContents.text = item.subject

                root.setOnClickListener {
                    onItemClicked(item)
                }
            }
        }
    }

    companion object {
        private val CommentDiff = object : DiffUtil.ItemCallback<ClubStorageReplyDto>() {
            override fun areItemsTheSame(
                oldItem: ClubStorageReplyDto,
                newItem: ClubStorageReplyDto
            ): Boolean {
                return oldItem.clubId == newItem.clubId
            }

            override fun areContentsTheSame(
                oldItem: ClubStorageReplyDto,
                newItem: ClubStorageReplyDto
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}