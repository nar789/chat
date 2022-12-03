package com.rndeep.fns_fantoo.ui.club.detail.archive

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.ui.common.post.BlankErrorVH
import com.rndeep.fns_fantoo.databinding.BlankLayoutForErrorBinding
import com.rndeep.fns_fantoo.databinding.TabClubPageArchiveImageListLayoutBinding
import com.rndeep.fns_fantoo.databinding.TabClubPageArchiveTopNameLayoutBinding
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostData
import com.rndeep.fns_fantoo.ui.club.detail.ClubPageFragmentDirections
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.setImageWithPlaceHolder

class ArchiveImageAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TITLETYPE = 0
    private val IMAGETYPE = 1

    private var imageList = listOf<ClubPostData>()

    private var categoryName =""

    private var archiveNameClickListener :ArchiveTopTypeClickListener? = null

    fun setOnArchiveNameClickListener(listener:ArchiveTopTypeClickListener){
        this.archiveNameClickListener=listener
    }

    inner class ArchiveImageVH(private val binding: TabClubPageArchiveImageListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(archiveItem: ClubPostData) {
            setImageWithPlaceHolder(
                binding.ivArchiveImage,itemView.context.getString(R.string.imageUrlBase,archiveItem.firstImage)
            )
            itemView.setOnClickListener {
                val action =
                    ClubPageFragmentDirections.actionClubDetailPageFragmentToClubPost(
                        ConstVariable.TYPE_CLUB,archiveItem.categoryCode,archiveItem.clubId,archiveItem.postId)
                itemView.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TITLETYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tab_club_page_archive_top_name_layout, parent, false)
                ArchiveNameVH(TabClubPageArchiveTopNameLayoutBinding.bind(view),ConstVariable.ARCHIVEIMAGETYPE,archiveNameClickListener)
            }
            IMAGETYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tab_club_page_archive_image_list_layout, parent, false)
                ArchiveImageVH(TabClubPageArchiveImageListLayoutBinding.bind(view))
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.blank_layout_for_error, parent, false)
                BlankErrorVH(BlankLayoutForErrorBinding.bind(view))
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ArchiveImageVH -> {
                holder.bind(imageList[holder.bindingAdapterPosition-1])
            }
            is ArchiveNameVH -> {
                holder.bind(categoryName)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            RecyclerView.NO_POSITION->{
                -1
            }
            0->{
                TITLETYPE
            }
            else ->{
                IMAGETYPE
            }
        }
    }

    override fun getItemCount() = imageList.size+1

    fun setUrlItem(items: List<ClubPostData>) {
        imageList = items
    }

    fun setArchiveCategoryName(categoryName :String){
        this.categoryName=categoryName
    }
    interface ArchiveTopTypeClickListener{
        fun onListClick()
    }
}