package com.rndeep.fns_fantoo.ui.club.post

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.PostThumbnail
import com.rndeep.fns_fantoo.data.remote.model.community.*
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostAttachList
import com.rndeep.fns_fantoo.databinding.DetailClubPostContentLayoutBinding
import com.rndeep.fns_fantoo.ui.common.post.PostDetailHashTagAdapter
import com.rndeep.fns_fantoo.ui.common.post.PostDetailImageAdapter
import com.rndeep.fns_fantoo.utils.*

class ClubPostDetailVH constructor(
    private val binding : DetailClubPostContentLayoutBinding,
    private val fullScreenListener: PostDetailImageAdapter.OnAttachListClickListener?,
    private val postOptionsClickListener : ClubPostDetailAdapter.OnClubPostDetailOptionsClickListener?
) : RecyclerView.ViewHolder(binding.root) {

    private val postDetailImageAdapter = PostDetailImageAdapter()

    //태그 어댑터
    private val postDetailHashTagAdapter = PostDetailHashTagAdapter()
    private var tagItemDecoration: HorizontalMarginItemDecoration

    init {
        //컨텐츠 이미지 or 비디오 adapter
        binding.rcPostImage.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
        binding.rcPostImage.adapter = postDetailImageAdapter

        tagItemDecoration = HorizontalMarginItemDecoration(
            20f,
            4f,
            itemView.context
        )

        //hashTag
        binding.rcPostHashTag.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        binding.rcPostHashTag.addSingleItemDecoRation(tagItemDecoration)
        binding.rcPostHashTag.adapter = postDetailHashTagAdapter
    }

    fun clubPostBind(postItem : ClubPostDetailData,type : String){
        goneView(type)

        //작성자 썸네일
        if(type == ConstVariable.TYPE_CLUB_CHALLENGE){
            setProfileAvatar(binding.ivPostAuthThumbnail,null,R.drawable.profile_character_manager)
        }else{
            val imageUrl = if(postItem.profileImg!=null) itemView.context.getString(R.string.imageUrlBase,postItem.profileImg)
            else null
            setProfileAvatar(binding.ivPostAuthThumbnail,imageUrl)
        }


        binding.tvPostNickname.text=if(type==ConstVariable.TYPE_CLUB_CHALLENGE){
            "FANTOO"
        }else{
            postItem.nickname
        }

        //시간
        binding.tvCreateTime.text= TimeUtils.diffTimeWithCurrentTime(postItem.createDate)



        if(postItem.status!=0 || (postItem.blockType?:0)!=0){
            setBlindContentItem(postItem)
        }else{
            setContentItem(postItem,postItem.attachList, postItem.hashtagList)
        }

        binding.tvCommentCount.text=postItem.replyCount?.toString() ?:"0"
        settingDefaultClickFun(postItem)
    }

    fun setContentItem(postItem: ClubPostDetailData, attachList: List<ClubPostAttachList>?, hashTagList: List<String>?){
        binding.tvPostContent.visibility= View.VISIBLE

        binding.tvArchiveTitle.text=postItem.categoryName2
        //제목 ,내용
        binding.tvPostTitle.setTextAppearance(R.style.Title51622Medium)
        binding.tvPostTitle.setTextColor(itemView.context.getColor(R.color.gray_870))

        setTranslateStateChange(postItem)

        //이미지 or 영상
        if((attachList == null) || attachList.isEmpty()){
            binding.rcPostImage.visibility=View.GONE
        }else{
            binding.rcPostImage.visibility= View.VISIBLE
            val contentItemUrls =  attachList.map {
                var url = ""
                val attachType = when(it.attachType){
                    0->{
                        url =if(it.attach !=null) itemView.context.getString(R.string.imageUrlBase,it.attach) else ""
                        "image"
                    }
                    1->{
                        url =if(it.attach !=null) itemView.context.getString(R.string.videoUrlHLSBase,it.attach) else ""
                        "video"
                    }
                    2->{
                        "link"
                    }
                    else ->{
                        url =if(it.attach !=null) itemView.context.getString(R.string.imageUrlBase,it.attach) else ""
                        "image"
                    }
                }
                PostThumbnail(attachType,url)
            }

            postDetailImageAdapter.submitList(contentItemUrls)
        }

        //해쉬태그
        if((hashTagList == null) || hashTagList.isEmpty()){
            binding.rcPostHashTag.visibility= View.GONE
        }else{
            binding.rcPostHashTag.visibility= View.VISIBLE

            postDetailHashTagAdapter.setHashTag(hashTagList?: arrayListOf())
        }
    }

    fun setBlindContentItem(postItem: ClubPostDetailData){
        binding.tvPostContent.visibility= View.GONE
        binding.rcPostImage.visibility= View.GONE
        binding.rcPostHashTag.visibility= View.GONE

        binding.tvArchiveTitle.text=postItem.categoryName2
        binding.tvPostTitle.setTextAppearance(R.style.Body21420Regular)
        binding.tvPostTitle.setTextColor(itemView.context.getColor(R.color.gray_400))
        postItem.subject=if(postItem.status==1){
            itemView.context.getString(R.string.se_s_post_hide_by_report)
        }else if(postItem.status==2){
            itemView.context.getString(R.string.se_j_deleted_comment_by_writer)
        }else if(postItem.status==3){
            itemView.context.getString(R.string.delete_club_master)
        }else if(postItem.blockType==1 ||postItem.blockType==2){
            itemView.context.getString(R.string.se_c_blocked_post)
        }else{
            itemView.context.getString(R.string.se_c_blocked_post)
        }
        postItem.content=""
        binding.tvPostTitle.text=postItem.subject
    }

    //아너 상태 변화
    fun setHonorIconStateChange(postItem: ClubPostDetailData){
        if(postItem.myHonorYn!=null){
            binding.ivHonorIcon.setColorFilter(itemView.context.getColor(if(postItem.myHonorYn!!)
                R.color.state_active_primary_default else R.color.state_disabled_gray_200))
            binding.tvHonorCount.setTextColor(itemView.context.getColor(if(postItem.myHonorYn!!)
                R.color.state_active_primary_default else R.color.state_active_gray_700))
        }else{
            binding.ivHonorIcon.setColorFilter(itemView.context.getColor(R.color.state_disabled_gray_200))
            binding.tvHonorCount.setTextColor(itemView.context.getColor(R.color.state_active_gray_700))
        }
        binding.tvHonorCount.text = postItem.honorCount?.toString()?:"0"
    }

    fun setTranslateStateChange(postItem: ClubPostDetailData){
        if(postItem.translateYn!=null){
            binding.ivTranslate.setColorFilter(itemView.context.getColor(if(postItem.translateYn!!)
                R.color.state_active_primary_default else R.color.gray_700))
        }else{
            binding.ivTranslate.setColorFilter(R.color.gray_700)
        }
        binding.tvPostTitle.text=postItem.subject
        binding.tvPostContent.text=postItem.content
    }

    fun getImageAdapter() =postDetailImageAdapter
    fun getBindingImageRc()=binding.rcPostImage

    fun settingDefaultClickFun(postItem: ClubPostDetailData){
        fullScreenListener?.let {
            postDetailImageAdapter.setOnFullScreenClickListener(it)
        }
        binding.ivPostAuthThumbnail.setOnClickListener {
            postOptionsClickListener?.onProfileClick(postItem.clubId,postItem.memberId,postItem.nickname,postItem.blockType==1,postItem.profileImg?:"")
        }
        binding.tvPostNickname.setOnClickListener {
            postOptionsClickListener?.onProfileClick(postItem.clubId,postItem.memberId,postItem.nickname,postItem.blockType==1,postItem.profileImg?:"")
        }
        binding.tvCreateTime.setOnClickListener {
            postOptionsClickListener?.onProfileClick(postItem.clubId,postItem.memberId,postItem.nickname,postItem.blockType==1,postItem.profileImg?:"")
        }
        //번역 클릭
        binding.ivTranslate.setOnClickListener {
            val items = listOf(
                postItem.subject,
                postItem.content
            )
            postOptionsClickListener?.onTransLateClick(items,postItem.translateYn?:false)
        }
        binding.ivShareIcon.setOnClickListener {
            postOptionsClickListener?.onShareClick()
        }
        binding.clArchiveTitleContainer.setOnClickListener {
            postOptionsClickListener?.onCategoryTextClick(postItem.clubId,postItem.categoryCode)
        }
    }

    private fun goneView(type :String){
        binding.ivAD.visibility= View.GONE
        binding.llLikeContainer.visibility=View.GONE
        binding.ivDisLikeIcon.visibility=View.GONE
        binding.llHonorContainer.visibility=View.GONE
        when(type){
            ConstVariable.TYPE_CLUB_CHALLENGE->{
                binding.clArchiveTitleContainer.visibility=View.GONE
                binding.rcPostHashTag.visibility=View.GONE
                binding.ivCommentIcon.visibility=View.GONE
                binding.tvCommentCount.visibility=View.GONE
            }
        }
    }

}