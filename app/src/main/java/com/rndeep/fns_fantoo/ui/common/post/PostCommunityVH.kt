package com.rndeep.fns_fantoo.ui.common.post

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.post.BoardPostData
import com.rndeep.fns_fantoo.data.remote.model.community.DetailAttachList
import com.rndeep.fns_fantoo.databinding.CategoryHomePostBinding
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.utils.*
import timber.log.Timber

class PostCommunityVH(
    private val binding: CategoryHomePostBinding,
    private val optionsClickListener: BoardListeners.OnBoardPostClickListener?,
    private val postType: String,
    private val viewType: String
) : RecyclerView.ViewHolder(binding.root) {

    fun communityViewBind(item: BoardPostData?, dbPkId: Int) {
        itemView.visibility = if (item == null) {
            View.GONE
            return
        } else View.VISIBLE
        //10.7 일자로 더보기 삭제
        binding.ivPostOptionIcon.visibility=View.GONE
        binding.llHonorContainer.visibility=View.GONE
        changeViewBackground()

        //topview
        setProfileAvatar(
            binding.ivPostProfileThumbnail,
            itemView.context.getString(R.string.imageUrlBase, item.userPhoto)
        )
        settingTopText(viewType, item)
        binding.ivPostCreateTime.text = TimeUtils.diffTimeWithCurrentTime(item.createDate)

        //contents
        if (item.userBlockYn == true || item.pieceBlockYn == true) {
            settingBlindItem(item)
        }else {
            settingViewType(postType)
            attachHeaderState(item)
        }

        //BottomView
        changeLikeColor(item.likeYn == true, item.likeCnt?:0,item.dislikeCnt?:0)
        changeDisLikeColor(item.dislikeYn == true)
        changeHonorColor(item.honorYn == true, item.honorCnt?.toString() ?: "0")
        binding.tvCommentCount.text = item.replyCnt?.toString() ?: "0"

        clickOfViewType(item,dbPkId)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun changeViewBackground(){
        val valueOfPostView : Triple<Drawable?,Float,Float> =when(postType){
            ConstVariable.POST_TYPE_FEED->{
                if(viewType==ConstVariable.VIEW_CLUB_ARCHIVE_POST){
                    Triple(itemView.context.getDrawable(R.color.gray_25),16f,15f)
                }else{
                    Triple(itemView.context.getDrawable(R.drawable.bg_main_contents),26f,25f)
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

    fun settingBlindItem(item: BoardPostData) {
        binding.tvPostContent.visibility = View.GONE
        binding.flThumbnails.visibility = View.GONE
        binding.clOgTagContainer.visibility = View.GONE
        binding.tvPostTitle.setTextAppearance(R.style.Body21420Regular)
        binding.tvPostTitle.setTextColor(itemView.context.getColor(R.color.gray_400))
        binding.clPostContainer.elevation = 0f
        binding.tvPostTitle.text = item.title

        changeLikeColor(item.likeYn == true, item.likeCnt?:0,item.dislikeCnt?:0)
        changeDisLikeColor(item.dislikeYn == true)
        changeHonorColor(item.honorYn == true, item.honorCnt?.toString() ?: "0")
    }

    private fun settingTopText(type: String, item: BoardPostData) {
        when (type) {
            ConstVariable.VIEW_COMMUNITY_MAIN,
            ConstVariable.VIEW_COMMUNITY_BOOKMARK,
            ConstVariable.VIEW_COMMUNITY_MY,
            ConstVariable.VIEW_HOME_MAIN,
            ConstVariable.VIEW_COMMUNITY_SEARCH,
            ConstVariable.VIEW_COMMUNITY_PAGE_HOT -> {
                binding.ivPostProfileBoardName.visibility = View.VISIBLE
                binding.ivGrayDot.visibility = View.VISIBLE
                binding.ivPostProfileNickName.apply {
                    text = context.getStringByIdentifier(ConstVariable.Community.CategoryCode.compare(item.code).code)
                }
                binding.ivPostProfileBoardName.text = item.userNick
            }
            ConstVariable.VIEW_COMMUNITY_PAGE_COMMON -> {
                binding.ivPostProfileBoardName.visibility = View.GONE
                binding.ivGrayDot.visibility = View.GONE
                binding.ivPostProfileNickName.text = item.userNick
            }
        }
    }

    private fun attachHeaderState(item: BoardPostData) {
        when (postType) {
            ConstVariable.POST_TYPE_FEED -> {
                settingThumbnail(item.attachList)
                binding.tvPostTitle.text = item.title
                binding.tvPostContent.text = item.content
            }
            ConstVariable.POST_TYPE_LIST -> {
                when (viewType) {
                    ConstVariable.VIEW_COMMUNITY_MAIN,
                    ConstVariable.VIEW_COMMUNITY_PAGE_HOT,
                    ConstVariable.VIEW_COMMUNITY_SEARCH,
                    ConstVariable.VIEW_COMMUNITY_BOOKMARK,
                    ConstVariable.VIEW_COMMUNITY_MY -> {
                        settingHeadTitle(item.title, null, item.attachYn == true)
                    }
                    ConstVariable.VIEW_COMMUNITY_PAGE_COMMON -> {
                        settingHeadTitle(item.title, item.subCode, item.attachYn == true)
                    }
                }
                binding.tvPostContent.text = item.content
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
            binding.tvPostTitle.setTextAppearance(R.style.Body21420Regular)
            binding.clPostContainer.elevation = 0f
        }

        binding.tvPostTitle.setTextColor(itemView.context.getColor(R.color.gray_870))
        binding.tvPostTitle.visibility = View.VISIBLE
    }

    private fun settingThumbnail(
        thumbnailList: List<DetailAttachList>?
    ) {
        if (thumbnailList?.isEmpty() == true || thumbnailList == null) {
            binding.clOgTagContainer.visibility = View.GONE
            binding.flThumbnails.visibility = View.GONE
        } else if (thumbnailList[0].archiveType == "link") {
            binding.clOgTagContainer.visibility = View.VISIBLE
            binding.flThumbnails.visibility = View.GONE
            binding.ivOgTagImage.clipToOutline = true
            val imageUrl = if(thumbnailList[0].id?.startsWith("http")==true) thumbnailList[0].id
                      else itemView.context.getString(R.string.imageThumbnailUrl,thumbnailList[0].id)
            setImageWithPlaceHolder(binding.ivOgTagImage,imageUrl,placeHolderId= R.drawable.club_no_image)
            binding.tvOgTagTitle.text = "Og Tag 타이틀"
            binding.tvOgLink.text = "Og Tag 링크"
        } else if (thumbnailList[0].archiveType == "video") {
            binding.clOgTagContainer.visibility = View.GONE
            binding.flExoplayer.visibility = View.VISIBLE
            binding.flThumbnails.visibility = View.VISIBLE
            binding.thumbnailGrid.visibility = View.GONE
            val imageUrl = if(thumbnailList[0].id?.startsWith("http") == true) thumbnailList[0].id
                    else itemView.context.getString(R.string.imageThumbnailUrl,thumbnailList[0].id)
            setImageWithPlaceHolder(binding.ivVideoThumbnail,imageUrl)
        } else if (thumbnailList.size > 2) {
            settingGridLayout(thumbnailList.size)
            val imageUrl1 = if(thumbnailList[0].id?.startsWith("http") == true) thumbnailList[0].id
                    else itemView.context.getString(R.string.imageThumbnailUrl,thumbnailList[0].id)
            setImageWithPlaceHolder(binding.ivThumbnailFirst,imageUrl1)
            val imageUrl2 = if(thumbnailList[1].id?.startsWith("http") == true) thumbnailList[1].id
                    else itemView.context.getString(R.string.imageThumbnailUrl,thumbnailList[1].id)
            setImageWithPlaceHolder(binding.ivThumbnailSecond,imageUrl2)
            val imageUrl3 = if(thumbnailList[2].id?.startsWith("http") == true) thumbnailList[2].id
                    else itemView.context.getString(R.string.imageThumbnailUrl,thumbnailList[2].id)
            setImageWithPlaceHolder(binding.ivThumbnailThird,imageUrl3)
            binding.clOgTagContainer.visibility = View.GONE
            binding.flExoplayer.visibility = View.GONE
            binding.flThumbnails.visibility = View.VISIBLE
            binding.thumbnailGrid.visibility = View.VISIBLE
        } else if (thumbnailList.size == 2) {
            settingGridLayout(thumbnailList.size)
            val imageUrl1 = if(thumbnailList[0].id?.startsWith("http") == true) thumbnailList[0].id
                            else itemView.context.getString(R.string.imageThumbnailUrl,thumbnailList[0].id)
            setImageWithPlaceHolder(binding.ivThumbnailFirst,imageUrl1)
            val imageUrl2 = if(thumbnailList[1].id?.startsWith("http") == true) thumbnailList[1].id
                            else itemView.context.getString(R.string.imageThumbnailUrl,thumbnailList[1].id)
            setImageWithPlaceHolder(binding.ivThumbnailSecond,imageUrl2)
            binding.clOgTagContainer.visibility = View.GONE
            binding.flExoplayer.visibility = View.GONE
            binding.flThumbnails.visibility = View.VISIBLE
            binding.thumbnailGrid.visibility = View.VISIBLE
        } else if (thumbnailList.size == 1) {
            settingGridLayout(thumbnailList.size)
            val imageUrl1 = if(thumbnailList[0].id?.startsWith("http") == true) thumbnailList[0].id
                            else itemView.context.getString(R.string.imageThumbnailUrl,thumbnailList[0].id)
            setImageWithPlaceHolder(binding.ivThumbnailFirst,imageUrl1)
            binding.clOgTagContainer.visibility = View.GONE
            binding.flExoplayer.visibility = View.GONE
            binding.flThumbnails.visibility = View.VISIBLE
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
        try {
            val s = if (header == "C_HOT" || header == null) {
                SpannableString("   ${text}").apply {
                    setSpan(imageSpan, 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            } else {
                val headerSplitStrings = header.split("_")
                val headerStr = headerSplitStrings[headerSplitStrings.size-1]
                val convertHeader = itemView.context.getStringByIdentifier(
                    ConstVariable.Community.HeaderTitleCode.compare(headerStr).code
                )
                SpannableString("[${convertHeader}]   ${text}").apply {
                    setSpan(
                        imageSpan,
                        convertHeader.length + 3,
                        convertHeader.length + 4,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    setSpan(
                        ForegroundColorSpan(itemView.context.getColor(R.color.primary_500)),
                        0,
                        convertHeader.length + 2,
                        0
                    )
                }
            }
    //        s.setSpan(imageSpan, header.length+3, header.length+4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.tvPostTitle.text = s
        }catch (e:Exception){
            Timber.e("${e.printStackTrace()}")
        }
    }

    fun changeLikeColor(isMyLike: Boolean, likeCnt: Int,disLikeCnt :Int) {
        binding.ivLikeIcon.setColorFilter(
            if (isMyLike) itemView.context.getColor(R.color.state_active_primary_default) else itemView.context.getColor(
                R.color.state_disabled_gray_200
            )
        )
        binding.tvLikeCount.setTextColor(
            if (isMyLike) itemView.context.getColor(R.color.state_active_primary_default) else itemView.context.getColor(
                R.color.state_active_gray_700
            )
        )
        binding.tvLikeCount.text = (likeCnt-disLikeCnt).toString()
    }

    fun changeDisLikeColor(isMyHonor: Boolean) {
        binding.ivDisLikeIcon.setColorFilter(
            if (isMyHonor) itemView.context.getColor(R.color.state_active_gray_700) else itemView.context.getColor(
                R.color.state_disabled_gray_200
            )
        )
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

    fun clickOfViewType(item : BoardPostData, dbPkId:Int){
        if (item.pieceBlockYn == true || item.userBlockYn == true) {
            settingBlindClick(item.postId.toString(), item, dbPkId)
        }else{
            when (viewType) {
                ConstVariable.VIEW_COMMUNITY_BOOKMARK,
                ConstVariable.VIEW_COMMUNITY_MY -> {
                    settingMyStorageClick(item.postId.toString(), item)
                }
                else -> {
                    settingDefaultClickFunc(item.postId.toString(), item, dbPkId)
                }
            }
        }
    }

    fun settingDefaultClickFunc(postId: String, postItem: BoardPostData, pkId: Int) {
        //좋아요 선택
        binding.llLikeContainer.setOnClickListener {
            optionsClickListener?.onLikeClick(
                pkId,
                postItem,
                ConstVariable.TYPE_COMMUNITY,
                bindingAdapterPosition,
                this
            )
        }
        //싫어요 선택
        binding.ivDisLikeIcon.setOnClickListener {
            optionsClickListener?.onDisLikeClick(
                pkId,
                postItem,
                ConstVariable.TYPE_COMMUNITY,
                bindingAdapterPosition,
                this
            )
        }
        //아너 선택
        binding.llHonorContainer.setOnClickListener {
            optionsClickListener?.onHonorClick(
                pkId,
                postItem,
                ConstVariable.TYPE_COMMUNITY,
                bindingAdapterPosition,
                this
            )
        }

        //프로필 선택
        binding.llPostProfileInfo.setOnClickListener {
            optionsClickListener?.onProfileClick(postItem)
        }

        //컨텐츠 선택
        binding.rlContentContainer.setOnClickListener {
            optionsClickListener?.onPostClick(postItem.code, postId.toInt(),ConstVariable.TYPE_COMMUNITY,"-1")
        }
        binding.flExoCover.setOnClickListener {
            optionsClickListener?.onPostClick(postItem.code, postId.toInt(),ConstVariable.TYPE_COMMUNITY,"-1")
        }
        //옵션 선택
        binding.ivPostOptionIcon.setOnClickListener {
            optionsClickListener?.onOptionClick(
                pkId,
                postItem.integUid,
                ConstVariable.TYPE_COMMUNITY,
                bindingAdapterPosition,
                postId.toInt(),
                postItem.pieceBlockYn,
                postItem.userBlockYn,
                postItem.code,
            )
        }
    }

    private fun settingMyStorageClick(postId: String, postItem: BoardPostData) {
        //컨텐츠 선택
        binding.rlContentContainer.setOnClickListener {
            optionsClickListener?.onPostClick(postItem.code, postId.toInt(),ConstVariable.TYPE_COMMUNITY,"-1")
        }
        binding.flExoCover.setOnClickListener {
            optionsClickListener?.onPostClick(postItem.code, postId.toInt(),ConstVariable.TYPE_COMMUNITY,"-1")
        }
        //프로필 선택
        binding.llPostProfileInfo.setOnClickListener {
            optionsClickListener?.onProfileClick(postItem)
        }
    }

    private fun settingBlindClick(postId: String, postItem: BoardPostData, pkId: Int) {
        //컨텐츠 선택
        binding.rlContentContainer.setOnClickListener {
            optionsClickListener?.onPostClick(postItem.code, postId.toInt(),ConstVariable.TYPE_COMMUNITY,"-1")
        }
        binding.flExoCover.setOnClickListener {
            optionsClickListener?.onPostClick(postItem.code, postId.toInt(),ConstVariable.TYPE_COMMUNITY,"-1")
        }

        binding.ivPostOptionIcon.setOnClickListener {
            optionsClickListener?.onOptionClick(
                pkId,
                postItem.integUid,
                ConstVariable.TYPE_COMMUNITY,
                bindingAdapterPosition,
                postId.toInt(),
                postItem.pieceBlockYn,
                postItem.userBlockYn,
                postItem.code,
            )
        }

    }
}
