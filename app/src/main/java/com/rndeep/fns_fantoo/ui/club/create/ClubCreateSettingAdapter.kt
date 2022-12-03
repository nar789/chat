package com.rndeep.fns_fantoo.ui.club.create

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TabClubCreateSettingListLayoutBinding
import com.rndeep.fns_fantoo.data.local.model.SettingItem

class ClubCreateSettingAdapter :
    RecyclerView.Adapter<ClubCreateSettingAdapter.ClubCreateSettingVH>() {
    private var settingList = arrayListOf<SettingItem>()

    interface OnSettingClickListener {
        fun onSettingClick(v: View, position: Int, type: Int)
    }

    private var onSettingClickListener: OnSettingClickListener? = null

    fun setOnSettingClickListener(listener: OnSettingClickListener){
        this.onSettingClickListener=listener
    }

    inner class ClubCreateSettingVH(private val binding: TabClubCreateSettingListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun settingBind(item: SettingItem, listener: OnSettingClickListener?) {
            itemView.setOnClickListener {
                listener?.onSettingClick(itemView,bindingAdapterPosition,item.settingType)
            }
            binding.tvSettingTitle.text = item.settingTitle
            binding.tvCurrentSetting.text = item.currentSetting
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubCreateSettingVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tab_club_create_setting_list_layout, parent, false)
        return ClubCreateSettingVH(TabClubCreateSettingListLayoutBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ClubCreateSettingVH, position: Int) {
        holder.settingBind(settingList[holder.bindingAdapterPosition],onSettingClickListener)

    }

    override fun getItemCount() = settingList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setSettingItem(items: ArrayList<SettingItem>) {
        this.settingList = items
        notifyDataSetChanged()
    }

    fun setNotifyData(pos :Int,item : String){
        this.settingList[pos]=settingList[pos].apply {
            currentSetting=item
        }
        notifyItemChanged(pos)
    }

    fun getItem()=settingList

    fun getSelectedSettingItems():HashMap<Int,String>{
        return HashMap<Int,String>().apply {
            for(a in settingList){
                put(a.settingType,a.currentSetting)
            }
        }
    }
}