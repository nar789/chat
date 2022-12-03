package com.rndeep.fns_fantoo.ui.club.settings.management.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.ClubSubCategoryItem
import com.rndeep.fns_fantoo.ui.common.RecyclerViewItemClickListener
import com.rndeep.fns_fantoo.databinding.TabClubSettingArchiveEmptyBinding
import com.rndeep.fns_fantoo.databinding.TabClubSettingArchiveItemBinding
import com.rndeep.fns_fantoo.ui.club.settings.management.swipe.ItemStartDragListener
import com.rndeep.fns_fantoo.utils.LanguageUtils
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

private const val MODE_NONE = 0
private const val MODE_EDIT = 1

private const val VIEW_EMPTY = 0
private const val VIEW_ITEM = 1
class ArchiveBoardAdapter(private val currentAppLanguageCode:String?) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var archiveArryList = ArrayList<ClubSubCategoryItem>()
    private var viewMode = MODE_NONE

    private var itemClickListener: RecyclerViewItemClickListener? = null

    private var dragStartListener: ItemStartDragListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHoder:RecyclerView.ViewHolder
        when(viewType){
            VIEW_EMPTY ->{
                viewHoder = EmptyVH(TabClubSettingArchiveEmptyBinding.bind(
                    LayoutInflater.from(parent.context).inflate(R.layout.tab_club_setting_archive_empty, parent, false)))
            }
            VIEW_ITEM ->{
                viewHoder = ArchiveVH(TabClubSettingArchiveItemBinding.bind(
                    LayoutInflater.from(parent.context).inflate(R.layout.tab_club_setting_archive_item, parent, false)
                ))
            }
            else ->{
                viewHoder = ArchiveVH(TabClubSettingArchiveItemBinding.bind(
                    LayoutInflater.from(parent.context).inflate(R.layout.tab_club_setting_archive_item, parent, false)
                ))
            }
        }
        return viewHoder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(archiveArryList.size == 0){
            //EmptyVH
        }else{
            (holder as ArchiveVH).bind(archiveArryList[position])
        }
    }

    override fun getItemCount(): Int {
        if(archiveArryList.size == 0)return 1//empty
        return archiveArryList.size
    }

    override fun getItemViewType(position: Int): Int {
        if(archiveArryList.size > 0){
            return VIEW_ITEM
        }
        return  VIEW_EMPTY
    }

    fun getItemList() : ArrayList<ClubSubCategoryItem>{
        return archiveArryList
    }

    fun setItemList(itemsList:List<ClubSubCategoryItem>){
        try {
            archiveArryList.clear()
            archiveArryList.addAll(itemsList)
            notifyDataSetChanged()
        }catch (e: Exception){
            Timber.e("setItemList err : ${e.message}")
        }
    }

    inner class ArchiveVH(val binding: TabClubSettingArchiveItemBinding):RecyclerView.ViewHolder(binding.root){
        @SuppressLint("ClickableViewAccessibility")
        fun bind(archiveData: ClubSubCategoryItem){
            binding.tvArchiveName.text = archiveData.categoryName
            if(archiveData.boardType == 2){//게시판 타입 0:없음, 1:통합, 2:이미지, 3:아카이브(통합/이미지)
                binding.tvArchiveType.visibility = View.VISIBLE
            }else{
                binding.tvArchiveType.visibility = View.GONE
            }
            binding.ivArchiveGo.visibility = if(viewMode == MODE_EDIT)View.GONE else View.VISIBLE
            binding.ivDrag.visibility = if(viewMode == MODE_EDIT)View.VISIBLE else View.GONE
            binding.ivDrag.isHapticFeedbackEnabled = false
            binding.ivDrag.setOnTouchListener { v, event ->
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    dragStartListener?.onStartDrag(this@ArchiveVH)
                }
                true
            }
            binding.root.setOnClickListener {
                if(viewMode == MODE_EDIT)return@setOnClickListener
                itemClickListener?.onItemClick(archiveData)
            }
        }
    }

    inner class EmptyVH(val binding:TabClubSettingArchiveEmptyBinding):RecyclerView.ViewHolder(binding.root)

    @SuppressLint("NotifyDataSetChanged")
    fun setViewEditMode(isEditMode:Boolean){
        viewMode = if(isEditMode)MODE_EDIT else MODE_NONE
        notifyDataSetChanged()
    }

    // 현재 선택된 데이터와 드래그한 위치에 있는 데이터를 교환
    fun swapData(fromPos: Int, toPos: Int) {
        Collections.swap(archiveArryList, fromPos, toPos)
        notifyItemMoved(fromPos, toPos)
    }

    fun setDragStartListener(dragListener: ItemStartDragListener){
        this.dragStartListener = dragListener
    }

    fun setOnItemClickListener(itemClickListener: RecyclerViewItemClickListener){
        this.itemClickListener = itemClickListener
    }
}