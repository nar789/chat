package com.rndeep.fns_fantoo.ui.community.postdetail.viewholder

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.CommentReply
import com.rndeep.fns_fantoo.data.remote.model.CommunityReplyData
import com.rndeep.fns_fantoo.data.remote.model.PostReplyData
import com.rndeep.fns_fantoo.data.remote.model.community.DetailAttachList
import com.rndeep.fns_fantoo.data.remote.model.post.CommunityOpenGraphItem
import com.rndeep.fns_fantoo.databinding.PostDetailCommentLayoutBinding
import com.rndeep.fns_fantoo.ui.common.comment.CommentImageAdapter
import com.rndeep.fns_fantoo.ui.community.postdetail.CommunityDetailPostAdapter
import com.rndeep.fns_fantoo.ui.common.post.PostDetailReplayAdapter
import com.rndeep.fns_fantoo.utils.*

//댓글 뷰 홀더
class CommunityPostDetailCommentVH(
    private val binding: PostDetailCommentLayoutBinding,
    private val listener: CommunityDetailPostAdapter.OnCommentClickListener?,
    private val replyListener: PostDetailReplayAdapter.OnReplyClickListener?,
    private val postType :String?,
    private val isLoginUser :Boolean
) :
    RecyclerView.ViewHolder(binding.root) {

    private val replayAdapter = PostDetailReplayAdapter(replyListener)

    private val replyDividerDecoration =
        CustomDividerDecoration(0.5f, 0f, itemView.context.getColor(R.color.gray_400_opacity12), false)


    fun communityCommentBind(commentItem: CommunityReplyData) {
        //댓글 프로필 이미지
        setProfileAvatar(
            binding.ivProfileThumbnail,
            if (commentItem.userPhoto != null) itemView.context.getString(
                R.string.imageUrlBase,
                commentItem.userPhoto
            )
            else commentItem.userPhoto
        )
        binding.tvNickName.text = commentItem.userNick
        binding.tvCreateDate.text = TimeUtils.diffTimeWithCurrentTime(commentItem.createDate)

        if (commentItem.userBlockYn == true || commentItem.pieceBlockYn == true) {
            setBlockCommentView()
        } else {
            changeViewStyle(commentItem.activeStatus,commentItem.attachList,commentItem.openGraphList)
        }

        binding.tvContent.text = commentItem.content
        setCommentReplyView(commentItem.childReplyList, commentItem.replyCnt)

        if (commentItem.activeStatus == 2){
            binding.ivCommentOption.visibility=View.INVISIBLE
            setShowHideLikeOption(false)
        }else if(isLoginUser){
            binding.ivCommentOption.visibility=View.VISIBLE
            setShowHideLikeOption(true)
        }else{
            binding.ivCommentOption.visibility=View.INVISIBLE
            setShowHideLikeOption(true)
        }
        setCommentLikeStateChange(commentItem)
        setCommentDisLikeStateChange(commentItem)

//        binding.tvCommentCount.text = commentItem.replyCnt?.toString()?:"0"

        if(commentItem.translateYn == true){
            binding.tvTranslate.text=itemView.context.getString(R.string.b_do_translate_cancel)
        }else{
            binding.tvTranslate.text=itemView.context.getString(R.string.b_do_translate)
        }

        if(commentItem.pieceBlockYn==true || commentItem.userBlockYn==true){
            binding.ivCommentOption.setOnClickListener {
                listener?.onOptionsClick(commentItem, bindingAdapterPosition)
            }
        }else{
            if (commentItem.activeStatus == 1) {
                setDefaultClick(commentItem)
            }
        }
        contentClick(commentItem)
    }

    fun changeViewStyle(state: Int, attachList: List<DetailAttachList>?, openGraphList: List<CommunityOpenGraphItem>?) {
        when (state) {
            2,0 -> {
                binding.tvNickName.setTextColor(itemView.context.getColor(R.color.gray_200))
                binding.tvContent.setTextColor(itemView.context.getColor(R.color.gray_300))
                binding.llLikeContainer.visibility=View.GONE
                binding.ivDisLikeIcon.visibility=View.GONE
                binding.tvTranslate.visibility = View.GONE
                binding.rcCommentImage.visibility = View.GONE
            }
            else -> {
                binding.tvNickName.setTextColor(itemView.context.getColor(R.color.gray_900))
                binding.tvContent.setTextColor(itemView.context.getColor(R.color.gray_870))
                binding.tvTranslate.visibility= View.VISIBLE
                binding.llLikeContainer.visibility=View.VISIBLE
                binding.ivDisLikeIcon.visibility=View.VISIBLE
                setDefaultCommentView(attachList,openGraphList)
            }
        }
    }

    fun setBlockCommentView(){
        binding.tvNickName.setTextColor(itemView.context.getColor(R.color.gray_200))
        binding.tvContent.setTextColor(itemView.context.getColor(R.color.gray_300))
        binding.tvTranslate.visibility = View.GONE
        binding.rcCommentImage.visibility = View.GONE
    }

    fun setTranslateContent(transString :String, translateYn :Boolean){
        if(translateYn){
            binding.tvTranslate.text=itemView.context.getString(R.string.b_do_translate_cancel)
        }else{
            binding.tvTranslate.text=itemView.context.getString(R.string.b_do_translate)
        }
        binding.tvContent.text=transString
        binding.tvTranslate.setOnClickListener {
            listener?.onTransClick(transString,bindingAdapterPosition,translateYn)
        }
    }

    fun setDefaultCommentView(attachList: List<DetailAttachList>?,openGraphList: List<CommunityOpenGraphItem>?){
        binding.tvTranslate.visibility = View.VISIBLE
        if (attachList == null && openGraphList == null) binding.rcCommentImage.visibility = View.GONE
        attachList?.let {
            if (it.isEmpty() && openGraphList.isNullOrEmpty()) {
                binding.rcCommentImage.visibility = View.GONE
                return@let
            } else {
                binding.rcCommentImage.visibility = View.VISIBLE
                binding.rcCommentImage.layoutManager = LinearLayoutManager(itemView.context)
                binding.rcCommentImage.adapter = CommentImageAdapter(it,openGraphList).apply {
                    setOnCommentImageClickListener(object :CommentImageAdapter.CommentImageClickListener{
                        override fun onImageClick(imageUrl: String) {
                            listener?.onImageClick(imageUrl)
                        }
                    })
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun contentClick(commentItem: PostReplyData){
        itemView.setOnClickListener {
            listener?.onCommentClick(commentItem)
        }
        binding.rcCommentImage.setOnTouchListener { view, motionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_UP){
                listener?.onCommentClick(commentItem)
                view.performClick()
            }else{
                false
            }
        }
        binding.rcReplyList.setOnTouchListener { view, motionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_UP){
                listener?.onCommentClick(commentItem)
                view.performClick()
            }else{
                false
            }
        }
    }

    fun setCommentReplyView(commentReply: List<PostReplyData>?, replyCnt :Int?){
        if (commentReply == null) {
            binding.clReplyContainer.visibility = View.GONE
            binding.llCommentContainer.visibility = View.GONE
        }

        commentReply?.let {
            if (it.isEmpty()) {
                binding.clReplyContainer.visibility = View.GONE
                return@let
            } else {
                binding.clReplyContainer.visibility = View.VISIBLE
                binding.rcReplyList.layoutManager = LinearLayoutManager(itemView.context)
                binding.rcReplyList.addSingleItemDecoRation(replyDividerDecoration)
                binding.llCommentContainer.visibility = View.VISIBLE
                if((replyCnt ?: 0) < 3){
                    binding.tvReplyCount.visibility = View.GONE
                }else{
                    binding.tvReplyCount.visibility=View.VISIBLE
                    binding.tvReplyCount.text=itemView.context.getString(R.string.comments_reply_more,((replyCnt!!)-2))
                }
                replayAdapter.setReplyItem(it)
                replayAdapter.setParentPos(bindingAdapterPosition)
                binding.rcReplyList.adapter = replayAdapter
            }
        }
    }

    fun setDefaultClick(commentItem: PostReplyData){
        binding.ivCommentOption.setOnClickListener {
            listener?.onOptionsClick(commentItem, bindingAdapterPosition)
        }

        binding.llLikeContainer.setOnClickListener {
            if(commentItem is CommunityReplyData)
            listener?.onLikeClick(commentItem, bindingAdapterPosition)
        }

        binding.ivDisLikeIcon.setOnClickListener {
            if(commentItem is CommunityReplyData)
            listener?.onDisLikeClick(commentItem, bindingAdapterPosition)
        }
        binding.ivProfileThumbnail.setOnClickListener {
            listener?.onProfileClick(commentItem)
        }
        binding.tvNickName.setOnClickListener {
            listener?.onProfileClick(commentItem)
        }
        binding.tvTranslate.setOnClickListener {
            if(commentItem is CommunityReplyData)
            listener?.onTransClick(commentItem.content,bindingAdapterPosition,commentItem.translateYn?:false)
        }
    }

    //좋아요 상태 변화
    fun setCommentLikeStateChange(commentItem: CommunityReplyData) {
        if (commentItem.myLikeYn != null) {
            binding.ivLikeIcon.setColorFilter(
                itemView.context.getColor(
                    if (commentItem.myLikeYn!!)
                        R.color.state_active_primary_default else R.color.state_disabled_gray_200
                )
            )
            binding.tvLikeCount.setTextColor(
                itemView.context.getColor(
                    if (commentItem.myLikeYn!!)
                        R.color.state_active_primary_default else R.color.state_active_gray_700
                )
            )
        } else {
            binding.ivLikeIcon.setColorFilter(itemView.context.getColor(R.color.state_disabled_gray_200))
            binding.tvLikeCount.setTextColor(itemView.context.getColor(R.color.state_active_gray_700))
        }
        binding.tvLikeCount.text = ((commentItem.likeCnt?:0) - (commentItem.dislikeCnt?:0)).toString()
    }

    //싫어요 상태 변화
    fun setCommentDisLikeStateChange(commentItem: CommunityReplyData) {
        if (commentItem.myDisLikeYn != null) {
            binding.ivDisLikeIcon.setColorFilter(
                itemView.context.getColor(
                    if (commentItem.myDisLikeYn!!)
                        R.color.state_active_gray_700 else R.color.state_disabled_gray_200
                )
            )
        } else {
            binding.ivDisLikeIcon.setColorFilter(itemView.context.getColor(R.color.state_disabled_gray_200))
        }
    }

    //대_댓글 좋아요
    fun setReplyLikeStateChange(position: Int, commentItem: CommunityReplyData) {
        val replyHolder = binding.rcReplyList.findViewHolderForAdapterPosition(position)
        if (replyHolder is CommentReplyVH) {
            commentItem.childReplyList?.let {
                replyHolder.setReplyLikeStateChange(it[position])
            }
        }
    }

    //대_댓글 싫어요
    fun setReplyDisLikeStateChange(position: Int, commentItem: CommunityReplyData) {
        val replyHolder = binding.rcReplyList.findViewHolderForAdapterPosition(position)
        if (replyHolder is CommentReplyVH) {
            commentItem.childReplyList?.let {
                replyHolder.setReplyDisLikeStateChange(it[position])
            }
        }
    }

    //대_댓글 번역
    fun setReplyTranslate(position: Int, commentItem: CommunityReplyData){
        val replyHolder = binding.rcReplyList.findViewHolderForAdapterPosition(position)
        if (replyHolder is CommentReplyVH) {
            commentItem.childReplyList?.let {
                replyHolder.setCommunityTranslateStringChange(it[position],bindingAdapterPosition)
            }
        }
    }

    fun setShowHideLikeOption(showHide :Boolean){
        binding.llLikeContainer.visibility=if(showHide) View.VISIBLE else View.GONE
        binding.ivDisLikeIcon.visibility=if(showHide) View.VISIBLE else View.GONE
    }
}