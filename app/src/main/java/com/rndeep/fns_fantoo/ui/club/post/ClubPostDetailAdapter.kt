package com.rndeep.fns_fantoo.ui.club.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.ClubReplyData
import com.rndeep.fns_fantoo.databinding.BlankLayoutForErrorBinding
import com.rndeep.fns_fantoo.databinding.DetailClubPostContentLayoutBinding
import com.rndeep.fns_fantoo.databinding.PostDetailCommentLayoutBinding
import com.rndeep.fns_fantoo.ui.common.post.BlankErrorVH
import com.rndeep.fns_fantoo.ui.common.post.PostDetailReplayAdapter
import com.rndeep.fns_fantoo.ui.common.post.PostDetailImageAdapter
import com.rndeep.fns_fantoo.ui.community.postdetail.data.DetailPostItem
import com.rndeep.fns_fantoo.utils.ConstVariable

class ClubPostDetailAdapter : ListAdapter<DetailPostItem, RecyclerView.ViewHolder>(diff) {

    private val TYPE_POST_COMMENT = 2
    private val TYPE_POST_CLUB = 3

    private var postType : String? =null

    private var isUser : Boolean = false

    //replyPos
    private var replyPosition :Int? =null


    //전제 화면 리스너
    private var fullScreenListener : PostDetailImageAdapter.OnAttachListClickListener? =null

    fun setOnFullScreenClickListener(listener : PostDetailImageAdapter.OnAttachListClickListener){
        this.fullScreenListener=listener
    }

    //게시글 선택 리스너
    interface OnClubPostDetailOptionsClickListener{
        fun onTransLateClick(originItem :List<String>,transYn :Boolean)
        fun onShareClick()
        fun onCategoryTextClick(clubId: String, categoryCode :String)
        fun onProfileClick(clubId :String,memberId :Int,nickName:String,isBlock :Boolean,userPhoto :String)
    }

    private var postDetailListener : OnClubPostDetailOptionsClickListener? =null

    fun setOnPostDetailOptionsClickListener(listener : OnClubPostDetailOptionsClickListener){
        this.postDetailListener=listener
    }

     //댓글 리스너
    interface OnClubCommentClickListener {
        fun onCommentClick(commentItem: ClubReplyData)
        fun onOptionsClick(commentItem: ClubReplyData, pos : Int)
        fun onProfileClick(commentItem: ClubReplyData)
        fun onTranslateClick(commentItem: ClubReplyData,pos :Int)
        fun onCommentImageClick(imageUrl :String)
    }

    private var commentClickListener: OnClubCommentClickListener? = null

    fun setOnCommentClickListener(listener: OnClubCommentClickListener) {
        this.commentClickListener = listener
    }

    private var replyClickListener: PostDetailReplayAdapter.OnReplyClickListener? = null

    fun setOnReplyClickListener(listener: PostDetailReplayAdapter.OnReplyClickListener) {
        this.replyClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TYPE_POST_CLUB->{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.detail_club_post_content_layout,parent,false)
                ClubPostDetailVH(DetailClubPostContentLayoutBinding.bind(view),fullScreenListener,postDetailListener)
            }
            TYPE_POST_COMMENT->{
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.post_detail_comment_layout, parent, false)
                ClubPostCommentVH(PostDetailCommentLayoutBinding.bind(view),commentClickListener,replyClickListener,postType,isUser)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.blank_layout_for_error,parent,false)
                BlankErrorVH(BlankLayoutForErrorBinding.bind(view))
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if(payloads.isNotEmpty()){
            val item =getItem(holder.bindingAdapterPosition)
            payloads.forEach { payload ->
                if(payload is String){
                    when(payload){
                        ConstVariable.PAYLOAD_TRANSLATE_CLICK->{
                            when(holder){
                                is ClubPostDetailVH->{
                                    if(item is DetailPostItem.ClubPostDetail){
                                        holder.setTranslateStateChange(item.item)
                                    }
                                }
                                is ClubPostCommentVH->{
                                    if(item is DetailPostItem.ClubPostComment){
                                        if(replyPosition!=null){
                                            holder.setTranslateReplyChange(replyPosition!!,item.item)
                                            replyPosition=null
                                        }else{
                                            holder.setTranslateChange(item.item)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }else{
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(holder.bindingAdapterPosition)
        when(holder) {
            is ClubPostDetailVH -> {
                when (item) {
                    is DetailPostItem.ClubPostDetail -> {
                        holder.clubPostBind(item.item,item.type)
                    }
                    else -> {}
                }
            }
            is ClubPostCommentVH->{
                when(item){
                    is DetailPostItem.ClubPostComment->{
                        holder.clubCommentBind(item.item)
                    }
                    else -> {}
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is DetailPostItem.ClubPostDetail->{
                TYPE_POST_CLUB
            }
            is DetailPostItem.ClubPostComment->{
                TYPE_POST_COMMENT
            }
            else -> {
                -1
            }
        }
    }

     fun setPostType(type : String){
        this.postType=type
    }

    fun isLoginUser(loginUser:Boolean){
        isUser=loginUser
    }

    fun replyPosition(pos : Int){
        this.replyPosition=pos
    }

    companion object{
        val diff  =object : DiffUtil.ItemCallback<DetailPostItem>() {
            override fun areItemsTheSame(
                oldItem: DetailPostItem,
                newItem: DetailPostItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: DetailPostItem,
                newItem: DetailPostItem
            ): Boolean {
                return if(oldItem is DetailPostItem.ClubPostDetail && newItem is DetailPostItem.ClubPostDetail){
                    (oldItem.item.postId == newItem.item.postId)
                            || (oldItem.item.translateYn == newItem.item.translateYn)
                            || (oldItem.item.clubId == newItem.item.clubId)
                            || (oldItem.item.status == newItem.item.status)
                }else if(oldItem is DetailPostItem.ClubPostComment && newItem is DetailPostItem.ClubPostComment){
                    oldItem.item.replyId==newItem.item.replyId
                }else{
                    false
                }
            }
        }
    }
}