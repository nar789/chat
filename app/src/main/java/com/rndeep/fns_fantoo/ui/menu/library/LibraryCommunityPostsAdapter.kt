package com.rndeep.fns_fantoo.ui.menu.library


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityMyPost
import com.rndeep.fns_fantoo.databinding.ListItemLibraryPostsBinding
import com.rndeep.fns_fantoo.utils.TimeUtils
import com.rndeep.fns_fantoo.utils.getImageUrlFromCDN
import com.rndeep.fns_fantoo.utils.setProfileAvatar

class LibraryCommunityPostsAdapter(
    private val onItemClicked: (CommunityMyPost) -> Unit
) : ListAdapter<CommunityMyPost, RecyclerView.ViewHolder>(CommunityMyPostDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ListItemLibraryPostsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LibraryPostsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        (holder as LibraryPostsViewHolder).bind(item)
    }

    inner class LibraryPostsViewHolder(private val binding: ListItemLibraryPostsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CommunityMyPost) {
            binding.apply {

                setProfileAvatar(img, getImageUrlFromCDN(root.context, item.userPhoto))

                binding.title.text = item.title
                binding.nickname.text = item.userNick
                binding.time.text = TimeUtils.diffTimeWithCurrentTime(item.createDate)

                binding.contents.text = item.content
                binding.likeCount.text = item.likeCnt.toString()
                binding.honorCount.text = item.honorCnt.toString()
                binding.commentCount.text = item.replyCnt.toString()

                val likeColor = if(item.likeYn) {
                    root.context.getColor(R.color.state_enable_primary_default)
                } else {
                    root.context.getColor(R.color.state_enable_gray_200)
                }
                binding.like.setColorFilter(likeColor)
                binding.likeCount.setTextColor(likeColor)

                val dislikeColor = if (item.dislikeYn) {
                    root.context.getColor(R.color.state_active_gray_700)
                } else {
                    root.context.getColor(R.color.state_disabled_gray_200)
                }
                binding.dislike.setColorFilter(dislikeColor)

                root.setOnClickListener {
                    onItemClicked(item)
                }
            }
        }
    }

    companion object {
        private val CommunityMyPostDiff = object : DiffUtil.ItemCallback<CommunityMyPost>() {
            override fun areItemsTheSame(oldItem: CommunityMyPost, newItem: CommunityMyPost): Boolean {
                return oldItem.postId == newItem.postId
            }

            override fun areContentsTheSame(oldItem: CommunityMyPost, newItem: CommunityMyPost): Boolean {
                return oldItem == newItem
            }
        }
    }
}