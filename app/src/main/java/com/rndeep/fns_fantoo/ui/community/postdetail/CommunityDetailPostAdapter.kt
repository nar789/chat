package com.rndeep.fns_fantoo.ui.community.postdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.CommunityReplyData
import com.rndeep.fns_fantoo.data.remote.model.PostReplyData
import com.rndeep.fns_fantoo.databinding.BlankLayoutForErrorBinding
import com.rndeep.fns_fantoo.databinding.DetailCommunityPostContentLayoutBinding
import com.rndeep.fns_fantoo.databinding.DetailNoticePostContentLayoutBinding
import com.rndeep.fns_fantoo.databinding.PostDetailCommentLayoutBinding
import com.rndeep.fns_fantoo.ui.common.post.BlankErrorVH
import com.rndeep.fns_fantoo.ui.common.post.PostDetailImageAdapter
import com.rndeep.fns_fantoo.ui.common.post.PostDetailReplayAdapter
import com.rndeep.fns_fantoo.ui.community.postdetail.data.DetailPostItem
import com.rndeep.fns_fantoo.ui.community.postdetail.viewholder.CommunityPostCommentVH
import com.rndeep.fns_fantoo.ui.community.postdetail.viewholder.CommunityPostDetailVH
import com.rndeep.fns_fantoo.ui.community.postdetail.viewholder.NoticePostVH
import com.rndeep.fns_fantoo.utils.ConstVariable

class CommunityDetailPostAdapter : ListAdapter<DetailPostItem, RecyclerView.ViewHolder>(diff) {

    private val TYPE_POST_COMMUNITY = 0
    private val TYPE_POST_NOTICE = 1
    private val TYPE_POST_COMMENT = 2

    //replyPos
    private var replyPosition :Int? =null

    private var boardCode :String? =null

    private var postType : String? =null

    private var isLoginUser : Boolean =false

    //전제 화면 리스너
    private var fullScreenListener : PostDetailImageAdapter.OnFullScreenClickListener? =null

    fun setOnFullScreenClickListener(listener : PostDetailImageAdapter.OnFullScreenClickListener){
        this.fullScreenListener=listener
    }

    //게시글 선택 리스너
    interface OnPostDetailOptionsClickListener{
        fun onTransLateClick(transItems :List<String>,isTranslate :Boolean)
        fun onLikeClick(isMyLike : Boolean?)
        fun onDisListClick(isMyDisLike :Boolean?)
        fun onHonorClick()
        fun onShareClick()
    }

    private var postDetailListener : OnPostDetailOptionsClickListener? =null

    fun setOnPostDetailOptionsClickListener(listener : OnPostDetailOptionsClickListener){
        this.postDetailListener=listener
    }

    //댓글 리스너
    interface OnCommentClickListener {
        fun onCommentClick(commentItem: PostReplyData)
        fun onOptionsClick(commentItem: PostReplyData, pos : Int)
        fun onLikeClick(commentItem: CommunityReplyData, pos :Int)
        fun onDisLikeClick(commentItem: CommunityReplyData, pos :Int)
        fun onTransClick(comment :String,pos:Int,translateYn :Boolean)
        fun onProfileClick(commentItem: PostReplyData)
        fun onImageClick(imageUrl: String)
    }

    private var commentClickListener: OnCommentClickListener? = null

    fun setOnCommentClickListener(listener: OnCommentClickListener) {
        this.commentClickListener = listener
    }

    private var replyClickListener: PostDetailReplayAdapter.OnReplyClickListener? = null

    fun setOnReplyClickListener(listener: PostDetailReplayAdapter.OnReplyClickListener) {
        this.replyClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
          return when(viewType){
              TYPE_POST_COMMUNITY->{
                  val view = LayoutInflater.from(parent.context).inflate(R.layout.detail_community_post_content_layout,parent,false)
                  CommunityPostDetailVH(DetailCommunityPostContentLayoutBinding.bind(view),fullScreenListener,postDetailListener)
              }
              TYPE_POST_NOTICE->{
                  val view = LayoutInflater.from(parent.context).inflate(R.layout.detail_notice_post_content_layout,parent,false)
                  NoticePostVH(DetailNoticePostContentLayoutBinding.bind(view),fullScreenListener,postDetailListener)
              }
              TYPE_POST_COMMENT->{
                  val view = LayoutInflater.from(parent.context)
                      .inflate(R.layout.post_detail_comment_layout, parent, false)
                  CommunityPostCommentVH(PostDetailCommentLayoutBinding.bind(view),commentClickListener,replyClickListener,postType,isLoginUser)
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
        if(payloads.isEmpty()){
            super.onBindViewHolder(holder, position, payloads)
        }else{
            val item =getItem(holder.bindingAdapterPosition)
            for(payload in payloads){
                if(payload is String){
                    when(payload){
                        //좋아요,싫어요 선택시 holder 의 like 부분만 처리
                        ConstVariable.PAYLOAD_LIKE_CLICK->{
                            when(holder){
                                is CommunityPostDetailVH ->{
                                    if(item is DetailPostItem.CommunityPostDetail){
                                        holder.setLikeIconStateChange(item.item)
                                        holder.setDisLikeIconStateChange(item.item)
                                        holder.settingDefaultClickFun(item.item)
                                    }
                                }
                                is CommunityPostCommentVH ->{
                                    if(item is DetailPostItem.CommunityPostComment){
                                        holder.setCommentLikeStateChange(item.item)
                                        holder.setCommentDisLikeStateChange(item.item)
                                    }
                                }
                            }
                        }
                        //아너 선택시 holder 의 honor 부분만 처리
                        ConstVariable.PAYLOAD_HONOR_CLICK ->{
                            when(holder){
                                is CommunityPostDetailVH ->{
                                    if(item is DetailPostItem.CommunityPostDetail){
                                        holder.setHonorIconStateChange(item.item)
                                        holder.settingDefaultClickFun(item.item)
                                    }
                                }
                            }
                        }
                        //번역 선택시 holder 의 번역 부분만 처리
                        ConstVariable.PAYLOAD_TRANSLATE_CLICK->{
                            when(holder){
                                is CommunityPostDetailVH ->{
                                    if(item is DetailPostItem.CommunityPostDetail){
                                        holder.setTranslateStateChange(item.item)
                                        holder.settingDefaultClickFun(item.item)
                                    }
                                }
                                is NoticePostVH ->{
                                    if(item is DetailPostItem.NoticePostDetail){
                                        holder.setTranslateStateChange(item.item)
                                        holder.settingClickFun(
                                            listOf(item.item.title,item.item.content),item.item.translateYn?:false
                                        )
                                    }
                                }
                                is CommunityPostCommentVH->{
                                    if(item is DetailPostItem.CommunityPostComment){
                                        if(replyPosition!=null){
                                            holder.setReplyTranslate(replyPosition!!,item.item)
                                            replyPosition=null
                                        }else{
                                            holder.setTranslateContent(item.item.content,item.item.translateYn?:false)
                                        }
                                    }
                                }
                            }
                        }
                        //대_댓글 좋아요
                        ConstVariable.PAYLOAD_REPLY_LIKE_CLICK->{
                            when(holder){
                                is CommunityPostCommentVH -> {
                                    if(item is DetailPostItem.CommunityPostComment){
                                        if(replyPosition!=null){
                                            holder.setReplyLikeStateChange(replyPosition!!,item.item)
                                            holder.setReplyDisLikeStateChange(replyPosition!!,item.item)
                                            replyPosition=null
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(holder.bindingAdapterPosition)
        when(holder){
            is CommunityPostDetailVH ->{
                when(item){
                    is DetailPostItem.CommunityPostDetail->{
                        holder.communityPostBind(item.item,item.attachItems,item.hashTagItems,item.openGraphItems)
                    }
                    else -> {}
                }
            }
            is NoticePostVH ->{
                when(item){
                    is DetailPostItem.NoticePostDetail->{
                        holder.noticeBind(item.item,boardCode,item.attachItems)
                    }
                    else -> {}
                }
            }
            is CommunityPostCommentVH ->{
                when(item){
                    is DetailPostItem.CommunityPostComment->{
                        holder.communityCommentBind(item.item)
                    }
                    else -> {}
                }
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is DetailPostItem.CommunityPostDetail->{
                TYPE_POST_COMMUNITY
            }
            is DetailPostItem.NoticePostDetail->{
                TYPE_POST_NOTICE
            }
            is DetailPostItem.CommunityPostComment->{
                TYPE_POST_COMMENT
            }
            else -> {
                -1
            }
        }
    }

    fun setIsLogin(isLogin :Boolean){
        isLoginUser=isLogin
    }

    fun setBoardCode(code: String?){
        this.boardCode=code
    }

    fun setPostType(type : String){
        this.postType=type
    }

    fun replyPosition(pos : Int){
        this.replyPosition=pos
    }

    companion object{
        val diff =object :DiffUtil.ItemCallback<DetailPostItem>(){
            override fun areItemsTheSame(
                oldItem: DetailPostItem,
                newItem: DetailPostItem
            ): Boolean {
                return oldItem==newItem
            }

            override fun areContentsTheSame(
                oldItem: DetailPostItem,
                newItem: DetailPostItem
            ): Boolean {
                return oldItem==newItem
            }

        }

    }



}