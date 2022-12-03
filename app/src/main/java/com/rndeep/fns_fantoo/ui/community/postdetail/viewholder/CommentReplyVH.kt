package com.rndeep.fns_fantoo.ui.community.postdetail.viewholder

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.ClubReplyData
import com.rndeep.fns_fantoo.data.remote.model.CommunityReplyData
import com.rndeep.fns_fantoo.data.remote.model.community.DetailAttachList
import com.rndeep.fns_fantoo.databinding.PostDetailReplyLayoutBinding
import com.rndeep.fns_fantoo.ui.common.comment.CommentImageAdapter
import com.rndeep.fns_fantoo.ui.common.post.PostDetailReplayAdapter
import com.rndeep.fns_fantoo.utils.TimeUtils
import com.rndeep.fns_fantoo.utils.setProfileAvatar
//대 댓 뷰 홀더
class CommentReplyVH(
    private val binding: PostDetailReplyLayoutBinding,
    private val isLikeShow : Boolean?,
    private val listener: PostDetailReplayAdapter.OnReplyClickListener?,
) : RecyclerView.ViewHolder(binding.root) {

    fun communityCommentBind(
        commentItem: CommunityReplyData,
        parentPosition: Int
    ) {
        setProfileAvatar(binding.ivProfileThumbnail, commentItem.langCode)
        binding.tvNickName.text = commentItem.userNick
        binding.tvCreateDate.text = TimeUtils.diffTimeWithCurrentTime(commentItem.createDate)
        binding.tvContent.text = commentItem.content

        if (commentItem.attachList == null && commentItem.openGraphList == null) binding.rcCommentImage.visibility = View.GONE
        commentItem.attachList?.let {
            if (it.isEmpty() && commentItem.openGraphList.isNullOrEmpty()) {
                binding.rcCommentImage.visibility = View.GONE
                return@let
            } else {
                binding.rcCommentImage.visibility = View.VISIBLE
                binding.rcCommentImage.layoutManager = LinearLayoutManager(itemView.context)
                binding.rcCommentImage.adapter = CommentImageAdapter(it,commentItem.openGraphList).apply {
                    setOnCommentImageClickListener(object :CommentImageAdapter.CommentImageClickListener{
                        override fun onImageClick(imageUrl: String) {
                            listener?.onImageClick(imageUrl)
                        }
                    })
                }
            }
        }
        settingViewType(commentItem.activeStatus)
        if(isLikeShow==true){
            likeDisLikeShowHide(true)
            setReplyLikeStateChange(commentItem)
            setReplyDisLikeStateChange(commentItem)
        }else{
            likeDisLikeShowHide(false)
        }

        if(commentItem.translateYn == true){
            binding.tvTranslate.text=itemView.context.getString(R.string.b_do_translate_cancel)
        }else{
            binding.tvTranslate.text=itemView.context.getString(R.string.b_do_translate)
        }

        if(commentItem.activeStatus ==1 ){
            binding.llLikeContainer.setOnClickListener {
                listener?.onLikeClick(commentItem, parentPosition, bindingAdapterPosition)
            }

            binding.ivDisLikeIcon.setOnClickListener {
                listener?.onDisLikeClick(commentItem, parentPosition, bindingAdapterPosition)
            }
            binding.tvTranslate.setOnClickListener {
                listener?.onTranslate(commentItem.content,parentPosition,bindingAdapterPosition,commentItem.translateYn?:false)
            }
        }
        profileClickListener(
            commentItem.userNick ?: "",
            null,
            null,
            commentItem.userBlockYn == true,
            commentItem.integUid,
            commentItem.userPhoto ?: ""
        )
    }
    fun clubCommentBind(
        commentItem: ClubReplyData,
        parentPosition:Int
    ) {
        setProfileAvatar(binding.ivProfileThumbnail, itemView.context.getString(R.string.imageThumbnailUrl,commentItem.profileImg))
        binding.tvNickName.text = commentItem.nickname
        binding.tvCreateDate.text = TimeUtils.diffTimeWithCurrentTime(commentItem.createDate)
        binding.tvContent.text = commentItem.content

        if (commentItem.attachList == null && commentItem.openGraphItem ==null) binding.rcCommentImage.visibility = View.GONE
        commentItem.attachList?.let {
            if (it.isEmpty() && commentItem.openGraphItem?.isEmpty() != false) {
                binding.rcCommentImage.visibility = View.GONE
                return@let
            } else {
                binding.rcCommentImage.visibility = View.VISIBLE
                binding.rcCommentImage.layoutManager = LinearLayoutManager(itemView.context)
                binding.rcCommentImage.adapter = CommentImageAdapter(it.map {
                    val attachType= when(it.attachType){
                        0->"video"
                        1->"image"
                        2->"link"
                        else -> "image"
                    }
                    DetailAttachList(attachType,it.attach)
                },commentItem.openGraphItem).apply {
                    setOnCommentImageClickListener(object :CommentImageAdapter.CommentImageClickListener{
                        override fun onImageClick(imageUrl: String) {
                            listener?.onImageClick(imageUrl)
                        }
                    })
                }
            }
        }
        binding.tvTranslate.setOnClickListener {
            listener?.onTranslate(commentItem.content,parentPosition,bindingAdapterPosition,commentItem.translateYn?:false)
        }
        settingViewType(commentItem.status)
        likeDisLikeShowHide(false)

        profileClickListener(
            commentItem.nickname,
            commentItem.clubId,
            commentItem.memberId.toString(),
            commentItem.status == 1,
            null,
            commentItem.profileImg
        )

    }

    //좋아요 상태 변화
    fun setReplyLikeStateChange(commentItem: CommunityReplyData) {
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
        binding.tvLikeCount.text = ((commentItem.likeCnt?:0)-(commentItem.dislikeCnt?:0)).toString()
    }

    //싫어요 상태 변화
    fun setReplyDisLikeStateChange(commentItem: CommunityReplyData) {
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

    //번역
    fun setCommunityTranslateStringChange(commentItem: CommunityReplyData, parentPosition :Int){
        if(commentItem.translateYn == true){
            binding.tvTranslate.text=itemView.context.getString(R.string.b_do_translate_cancel)
        }else{
            binding.tvTranslate.text=itemView.context.getString(R.string.b_do_translate)
        }
        binding.tvContent.text=commentItem.content
        binding.tvTranslate.setOnClickListener {
            listener?.onTranslate(commentItem.content,parentPosition,bindingAdapterPosition,commentItem.translateYn?:false)
        }
    }
    fun setClubTranslateStringChange(commentItem: ClubReplyData,parentPosition :Int){
        if(commentItem.translateYn == true){
            binding.tvTranslate.text=itemView.context.getString(R.string.b_do_translate_cancel)
        }else{
            binding.tvTranslate.text=itemView.context.getString(R.string.b_do_translate)
        }
        binding.tvContent.text=commentItem.content
        binding.tvTranslate.setOnClickListener {
            listener?.onTranslate(commentItem.content,parentPosition,bindingAdapterPosition,commentItem.translateYn?:false)
        }
    }

    private fun profileClickListener(
        nickName: String,
        clubId: String?,
        memberId: String?,
        isBlock: Boolean,
        targetUid: String?,
        userPhoto: String
    ) {
        binding.ivProfileThumbnail.setOnClickListener {
            listener?.onProfileClick(nickName,clubId,memberId,isBlock,targetUid,userPhoto)
        }
        binding.tvNickName.setOnClickListener {
            listener?.onProfileClick(nickName,clubId,memberId,isBlock,targetUid,userPhoto)
        }
        binding.tvCreateDate.setOnClickListener {
            listener?.onProfileClick(nickName,clubId,memberId,isBlock,targetUid,userPhoto)
        }
    }

    private fun settingViewType(viewState : Int){
        when(viewState){
            1 ->{
                binding.tvNickName.setTextColor(itemView.context.getColor(R.color.gray_900))
                binding.tvContent.setTextColor(itemView.context.getColor(R.color.gray_870))
                binding.tvTranslate.visibility=View.VISIBLE
            }
            2 -> {
                binding.tvNickName.setTextColor(itemView.context.getColor(R.color.gray_200))
                binding.tvContent.setTextColor(itemView.context.getColor(R.color.gray_300))
                binding.tvTranslate.visibility=View.GONE
            }
            else ->{
                binding.tvNickName.setTextColor(itemView.context.getColor(R.color.gray_900))
                binding.tvContent.setTextColor(itemView.context.getColor(R.color.gray_870))
                binding.tvTranslate.visibility=View.VISIBLE
            }
        }
    }

    //좋아요 싫어요 보이기 여부
    private fun likeDisLikeShowHide(isShow :Boolean){
        binding.llLikeContainer.visibility=if(isShow)View.VISIBLE else View.GONE
        binding.ivDisLikeIcon.visibility=if(isShow)View.VISIBLE else View.GONE
    }

}