package com.rndeep.fns_fantoo.ui.menu.fantooclub.contents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.FantooClubPost
import com.rndeep.fns_fantoo.databinding.ListItemContentsBinding
import com.rndeep.fns_fantoo.utils.TimeUtils
import com.rndeep.fns_fantoo.utils.getImageUrlFromCDN
import com.rndeep.fns_fantoo.utils.getVideoThumbnailFromCDN

class ContentsAdapter(
    private val onItemClicked: (FantooClubPost, ContentsClickType) -> Unit,
) : PagingDataAdapter<FantooClubPost, RecyclerView.ViewHolder>(ContentsDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ListItemContentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContentsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            (holder as ContentsViewHolder).bind(item)
        }
    }

    inner class ContentsViewHolder(private val binding: ListItemContentsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FantooClubPost) {
            binding.apply {
                category.text = item.categoryName1
                createdAt.text = TimeUtils.diffTimeWithCurrentTime(item.createDate)
                title.text = item.subject
                item.content?.let {
                    body.text = item.content
                    body.visibility = View.VISIBLE
                }
                item.like?.let { like ->
                    like.likeYn?.let {
                        if (it) {
                            binding.like.setColorFilter(root.context.getColor(R.color.state_active_gray_700))
                            binding.dislike.setColorFilter(root.context.getColor(R.color.state_disabled_gray_200))
                        } else {
                            binding.like.setColorFilter(root.context.getColor(R.color.state_disabled_gray_200))
                            binding.dislike.setColorFilter(root.context.getColor(R.color.state_active_gray_700))
                        }
                    } ?: kotlin.run {
                        binding.like.setColorFilter(root.context.getColor(R.color.state_disabled_gray_200))
                        binding.dislike.setColorFilter(root.context.getColor(R.color.state_disabled_gray_200))
                    }
                    val count = like.likeCount - like.dislikeCount
                    binding.likeCount.text = count.toString()
                } ?: run {
                    like.visibility = View.GONE
                    likeCount.visibility = View.GONE
                    dislike.visibility = View.GONE
                }

                honor.visibility = View.GONE
                honorCount.visibility = View.GONE

                if (item.clubId != CLUB_ID_HANTYU_TIMES) {
                    commentCount.text = item.replyCount.toString()
                    commentCount.visibility = View.VISIBLE
                    comment.visibility = View.VISIBLE
                    logo.setImageResource(R.drawable.ic_fantoo_tv)
                } else {
                    logo.setImageResource(R.drawable.ic_hanryu_times)
                }
                item.attachList?.let { attaches ->
                    if (attaches.isNotEmpty()) {
                        // type
                        // 0: image , 1: video
                        if (attaches[0].attachType == 0) {
                            Glide.with(root.context)
                                .load(getImageUrlFromCDN(root.context, attaches[0].attach))
                                .centerCrop()
                                .into(imageThumbnail)
                            imageThumbnail.visibility = View.VISIBLE
                            videoThumbnail.visibility = View.GONE
                            playIcon.visibility = View.GONE
                        } else {
//                        val thumbnail = getYoutubeThumbnail(it)
                            Glide.with(root.context)
                                .load(getVideoThumbnailFromCDN(root.context, attaches[0].attach))
                                .centerCrop()
                                .into(videoThumbnail)
                            videoThumbnail.visibility = View.VISIBLE
                            imageThumbnail.visibility = View.GONE
                            playIcon.visibility = View.VISIBLE
                        }
                    }
                }
                contentsContainer.setOnClickListener {
                    onItemClicked(item, ContentsClickType.ITEM)
                }
                more.setOnClickListener {
                    onItemClicked(item, ContentsClickType.MORE)
                }
                like.setOnClickListener {
                    onItemClicked(item, ContentsClickType.LIKE)
                }
                dislike.setOnClickListener {
                    onItemClicked(item, ContentsClickType.DISLIKE)
                }
                honor.setOnClickListener {
                    onItemClicked(item, ContentsClickType.HONOR)
                }

                more.visibility = View.GONE
            }
        }
    }

    companion object {
        private val ContentsDiff = object : DiffUtil.ItemCallback<FantooClubPost>() {
            override fun areItemsTheSame(oldItem: FantooClubPost, newItem: FantooClubPost): Boolean {
                return oldItem.postId == newItem.postId
            }

            override fun areContentsTheSame(oldItem: FantooClubPost, newItem: FantooClubPost): Boolean {
                return oldItem == newItem
            }
        }

        const val CLUB_ID_HANTYU_TIMES = "hanryu_times"
    }
}