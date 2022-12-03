package com.rndeep.fns_fantoo.ui.club.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TabClubPageArchiveSmallImageLayoutBinding
import com.rndeep.fns_fantoo.utils.setProfileAvatar

class ClubPageArchiveSmallImageAdapter : RecyclerView.Adapter<ClubPageArchiveSmallImageAdapter.SmallImageVH>() {

    private var imageList = listOf<String>()

    inner class SmallImageVH(private val binding: TabClubPageArchiveSmallImageLayoutBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(imageUrl : String){
            setProfileAvatar(binding.ivArchiveSmallImage,imageUrl)

            binding.rlImageContainer.clipToOutline=true
            binding.rlImageContainer.clipToPadding=true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmallImageVH {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.tab_club_page_archive_small_image_layout,parent,false)
        return SmallImageVH(TabClubPageArchiveSmallImageLayoutBinding.bind(view))
    }

    override fun onBindViewHolder(holder: SmallImageVH, position: Int) {
        holder.bind(imageList[holder.bindingAdapterPosition])
    }

    override fun getItemCount()=imageList.size

    fun setImageList(items :List<String>){
        imageList=items
    }
}