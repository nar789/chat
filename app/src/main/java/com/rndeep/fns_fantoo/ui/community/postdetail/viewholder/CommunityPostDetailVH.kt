package com.rndeep.fns_fantoo.ui.community.postdetail.viewholder

import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.PostThumbnail
import com.rndeep.fns_fantoo.data.remote.model.community.*
import com.rndeep.fns_fantoo.databinding.DetailCommunityPostContentLayoutBinding
import com.rndeep.fns_fantoo.ui.community.postdetail.CommunityDetailPostAdapter
import com.rndeep.fns_fantoo.ui.common.post.PostDetailHashTagAdapter
import com.rndeep.fns_fantoo.ui.common.post.PostDetailImageAdapter
import com.rndeep.fns_fantoo.ui.common.post.PostDetailOGTagAdapter
import com.rndeep.fns_fantoo.ui.community.postdetail.CommunityDetailPostFragmentDirections
import com.rndeep.fns_fantoo.utils.HorizontalMarginItemDecoration
import com.rndeep.fns_fantoo.utils.TimeUtils
import com.rndeep.fns_fantoo.utils.addSingleItemDecoRation
import com.rndeep.fns_fantoo.utils.setProfileAvatar
import timber.log.Timber

//커뮤니티 뷰 홀더
class CommunityPostDetailVH constructor(
    private val binding : DetailCommunityPostContentLayoutBinding,
    private val fullScreenListener: PostDetailImageAdapter.OnAttachListClickListener?,
    private val postOptionsClickListener : CommunityDetailPostAdapter.OnPostDetailOptionsClickListener?
    ) :RecyclerView.ViewHolder(binding.root) {

    private val postDetailOGTagAdapter = PostDetailOGTagAdapter()

    //이미지 어댑터
    private val postDetailImageAdapter = PostDetailImageAdapter()

    //태그 어댑터
    private val postDetailHashTagAdapter = PostDetailHashTagAdapter()
    private var tagItemDecoration: HorizontalMarginItemDecoration

    init {
        binding.rcOGTag.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
        binding.rcOGTag.adapter = postDetailOGTagAdapter

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

    fun communityPostBind(postItem : BoardPostDetailData,attachList :List<DetailAttachList>?,hashTagList : List<BoardPostDetailHashTag>?,ogTagList : List<PostDetailOpenGraphTag>?){

        goneView()
        //작성자 썸네일
        val imageUrl = if(postItem.userPhoto!=null) itemView.context.getString(R.string.imageUrlBase,postItem.userPhoto)
        else null
        setProfileAvatar(binding.ivPostAuthThumbnail,imageUrl)

        //커뮤니티의 카테고리 명
        binding.tvPostNickname.text=postItem.code

        //닉네임
        binding.tvGroupName.text=postItem.userNick

        //시간
        binding.tvCreateTime.text= TimeUtils.diffTimeWithCurrentTime(postItem.createDate)

        if(postItem.userBlockYn ==true || postItem.pieceBlockYn==true){
            setBlindContentItem(postItem)
        }else{
            setContentItem(postItem,attachList, hashTagList, ogTagList)
        }


        //하단 좋아요 아너 코멘트
        setLikeIconStateChange(postItem)
        setDisLikeIconStateChange(postItem)
        setHonorIconStateChange(postItem)
        binding.tvCommentCount.text=postItem.replyCnt?.toString() ?:"0"
        settingDefaultClickFun(postItem)
    }

    fun setContentItem(postItem: BoardPostDetailData, attachList: List<DetailAttachList>?, hashTagList: List<BoardPostDetailHashTag>?, ogTagList:List<PostDetailOpenGraphTag>?){
        binding.tvPostContent.visibility= View.VISIBLE
        binding.rcPostImage.visibility= View.VISIBLE
        binding.rcPostHashTag.visibility= View.VISIBLE
        //제목
        binding.tvPostTitle.setTextAppearance(R.style.Title51622Medium)
        binding.tvPostTitle.setTextColor(itemView.context.getColor(R.color.gray_870))
        binding.tvPostTitle.text=postItem.title

        //내용
        binding.tvPostContent.text=postItem.content

        if(ogTagList != null && ogTagList.isNotEmpty()){
            binding.rcOGTag.visibility = View.VISIBLE
            postDetailOGTagAdapter.submitList(ogTagList)
        }

        //이미지 or 영상
        if((attachList == null) || attachList.isEmpty()){
            binding.rcPostImage.visibility=View.GONE
        }else{
            binding.rcPostImage.visibility= View.VISIBLE
            val contentItemUrls =  attachList.map {
                val url =when(it.archiveType){
                    "image"->{
                        if(it.id !=null) itemView.context.getString(R.string.imageUrlBase,it.id) else ""
                    }
                    "video"->{
                        if(it.id !=null) itemView.context.getString(R.string.videoUrlHLSBase,it.id) else ""
                    }
                    else ->{
                       return
                    }
                }
                PostThumbnail(it.archiveType,url)
            }
            postDetailImageAdapter.submitList(contentItemUrls?: arrayListOf())
        }

        //해쉬태그
        if((hashTagList == null) || hashTagList.isEmpty()){
            binding.rcPostHashTag.visibility= View.GONE
        }else{
            binding.rcPostHashTag.visibility= View.VISIBLE

            val hashTags = hashTagList?.map {
                it.tagText
            }
            postDetailHashTagAdapter.setHashTag(hashTags?: arrayListOf())
        }


    }

    fun setBlindContentItem(postItem: BoardPostDetailData){
        binding.tvPostContent.visibility= View.GONE
        binding.rcPostImage.visibility= View.GONE
        binding.rcPostHashTag.visibility= View.GONE

        binding.tvPostTitle.setTextAppearance(R.style.Body21420Regular)
        binding.tvPostTitle.setTextColor(itemView.context.getColor(R.color.gray_400))
        binding.tvPostTitle.text=postItem.title
    }

    //좋아요 상태 변화
    fun setLikeIconStateChange(postItem: BoardPostDetailData){
        if(postItem.myLikeYn!=null){
            binding.ivLikeIcon.setColorFilter(itemView.context.getColor(if(postItem.myLikeYn!!)
                R.color.state_active_primary_default else R.color.state_disabled_gray_200))
            binding.tvLikeCount.setTextColor(itemView.context.getColor(if(postItem.myLikeYn!!)
                R.color.state_active_primary_default else R.color.state_active_gray_700))
            binding.tvLikeCount.text = ((postItem.likeCnt?:0) - (postItem.dislikeCnt ?: 0)).toString()
        }else{
            binding.ivLikeIcon.setColorFilter(itemView.context.getColor(R.color.state_disabled_gray_200))
            binding.tvLikeCount.setTextColor(itemView.context.getColor(R.color.state_active_gray_700))
            binding.tvLikeCount.text = ((postItem.likeCnt?:0) - (postItem.dislikeCnt ?: 0)).toString()
        }
    }

    //싫어요 상태 변화
    fun setDisLikeIconStateChange(postItem: BoardPostDetailData){
        if(postItem.myDisLikeYn!=null){
            binding.ivDisLikeIcon.setColorFilter(itemView.context.getColor(if(postItem.myDisLikeYn!!)
                R.color.state_active_gray_700 else R.color.state_disabled_gray_200))
        }else{
            binding.ivDisLikeIcon.setColorFilter(itemView.context.getColor(R.color.state_disabled_gray_200))
        }
    }

    //아너 상태 변화
    fun setHonorIconStateChange(postItem: BoardPostDetailData){
        if(postItem.myHonorYn!=null){
            binding.ivHonorIcon.setColorFilter(itemView.context.getColor(if(postItem.myHonorYn!!)
                R.color.state_active_primary_default else R.color.state_disabled_gray_200))
            binding.tvHonorCount.setTextColor(itemView.context.getColor(if(postItem.myHonorYn!!)
                R.color.state_active_primary_default else R.color.state_active_gray_700))
        }else{
            binding.ivHonorIcon.setColorFilter(itemView.context.getColor(R.color.state_disabled_gray_200))
            binding.tvHonorCount.setTextColor(itemView.context.getColor(R.color.state_active_gray_700))
        }
        binding.tvHonorCount.text = postItem.honorCnt?.toString()?:"0"
    }

    fun setTranslateStateChange(postItem: BoardPostDetailData){
        if(postItem.translateYn!=null){
            binding.ivTranslate.setColorFilter(itemView.context.getColor(if(postItem.translateYn!!)
                R.color.state_active_primary_default else R.color.gray_700))
        }else{
            binding.ivTranslate.setColorFilter(R.color.gray_700)
        }
        binding.tvPostTitle.text=postItem.title
        binding.tvPostContent.text=postItem.content
    }

    fun getImageAdapter() =postDetailImageAdapter
    fun getBindingImageRc()=binding.rcPostImage

    fun settingDefaultClickFun(postItem: BoardPostDetailData){
        fullScreenListener?.let {
            postDetailImageAdapter.setOnFullScreenClickListener(it)
        }
        binding.ivPostAuthThumbnail.setOnClickListener {
            itemView.findNavController().navigate( CommunityDetailPostFragmentDirections.actionDetailPostToProfile(
                postItem.userNick?:"",postItem.userBlockYn==true,postItem.integUid,postItem.userPhoto?:""
            ))
        }

        //번역 클릭
        binding.ivTranslate.setOnClickListener {
            postOptionsClickListener?.onTransLateClick(
                listOf(postItem.title?:"",postItem.content?:""),
                postItem.translateYn?:false
            )
        }
        //좋아요 클릭
        binding.llLikeContainer.setOnClickListener {
            postOptionsClickListener?.onLikeClick(postItem.myLikeYn)
        }
        //싫어요 클릭
        binding.ivDisLikeIcon.setOnClickListener {
            postOptionsClickListener?.onDisListClick(postItem.myDisLikeYn)
        }
        //아너 클릭
        binding.llHonorContainer.setOnClickListener {
            postOptionsClickListener?.onHonorClick()
        }
        //공유 클릭
        binding.ivShareIcon.setOnClickListener {
            postOptionsClickListener?.onShareClick()
        }
    }

    private fun goneView(){
        binding.ivAD.visibility= View.GONE
        binding.llHonorContainer.visibility=View.GONE
    }

}