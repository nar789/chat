package com.rndeep.fns_fantoo.ui.club.comment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.ClubReplyData
import com.rndeep.fns_fantoo.data.remote.model.community.DetailAttachList
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostAttachList
import com.rndeep.fns_fantoo.data.remote.model.post.CommunityOpenGraphItem
import com.rndeep.fns_fantoo.databinding.CommunityCommentLayoutBinding
import com.rndeep.fns_fantoo.ui.common.comment.CommentImageAdapter
import com.rndeep.fns_fantoo.utils.TimeUtils
import com.rndeep.fns_fantoo.utils.setProfileAvatar

class ClubCommentVH(
    private val binding: CommunityCommentLayoutBinding,
    private val listener : ClubCommentAdapter.ClubCommentReplyClickListener?,
    private val isUser : Boolean
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item : ClubReplyData){
        goneView()
        //최상단 뷰는 gray 25 , 나머지는 gray 50 으로 변경
        if(item.depth==1){
            binding.rlCommentOption.visibility=View.VISIBLE
            binding.tvCommentCount.text = item.replyCount?.toString() ?:"0"
            binding.root.setBackgroundColor(itemView.context.getColor(R.color.gray_25))
        }else{
            binding.rlCommentOption.visibility=View.GONE
            binding.root.setBackgroundColor(itemView.context.getColor(R.color.state_disabled_gray_50))
        }

        setProfileAvatar(binding.ivProfileThumbnail,itemView.context.getString(R.string.imageUrlBase,item.profileImg))

        binding.tvNickName.text=item.nickname

        binding.tvCreateDate.text=TimeUtils.diffTimeWithCurrentTime(item.createDate)

        binding.tvContent.text=if(item.status==2) itemView.context.getString(R.string.se_j_deleted_comment_by_writer)
                            else if(item.status==3) itemView.context.getString(R.string.delete_club_master)
                            else if(item.status==1) itemView.context.getString(R.string.se_s_comment_hide_by_report)
                            else if(item.blockType==1||
                                    item.blockType==2||
                                    item.blockType==3||
                                    item.blockType==4) itemView.context.getString(R.string.se_c_blocked_comment)
                            else item.content
        if (item.status!=0 ||item.blockType==1||item.blockType==2){
            settingBlockClickListener(item)
        }else{
            settingCLickListener(item)
        }

        if(isUser){
            binding.ivCommentOption.visibility=View.VISIBLE
        }else{
            binding.ivCommentOption.visibility=View.GONE
        }

        if(item.status!=0 ||item.blockType==1||item.blockType==2){
            settingBlockView()
        }else{
            setDefaultView(item.attachList,item.openGraphItem)
        }
    }

    private fun goneView(){
        binding.llLikeContainer.visibility= View.GONE
        binding.ivDisLikeIcon.visibility=View.GONE
    }

    fun setDefaultView(attachList: List<ClubPostAttachList>?,openGraphItem: List<CommunityOpenGraphItem>?){
        binding.tvTranslate.visibility = View.VISIBLE
        binding.rcCommentImage.visibility = View.VISIBLE
        binding.tvNickName.setTextColor(itemView.context.getColor(R.color.gray_900))
        binding.tvContent.setTextColor(itemView.context.getColor(R.color.gray_870))

        //댓글 이미지
        if (attachList == null && openGraphItem == null) binding.rcCommentImage.visibility = View.GONE
        attachList?.let { clubAttachList ->
            if (clubAttachList.isEmpty() && openGraphItem?.isEmpty() != false) {
                binding.rcCommentImage.visibility = View.GONE
                return@let
            } else {
                binding.rcCommentImage.visibility = View.VISIBLE
                binding.rcCommentImage.layoutManager = LinearLayoutManager(itemView.context)
                binding.rcCommentImage.adapter = CommentImageAdapter(clubAttachList.map {
                    val attachType =when(it.attachType){
                        0->"image"
                        1->"video"
                        2->"link"
                        else->"image"
                    }
                    DetailAttachList(attachType,it.attach)
                },openGraphItem).apply {
                    setOnCommentImageClickListener(object :CommentImageAdapter.CommentImageClickListener{
                        override fun onImageClick(imageUrl: String) {
                            listener?.onCommentImageClick(imageUrl)
                        }
                    })
                }
            }
        }
    }
    fun settingBlockClickListener(item : ClubReplyData){
        binding.ivCommentOption.setOnClickListener {
            listener?.onReplyOptionClick(item,bindingAdapterPosition)
        }
    }

    fun settingCLickListener(item : ClubReplyData){
        binding.ivCommentOption.setOnClickListener {
            listener?.onReplyOptionClick(item,bindingAdapterPosition)
        }
        binding.tvTranslate.setOnClickListener {
            listener?.onTranslateClick(item,bindingAdapterPosition)
        }
    }

    fun changeTranslateState(item : ClubReplyData){
        if(item.translateYn==true){
            binding.tvTranslate.text=itemView.context.getString(R.string.b_do_translate_cancel)
        }else{
            binding.tvTranslate.text=itemView.context.getString(R.string.b_do_translate)
        }
        binding.tvContent.text=item.content
        binding.tvTranslate.setOnClickListener {
            listener?.onTranslateClick(item,bindingAdapterPosition)
        }
    }

    fun settingBlockView(){
        binding.tvNickName.setTextColor(itemView.context.getColor(R.color.gray_200))
        binding.tvContent.setTextColor(itemView.context.getColor(R.color.gray_300))
        binding.tvTranslate.visibility = View.GONE
        binding.rcCommentImage.visibility = View.GONE
    }
}