package com.rndeep.fns_fantoo.ui.community.postdetail.viewholder

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.PostThumbnail
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityNoticeData
import com.rndeep.fns_fantoo.data.remote.model.community.DetailAttachList
import com.rndeep.fns_fantoo.databinding.DetailNoticePostContentLayoutBinding
import com.rndeep.fns_fantoo.ui.community.postdetail.CommunityDetailPostAdapter
import com.rndeep.fns_fantoo.ui.common.post.PostDetailImageAdapter
import com.rndeep.fns_fantoo.utils.TimeUtils
import com.rndeep.fns_fantoo.utils.setProfileAvatar

//공지 뷰 홀더
class NoticePostVH constructor(
    private val binding: DetailNoticePostContentLayoutBinding,
    private val fullScreenListener: PostDetailImageAdapter.OnAttachListClickListener?,
    private val postOptionsClickListener: CommunityDetailPostAdapter.OnPostDetailOptionsClickListener?
) : RecyclerView.ViewHolder(binding.root) {
    //이미지 어댑터
    private val postDetailImageAdapter = PostDetailImageAdapter()

    init {
        //컨텐츠 이미지 or 비디오 adapter
        binding.rcPostImage.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
        binding.rcPostImage.adapter = postDetailImageAdapter
    }


    fun noticeBind(
        communityNoticeData: CommunityNoticeData,
        boardCode: String?,
        noticeAttachList: List<DetailAttachList>?
    ) {
        goneView()
        //프로필 이미지
        setProfileAvatar(
            binding.ivPostAuthThumbnail,
            itemView.context.getString(R.string.imageUrlBase, communityNoticeData.userPhoto)
        )
        //닉네임, 소속 및 시간
        if (boardCode == null) {
            binding.tvPostNickname.text = communityNoticeData.userNick
            binding.nickGroup1.visibility = View.GONE
        } else {
            binding.tvPostNickname.text = boardCode
            binding.nickGroup1.visibility = View.VISIBLE
            binding.tvGroupName.text = communityNoticeData.userNick
        }
        binding.tvCreateTime.text =
            TimeUtils.diffTimeWithCurrentTime(communityNoticeData.createDate)

        //제목
        binding.tvPostTitle.text = communityNoticeData.title

        //내용
        binding.tvPostContent.text = communityNoticeData.content

        //이미지 or 영상
        val contentItemUrls = arrayListOf<PostThumbnail>()
        noticeAttachList?.forEach {
            val url =
                if (it.id != null) itemView.context.getString(R.string.imageUrlBase, it.id) else ""
            contentItemUrls.add(PostThumbnail(it.archiveType, url))
        }
        postDetailImageAdapter.submitList(contentItemUrls)

        val transItem = listOf(
            communityNoticeData.title,
            communityNoticeData.content
        )
        settingClickFun(transItem,communityNoticeData.translateYn?:false)
    }

    fun settingClickFun(transItems :List<String>,isTranslate :Boolean) {
        fullScreenListener?.let {
            postDetailImageAdapter.setOnFullScreenClickListener(it)
        }
        //번역 선택
        binding.ivTranslate.setOnClickListener {
            postOptionsClickListener?.onTransLateClick(transItems,isTranslate)
        }
        //공유 클릭
        binding.ivShareIcon.setOnClickListener {
            postOptionsClickListener?.onShareClick()
        }
    }

    fun setTranslateStateChange(postItem: CommunityNoticeData) {
        if (postItem.translateYn != null) {
            binding.ivTranslate.setColorFilter(itemView.context.getColor(if (postItem.translateYn!!)
                R.color.state_active_primary_default else R.color.gray_700
            ))
        } else {
            binding.ivTranslate.setColorFilter(R.color.gray_700)
        }
        binding.tvPostTitle.text=postItem.title
        binding.tvPostContent.text=postItem.content
    }

    fun getImageAdapter() = postDetailImageAdapter
    fun getBindingImageRc() = binding.rcPostImage

    private fun goneView() {
        with(binding) {
            llLikeContainer.visibility = View.GONE
            ivDisLikeIcon.visibility = View.GONE
            llHonorContainer.visibility = View.GONE
            rcPostHashTag.visibility = View.GONE
            binding.ivAD.visibility = View.GONE
            binding.ivCommentIcon.visibility=View.GONE
        }
    }

}