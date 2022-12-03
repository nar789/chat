package com.rndeep.fns_fantoo.ui.menu.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.data.remote.dto.PostDto
import com.rndeep.fns_fantoo.databinding.ListItemLibraryClubPostBinding
import com.rndeep.fns_fantoo.utils.TimeUtils
import com.rndeep.fns_fantoo.utils.getImageUrlFromCDN
import com.rndeep.fns_fantoo.utils.setProfileAvatar

class LibraryClubPostAdapter(
    private val onItemClicked: (PostDto) -> Unit
) : ListAdapter<PostDto, RecyclerView.ViewHolder>(PostDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ListItemLibraryClubPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LibraryPostsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        (holder as LibraryPostsViewHolder).bind(item)
    }

    inner class LibraryPostsViewHolder(private val binding: ListItemLibraryClubPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PostDto) {
            binding.apply {

                setProfileAvatar(img, getImageUrlFromCDN(root.context, item.profileImg))

                binding.title.text = item.clubName
                binding.nickname.text = item.nickname
                binding.time.text = TimeUtils.diffTimeWithCurrentTime(item.createDate)

                binding.contents.text = item.subject
                binding.honorCount.text = item.honorCount.toString()
                binding.commentCount.text = item.replyCount.toString()

                root.setOnClickListener {
                    onItemClicked(item)
                }
            }
        }
    }

    companion object {
        private val PostDiff = object : DiffUtil.ItemCallback<PostDto>() {
            override fun areItemsTheSame(oldItem: PostDto, newItem: PostDto): Boolean {
                return oldItem.clubId == newItem.clubId
            }

            override fun areContentsTheSame(oldItem: PostDto, newItem: PostDto): Boolean {
                return oldItem == newItem
            }
        }
    }
}