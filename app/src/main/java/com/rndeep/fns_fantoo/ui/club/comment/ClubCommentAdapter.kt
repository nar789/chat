package com.rndeep.fns_fantoo.ui.club.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.ClubReplyData
import com.rndeep.fns_fantoo.data.remote.model.CommunityReplyData
import com.rndeep.fns_fantoo.data.remote.model.PostReplyData
import com.rndeep.fns_fantoo.databinding.CommunityCommentLayoutBinding

class ClubCommentAdapter  : ListAdapter<PostReplyData, RecyclerView.ViewHolder>(diff) {

    private var isUser :Boolean = false

    interface ClubCommentReplyClickListener{
        fun onReplyOptionClick(replyData : ClubReplyData, pos :Int)
        fun onTranslateClick(replyData: ClubReplyData, pos: Int)
        fun onCommentImageClick(imageUrl :String)
    }

    private var communityCommentReplyClickListener : ClubCommentReplyClickListener? =null

    fun setClubCommentReplyClickListener(listener: ClubCommentReplyClickListener){
        this.communityCommentReplyClickListener=listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.community_comment_layout, parent, false)
        return ClubCommentVH(CommunityCommentLayoutBinding.bind(view),communityCommentReplyClickListener,isUser)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if(payloads.isEmpty()){
            super.onBindViewHolder(holder, position, payloads)
        }else{
            val item =currentList[position]
            payloads.forEach { payload ->
                if(payload is String){
                    when(holder){
                        is ClubCommentVH->{
                            if(item is ClubReplyData)
                            holder.changeTranslateState(item)
                        }
                    }
                }
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val commentItem =currentList[holder.bindingAdapterPosition]
        when(holder) {
            is ClubCommentVH ->{
                if(commentItem is ClubReplyData){
                    holder.bind(commentItem)
                }
            }
        }
    }


    fun isLoginUser(isUser: Boolean){
        this.isUser=isUser
    }

    companion object{
        val diff = object : DiffUtil.ItemCallback<PostReplyData>() {
            override fun areItemsTheSame(oldItem: PostReplyData, newItem: PostReplyData): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: PostReplyData,
                newItem: PostReplyData
            ): Boolean {
                if(oldItem is CommunityReplyData && newItem is CommunityReplyData){
                    return (oldItem.likeCnt == newItem.likeCnt)
                            && (oldItem.replyId == newItem.replyId)
                            && (oldItem.myLikeYn == newItem.myLikeYn)
                            && (oldItem.myDisLikeYn == newItem.myDisLikeYn)
                            && (oldItem.content == newItem.content)
                            && (oldItem.attachList == newItem.attachList)
                            && (oldItem.activeStatus == newItem.activeStatus)
                }else if(oldItem is ClubReplyData && newItem is ClubReplyData){
                    return (oldItem.replyId == newItem.replyId)
                            && (oldItem.attachList == newItem.attachList)
                            && (oldItem.status == newItem.status)
                            && (oldItem.content == newItem.content)
                }else{
                    return false
                }
            }

        }
    }
}