package com.rndeep.fns_fantoo.ui.club.post

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.ClubReplyData
import com.rndeep.fns_fantoo.data.remote.model.PostReplyData
import com.rndeep.fns_fantoo.data.remote.model.community.DetailAttachList
import com.rndeep.fns_fantoo.data.remote.model.post.CommunityOpenGraphItem
import com.rndeep.fns_fantoo.databinding.PostDetailCommentLayoutBinding
import com.rndeep.fns_fantoo.ui.common.comment.CommentImageAdapter
import com.rndeep.fns_fantoo.ui.common.post.PostDetailReplayAdapter
import com.rndeep.fns_fantoo.ui.community.postdetail.viewholder.CommentReplyVH
import com.rndeep.fns_fantoo.utils.*

class ClubPostCommentVH(
    private val binding: PostDetailCommentLayoutBinding,
    private val listener: ClubPostDetailAdapter.OnClubCommentClickListener?,
    private val replyListener: PostDetailReplayAdapter.OnReplyClickListener?,
    private val postType :String?,
    private val isUser :Boolean
) : RecyclerView.ViewHolder(binding.root) {

    private val replayAdapter = PostDetailReplayAdapter(replyListener)

    private val replyDividerDecoration =
        CustomDividerDecoration(0.5f, 0f, itemView.context.getColor(R.color.gray_400_opacity12), false)

    fun clubCommentBind(commentItem: ClubReplyData) {
        //댓글 프로필 이미지
        setProfileAvatar(
            binding.ivProfileThumbnail,
            if (commentItem.profileImg != null) itemView.context.getString(
                R.string.imageUrlBase,
                commentItem.profileImg
            )
            else commentItem.profileImg
        )
        binding.tvNickName.text = commentItem.nickname
        binding.tvCreateDate.text = TimeUtils.diffTimeWithCurrentTime(commentItem.createDate)

        if(commentItem.status !=0 || (commentItem.blockType?:0) !=0){
            setBlockCommentView()
        }else{
            binding.tvNickName.setTextColor(itemView.context.getColor(R.color.gray_900))
            binding.tvContent.setTextColor(itemView.context.getColor(R.color.gray_870))
            setDefaultCommentView(commentItem.attachList?.map {
                val attachType =when(it.attachType){
                    0->"image"
                    1->"video"
                    2->"link"
                    else -> "image"
                }
                DetailAttachList(attachType,it.attach)
            },commentItem.openGraphItem)
        }
        commentItem.content = if(commentItem.status==2) itemView.context.getString(R.string.se_j_deleted_comment_by_writer)
                                else if(commentItem.status==3) itemView.context.getString(R.string.delete_club_master)
                                else if(commentItem.status==1) itemView.context.getString(R.string.se_s_comment_hide_by_report)
                                else if(commentItem.blockType==1 ||
                                    commentItem.blockType==2 ||
                                    commentItem.blockType==3 ||
                                    commentItem.blockType==4) itemView.context.getString(R.string.se_c_blocked_comment)
                                else commentItem.content
        binding.tvContent.text =commentItem.content

        setCommentReplyView(commentItem.childReplyList,commentItem.replyCount)

        setShowHideLikeOption(false)
        setTranslateChange(commentItem)
        if(isUser){
            binding.ivCommentOption.visibility=View.VISIBLE
        }else{
            binding.ivCommentOption.visibility=View.INVISIBLE
        }

        binding.tvCommentCount.text = commentItem.replyCount?.toString()?:"0"


        if(commentItem.status==1 || commentItem.status==2 || commentItem.status==3){
            binding.ivCommentOption.visibility=View.INVISIBLE
        }else if(commentItem.blockType == 1 || commentItem.blockType == 2 || commentItem.blockType == 3 || commentItem.blockType == 4){
            binding.ivCommentOption.setOnClickListener {
                listener?.onOptionsClick(commentItem, bindingAdapterPosition)
            }
        }else{
            setDefaultClick(commentItem)
        }
        contentClick(commentItem)

    }

    fun setBlockCommentView(){
        binding.tvNickName.setTextColor(itemView.context.getColor(R.color.gray_200))
        binding.tvContent.setTextColor(itemView.context.getColor(R.color.gray_300))
        binding.tvTranslate.visibility = View.GONE
        binding.rcCommentImage.visibility = View.GONE
    }

    fun setDefaultCommentView(attachList: List<DetailAttachList>?,openGraphItem: List<CommunityOpenGraphItem>?){
        binding.tvTranslate.visibility = View.VISIBLE
        binding.rcCommentImage.visibility = View.VISIBLE
        binding.tvTranslate.visibility = View.VISIBLE
        if (attachList == null && openGraphItem == null) binding.rcCommentImage.visibility = View.GONE
        attachList?.let {
            if (it.isEmpty() && openGraphItem?.isEmpty() != false) {
                binding.rcCommentImage.visibility = View.GONE
                return@let
            } else {
                binding.rcCommentImage.visibility = View.VISIBLE
                binding.rcCommentImage.layoutManager = LinearLayoutManager(itemView.context)
                binding.rcCommentImage.adapter = CommentImageAdapter(it,openGraphItem).apply {
                    setOnCommentImageClickListener(object :CommentImageAdapter.CommentImageClickListener{
                        override fun onImageClick(imageUrl: String) {
                            listener?.onCommentImageClick(imageUrl)
                        }
                    })
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun contentClick(commentItem: ClubReplyData){
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
            binding.rlCommentOption.visibility=View.GONE
        }
        commentReply?.let {
            if (it.isEmpty()) {
                binding.clReplyContainer.visibility = View.GONE
                binding.rlCommentOption.visibility=View.GONE
                return@let
            } else {
                binding.clReplyContainer.visibility = View.VISIBLE
                binding.rcReplyList.layoutManager = LinearLayoutManager(itemView.context)
                binding.rcReplyList.addSingleItemDecoRation(replyDividerDecoration)
                binding.llCommentContainer.visibility = View.VISIBLE
                if((replyCnt ?: 0) < 3){
                    binding.tvReplyCount.visibility = View.GONE
                }else{
                    binding.tvReplyCount.visibility= View.VISIBLE
                    binding.tvReplyCount.text=itemView.context.getString(R.string.comments_reply_more,((replyCnt!!)-2))
                }
                replayAdapter.setReplyItem(it)
                replayAdapter.setParentPos(bindingAdapterPosition)
                replayAdapter.setLikeShow(postType != ConstVariable.TYPE_CLUB)
                binding.rcReplyList.adapter = replayAdapter
            }
        }
    }

    fun setDefaultClick(commentItem: ClubReplyData){
        binding.ivCommentOption.setOnClickListener {
            listener?.onOptionsClick(commentItem, bindingAdapterPosition)
        }
        binding.ivProfileThumbnail.setOnClickListener {
            listener?.onProfileClick(commentItem)
        }
        binding.tvNickName.setOnClickListener {
            listener?.onProfileClick(commentItem)
        }
        binding.tvTranslate.setOnClickListener {
            listener?.onTranslateClick(commentItem,bindingAdapterPosition)
        }
    }

    //번역 상태
    fun setTranslateChange(commentItem : ClubReplyData){
        if(commentItem.translateYn == true){
            binding.tvTranslate.text=itemView.context.getString(R.string.b_do_translate_cancel)
        }else{
            binding.tvTranslate.text=itemView.context.getString(R.string.b_do_translate)
        }
        binding.tvContent.text=commentItem.content
    }

    fun setShowHideLikeOption(showHide :Boolean){
        binding.llLikeContainer.visibility=if(showHide) View.VISIBLE else View.GONE
        binding.ivDisLikeIcon.visibility=if(showHide) View.VISIBLE else View.GONE
    }

    //대댓번역
    fun setTranslateReplyChange(position :Int,commentItem : ClubReplyData){
        val replyHolder = binding.rcReplyList.findViewHolderForAdapterPosition(position)
        if(replyHolder is CommentReplyVH){
            commentItem.childReplyList?.let {
                replyHolder.setClubTranslateStringChange(it[position],bindingAdapterPosition)
            }
        }
    }
}