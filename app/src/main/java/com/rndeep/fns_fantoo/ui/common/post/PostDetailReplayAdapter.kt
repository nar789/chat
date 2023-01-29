package com.rndeep.fns_fantoo.ui.common.post

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.ClubReplyData
import com.rndeep.fns_fantoo.data.remote.model.CommunityReplyData
import com.rndeep.fns_fantoo.data.remote.model.PostReplyData
import com.rndeep.fns_fantoo.databinding.PostDetailReplyLayoutBinding
import com.rndeep.fns_fantoo.ui.community.postdetail.viewholder.CommentReplyVH

class PostDetailReplayAdapter(
    private val listener: OnReplyClickListener?
) : RecyclerView.Adapter<CommentReplyVH>() {

    private var replyItemList= listOf<PostReplyData>()
    private var parentPosition : Int=-1

    interface OnReplyClickListener {
        fun onLikeClick(commentItem: CommunityReplyData, parentPosition: Int, position: Int)
        fun onDisLikeClick(commentItem: CommunityReplyData, parentPosition: Int, position: Int)
        fun onTranslate(comment: String, parentPosition: Int, position: Int,translateYn :Boolean)
        fun onProfileClick(nickName:String,clubId:String?,memberId :String?,isBlock :Boolean,targetUid :String?,userPhoto:String)
        fun onImageClick(imageUrl :String)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentReplyVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_detail_reply_layout, parent, false)
        return CommentReplyVH(PostDetailReplyLayoutBinding.bind(view),listener)
    }

    override fun onBindViewHolder(holder: CommentReplyVH, position: Int) {
        val item =replyItemList[holder.bindingAdapterPosition]
        when(item){
            is CommunityReplyData->{
                holder.communityCommentBind(item,parentPosition)
            }
            is ClubReplyData->{
                holder.clubCommentBind(item,parentPosition)
            }
        }
    }

    override fun getItemCount() = replyItemList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setReplyItem(item :List<PostReplyData>){
        this.replyItemList=item
        notifyDataSetChanged()
    }

    fun setParentPos(pos : Int){
        parentPosition=pos
    }

}