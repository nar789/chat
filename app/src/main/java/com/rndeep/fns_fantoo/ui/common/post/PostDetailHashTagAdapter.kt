package com.rndeep.fns_fantoo.ui.common.post

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.PostDetailHashtagLayoutBinding

class PostDetailHashTagAdapter : RecyclerView.Adapter<PostDetailHashTagAdapter.ClubSortListVH>() {
    private var clubSortList= listOf<String>()
    inner class ClubSortListVH(private val binding: PostDetailHashtagLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(item:String){
            if(item=="")return
            binding.tvSortText.text="# ${item}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubSortListVH {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.post_detail_hashtag_layout,parent,false)
        return ClubSortListVH(PostDetailHashtagLayoutBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ClubSortListVH, position: Int) {
        if(holder.bindingAdapterPosition== RecyclerView.NO_POSITION){
            return
        }
        holder.bind(clubSortList[holder.bindingAdapterPosition])
    }

    override fun getItemCount()=clubSortList.size

    fun setHashTag(items : List<String>){
        clubSortList=items
    }

}