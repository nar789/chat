package com.rndeep.fns_fantoo.ui.community.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.ClubReplyData
import com.rndeep.fns_fantoo.data.remote.model.CommunityReplyData
import com.rndeep.fns_fantoo.data.remote.model.PostReplyData
import com.rndeep.fns_fantoo.databinding.BlankLayoutForErrorBinding
import com.rndeep.fns_fantoo.databinding.CommunityCommentLayoutBinding
import com.rndeep.fns_fantoo.ui.common.post.BlankErrorVH
import com.rndeep.fns_fantoo.utils.ConstVariable

class CommunityCommentAdapter : ListAdapter<PostReplyData,RecyclerView.ViewHolder>(diff) {

    private val COMMENTTYPE =2

    private var isLogin = false

    interface OnCommunityCommentReplyClickListener{
        fun onReplyOptionClick(replyData : CommunityReplyData, pos :Int)
        fun onTranslateClick(replyData: CommunityReplyData, pos: Int)
        fun onReplyLikeClick(replyItem: CommunityReplyData, pos: Int)
        fun onReplyDisListClick(replyItem: CommunityReplyData, pos: Int)
        fun onImageClick(imageUrl :String)
    }

    private var communityCommentReplyClickListener : OnCommunityCommentReplyClickListener? =null

    fun setCommunityCommentReplyClickListener(listener: OnCommunityCommentReplyClickListener){
        this.communityCommentReplyClickListener=listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            COMMENTTYPE->{
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.community_comment_layout, parent, false)
                CommunityPostCommentVH(CommunityCommentLayoutBinding.bind(view),communityCommentReplyClickListener,isLogin)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.blank_layout_for_error,parent,false)
                BlankErrorVH(BlankLayoutForErrorBinding.bind(view))
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        when(holder){
            is CommunityPostCommentVH ->{
                if(payloads.isEmpty()){
                    super.onBindViewHolder(holder, position, payloads)
                }else{
                    val item =currentList[holder.bindingAdapterPosition]
                    for(payload in payloads) {
                        if (payload is String) {
                            when (payload) {
                                ConstVariable.PAYLOAD_LIKE_CLICK ->{
                                    if(item is CommunityReplyData){
                                        holder.settingLikeDislikeColor(item)
                                        holder.settingCLickListener(item)
                                    }
                                }
                                ConstVariable.PAYLOAD_TRANSLATE_CLICK->{
                                    if(item is CommunityReplyData){
                                        holder.settingTranslateChange(item)
                                        holder.settingCLickListener(item)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else ->{
                super.onBindViewHolder(holder, position, payloads)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val commentItem =currentList[holder.bindingAdapterPosition]
        when(holder) {
            is CommunityPostCommentVH -> {
                if(commentItem is CommunityReplyData){
                    holder.bind(commentItem)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(currentList[position]){
            is PostReplyData ->{
                COMMENTTYPE
            }
            else -> -1
        }
    }

    fun setIsLogin(isLoginState :Boolean){
        this.isLogin= isLoginState
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