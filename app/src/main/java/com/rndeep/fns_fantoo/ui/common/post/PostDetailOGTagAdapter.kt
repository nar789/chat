package com.rndeep.fns_fantoo.ui.common.post

import android.content.Intent
import android.net.Uri
import android.util.Patterns
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.community.PostDetailOpenGraphTag
import com.rndeep.fns_fantoo.databinding.PostDetailOgtagLayoutBinding
import com.rndeep.fns_fantoo.utils.ImageUtils
import timber.log.Timber

class PostDetailOGTagAdapter : ListAdapter<PostDetailOpenGraphTag, PostDetailOGTagAdapter.PostDetailOGTagVH>(diff) {

    inner class PostDetailOGTagVH(private val binding : PostDetailOgtagLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(ogTag: PostDetailOpenGraphTag){
            binding.tvOGTagTitle.text = ogTag.title
            binding.tvOGTagLink.text = binding.root.context.getString(R.string.c_source)+ogTag.url
            Glide.with(binding.root.context)
                .load(ogTag.image)
                .into(binding.ivOGTagImage)
            binding.root.setOnClickListener {
                try {
                    if(Patterns.WEB_URL.matcher(ogTag.url).matches()) {
                        binding.root.context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(ogTag.url)
                            )
                        )
                    }
                }catch (e:Exception){
                    Timber.e("${e.printStackTrace()}")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostDetailOGTagVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_detail_ogtag_layout, parent,false)
        return PostDetailOGTagVH(PostDetailOgtagLayoutBinding.bind(view))
    }

    override fun onBindViewHolder(holder: PostDetailOGTagVH, position: Int) {
        holder.bind(currentList[position])
    }

    companion object{
        val diff = object : DiffUtil.ItemCallback<PostDetailOpenGraphTag>(){
            override fun areItemsTheSame(
                oldItem: PostDetailOpenGraphTag,
                newItem: PostDetailOpenGraphTag
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: PostDetailOpenGraphTag,
                newItem: PostDetailOpenGraphTag
            ): Boolean {
                return (oldItem.url==newItem.url)
            }

        }
    }
}