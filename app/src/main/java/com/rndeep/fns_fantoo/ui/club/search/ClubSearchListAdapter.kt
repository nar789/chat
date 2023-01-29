package com.rndeep.fns_fantoo.ui.club.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.club.ClubSearchItem
import com.rndeep.fns_fantoo.databinding.TabClubSearchHotClubLlistBinding

class ClubSearchListAdapter : ListAdapter<ClubSearchItem,ClubSearchHotClubVH>(diff) {

    interface OnClubClickListener{
        fun onClubClick(v :View,clubId :String)
    }

    private var onClubClickListener : OnClubClickListener?=null

    fun setOnClubClickListener(listener: OnClubClickListener){
        this.onClubClickListener =listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubSearchHotClubVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tab_club_search_hot_club_llist, parent, false)
        return ClubSearchHotClubVH(TabClubSearchHotClubLlistBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ClubSearchHotClubVH, position: Int) {
        holder.hotClubBind(currentList[position],onClubClickListener)
    }

    companion object{
        val diff =object: DiffUtil.ItemCallback<ClubSearchItem>(){
            override fun areItemsTheSame(
                oldItem: ClubSearchItem,
                newItem: ClubSearchItem
            ): Boolean {
                return oldItem==newItem
            }

            override fun areContentsTheSame(
                oldItem: ClubSearchItem,
                newItem: ClubSearchItem
            ): Boolean {
                return (oldItem.clubId==newItem.clubId)
            }

        }
    }

}