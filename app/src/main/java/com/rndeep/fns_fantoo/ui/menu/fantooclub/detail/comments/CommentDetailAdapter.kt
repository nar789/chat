package com.rndeep.fns_fantoo.ui.menu.fantooclub.detail.comments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.FantooClubComment
import com.rndeep.fns_fantoo.databinding.*
import com.rndeep.fns_fantoo.utils.TimeUtils
import com.rndeep.fns_fantoo.utils.getImageUrlFromCDN
import com.rndeep.fns_fantoo.utils.setProfileAvatar
import timber.log.Timber

class CommentDetailAdapter(
    private val onItemClicked: (FantooClubComment, CommentsClickType, Int) -> Unit
) : ListAdapter<FantooClubComment, RecyclerView.ViewHolder>(CommentsDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_COMMENT -> {
            val binding =
                ListItemCommentDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            CommentsViewHolder(binding)
        }
        TYPE_COMMENT_REPLY -> {
            val binding =
                ListItemCommentReplyDetailBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            CommentsReplyViewHolder(binding)
        }
        TYPE_COMMENT_REPORTED -> {
            val binding =
                ListItemCommentsReportedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            CommentsReportedViewHolder(binding)
        }
        TYPE_COMMENT_DELETED -> {
            val binding =
                ListItemCommentsDeletedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            CommentsDeletedViewHolder(binding)
        }
        TYPE_COMMENT_BLOCK -> {
            val binding =
                ListItemCommentsBlockBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            CommentsBlockViewHolder(binding)
        }
        else -> {
            throw IllegalStateException("Not Found ViewHolder Type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is CommentsViewHolder -> holder.bind(item)
            is CommentsReplyViewHolder -> holder.bind(item)
            is CommentsReportedViewHolder -> holder.bind(item)
            is CommentsDeletedViewHolder -> holder.bind(item)
            is CommentsBlockViewHolder -> holder.bind(item)
        }
    }

    override fun getItemViewType(position: Int) = when (getViewType(getItem(position))) {
        0 -> TYPE_COMMENT
        1 -> TYPE_COMMENT_REPLY
        2 -> TYPE_COMMENT_REPORTED
        3 -> TYPE_COMMENT_DELETED
        4 -> TYPE_COMMENT_BLOCK
        else -> {
            throw IllegalStateException("Not Found Item View Type ${getItem(position).status}")
        }
    }

    inner class CommentsViewHolder(private val binding: ListItemCommentDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FantooClubComment) {
            binding.apply {
                setProfileAvatar(avatar, getImageUrlFromCDN(root.context, item.profileImg))
                nickname.text = item.nickname
                createdAt.text = TimeUtils.diffTimeWithCurrentTime(item.createDate)

                item.translatedContent?.let {
                    body.text = it
                    translate.text = root.context.getString(R.string.b_do_translate_cancel)
                } ?: kotlin.run {
                    body.text = item.content
                    translate.text = root.context.getString(R.string.b_do_translate)
                }
                Timber.d("item.translatedContent: ${item.translatedContent}")

                if (item.attaches.isNotEmpty()) {
                    binding.commentImg.visibility = View.VISIBLE
                    Glide.with(binding.root.context)
                        .load(getImageUrlFromCDN(root.context, item.attaches[0].attach))
                        .into(commentImg)
                }

                item.like.likeYn?.let { isLike ->
                    if (isLike) {
                        binding.like.setColorFilter(root.context.getColor(R.color.state_active_gray_700))
                        binding.dislike.setColorFilter(root.context.getColor(R.color.state_disabled_gray_200))
                    } else {
                        binding.dislike.setColorFilter(root.context.getColor(R.color.state_active_gray_700))
                        binding.like.setColorFilter(root.context.getColor(R.color.state_disabled_gray_200))
                    }
                } ?: kotlin.run {
                    binding.like.setColorFilter(root.context.getColor(R.color.state_disabled_gray_200))
                    binding.dislike.setColorFilter(root.context.getColor(R.color.state_disabled_gray_200))
                }
                val count = item.like.likeCount - item.like.dislikeCount
                likeCount.text = count.toString()

                commentCount.text = item.replyCount.toString()

                more.setOnClickListener {
                    onItemClicked(item, CommentsClickType.MORE, bindingAdapterPosition)
                }

                translate.setOnClickListener {
                    onItemClicked(item, CommentsClickType.TRANSLATE, bindingAdapterPosition)
                }

                like.setOnClickListener {
                    onItemClicked(item, CommentsClickType.LIKE, bindingAdapterPosition)
                }

                dislike.setOnClickListener {
                    onItemClicked(item, CommentsClickType.DISLIKE, bindingAdapterPosition)
                }
            }
        }
    }

    inner class CommentsReplyViewHolder(private val binding: ListItemCommentReplyDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FantooClubComment) {
            binding.apply {
                setProfileAvatar(avatar, getImageUrlFromCDN(root.context, item.profileImg))
                nickname.text = item.nickname
                createdAt.text = TimeUtils.diffTimeWithCurrentTime(item.createDate)

                item.translatedContent?.let {
                    body.text = it
                    translate.text = root.context.getString(R.string.b_do_translate_cancel)
                } ?: kotlin.run {
                    body.text = item.content
                    translate.text = root.context.getString(R.string.b_do_translate)
                }
                Timber.d("item.translatedContent: ${item.translatedContent}")

                if (item.attaches.isNotEmpty()) {
                    binding.commentImg.visibility = View.VISIBLE
                    Glide.with(binding.root.context)
                        .load(getImageUrlFromCDN(root.context, item.attaches[0].attach))
                        .into(commentImg)
                }

                item.like.likeYn?.let { isLike ->
                    if (isLike) {
                        binding.like.setColorFilter(root.context.getColor(R.color.state_active_gray_700))
                        binding.dislike.setColorFilter(root.context.getColor(R.color.state_disabled_gray_200))
                    } else {
                        binding.dislike.setColorFilter(root.context.getColor(R.color.state_active_gray_700))
                        binding.like.setColorFilter(root.context.getColor(R.color.state_disabled_gray_200))
                    }
                } ?: kotlin.run {
                    binding.like.setColorFilter(root.context.getColor(R.color.state_disabled_gray_200))
                    binding.dislike.setColorFilter(root.context.getColor(R.color.state_disabled_gray_200))
                }
                val count = item.like.likeCount - item.like.dislikeCount
                likeCount.text = count.toString()

                more.setOnClickListener {
                    onItemClicked(item, CommentsClickType.MORE, bindingAdapterPosition)
                }

                translate.setOnClickListener {
                    onItemClicked(item, CommentsClickType.TRANSLATE, bindingAdapterPosition)
                }

                like.setOnClickListener {
                    onItemClicked(item, CommentsClickType.LIKE, bindingAdapterPosition)
                }

                dislike.setOnClickListener {
                    onItemClicked(item, CommentsClickType.DISLIKE, bindingAdapterPosition)
                }
            }
        }
    }

    inner class CommentsReportedViewHolder(private val binding: ListItemCommentsReportedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FantooClubComment) {
            binding.apply {

            }
        }
    }

    inner class CommentsDeletedViewHolder(private val binding: ListItemCommentsDeletedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FantooClubComment) {
            binding.apply {

            }
        }
    }

    inner class CommentsBlockViewHolder(private val binding: ListItemCommentsBlockBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FantooClubComment) {
            binding.apply {
                setProfileAvatar(avatar, getImageUrlFromCDN(root.context, item.profileImg))
                nickname.text = item.nickname
                createdAt.text = TimeUtils.diffTimeWithCurrentTime(item.createDate)

                item.like.likeYn?.let { isLike ->
                    if (isLike) {
                        binding.like.setColorFilter(root.context.getColor(R.color.state_active_gray_700))
                        binding.dislike.setColorFilter(root.context.getColor(R.color.state_disabled_gray_200))
                    } else {
                        binding.dislike.setColorFilter(root.context.getColor(R.color.state_active_gray_700))
                        binding.like.setColorFilter(root.context.getColor(R.color.state_disabled_gray_200))
                    }
                } ?: kotlin.run {
                    binding.like.setColorFilter(root.context.getColor(R.color.state_disabled_gray_200))
                    binding.dislike.setColorFilter(root.context.getColor(R.color.state_disabled_gray_200))
                }
                val count = item.like.likeCount - item.like.dislikeCount
                likeCount.text = count.toString()

                more.setOnClickListener {
                    onItemClicked(item, CommentsClickType.MORE, bindingAdapterPosition)
                }

                like.setOnClickListener {
                    onItemClicked(item, CommentsClickType.LIKE, bindingAdapterPosition)
                }

                dislike.setOnClickListener {
                    onItemClicked(item, CommentsClickType.DISLIKE, bindingAdapterPosition)
                }
            }
        }
    }

    private fun getViewType(item: FantooClubComment) = when {
        // status 0: normal , 1: report, 2: delete, 4: block
        item.status == 0 && item.depth == 2 -> {
            TYPE_COMMENT_REPLY
        }
        item.status == 1 -> {
            TYPE_COMMENT_REPORTED
        }
        item.status == 2 -> {
            TYPE_COMMENT_DELETED
        }
        item.status == 4 -> {
            TYPE_COMMENT_BLOCK
        }
        else -> TYPE_COMMENT
    }

    companion object {
        private const val TYPE_COMMENT = 0
        private const val TYPE_COMMENT_REPLY = 1
        private const val TYPE_COMMENT_REPORTED = 2
        private const val TYPE_COMMENT_DELETED = 3
        private const val TYPE_COMMENT_BLOCK = 4

        private val CommentsDiff = object : DiffUtil.ItemCallback<FantooClubComment>() {
            override fun areItemsTheSame(
                oldItem: FantooClubComment,
                newItem: FantooClubComment
            ): Boolean {
                return oldItem.replyId == newItem.replyId
            }

            override fun areContentsTheSame(
                oldItem: FantooClubComment,
                newItem: FantooClubComment
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}