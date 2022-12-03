package com.rndeep.fns_fantoo.ui.editor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.rndeep.fns_fantoo.databinding.ListItemEditorImageBinding
import com.rndeep.fns_fantoo.databinding.ListItemEditorVideoBinding
import timber.log.Timber


class MultimediaAdapter(
    private val onDeleteClicked: (MultimediaItem) -> Unit
) : ListAdapter<MultimediaItem, RecyclerView.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_IMAGE -> {
            Timber.d("onCreateViewHolder")
            val binding =
                ListItemEditorImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ImageViewHolder(binding)
        }
        TYPE_VIDEO -> {
            val binding =
                ListItemEditorVideoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            VideoViewHolder(binding)
        }
        else -> {
            throw IllegalStateException("Not Found ViewHolder Type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        Timber.d("onBindViewHolder : item : $item")
        when (holder) {
            is ImageViewHolder -> holder.bind(item)
            is VideoViewHolder -> holder.bind(item)
        }
    }

    override fun getItemViewType(position: Int) = when (getItem(position).type) {
        MultimediaType.IMAGE -> TYPE_IMAGE
        else -> TYPE_VIDEO
    }

    inner class ImageViewHolder(private val binding: ListItemEditorImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MultimediaItem) {
            Timber.d("ImageViewHolder : item : $item")
            binding.apply {
                val option = MultiTransformation(CenterCrop(), RoundedCorners(24))
                Glide.with(root.context)
                    .load(item.url)
                    .centerCrop()
                    .apply(RequestOptions.bitmapTransform(option))
                    .into(image)

                deleteBadge.setOnClickListener {
                    onDeleteClicked(item)
                }
            }
        }
    }

    inner class VideoViewHolder(private val binding: ListItemEditorVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MultimediaItem) {
            Timber.d("VideoViewHolder : item : $item")
            binding.apply {
                val option = MultiTransformation(CenterCrop(), RoundedCorners(24))
                Glide.with(root.context)
                    .load(item.url)
                    .centerCrop()
                    .apply(RequestOptions.bitmapTransform(option))
                    .into(videoThumbnail)

                deleteBadge.setOnClickListener {
                    onDeleteClicked(item)
                }
            }
        }
    }

    companion object {
        private const val TYPE_IMAGE = 0
        private const val TYPE_VIDEO = 1

        private val DiffCallback = object : DiffUtil.ItemCallback<MultimediaItem>() {
            override fun areItemsTheSame(
                oldItem: MultimediaItem,
                newItem: MultimediaItem
            ): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(
                oldItem: MultimediaItem,
                newItem: MultimediaItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}