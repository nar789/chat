package com.rndeep.fns_fantoo.ui.community.board

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.post.BoardPostData
import com.rndeep.fns_fantoo.databinding.CategoryHomePostBinding
import com.rndeep.fns_fantoo.utils.*

class BoardPostVH(
    private val binding: CategoryHomePostBinding,
    private val  listener: BoardListeners.OnBoardPostClickListener?) : RecyclerView.ViewHolder(binding.root) {

    fun boardPostBind(postItem: BoardPostData, postDBId:Int) {
        settingViewOfType(postItem.activeStatus)
        //topView
        if (postItem.subCode == "C_HOT") {
            binding.ivPostProfileNickName.text = postItem.subCode
            binding.ivPostProfileBoardName.text = postItem.userNick ?: "User01"
        } else {
            binding.ivPostProfileNickName.text = postItem.userNick ?: "User01"
            binding.ivPostProfileBoardName.visibility = View.GONE
            binding.ivGrayDot.visibility = View.GONE
        }
        setProfileAvatar(
            binding.ivPostProfileThumbnail,
            if(postItem.userPhoto!=null) itemView.context.getString(R.string.imageUrlBase, postItem.userPhoto) else null
        )
        binding.ivPostCreateTime.text =
            TimeUtils.diffTimeWithCurrentTime(postItem.createDate)

        //contentView
        binding.tvPostTitle.setTextAppearance(R.style.Body21420Regular)
        if(postItem.subCode=="C_HOT"){
            binding.tvPostTitle.text=postItem.title ?: itemView.context.getString(R.string.j_notitle)
        }else{
            settingHeadTitle(postItem.title,postItem.subCode,postItem.attachYn==true)
        }
        binding.clOgTagContainer.visibility=View.GONE
        binding.tvPostContent.visibility=View.GONE
        binding.flThumbnails.visibility=View.GONE

        binding.ivLikeIcon.setColorFilter(
            if(postItem.likeYn==true)itemView.context.getColor(R.color.state_active_primary_default)
            else itemView.context.getColor(R.color.state_disabled_gray_200)
        )

        binding.ivDisLikeIcon.setColorFilter(
            if(postItem.dislikeYn==true)itemView.context.getColor(R.color.gray_870)
            else itemView.context.getColor(R.color.state_disabled_gray_200)
        )

        binding.ivHonorIcon.setColorFilter(
            if(postItem.honorYn==true)itemView.context.getColor(R.color.state_active_primary_default)
            else itemView.context.getColor(R.color.state_disabled_gray_200)
        )

        binding.tvLikeCount.text=postItem.likeCnt?.toString() ?: "0"
        binding.tvHonorCount.text=postItem.honorCnt?.toString() ?: "0"
        binding.tvCommentCount.text=postItem.replyCnt?.toString() ?: "0"

        clickFunc(postDBId,postItem)
    }

    private fun settingHeadTitle(text :String?, header :String?,isAttach : Boolean) {
        if (header == null) {
            binding.tvPostTitle.text = text ?: itemView.context.getString(R.string.j_notitle)
            return
        }
        val imageHeight = SizeUtils.getDpValue(16f, itemView.context).toInt()
        val imageSpan = if(isAttach){
            itemView.context.getDrawable(R.drawable.posting_file)
                ?.apply {
                    setBounds(0, 0, imageHeight, imageHeight)
                }?.let {
                    VerticalImageSpan(it)
                }
        }else{
            itemView.context.getDrawable(R.drawable.posting_text)
                ?.apply {
                    setBounds(0, 0, imageHeight, imageHeight)
                }?.let {
                    VerticalImageSpan(it)
                }
        }

        val s =if(header=="C_HOT"){
            SpannableString("   ${text}").apply {
                setSpan(imageSpan, 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }else{
            SpannableString("[${header}]   ${text}").apply {
                setSpan(imageSpan, header.length+3, header.length+4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(
                    ForegroundColorSpan(itemView.context.getColor(R.color.primary_500)),
                    0,
                    header.length+2,
                    0
                )
            }
        }
//        s.setSpan(imageSpan, header.length+3, header.length+4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvPostTitle.text=s
    }

    fun deletePostBind(postItem: BoardPostData, postDBId:Int){
        settingViewOfType(postItem.activeStatus)
        binding.ivPostProfileNickName.text = postItem.userNick ?: "User01"
        binding.ivPostProfileBoardName.visibility = View.GONE
        binding.ivGrayDot.visibility = View.GONE

        binding.tvPostTitle.setTextAppearance(R.style.Body21420Regular)
        binding.tvPostTitle.text=postItem.title

        binding.clOgTagContainer.visibility=View.GONE
        binding.tvPostContent.visibility=View.GONE
        binding.flThumbnails.visibility=View.GONE

        binding.ivLikeIcon.setColorFilter(
            if(postItem.likeYn==true)itemView.context.getColor(R.color.state_active_primary_default)
            else itemView.context.getColor(R.color.state_disabled_gray_200)
        )

        binding.ivDisLikeIcon.setColorFilter(
            if(postItem.dislikeYn==true)itemView.context.getColor(R.color.gray_870)
            else itemView.context.getColor(R.color.state_disabled_gray_200)
        )

        binding.ivHonorIcon.setColorFilter(
            if(postItem.honorYn==true)itemView.context.getColor(R.color.state_active_primary_default)
            else itemView.context.getColor(R.color.state_disabled_gray_200)
        )

        binding.tvLikeCount.text=postItem.likeCnt?.toString() ?: "0"
        binding.tvHonorCount.text=postItem.honorCnt?.toString() ?: "0"
        binding.tvCommentCount.text=postItem.replyCnt?.toString() ?: "0"
    }

    private fun settingViewOfType(state :Int){
        when(state){
            1->{
                binding.ivPostProfileNickName.setTextColor(itemView.context.getColor(R.color.gray_900))
                binding.tvPostTitle.setTextColor(itemView.context.getColor(R.color.gray_870))
            }
            0,2->{
                binding.ivPostProfileNickName.setTextColor(itemView.context.getColor(R.color.gray_200))
                binding.tvPostTitle.setTextColor(itemView.context.getColor(R.color.gray_400))
            }
            else ->{
                binding.ivPostProfileNickName.setTextColor(itemView.context.getColor(R.color.gray_900))
                binding.tvPostTitle.setTextColor(itemView.context.getColor(R.color.gray_870))
            }
        }
    }

    private fun clickFunc(postDBId:Int, item : BoardPostData){
        binding.llLikeContainer.setOnClickListener {
            listener?.onLikeClick(postDBId,item,ConstVariable.TYPE_COMMUNITY,bindingAdapterPosition,this)
        }

        binding.ivDisLikeIcon.setOnClickListener {
            listener?.onDisLikeClick(postDBId,item,ConstVariable.TYPE_COMMUNITY,bindingAdapterPosition,this)
        }

        binding.llHonorContainer.setOnClickListener {
            listener?.onHonorClick(postDBId,item,ConstVariable.TYPE_COMMUNITY,bindingAdapterPosition,this)
        }
        binding.ivPostOptionIcon.setOnClickListener {
            listener?.onOptionClick(postDBId,item.integUid,ConstVariable.TYPE_COMMUNITY,bindingAdapterPosition,item.postId,
                item.userBlockYn == true, item.pieceBlockYn == true,item.code)
        }

        binding.rlContentContainer.setOnClickListener {
            listener?.onPostClick(item.code,item.postId,ConstVariable.TYPE_COMMUNITY,"-1")
        }
    }

}