package com.rndeep.fns_fantoo.ui.common.post

import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostAttachList
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostData
import com.rndeep.fns_fantoo.databinding.CategoryHomePostBinding
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.utils.*
import com.rndeep.fns_fantoo.utils.ConstVariable.VIEW_CLUB_ARCHIVE_POST

/**
 *  클럽뷰는 기본적으로 좋아요 싫어요가 없음
 */
class PostClubVH(
    private val binding: CategoryHomePostBinding,
    private val optionsClickListener: BoardListeners.OnBoardPostClickListener?,
    private val postListOrFeed: String,
    private val viewType: String,
) : RecyclerView.ViewHolder(binding.root) {

    fun clubViewBind(postItem: ClubPostData?, dbPkId: Int) {
        //item 이 존재하지 않을시 view 를 Gone 처리 후 return
        noItem(postItem)
        val item : ClubPostData =postItem!!
        //10.7 일자로 더보기 삭제
        binding.ivPostOptionIcon.visibility=View.GONE

        changeViewBackground()

        //topView
        setProfileAvatar(
            binding.ivPostProfileThumbnail,
            itemView.context.getString(R.string.imageUrlBase, item.profileImg)
        )
        settingTopText(item)

        //contentView
        if (item.status!=0 ||item.blockType ==1 || item.blockType ==2) {
            settingBlindItem(item)
        } else {
            settingViewType(postListOrFeed)
            attachHeaderState(item)
        }
        //bottom
        binding.llOptionContainer.visibility = View.GONE
        binding.llHonorContainer.visibility = View.GONE

        binding.tvCommentCount.text = item.replyCount.toString()

        clickOfViewType(item, dbPkId)
    }

    private fun changeViewBackground(){
        val valueOfPostView : Triple<Drawable?,Float,Float> =when(postListOrFeed){
            ConstVariable.POST_TYPE_FEED->{
                if(viewType==VIEW_CLUB_ARCHIVE_POST){
                    Triple(itemView.context.getDrawable(R.color.gray_25),16f,15f)
                }else{
                    Triple(itemView.context.getDrawable(R.drawable.bg_main_contents),23f,25f)
                }
            }
            ConstVariable.POST_TYPE_LIST->{
                Triple(itemView.context.getDrawable(R.color.gray_25),16f,15f)
            }
            else -> {
                Triple(itemView.context.getDrawable(R.color.gray_25),16f,15f)
            }
        }
        binding.clPostContainer.background=valueOfPostView.first
        binding.clPostContainer.setPadding(
            0,
            SizeUtils.getDpValue(valueOfPostView.second,itemView.context).toInt(),
            0,
            SizeUtils.getDpValue(valueOfPostView.third,itemView.context).toInt()
        )
    }

    private fun settingBlindItem(item: ClubPostData) {
        binding.tvPostContent.visibility = View.GONE
        binding.flThumbnails.visibility = View.GONE
        binding.clOgTagContainer.visibility = View.GONE
        binding.tvPostTitle.setTextAppearance(R.style.Body21420Regular)
        binding.tvPostTitle.setTextColor(itemView.context.getColor(R.color.gray_400))
        binding.clPostContainer.elevation = 0f
        binding.tvPostTitle.text = if(item.status==1){
            itemView.context.getString(R.string.se_s_post_hide_by_report)
        }else if(item.status==2){
            itemView.context.getString(R.string.se_j_deleted_comment_by_writer)
        }else if(item.status==3) {
            itemView.context.getString(R.string.delete_club_master)
        }else if(item.blockType==1){
            itemView.context.getString(R.string.c_block_account_title)
        }else if(item.blockType==2){
            itemView.context.getString(R.string.se_c_blocked_post)
        }else{
            item.subject
        }

    }

    private fun settingTopText(item: ClubPostData) {
        val blockColor = if(item.status!=0 || item.blockType !=0){
                itemView.context.getColor(R.color.gray_200)
        }else{
                itemView.context.getColor(R.color.gray_400)
        }
        when (viewType) {
            //홈탭 메인
            ConstVariable.VIEW_HOME_MAIN -> {
                binding.ivPostProfileBoardName.visibility = View.VISIBLE
                binding.ivGrayDot.visibility = View.VISIBLE
                binding.ivPostProfileNickName.text = item.clubName
                binding.ivPostProfileBoardName.text = item.nickname
                binding.ivPostProfileBoardName.setTextColor(blockColor)
            }
            //클럽 메인
            ConstVariable.VIEW_CLUB_MAIN -> {
                binding.ivPostProfileBoardName.visibility = View.VISIBLE
                binding.ivGrayDot.visibility = View.VISIBLE
                binding.ivPostProfileNickName.text = item.clubId
                binding.ivPostProfileBoardName.text = item.nickname
                binding.ivPostProfileBoardName.setTextColor(blockColor)
            }
            //클럽 페이지 > 홈탭,멤버
            ConstVariable.VIEW_CLUB_PAGE_HOME -> {
                binding.ivPostProfileBoardName.visibility = View.VISIBLE
                binding.ivGrayDot.visibility = View.VISIBLE
                binding.ivPostProfileNickName.text = item.categoryName2
                binding.ivPostProfileBoardName.text = item.nickname
                binding.ivPostProfileBoardName.setTextColor(blockColor)
            }
            //클럽 페이지 > 아카이브, 자유게시판 ||   클럽 챌린지
            ConstVariable.VIEW_CLUB_FREE_BOARD,ConstVariable.VIEW_CLUB_ARCHIVE_POST -> {
                binding.ivPostProfileNickName.text = item.nickname
                binding.ivPostProfileNickName.setTextColor(blockColor)
                binding.ivPostProfileBoardName.visibility = View.GONE
                binding.ivGrayDot.visibility = View.GONE
            }
        }
        binding.ivPostCreateTime.text = TimeUtils.diffTimeWithCurrentTime(item.createDate)
    }

    private fun attachHeaderState(item: ClubPostData) {
        when (postListOrFeed) {
            ConstVariable.POST_TYPE_FEED -> {
                settingThumbnail(item.attachList)
                binding.tvPostTitle.text = item.subject
                binding.tvPostContent.text = item.content
                if(item.categoryCode.contains("freeboard")){
                    settingHeadTitle(
                        item.subject,
                        item.categoryName2,
                        item.attachList?.isNotEmpty() == true
                    )
                }
            }
            ConstVariable.POST_TYPE_LIST -> {
                if (viewType == ConstVariable.VIEW_CLUB_FREE_BOARD) {
                    settingHeadTitle(
                        item.subject,
                        item.categoryName2,
                        item.attachList?.isNotEmpty() == true
                    )
                } else {
                    settingHeadTitle(item.subject, null, item.attachList?.isNotEmpty() == true)
                }
            }
        }

    }

    private fun settingViewType(type: String) {
        //피드 형식(썸네일 O , 게시글 내용 O)
        if (type == ConstVariable.POST_TYPE_FEED) {
            binding.flThumbnails.visibility = View.VISIBLE
            binding.tvPostContent.visibility = View.VISIBLE
            binding.tvPostContent.maxLines = 4
            binding.tvPostTitle.setTextAppearance(R.style.Title51622Medium)
        } else if (type == ConstVariable.POST_TYPE_LIST) {
            binding.clOgTagContainer.visibility = View.GONE
            binding.flThumbnails.visibility = View.GONE
            binding.tvPostContent.visibility = View.GONE
            binding.clPostContainer.elevation = 0f
            binding.tvPostTitle.setTextAppearance(R.style.Body21420Regular)
        }
        binding.tvPostTitle.setTextColor(itemView.context.getColor(R.color.gray_870))
        binding.tvPostTitle.visibility = View.VISIBLE
    }

    private fun settingThumbnail(
        thumbnailList: List<ClubPostAttachList>?,
    ) {
        if (thumbnailList?.isEmpty() == true || thumbnailList == null) {
            binding.clOgTagContainer.visibility = View.GONE
            binding.flThumbnails.visibility = View.GONE
        } else if (thumbnailList[0].attachType == 3) {
            binding.clOgTagContainer.visibility = View.VISIBLE
            binding.flThumbnails.visibility = View.GONE
            binding.ivOgTagImage.clipToOutline = true
            val imageUrl = if(thumbnailList[0].attach.startsWith("http")) thumbnailList[0].attach
                            else itemView.context.getString(R.string.imageThumbnailUrl,thumbnailList[0].attach)
            setImageWithPlaceHolder(binding.ivOgTagImage,imageUrl,placeHolderId= R.drawable.club_no_image)
            binding.tvOgTagTitle.text = "Og Tag 타이틀"
            binding.tvOgLink.text = "Og Tag 링크"
        } else if (thumbnailList[0].attachType == 1) {
            binding.clOgTagContainer.visibility = View.GONE
            binding.flThumbnails.visibility = View.VISIBLE
            binding.flExoplayer.visibility = View.VISIBLE
            binding.thumbnailGrid.visibility = View.GONE
            val imageUrl = if(thumbnailList[0].attach.startsWith("http")) thumbnailList[0].attach
                    else itemView.context.getString(R.string.imageThumbnailUrl,thumbnailList[0].attach)
            setImageWithPlaceHolder(binding.ivVideoThumbnail,imageUrl)
        } else if (thumbnailList.size > 2) {
            settingGridLayout(thumbnailList.size)
            val imageUrl1 = if(thumbnailList[0].attach.startsWith("http")) thumbnailList[0].attach
                else itemView.context.getString(R.string.imageThumbnailUrl,thumbnailList[0].attach)
            setImageWithPlaceHolder(binding.ivThumbnailFirst,imageUrl1)
            val imageUrl2 = if(thumbnailList[0].attach.startsWith("http")) thumbnailList[1].attach
                else itemView.context.getString(R.string.imageThumbnailUrl,thumbnailList[1].attach)
            setImageWithPlaceHolder(binding.ivThumbnailSecond,imageUrl2)
            val imageUrl3 = if(thumbnailList[0].attach.startsWith("http")) thumbnailList[2].attach
                else itemView.context.getString(R.string.imageThumbnailUrl,thumbnailList[2].attach)
            setImageWithPlaceHolder(binding.ivThumbnailThird,imageUrl3)
            binding.clOgTagContainer.visibility = View.GONE
            binding.flThumbnails.visibility = View.VISIBLE
            binding.flExoplayer.visibility = View.GONE
            binding.thumbnailGrid.visibility = View.VISIBLE
        } else if (thumbnailList.size == 2) {
            settingGridLayout(thumbnailList.size)
            val imageUrl1 = if (thumbnailList[0].attach.startsWith("http")) thumbnailList[0].attach
                    else itemView.context.getString(R.string.imageThumbnailUrl, thumbnailList[0].attach)
            setImageWithPlaceHolder(binding.ivThumbnailFirst, imageUrl1)
            val imageUrl2 = if (thumbnailList[0].attach.startsWith("http")) thumbnailList[1].attach
                    else itemView.context.getString(R.string.imageThumbnailUrl, thumbnailList[1].attach)
            setImageWithPlaceHolder(binding.ivThumbnailSecond, imageUrl2)
            binding.clOgTagContainer.visibility = View.GONE
            binding.flThumbnails.visibility = View.VISIBLE
            binding.flExoplayer.visibility = View.GONE
            binding.thumbnailGrid.visibility = View.VISIBLE
        } else if (thumbnailList.size == 1) {
            settingGridLayout(thumbnailList.size)
            val imageUrl1 = if (thumbnailList[0].attach.startsWith("http")) thumbnailList[0].attach
                    else itemView.context.getString(R.string.imageThumbnailUrl, thumbnailList[0].attach)
            setImageWithPlaceHolder(binding.ivThumbnailFirst, imageUrl1)
            binding.clOgTagContainer.visibility = View.GONE
            binding.flThumbnails.visibility = View.VISIBLE
            binding.flExoplayer.visibility = View.GONE
            binding.thumbnailGrid.visibility = View.VISIBLE
        }
    }

    private fun settingGridLayout(count: Int) {
        when (count) {
            0 -> {
                binding.thumbnailGrid.visibility = View.GONE
                binding.ivThumbnailFirst.visibility = View.GONE
                binding.ivThumbnailSecond.visibility = View.GONE
                binding.flThumbnailThird.visibility = View.GONE
            }
            1 -> {
                binding.thumbnailGrid.visibility = View.VISIBLE
                binding.ivThumbnailFirst.visibility = View.VISIBLE
                binding.ivThumbnailSecond.visibility = View.GONE
                binding.flThumbnailThird.visibility = View.GONE
            }
            2 -> {
                binding.thumbnailGrid.visibility = View.VISIBLE
                binding.ivThumbnailFirst.visibility = View.VISIBLE
                binding.ivThumbnailSecond.visibility = View.VISIBLE
                binding.flThumbnailThird.visibility = View.GONE
            }
            else -> {
                binding.thumbnailGrid.visibility = View.VISIBLE
                binding.ivThumbnailFirst.visibility = View.VISIBLE
                binding.ivThumbnailSecond.visibility = View.VISIBLE
                binding.flThumbnailThird.visibility = View.VISIBLE
                binding.tvThumbnailGridCount.text = "+${count - 2}"
            }
        }
    }

    private fun settingHeadTitle(text: String?, header: String?, isAttach: Boolean) {
        val imageHeight = SizeUtils.getDpValue(16f, itemView.context).toInt()
        val imageSpan = if (isAttach) {
            itemView.context.getDrawable(R.drawable.posting_file)
                ?.apply {
                    setBounds(0, 0, imageHeight, imageHeight)
                }?.let {
                    VerticalImageSpan(it)
                }
        } else {
            itemView.context.getDrawable(R.drawable.posting_text)
                ?.apply {
                    setBounds(0, 0, imageHeight, imageHeight)
                }?.let {
                    VerticalImageSpan(it)
                }
        }
        val s = if (header == null) {
            SpannableString("   ${text}").apply {
                setSpan(imageSpan, 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        } else {
            SpannableString("[${header}]   ${text}").apply {
                if(postListOrFeed!=ConstVariable.POST_TYPE_FEED){
                    setSpan(
                        imageSpan,
                        header.length + 3,
                        header.length + 4,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                setSpan(
                    ForegroundColorSpan(itemView.context.getColor(R.color.primary_500)),
                    0,
                    header.length + 2,
                    0
                )
            }
        }
        binding.tvPostTitle.text = s
    }

    fun changeHonorColor(isMyHonor: Boolean, honorCnt: String) {
        binding.ivHonorIcon.setColorFilter(
            if (isMyHonor) itemView.context.getColor(R.color.state_active_primary_default) else itemView.context.getColor(
                R.color.state_disabled_gray_200
            )
        )
        binding.tvHonorCount.setTextColor(
            if (isMyHonor) itemView.context.getColor(R.color.primary_500) else itemView.context.getColor(
                R.color.state_active_gray_700
            )
        )
        binding.tvHonorCount.text = honorCnt
    }

    fun clickOfViewType(item: ClubPostData, dbPkId: Int) {
        if(item.status==1){
            //아무 기능 없음
        }else if(item.status==2){
            //리스트에서 제외?
        }else if(item.status==3){
            //리스트 제외?
        }else if(item.blockType==1 || item.blockType==2){
            settingBlindClick(item.postId.toString(),item,dbPkId)
        }else{
            settingDefaultClickFunc(item.postId.toString(), item, dbPkId)
        }
    }

    //클럽은 좋아요 싫어요 없음
    fun settingDefaultClickFunc(postId: String, postItem: ClubPostData, pkID: Int) {
        binding.llPostProfileInfo.setOnClickListener {

        }
        //컨텐츠 선택
        binding.rlContentContainer.setOnClickListener {
            optionsClickListener?.onPostClick(postItem.categoryCode, postId.toInt(),ConstVariable.TYPE_CLUB,postItem.clubId)
        }
        binding.flExoCover.setOnClickListener {
            optionsClickListener?.onPostClick(postItem.categoryCode, postId.toInt(),ConstVariable.TYPE_CLUB,postItem.clubId)
        }
        //옵션 선택
        binding.ivPostOptionIcon.setOnClickListener {
            optionsClickListener?.onOptionClick(
                pkID,
                postItem.integUid?:"",
                ConstVariable.TYPE_CLUB,
                bindingAdapterPosition,
                postId.toInt(),
                postItem.blockType==2,
                postItem.blockType==1,
                postItem.categoryCode
            )
        }
    }


    private fun settingBlindClick(postId: String, postItem: ClubPostData, pkId: Int) {
        binding.llPostProfileInfo.setOnClickListener {

        }
        //컨텐츠 선택
        binding.rlContentContainer.setOnClickListener {
            optionsClickListener?.onPostClick(postItem.categoryCode, postId.toInt(),ConstVariable.TYPE_CLUB,postItem.clubId)
        }
        binding.flExoCover.setOnClickListener {
            optionsClickListener?.onPostClick(postItem.categoryCode, postId.toInt(),ConstVariable.TYPE_CLUB,postItem.clubId)
        }

        binding.ivPostOptionIcon.setOnClickListener {
            optionsClickListener?.onOptionClick(
                pkId,
                postItem.integUid?:"",
                ConstVariable.TYPE_CLUB,
                bindingAdapterPosition,
                postId.toInt(),
                postItem.blockType==2,
                postItem.blockType==1,
                postItem.categoryCode,
            )
        }

    }

    private fun noItem(item: ClubPostData?){
        itemView.visibility = if (item == null) {
            View.GONE
            return
        } else View.VISIBLE
    }

}