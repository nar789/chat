package com.rndeep.fns_fantoo.ui.club.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.ClubSubCategoryItem
import com.rndeep.fns_fantoo.databinding.TabClubPageArchiveListLayoutBinding
import com.rndeep.fns_fantoo.utils.HorizontalMarginItemDecoration
import com.rndeep.fns_fantoo.utils.addSingleItemDecoRation

class ClubPageArchiveListAdapter :
    RecyclerView.Adapter<ClubPageArchiveListAdapter.ArchiveListVH>() {

    //아카이브의 리스트
    private var archiveList = listOf<ClubSubCategoryItem>()

    interface OnArchiveClickListener {
        fun onArchiveClick(v: View, postUrl: String,boardType:Int,categoryName:String)
    }

    private var archiveClick: OnArchiveClickListener? = null

    fun setOnArchiveClick(archiveClickListener: OnArchiveClickListener) {
        this.archiveClick = archiveClickListener
    }

    inner class ArchiveListVH(private val binding: TabClubPageArchiveListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val IMAGETYPE =2

        //이미지 타입의 경우에 필요한 작은 이미지 adpater
        private val smallImageAdapter = ClubPageArchiveSmallImageAdapter()
        private val itemDeco = HorizontalMarginItemDecoration(0f, 3f, itemView.context)
        fun archiveBind(
            archiveItem: ClubSubCategoryItem,
            archiveClick: OnArchiveClickListener?
        ) {
            if (archiveItem.boardType == IMAGETYPE) {
                binding.tvArchiveTypeName.visibility = View.VISIBLE
                binding.rcImageList.visibility = View.VISIBLE
                binding.tvArchiveTypeName.text = "image"

                binding.rcImageList.layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                binding.rcImageList.adapter = smallImageAdapter
                smallImageAdapter.setImageList(archiveItem.firstImageList?: listOf())
                binding.rcImageList.addSingleItemDecoRation(itemDeco)
            } else {
                binding.tvArchiveTypeName.visibility = View.GONE
                binding.rcImageList.visibility = View.GONE
                binding.rcImageList.layoutManager = null
                binding.rcImageList.adapter = null
                binding.tvArchiveCount
                binding.rcImageList
            }

            binding.tvArchiveName.text = archiveItem.categoryName
            binding.tvArchiveCount.text = archiveItem.postCount.toString()

            itemView.setOnClickListener {
                archiveClick?.onArchiveClick(itemView, archiveItem.url,archiveItem.boardType,archiveItem.categoryName)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchiveListVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tab_club_page_archive_list_layout, parent, false)
        return ArchiveListVH(TabClubPageArchiveListLayoutBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ArchiveListVH, position: Int) {
        holder.archiveBind(archiveList[holder.bindingAdapterPosition], archiveClick)
    }

    override fun getItemCount() = archiveList.size

    fun setArchiveList(items: List<ClubSubCategoryItem>) {
        this.archiveList = items
        notifyItemRangeChanged(0, this.archiveList.size)
    }

}