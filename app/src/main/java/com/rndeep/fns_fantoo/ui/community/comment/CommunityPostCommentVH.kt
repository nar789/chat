package com.rndeep.fns_fantoo.ui.community.comment

import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.CommunityReplyData
import com.rndeep.fns_fantoo.data.remote.model.community.DetailAttachList
import com.rndeep.fns_fantoo.data.remote.model.post.CommunityOpenGraphItem
import com.rndeep.fns_fantoo.databinding.CommunityCommentLayoutBinding
import com.rndeep.fns_fantoo.ui.common.comment.CommentImageAdapter
import com.rndeep.fns_fantoo.utils.TimeUtils
import com.rndeep.fns_fantoo.utils.setProfileAvatar

class CommunityPostCommentVH(
    private val binding: CommunityCommentLayoutBinding,
    private val replyListener : CommunityCommentAdapter.OnCommunityCommentReplyClickListener?,
    private val isLoginUser :Boolean
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(replyItem: CommunityReplyData) {

        //최상단 뷰는 gray 25 , 나머지는 gray 50 으로 변경
        if(replyItem.parentReplyId==0){
            binding.root.setBackgroundColor(itemView.context.getColor(R.color.gray_25))
            binding.llCommentContainer.visibility= View.VISIBLE
        }else{
            binding.root.setBackgroundColor(itemView.context.getColor(R.color.state_disabled_gray_50))
            binding.llCommentContainer.visibility= View.GONE
        }

        setProfileAvatar(
            binding.ivProfileThumbnail,
            if (replyItem.userPhoto != null) itemView.context.getString(
                R.string.imageUrlBase,
                replyItem.userPhoto
            )
            else replyItem.userPhoto
        )

        binding.tvNickName.text = replyItem.userNick
        binding.tvCreateDate.text = TimeUtils.diffTimeWithCurrentTime(replyItem.createDate)

        if(replyItem.userBlockYn==true || replyItem.pieceBlockYn ==true){
            settingBlockView()
        }else{
            changeViewStyle(replyItem.activeStatus,replyItem.attachList,replyItem.openGraphList)
        }

        binding.tvContent.text = replyItem.content

        if (replyItem.activeStatus == 2){
            binding.ivCommentOption.visibility=View.INVISIBLE
            showHideLikeContainer(false)
        }else if(isLoginUser){
            binding.ivCommentOption.visibility=View.VISIBLE
            showHideLikeContainer(true)
        }else{
            binding.ivCommentOption.visibility=View.INVISIBLE
            showHideLikeContainer(true)
        }

        settingLikeDislikeColor(replyItem)
        settingTranslateChange(replyItem)


        if(replyItem.parentReplyId==0){
            binding.tvCommentCount.visibility= View.VISIBLE
            binding.tvCommentCount.text = replyItem.replyCnt?.toString() ?:"0"
        }else{
            binding.tvCommentCount.visibility= View.GONE
        }

        if (replyItem.userBlockYn==true || replyItem.pieceBlockYn==true){
            settingBlockClickListener(replyItem)
        }else{
            if(replyItem.activeStatus==1){
                settingCLickListener(replyItem)
            }
        }

    }

    fun settingLikeDislikeColor(item : CommunityReplyData){
        binding.tvLikeCount.text = ((item.likeCnt?:0) - (item.dislikeCnt?:0)).toString()
        binding.tvLikeCount.setTextColor(
            if(item.myLikeYn==true)itemView.context.getColor(R.color.state_active_primary_default)
            else itemView.context.getColor(R.color.state_active_gray_700)
        )

        binding.ivLikeIcon.setColorFilter(
            if(item.myLikeYn==true)itemView.context.getColor(R.color.state_active_primary_default)
            else itemView.context.getColor(R.color.state_disabled_gray_200)
        )

        binding.ivDisLikeIcon.setColorFilter(
            if(item.myDisLikeYn == true)itemView.context.getColor(R.color.state_active_gray_700)
            else itemView.context.getColor(R.color.state_disabled_gray_200)
        )
    }

    fun settingTranslateChange(item : CommunityReplyData){
        if(item.translateYn == true){
            binding.tvTranslate.text=itemView.context.getString(R.string.b_do_translate_cancel)
        }else{
            binding.tvTranslate.text=itemView.context.getString(R.string.b_do_translate)
        }
        binding.tvContent.text=item.content

    }

    fun settingBlockClickListener(item : CommunityReplyData){
        binding.ivCommentOption.setOnClickListener {
            replyListener?.onReplyOptionClick(item,bindingAdapterPosition)
        }
    }

    fun settingCLickListener(item : CommunityReplyData){
        binding.ivCommentOption.setOnClickListener {
            replyListener?.onReplyOptionClick(item,bindingAdapterPosition)
        }
        binding.llLikeContainer.setOnClickListener {
            replyListener?.onReplyLikeClick(item,bindingAdapterPosition)
        }
        binding.ivDisLikeIcon.setOnClickListener {
            replyListener?.onReplyDisListClick(item,bindingAdapterPosition)
        }
        binding.tvTranslate.setOnClickListener {
            replyListener?.onTranslateClick(item,bindingAdapterPosition)
        }
    }


    private fun changeViewStyle(state :Int,attachList: List<DetailAttachList>?, openGraphList: List<CommunityOpenGraphItem>?){
        when(state){
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
                setDefaultView(attachList,openGraphList)
            }
        }
    }

    fun setDefaultView(attachList: List<DetailAttachList>?,openGraphItem :List<CommunityOpenGraphItem>?){
        binding.tvTranslate.visibility = View.VISIBLE
        binding.rcCommentImage.visibility = View.VISIBLE

        //댓글 이미지
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
                            replyListener?.onImageClick(imageUrl)
                        }
                    })
                }
            }
        }
    }

    fun settingBlockView(){
        binding.tvNickName.setTextColor(itemView.context.getColor(R.color.gray_200))
        binding.tvContent.setTextColor(itemView.context.getColor(R.color.gray_300))
        binding.tvTranslate.visibility = View.GONE
        binding.rcCommentImage.visibility = View.GONE
    }

    fun showHideLikeContainer(isShow : Boolean){
        binding.llLikeContainer.visibility=if(isShow) View.VISIBLE else View.GONE
        binding.ivDisLikeIcon.visibility=if(isShow) View.VISIBLE else View.GONE
    }
}