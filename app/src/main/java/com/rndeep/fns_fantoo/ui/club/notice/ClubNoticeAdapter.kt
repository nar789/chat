package com.rndeep.fns_fantoo.ui.club.notice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostData
import com.rndeep.fns_fantoo.databinding.ClubNoticeLayoutBinding

class ClubNoticeAdapter : ListAdapter<ClubPostData,ClubNoticeVH>(diff) {


    interface ClubNoticeClickListener{
        fun onItemClick(postId :Int,clubId:String,categoryCode: String)
        fun onOptionClick(postId :Int)
    }

    private var clubNoticeListener : ClubNoticeClickListener? =null

    fun setClubNoticeListener(listener : ClubNoticeClickListener){
        this.clubNoticeListener =listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubNoticeVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.club_notice_layout,parent,false)
        return ClubNoticeVH(ClubNoticeLayoutBinding.bind(view),clubNoticeListener)
    }

    override fun onBindViewHolder(holder: ClubNoticeVH, position: Int) {
        holder.noticeBind(currentList[position])
    }


    companion object{
        val diff = object : DiffUtil.ItemCallback<ClubPostData>(){
            override fun areItemsTheSame(oldItem: ClubPostData, newItem: ClubPostData): Boolean {
                return (oldItem.postId==newItem.postId) ||
                        (oldItem.status==newItem.status) ||
                        (oldItem.subject==newItem.subject) ||
                        (oldItem.content==newItem.content)
            }

            override fun areContentsTheSame(oldItem: ClubPostData, newItem: ClubPostData): Boolean {
                return oldItem==newItem
            }

        }
    }

}