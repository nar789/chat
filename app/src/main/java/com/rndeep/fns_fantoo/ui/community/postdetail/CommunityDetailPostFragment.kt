package com.rndeep.fns_fantoo.ui.community.postdetail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.get
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ToolbarUtils
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.dto.ComposeCommunityPost
import com.rndeep.fns_fantoo.data.remote.model.CommunityReplyData
import com.rndeep.fns_fantoo.data.remote.model.PostReplyData
import com.rndeep.fns_fantoo.data.remote.model.community.BoardPostDetailData
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityNoticeData
import com.rndeep.fns_fantoo.data.remote.model.community.DetailAttachList
import com.rndeep.fns_fantoo.databinding.FragmentDetailPostBinding
import com.rndeep.fns_fantoo.ui.common.post.PostDetailReplayAdapter
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetAdapter
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetItem
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.CustomBottomSheet
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.ui.common.post.PostDetailImageAdapter
import com.rndeep.fns_fantoo.ui.community.BoardInfo
import com.rndeep.fns_fantoo.ui.community.postdetail.data.DetailPostItem
import com.rndeep.fns_fantoo.ui.community.postdetail.viewholder.CommunityPostDetailVH
import com.rndeep.fns_fantoo.ui.community.postdetail.viewholder.NoticePostVH
import com.rndeep.fns_fantoo.ui.editor.EditorInfo
import com.rndeep.fns_fantoo.ui.editor.EditorType
import com.rndeep.fns_fantoo.utils.*
import com.rndeep.fns_fantoo.utils.ConstVariable.ERROR_COMMENT_IMAGE_UPLOAD_FAIL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class CommunityDetailPostFragment :
    BaseFragment<FragmentDetailPostBinding>(FragmentDetailPostBinding::inflate) {

    private val detailViewModel: CommunityDetailPostViewModel by viewModels()
    private val detailPostArg: CommunityDetailPostFragmentArgs by navArgs()
    private val communityDetailPostAdapter = CommunityDetailPostAdapter()
    private var currentLast =true

    //이미지 intent
    private lateinit var getImageResult: ActivityResultLauncher<Intent>
    private var imageFile: MultipartBody.Part? = null

    private var commentClickPos: Int? = null
    private var commentReplyClickPos: Int? = null

    private var moveInputCommentPosition =false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getImageResult = getCommentImageResult()
        initStatusBar()
    }

    override fun initUi() {
        initToolbar()
        settingInputMode()
        setTopScrollButtonVisibility(View.GONE)
        binding.rcPostList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        communityDetailPostAdapter.setIsLogin(detailViewModel.isUser())
        binding.rcPostList.adapter = communityDetailPostAdapter

        settingObserver()

        getPostInfoData()
    }

    private fun setNoticeView() {
        binding.rlCommentContainer.visibility = View.GONE
    }

    override fun initUiActionEvent() {
        //최상단으로 이동
        binding.scrollUpBtn.setOnClickListener {
            binding.rcPostList.anchorSmoothScrollToPosition(0)
        }

        binding.rcPostList.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(-1)){
                    setTopScrollButtonVisibility(View.GONE)
                }else{
                    setTopScrollButtonVisibility(View.VISIBLE)
                }
            }
        })
        binding.rcPostList.checkLastItemVisible {
            if (it) {
                if (detailPostArg.postType==ConstVariable.TYPE_COMMUNITY
                    && !currentLast
                    && communityDetailPostAdapter.itemCount>2) {
                    currentLast = true
                    detailViewModel.getCommunityCommentList(detailPostArg.postId.toString(), "add")
                }
            }
        }

        //툴바 선택
        binding.postDetailToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_post_bookmark -> {
                    if (communityDetailPostAdapter.itemCount > 0) {
                        val postItem = communityDetailPostAdapter.currentList[0]
                        if (postItem is DetailPostItem.CommunityPostDetail) {
                            detailViewModel.clickBookmarkPost(
                                detailPostArg.postId,
                                postItem.item.bookmarkYn
                            )
                            return@setOnMenuItemClickListener true
                        }
                    }
                    context?.showErrorSnackBar(
                        binding.root,
                        ConstVariable.ERROR_WAIT_FOR_SECOND
                    )
                    true
                }
                R.id.menu_post_options -> {
                    if(communityDetailPostAdapter.itemCount>0){
                       val item= communityDetailPostAdapter.currentList[0]
                        if(item is DetailPostItem.CommunityPostDetail){
                            postOptionSheet(item.item)
                        }else if(item is DetailPostItem.NoticePostDetail){
                            noticePostOptionSheet(item.item)
                        }
                    }
                    true
                }
                else -> {
                    false
                }
            }
        }

        //영상 전체 화면
        communityDetailPostAdapter.setOnFullScreenClickListener(settingPlayer())

        //컨텐츠 옵션 선택
        communityDetailPostAdapter.setOnPostDetailOptionsClickListener(object :
            CommunityDetailPostAdapter.OnPostDetailOptionsClickListener {
            override fun onTransLateClick(transItems: List<String>,isTranslate :Boolean) {
                when (detailPostArg.postType) {
                    ConstVariable.TYPE_COMMUNITY -> {
                        detailViewModel.clickTranslateContent(transItems,isTranslate)
                    }
                    ConstVariable.TYPE_COMMUNITY_NOTICE -> {
                        detailViewModel.clickTranslateContent(transItems,isTranslate)
                    }
                }
            }

            override fun onLikeClick(isMyLike: Boolean?) {
                when (detailPostArg.postType) {
                    ConstVariable.TYPE_COMMUNITY -> {
                        detailViewModel.clickPostLikeDislike(
                            detailPostArg.postId.toString(),
                            isMyLike == true,
                            ConstVariable.LIKE_CLICK,
                            ConstVariable.LIKE_DISLIKE_TYPE_POST
                        )
                    }
                }
            }

            override fun onDisListClick(isMyDisLike: Boolean?) {
                when (detailPostArg.postType) {
                    ConstVariable.TYPE_COMMUNITY -> {
                        detailViewModel.clickPostLikeDislike(
                            detailPostArg.postId.toString(),
                            isMyDisLike == true,
                            ConstVariable.DISLIKE_CLICK,
                            ConstVariable.LIKE_DISLIKE_TYPE_POST
                        )
                    }
                }
            }

            override fun onHonorClick() {
            }

            override fun onShareClick() {
                when (detailPostArg.postType) {
                    ConstVariable.TYPE_COMMUNITY -> {

                    }
                    ConstVariable.TYPE_COMMUNITY_NOTICE -> {

                    }
                }
            }
        })
        //댓글 선택
        communityDetailPostAdapter.setOnCommentClickListener(object :
            CommunityDetailPostAdapter.OnCommentClickListener {
            override fun onCommentClick(commentItem: PostReplyData) {
                if(commentItem is CommunityReplyData){
                    findNavController().navigate(
                        CommunityDetailPostFragmentDirections.actionDetailPostToComment(
                            commentItem.comPostId, commentItem.replyId
                        ))
                }
            }

            override fun onOptionsClick(commentItem: PostReplyData, pos: Int) {
                if(commentItem is CommunityReplyData){
                    showCommentBottomSheet(commentItem)
                }
            }

            override fun onLikeClick(commentItem: CommunityReplyData, pos: Int) {
                if(commentClickPos!=null) return
                commentClickPos = pos
                detailViewModel.clickCommentLike(
                    ConstVariable.LIKE_CLICK,
                    commentItem.replyId.toString(),
                    commentItem.myLikeYn == true
                )

            }

            override fun onDisLikeClick(commentItem: CommunityReplyData, pos: Int) {
                if(commentClickPos!=null) return
                commentClickPos = pos
                detailViewModel.clickCommentLike(
                    ConstVariable.DISLIKE_CLICK,
                    commentItem.replyId.toString(),
                    commentItem.myDisLikeYn == true
                )
            }

            override fun onTransClick(comment: String, pos: Int, translateYn: Boolean) {
                if(commentClickPos!=null) return
                commentClickPos=pos
                detailViewModel.transCommentText(comment,translateYn,pos,pos)
            }

            override fun onProfileClick(commentItem: PostReplyData) {
                if(!detailViewModel.isUser()){
                    activity?.showErrorSnackBar(binding.root,ConstVariable.ERROR_PLEASE_LATER_LOGIN)
                }else{
                    if(commentItem is CommunityReplyData){
                        findNavController().navigate(
                            CommunityDetailPostFragmentDirections.actionDetailPostToProfile(
                                commentItem.userNick ?: "",
                                commentItem.userBlockYn==true,
                                commentItem.integUid,
                                commentItem.userPhoto ?: ""
                            )
                        )
                    }
                }
            }

            override fun onImageClick(imageUrl: String) {
                findNavController().navigate(
                    CommunityDetailPostFragmentDirections.actionDetailPostToProfileImageViewerFragment6(
                        imageUrl
                    )
                )
            }
        })

        //대댓 좋아요
        communityDetailPostAdapter.setOnReplyClickListener(object :
            PostDetailReplayAdapter.OnReplyClickListener {
            override fun onLikeClick(
                commentItem: CommunityReplyData,
                parentPosition: Int,
                position: Int
            ) {
                commentReplayLikeDisLikeClick(
                    parentPosition,position,commentItem,ConstVariable.LIKE_CLICK,commentItem.myLikeYn?:false)
            }

            override fun onDisLikeClick(
                commentItem: CommunityReplyData,
                parentPosition: Int,
                position: Int
            ) {
                commentReplayLikeDisLikeClick(
                    parentPosition,position,commentItem,ConstVariable.DISLIKE_CLICK,commentItem.myDisLikeYn?:false)
            }

            override fun onTranslate(
                comment: String,
                parentPosition: Int,
                position: Int,
                translateYn: Boolean
            ) {
                if(commentClickPos!=null) return
                commentClickPos = parentPosition
                commentReplyClickPos = position
                detailViewModel.transCommentText(comment,translateYn,parentPosition,position)
            }

            override fun onProfileClick(
                nickName: String,
                clubId: String?,
                memberId: String?,
                isBlock: Boolean,
                targetUid: String?,
                userPhoto: String
            ) {
                if(!detailViewModel.isUser()){
                    activity?.showErrorSnackBar(binding.root,ConstVariable.ERROR_PLEASE_LATER_LOGIN)
                }else{
                    findNavController().navigate(
                        CommunityDetailPostFragmentDirections.actionDetailPostToProfile(
                            nickName,
                            isBlock,
                            targetUid!!,
                            userPhoto
                        )
                    )
                }
            }

            override fun onImageClick(imageUrl: String) {
                findNavController().navigate(
                    CommunityDetailPostFragmentDirections.actionDetailPostToProfileImageViewerFragment6(
                        imageUrl
                    )
                )
            }

        })
        //익명 여부
        binding.ivAnonymous.setOnClickListener {
            detailViewModel.setAnonymYN(!(detailViewModel.anonymYN.value?:false))
        }
        //댓글 등록
        binding.btnCommentRegister.setOnClickListener {
            if (detailViewModel.commentInputing.value == true) {
                activity?.showCustomSnackBar(binding.root, getString(R.string.se_s_sending_comment_to_server))
                return@setOnClickListener
            }
            if(detailViewModel.commentContent.value.isNullOrBlank()){
                activity?.showErrorSnackBar(binding.root,ConstVariable.ERROR_FE3045)
                return@setOnClickListener
            }
            detailViewModel.settingCommentInputingState(true)
            when(detailPostArg.postType){
                ConstVariable.TYPE_COMMUNITY->{
                    detailViewModel.setCommentItem()
                    registerCommunityComment()
                }
            }
        }
        //댓글 변경 취소
        binding.btnModifyCancel.setOnClickListener {
            settingInputMode()
        }
        //댓글 이미지 가져오기
        binding.ivImageSelect.setOnClickListener {
            if(!binding.edtCommentInput.hasFocus() && !detailViewModel.isUser()){
                binding.edtCommentInput.requestFocus()
                return@setOnClickListener
            }
            val imageIntent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
            }
            getImageResult.launch(Intent.createChooser(imageIntent, "GET Album"))
        }
        //이미지 지우기
        binding.ivCommentImageBack.setOnClickListener {
            goneImageView()
        }

        binding.edtCommentInput.setOnFocusChangeListener { view, b ->
            if(b && !detailViewModel.isUser()){
                binding.edtCommentInput.clearFocus()
                activity?.showErrorSnackBar(binding.root,ConstVariable.ERROR_PLEASE_LATER_LOGIN)
                return@setOnFocusChangeListener
            }
            commentLayoutFocusChange(b)
        }

        //댓글 색상
        binding.edtCommentInput.doOnTextChanged { text, _, _, _ ->
            detailViewModel.setCommentContent(text.toString())
        }
    }

    fun settingObserver() {
        detailViewModel.detailPostErrorLiveData.observe(this) {
            context?.showErrorSnackBar(binding.root, it)
            commentClickPos=null
            commentReplyClickPos=null
        }
        //게시글 및 댓글 아이템
        detailViewModel.postDetailDataLiveData.observe(this) {
            communityDetailPostAdapter.submitList(it) {
                if (moveInputCommentPosition) {
                    binding.rcPostList.smoothScrollToPosition(communityDetailPostAdapter.itemCount - 1)
                    moveInputCommentPosition = false
                }
                if (it[0] is DetailPostItem.CommunityPostDetail) {
                    val communityPostDetail = (it[0] as DetailPostItem.CommunityPostDetail)
                    settingToolbarBookmark(communityPostDetail.item.bookmarkYn)
                    detailPostArg.boardCode?.let { boardCode ->
                        detailViewModel.setModifyCommunityPost(
                            communityPostDetail,
                            detailPostArg.postId, boardCode
                        )
                    }
                }
            }

            arguments?.getInt("replyId",-1)?.let { argInt ->
                if(argInt != -1){
                    findNavController().navigate(
                        CommunityDetailPostFragmentDirections.actionDetailPostToComment(
                            detailPostArg.postId, detailPostArg.replyId
                        ))
                    arguments?.remove("replyId")
                }
            }
            currentLast=false
        }
        //툴바 이름
        detailViewModel.postToolbarNameLiveData.observe(this) {
            val code = it
            val title = requireContext().getStringByIdentifier(ConstVariable.Community.CategoryCode.compare(it).code)
            binding.postDetailToolbar.title = title

            //add toolbar title click event
            for(i in 0 until binding.postDetailToolbar.childCount){
                val childView = binding.postDetailToolbar[i]
                try {
                    if(childView is TextView){
                        childView.setOnClickListener {
                            findNavController().navigate(CommunityDetailPostFragmentDirections.actionCommunityTabFragmentToCommunityboard(
                                BoardInfo(title, code, ConstVariable.BOARD_COMMON_TYPE)
                            ))
                        }
                    }
                }catch (e:Exception){
                    Timber.e("${e.printStackTrace()}")
                }
            }
        }
        //댓글 텍스트 상태
        detailViewModel.commentContent.observe(this) {
            val buttonColor= if (it?.isNotEmpty() == true) {
                R.color.state_active_primary_default
            } else {
                R.color.state_disabled_gray_200
            }
            changeTextButtonColor(buttonColor)
            binding.btnCommentRegister.isClickable=it?.isNotEmpty()?:false
        }
        //게시글 좋아요 처리 , 싫어요 처리
        detailViewModel.likeClickResultLiveData.observe(this) {
            when (val adapterItem = communityDetailPostAdapter.currentList[0]) {
                is DetailPostItem.CommunityPostDetail -> {
                    if (it.first == ConstVariable.LIKE_CLICK) {
                        adapterItem.item.myLikeYn = !(adapterItem.item.myLikeYn?:false)
                        adapterItem.item.myDisLikeYn = false
                    } else if (it.first == ConstVariable.DISLIKE_CLICK) {
                        adapterItem.item.myDisLikeYn =!(adapterItem.item.myDisLikeYn?:false)
                        adapterItem.item.myLikeYn = false
                    }
                    adapterItem.item.likeCnt = it.second
                    adapterItem.item.dislikeCnt = it.third
                    communityDetailPostAdapter.notifyItemChanged(0, ConstVariable.PAYLOAD_LIKE_CLICK)
                }
                else -> {}
            }
        }
        //좋아요 싫어요 취소 처리
        detailViewModel.likeCancelResultLiveData.observe(this) {
            when (val adapterItem = communityDetailPostAdapter.currentList[0]) {
                is DetailPostItem.CommunityPostDetail -> {
                    if (it.first == ConstVariable.LIKE_CLICK) {
                        adapterItem.item.myLikeYn = false
                    } else if (it.first == ConstVariable.DISLIKE_CLICK) {
                        adapterItem.item.myDisLikeYn = false
                    }
                    adapterItem.item.likeCnt = it.second
                    adapterItem.item.dislikeCnt = it.third
                    communityDetailPostAdapter.notifyItemChanged(0, ConstVariable.PAYLOAD_LIKE_CLICK)
                }
                else -> {}
            }
        }

        //아너 처리
        detailViewModel.honorClickResultLiveData.observe(this) {
            when (val adapterItem = communityDetailPostAdapter.currentList[0]) {
                is DetailPostItem.CommunityPostDetail -> {
                    adapterItem.item.replyCnt = it.second
                    adapterItem.item.myHonorYn = it.first
                    communityDetailPostAdapter.notifyItemChanged(0, ConstVariable.PAYLOAD_HONOR_CLICK)
                }
                else -> {}
            }
        }

        //번역 클릭
        detailViewModel.translateClickResultLiveData.observe(this) {
            val items =it.second
            when (val adapterItem = communityDetailPostAdapter.currentList[0]) {
                is DetailPostItem.CommunityPostDetail -> {
                    adapterItem.item.translateYn = it.first
                    if(items!=null && items is DetailPostItem.CommunityPostDetail){
                        adapterItem.item.title=items.item.title
                        adapterItem.item.content=items.item.content
                    }
                    communityDetailPostAdapter.notifyItemChanged(0, ConstVariable.PAYLOAD_TRANSLATE_CLICK)
                }
                is DetailPostItem.NoticePostDetail -> {
                    adapterItem.item.translateYn = it.first
                    if(items!=null && items is DetailPostItem.NoticePostDetail){
                        adapterItem.item.title=items.item.title
                        adapterItem.item.content=items.item.content
                    }
                    communityDetailPostAdapter.notifyItemChanged(0, ConstVariable.PAYLOAD_TRANSLATE_CLICK)
                }
                else -> {}
            }
        }

        //댓글 좋아요 , 싫어요
        detailViewModel.commentLikeClickResultLiveData.observe(this) {
            commentClickPos ?: return@observe
            val adapterItem = communityDetailPostAdapter.currentList[commentClickPos!!]
            if(adapterItem is DetailPostItem.CommunityPostComment){
                when (it.first) {
                    ConstVariable.LIKE_CLICK -> {
                        adapterItem.item.myLikeYn = !(adapterItem.item.myLikeYn?:false)
                        adapterItem.item.myDisLikeYn = false
                    }
                    ConstVariable.DISLIKE_CLICK -> {
                        adapterItem.item.myDisLikeYn =!(adapterItem.item.myDisLikeYn?:false)
                        adapterItem.item.myLikeYn = false
                    }
                }
                adapterItem.item.likeCnt = it.second
                adapterItem.item.dislikeCnt = it.third
                communityDetailPostAdapter.notifyItemChanged(
                    commentClickPos!!,ConstVariable.PAYLOAD_LIKE_CLICK
                )
            }
            commentClickPos = null
        }

        //댓글 좋아요, 싫어요 취소 처리
        detailViewModel.commentLikeCancelResultLiveData.observe(this) {
            commentClickPos ?: return@observe
            val adapterItem = communityDetailPostAdapter.currentList[commentClickPos!!]
            if(adapterItem is DetailPostItem.CommunityPostComment){
                when (it.first) {
                    ConstVariable.LIKE_CLICK -> {
                        adapterItem.item.myLikeYn = false
                    }
                    ConstVariable.DISLIKE_CLICK -> {
                        adapterItem.item.myDisLikeYn = false
                    }
                }
                adapterItem.item.likeCnt = it.second
                adapterItem.item.dislikeCnt=it.third
                communityDetailPostAdapter.notifyItemChanged(
                    commentClickPos!!,
                    ConstVariable.PAYLOAD_LIKE_CLICK
                )
            }
            commentClickPos = null
        }

        //대_댓글 좋아요
        detailViewModel.replyLikeClickResultLiveData.observe(this) {
            if (commentClickPos != null && commentReplyClickPos != null) {
                val adapterItem = communityDetailPostAdapter.currentList[commentClickPos!!]
                if(adapterItem is DetailPostItem.CommunityPostComment){
                    val replyItems = adapterItem.item.childReplyList
                    if (replyItems != null) {
                        val item = replyItems[commentReplyClickPos!!]
                        when (it.first) {
                            ConstVariable.LIKE_CLICK -> {
                                item.myLikeYn =!(item.myLikeYn?:false)
                                item.myDisLikeYn = false
                            }
                            ConstVariable.DISLIKE_CLICK -> {
                                item.myDisLikeYn =!(item.myDisLikeYn?:false)
                                item.myLikeYn = false
                            }
                        }
                        item.likeCnt = it.second
                        item.dislikeCnt = it.third
                        communityDetailPostAdapter.replyPosition(commentReplyClickPos!!)
                        communityDetailPostAdapter.notifyItemChanged(
                            commentClickPos!!,
                            ConstVariable.PAYLOAD_REPLY_LIKE_CLICK
                        )
                    }
                }
            }
            commentReplyClickPos = null
            commentClickPos = null
        }

        //대 댓글 좋아요 싫어요 처리
        detailViewModel.commentReplyLikeCancelResultLiveData.observe(this) {
            if (commentClickPos != null && commentReplyClickPos != null) {
                val adapterItem = communityDetailPostAdapter.currentList[commentClickPos!!]
                if(adapterItem is DetailPostItem.CommunityPostComment){
                    val replyItems = adapterItem.item.childReplyList
                    if (replyItems != null) {
                        val item = replyItems[commentReplyClickPos!!]
                        when (it.first) {
                            ConstVariable.LIKE_CLICK -> {
                                item.myLikeYn = false
                            }
                            ConstVariable.DISLIKE_CLICK -> {
                                item.myDisLikeYn = false
                            }
                        }
                        item.likeCnt = it.second
                        item.dislikeCnt = it.third
                        communityDetailPostAdapter.replyPosition(commentReplyClickPos!!)
                        communityDetailPostAdapter.notifyItemChanged(
                            commentClickPos!!,
                            ConstVariable.PAYLOAD_REPLY_LIKE_CLICK
                        )
                    }
                }
            }
            commentReplyClickPos = null
            commentClickPos = null
        }

        //댓글 대댓 번역
        detailViewModel.transCommentResult.observe(this){
            if(communityDetailPostAdapter.currentList[commentClickPos!!] is DetailPostItem.CommunityPostComment){
                val adapterItem = communityDetailPostAdapter.currentList[commentClickPos!!] as DetailPostItem.CommunityPostComment
                if(commentReplyClickPos !=null){
                    val replyItem =adapterItem.item.childReplyList
                    if(!replyItem.isNullOrEmpty()){
                        val item =  replyItem[commentReplyClickPos!!]
                        item.content=it.second
                        item.translateYn=it.first
                    }
                    communityDetailPostAdapter.replyPosition(commentReplyClickPos!!)
                    communityDetailPostAdapter.notifyItemChanged(
                        commentClickPos!!,
                        ConstVariable.PAYLOAD_TRANSLATE_CLICK
                    )
                }else{
                    val replyItem =adapterItem.item
                    replyItem.content=it.second
                    replyItem.translateYn=it.first
                    communityDetailPostAdapter.notifyItemChanged(
                        commentClickPos!!,
                        ConstVariable.PAYLOAD_TRANSLATE_CLICK
                    )
                }
            }
            commentReplyClickPos = null
            commentClickPos = null
        }


        //댓글 이미지 추가
        detailViewModel.imageUrlLiveData.observe(viewLifecycleOwner) {
            it ?: return@observe
            binding.clCommentImageContainer.visibility = View.VISIBLE
            setImageWithPlaceHolder(binding.ivCommentImage, it.toString(), maskingId = R.drawable.border_radius_8)
            val color = requireContext().getColor(R.color.state_active_gray_900)
            binding.ivImageSelect.imageTintList = ColorStateList.valueOf(color)
        }
        //북마크 결과 표시
        detailViewModel.bookmarkClickResultLiveData.observe(this) {
            context?.let {
                val postItem = communityDetailPostAdapter.currentList[0]
                if (postItem is DetailPostItem.CommunityPostDetail) {
                    postItem.item.bookmarkYn = !postItem.item.bookmarkYn
                    settingToolbarBookmark(postItem.item.bookmarkYn)
                }
            }
        }
        //댓글 신고
        detailViewModel.reportCommentResultLiveData.observe(this) {
            context?.let {
                CommonDialog.Builder().apply {
                    title = it.getString(R.string.s_report_complete)
                    message = it.getString(R.string.se_s_report_complete)
                    positiveButtonString = it.getString(R.string.h_confirm)
                }.build().show(parentFragmentManager, "reportComplete")
            }
        }
        //댓글 차단
        detailViewModel.blockCommentResultLiveData.observe(this) {
            getCommentList()
        }

        //게시글 차단
        detailViewModel.blockPostResultLiveData.observe(this){
            getPostInfoData()
        }
        //게시글 삭제
        detailViewModel.deletePostResultLiveData.observe(this){
            if(it){
                activity?.showCustomSnackBar(binding.root,getString(R.string.se_g_post_deleted))
                findNavController().popBackStack()
            }
        }
        //댓글 계정 차단
        detailViewModel.blockAccountResultLiveData.observe(this) {
            getPostInfoData()
        }

        //댓글 등록 이미지 버전
        detailViewModel.cloudFlareUploadLiveData.observe(this) {
            if(it == ERROR_COMMENT_IMAGE_UPLOAD_FAIL){
                activity?.showCustomSnackBar(binding.root,"업로드가 실패했습니다. 네트워크 상태를 확인해 주세요.")
                goneImageView()
            }else{
                detailViewModel.setImageDataInCommentItem( it,"image" )
                if (detailViewModel.isModifyMode.value == true) {
                    detailViewModel.clickCommentModify(
                        detailPostArg.postId.toString(),
                        detailViewModel.replyId.value ?: ""
                    )
                } else {
                    detailViewModel.clickCommentSendBtn(
                        detailPostArg.postId,
                    )
                }
            }
        }

        //댓글 익명 여부 상태
        detailViewModel.anonymYN.observe(this) {
            if (it) {
                binding.ivAnonymous.setColorFilter(requireContext().getColor(R.color.state_active_primary_default))
            } else {
                binding.ivAnonymous.setColorFilter(requireContext().getColor(R.color.state_active_gray_700))
            }
        }

        //댓글 등록 결과
        detailViewModel.commentInputResultLiveData.observe(this) {
            detailViewModel.getCommunityCommentAllList(detailPostArg.postId.toString(),"init")
            moveInputCommentPosition=true
            clearInputState()
             commentLayoutFocusChange(false)
            detailViewModel.settingCommentInputingState(false)
        }

        //댓글 수정 결과
        detailViewModel.commentModifyResultLiveData.observe(this) {
            getCommentList()
            clearInputState()
             commentLayoutFocusChange(false)
            detailViewModel.setModifyMode(false)
            detailViewModel.settingCommentInputingState(false)
        }

        //댓글 삭제
        detailViewModel.commentDeleteResultLiveData.observe(this) {
            getCommentList()
        }

        //신고 리스트
        detailViewModel.reportMessageList.observe(this){
            reportBottomSheet(it.first,it.second.split("#&")[0],it.third,it.second.split("#&")[1])
        }

        //댓글 입력 상태
        detailViewModel.commentInputing.observe(this){
            isEnableCommentView(!it)
        }
    }

    fun isEnableCommentView(enable : Boolean){
        with(binding){
//            ivImageSelect.isClickable=enable
            if(!enable) goneImageView()
            ivImageSelect.isEnabled=enable
            ivAnonymous.isEnabled=enable
            edtCommentInput.isEnabled=enable
            btnModifyCancel.isEnabled=enable
            ivCommentImageBack.isEnabled=enable
        }
    }

    fun goneImageView(){
        binding.clCommentImageContainer.visibility = View.GONE
        detailViewModel.setInitAttachImage()
        imageFile = null
        val color = requireContext().getColor(R.color.state_enable_gray_400)
        binding.ivImageSelect.imageTintList = ColorStateList.valueOf(color)
    }

    fun commentReplayLikeDisLikeClick(parentPos :Int, pos :Int, commentItem: CommunityReplyData, type:String,isCancel: Boolean){
        if(commentClickPos!=null) return
        commentClickPos = parentPos
        commentReplyClickPos = pos
        detailViewModel.clickCommentReplyLike(
            type,
            commentItem.replyId.toString(),
            isCancel
        )
    }

    private fun clearInputState(){
        settingInputMode()
        binding.edtCommentInput.clearFocus()
        hideSoftInput(binding.root)
    }

    @SuppressLint("RestrictedApi")
    private fun initToolbar() {
        if (detailPostArg.postType == ConstVariable.TYPE_COMMUNITY_NOTICE) {
            binding.postDetailToolbar.menu.removeItem(R.id.menu_post_bookmark)
        }
        binding.postDetailToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun initStatusBar() {
        activity?.let {
            it.setStatusBarBack()
            it.window?.statusBarColor = Color.WHITE
            it.setDarkStatusBarIcon()
        }
    }

    private fun settingToolbarBookmark(isBookMark: Boolean) {
        context?.let { context ->
            val bookmarkMenu =
                binding.postDetailToolbar.menu.findItem(R.id.menu_post_bookmark)
            val bookmarkDrawable =
                if (isBookMark) ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.icon_fill_bookmark_1,
                    null
                )
                else ResourcesCompat.getDrawable(resources, R.drawable.icon_outline_favorite, null)
            DrawableCompat.setTint(
                bookmarkDrawable!!,
                ContextCompat.getColor(
                    context,
                    if (isBookMark) R.color.primary_default else R.color.state_enable_gray_900
                )
            )
            bookmarkMenu.icon = bookmarkDrawable
        }
    }

    private fun getPostInfoData(){
        communityDetailPostAdapter.setPostType(detailPostArg.postType)
        when (detailPostArg.postType) {
            ConstVariable.TYPE_COMMUNITY -> {
                detailViewModel.getCommunityPostData(
                    detailPostArg.boardCode ?: "",
                    detailPostArg.postId
                )
            }
            ConstVariable.TYPE_COMMUNITY_NOTICE -> {
                setNoticeView()
                communityDetailPostAdapter.setBoardCode(detailPostArg.boardCode)
                detailViewModel.requestCommunityNoticeDetailItem(
                    detailPostArg.boardCode,
                    detailPostArg.postId
                )
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun settingModifyMode(commentText: String, imageId: List<DetailAttachList>?,isAnonym:Boolean) {
        detailViewModel.setModifyMode(true)
        binding.btnModifyCancel.visibility = View.VISIBLE
        binding.btnCommentRegister.setImageDrawable(requireContext().getDrawable(R.drawable.icon_fill_check))
        var buttonDrawable: Drawable = binding.btnCommentRegister.background
        buttonDrawable = DrawableCompat.wrap(buttonDrawable)
        DrawableCompat.setTint(
            buttonDrawable,
            requireContext().getColor(R.color.state_active_primary_default)
        )
        binding.btnCommentRegister.background = buttonDrawable
        binding.edtCommentInput.setText(commentText)
        binding.edtCommentInput.requestFocus()
        if(isAnonym){
            detailViewModel.setAnonymYN(isAnonym)
            binding.ivAnonymous.visibility=View.VISIBLE
            binding.ivAnonymous.isClickable=false
        }else{
            binding.ivAnonymous.visibility=View.GONE
        }
        imageFile = null
        imageId?.let {
            detailViewModel.setAttachItem(it)
            if (it.isNotEmpty()) {
                binding.clCommentImageContainer.visibility = View.VISIBLE
                detailViewModel.addOnImageList(
                    Uri.parse(
                        requireContext().getString(
                            R.string.imageUrlBase,
                            it[0].id
                        )
                    )
                )
            } else {
                binding.clCommentImageContainer.visibility = View.GONE
                detailViewModel.addOnImageList(null)
            }
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun settingInputMode() {
        detailViewModel.setModifyMode(false)
        detailViewModel.setAnonymYN(false)
        binding.ivAnonymous.isClickable=true
        binding.btnModifyCancel.visibility = View.GONE
        binding.btnCommentRegister.setImageDrawable(requireContext().getDrawable(R.drawable.icon_outline_send))
        binding.ivAnonymous.setColorFilter(requireContext().getColor(R.color.state_active_gray_700))
        changeTextButtonColor(R.color.state_disabled_gray_200)
        binding.edtCommentInput.setText("")
        binding.clCommentImageContainer.visibility = View.GONE
        detailViewModel.addOnImageList(null)
        detailViewModel.setAttachItem(null)
        imageFile = null
    }

    private fun commentLayoutFocusChange(isFocus :Boolean){
        if(isFocus){
            (binding.ivImageSelect.layoutParams as ConstraintLayout.LayoutParams).topToBottom=binding.edtCommentInput.id
            (binding.edtCommentInput.layoutParams as ConstraintLayout.LayoutParams).startToEnd=ConstraintLayout.LayoutParams.UNSET
            (binding.edtCommentInput.layoutParams as ConstraintLayout.LayoutParams).startToStart=ConstraintLayout.LayoutParams.PARENT_ID
            (binding.edtCommentInput.layoutParams as ConstraintLayout.LayoutParams).width=ConstraintLayout.LayoutParams.MATCH_PARENT
            (binding.edtCommentInput.layoutParams as ConstraintLayout.LayoutParams).bottomToBottom=ConstraintLayout.LayoutParams.UNSET
            binding.ivAnonymous.visibility=View.VISIBLE
            binding.btnCommentRegister.visibility=View.VISIBLE
        }else{
            (binding.ivImageSelect.layoutParams as ConstraintLayout.LayoutParams).topToBottom=ConstraintLayout.LayoutParams.UNSET
            (binding.edtCommentInput.layoutParams as ConstraintLayout.LayoutParams).startToStart=ConstraintLayout.LayoutParams.UNSET
            (binding.edtCommentInput.layoutParams as ConstraintLayout.LayoutParams).startToEnd=binding.ivImageSelect.id
            (binding.edtCommentInput.layoutParams as ConstraintLayout.LayoutParams).width=0
            (binding.edtCommentInput.layoutParams as ConstraintLayout.LayoutParams).bottomToBottom=binding.ivImageSelect.id
            binding.ivAnonymous.visibility=View.GONE
            binding.btnCommentRegister.visibility=View.GONE
        }
    }

    private fun changeTextButtonColor(@ColorRes color :Int){
        var buttonDrawable: Drawable = binding.btnCommentRegister.background
        buttonDrawable = DrawableCompat.wrap(buttonDrawable)
        DrawableCompat.setTint(
            buttonDrawable,
            requireContext().getColor(color)
        )
        binding.btnCommentRegister.background = buttonDrawable
    }

    private fun getCommentList(){
        when(detailPostArg.postType){
            ConstVariable.TYPE_COMMUNITY->{
                detailViewModel.setInitReplyNextId()
                detailViewModel.getCommunityCommentList(detailPostArg.postId.toString(), "init")
            }
        }
    }

    private fun postOptionSheet(postItem : BoardPostDetailData) {
        activity?.let {
            CustomBottomSheet().apply {
                setBottomItems(
                    postOptionSheetItem(
                       postItem.pieceBlockYn?:false,
                        postItem.userBlockYn?:false,
                        postItem.integUid,
                        postItem.bookmarkYn,
                        detailViewModel.isUser()
                    )
                )
                setOnCliCkListener(object : BottomSheetAdapter.OnItemClickListener {
                    override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                        var dialogItems :Triple<String,String,CommonDialog.ClickListener>? =null
                        when (name) {
                            it.getString(R.string.a_unblock_this_user), it.getString(R.string.a_block_this_user) -> {
                                dialogItems = Triple(
                                    name,
                                    if(name==it.getString(R.string.a_unblock_this_user))getString(R.string.se_s_do_you_want_release_block_select_user)
                                        else getString(R.string.se_s_do_you_want_block_select_user),
                                    object :CommonDialog.ClickListener{ override fun onClick() {
                                            detailViewModel.clickBlockAccountComment(
                                                postItem.integUid,
                                                postItem.userBlockYn ?: false
                                            )
                                        }
                                    }
                                )
                            }
                            it.getString(R.string.g_unblock_post), it.getString(R.string.g_block_post) -> {
                                dialogItems = Triple(
                                    name,
                                    if(name==it.getString(R.string.g_unblock_post))getString(R.string.se_a_want_to_unblock_post)
                                    else getString(R.string.se_a_want_to_block_post),
                                    object :CommonDialog.ClickListener{ override fun onClick() {
                                            detailViewModel.clickBlockPost(
                                                detailPostArg.postId,
                                                postItem.integUid,
                                                postItem.pieceBlockYn ?: false,
                                            )
                                        }
                                    }
                                )
                            }
                            getString(R.string.j_to_save),getString(R.string.j_save_cancel)->{
                                detailViewModel.clickBookmarkPost(
                                    detailPostArg.postId,
                                    postItem.bookmarkYn
                                )
                            }
                            getString(R.string.g_to_share) ->{
                                requireContext().startActivity(Intent.createChooser(
                                    Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "")//todo : url값
                                        type = "text/plain"
                                    },
                                    null
                                ))
                            }
                            getString(R.string.bottom_sheet_edit) ->{
                                val modifyCommunityPost = detailViewModel.getModifyCommunityPost()
                                Timber.d("start editor for modify, post: $modifyCommunityPost")
                                val editorInfo = EditorInfo(EditorType.COMMUNITY, modifyCommunityPost, null, null)
                                findNavController().navigate(
                                    CommunityDetailPostFragmentDirections.actionDetailPostToEditorFragment(
                                        editorInfo
                                    )
                                )
                            }
                            getString(R.string.bottom_sheet_delete) ->{
                                dialogItems = Triple(
                                    name,
                                    getString(R.string.se_h_want_to_delete_post),
                                    object :CommonDialog.ClickListener{ override fun onClick() {
                                            detailViewModel.clickDeletePost(postItem.code,postItem.postId)
                                        }
                                    }
                                )

                            }
                            getString(R.string.s_to_report)->{
                                detailViewModel.getReportMessageList("POST",postItem.postId,postItem.integUid)
                                dismiss()
                                return
                            }
                        }
                        if(dialogItems!=null){
                            showCheckDialog(dialogItems.first,dialogItems.second,dialogItems.third)
                        }
                        dismiss()
                    }
                })
            }.show(childFragmentManager, "postDetailOPtions")
        }
    }

    private fun postOptionSheetItem(
        isPieceBlock: Boolean,
        isUserBlock: Boolean,
        commentUid: String,
        isSavePost :Boolean,
        isLoginUser: Boolean
    ) = if(!isLoginUser){
            arrayListOf(
                BottomSheetItem(
                    R.drawable.icon_outline_share,
                    getString(R.string.g_to_share),
                    null,
                    null
                ),
            )
        }else if (commentUid == detailViewModel.uId) {
            arrayListOf(
                BottomSheetItem(
                    R.drawable.icon_outline_share,
                    getString(R.string.g_to_share),
                    null,
                    null
                ),
                BottomSheetItem(
                    R.drawable.icon_outline_post_n,
                    getString(R.string.bottom_sheet_edit),
                    null,
                    null
                ),
                BottomSheetItem(
                    R.drawable.icon_outline_trash,
                    getString(R.string.bottom_sheet_delete),
                    null,
                    null
                ),
            )
        } else {
            arrayListOf(
                BottomSheetItem(
                    R.drawable.icon_outline_save,
                    if(isSavePost){
                        getString(R.string.j_save_cancel)
                    }else{
                        getString(R.string.j_to_save)
                    },
                    null,
                    null
                ),
                BottomSheetItem(
                    R.drawable.icon_outline_share,
                    getString(R.string.g_to_share),
                    null,
                    null
                ),
                BottomSheetItem(
                    R.drawable.icon_outline_siren,
                    getString(R.string.s_to_report),
                    null,
                    null
                ),
                BottomSheetItem(
                    R.drawable.icon_outline_hide,
                    if (isPieceBlock) getString(R.string.g_unblock_post) else getString(R.string.g_block_post),
                    null,
                    null
                ),
                BottomSheetItem(
                    R.drawable.icon_outline_blockaccount,
                    if (isUserBlock) getString(R.string.a_unblock_this_user) else getString(R.string.a_block_this_user),
                    null,
                    null
                ),
            )
        }

    private fun noticePostOptionSheet(postItem : CommunityNoticeData) {
        activity?.let {
            CustomBottomSheet().apply {
                setBottomItems(
                    getNoticePostSheetItem(
                        detailViewModel.isUser(),
                        false,
                    )
                )
                setOnCliCkListener(object : BottomSheetAdapter.OnItemClickListener {
                    override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                        var dialogItems :Triple<String,String,CommonDialog.ClickListener>? =null
                        when (name) {
                            getString(R.string.g_to_share) ->{

                            }
                            getString(R.string.j_to_save),getString(R.string.j_save_cancel) ->{
                                detailViewModel.clickBookmarkPost(
                                    detailPostArg.postId,
                                    false
                                )
                            }
                        }
                        if(dialogItems!=null){
                            showCheckDialog(dialogItems.first,dialogItems.second,dialogItems.third)
                        }
                        dismiss()
                    }
                })
            }.show(childFragmentManager, "postDetailOPtions")
        }
    }

    private fun getNoticePostSheetItem(isLoginUser: Boolean,isSavePost :Boolean)=
        if(isLoginUser){
            arrayListOf(
                BottomSheetItem(
                    R.drawable.icon_outline_share,
                    getString(R.string.g_to_share),
                    null,
                    null
                ),
            )
        }else{
            arrayListOf(
                BottomSheetItem(
                    R.drawable.icon_outline_share,
                    getString(R.string.g_to_share),
                    null,
                    null
                ),
            )
        }

    private fun showCommentBottomSheet(commentItem :CommunityReplyData){
        activity?.let {
            CustomBottomSheet().apply {
                setBottomItems(
                    commentOptionSheetItem(
                        commentItem.pieceBlockYn,
                        commentItem.userBlockYn,
                        commentItem.integUid,
                    )
                )
                setOnCliCkListener(object : BottomSheetAdapter.OnItemClickListener {
                    override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                        var dialogItems : Triple<String,String,CommonDialog.ClickListener>?= null
                        when (name) {
                            it.getString(R.string.s_to_report) -> {
                                //신고
                                detailViewModel.getReportMessageList("REPLY",commentItem.replyId,commentItem.integUid)
                                dismiss()
                                return
                            }
                            it.getString(R.string.a_block_comment) -> {
                                //게시글 차단
                                dialogItems= Triple( it.getString(R.string.a_block_comment),it.getString(R.string.se_s_want_selected_comment_block),object :
                                    CommonDialog.ClickListener {
                                    override fun onClick() {
                                        detailViewModel.clickBlockComment(
                                            commentItem.replyId,
                                            commentItem.integUid,
                                            commentItem.pieceBlockYn == true
                                        )
                                    }
                                })
                            }
                            it.getString(R.string.a_see_comment) -> {
                                dialogItems= Triple(  it.getString(R.string.a_see_comment),it.getString(R.string.se_a_want_see_comment),object :
                                    CommonDialog.ClickListener {
                                    override fun onClick() {
                                        detailViewModel.clickBlockComment(
                                            commentItem.replyId,
                                            commentItem.integUid,
                                            commentItem.pieceBlockYn == true
                                        )
                                    }
                                })
                            }
                            it.getString(R.string.g_block_account) -> {
                                dialogItems= Triple(it.getString(R.string.g_block_account),it.getString(R.string.se_s_do_you_want_block_select_user),object :
                                    CommonDialog.ClickListener {
                                    override fun onClick() {
                                        detailViewModel.clickBlockAccountComment(
                                            commentItem.integUid,
                                            commentItem.userBlockYn == true
                                        )
                                    }
                                })
                            }
                            it.getString(R.string.c_release_account_block) -> {
                                dialogItems= Triple(it.getString(R.string.c_release_account_block),it.getString(R.string.se_s_do_you_want_release_block_select_user),object :
                                    CommonDialog.ClickListener {
                                    override fun onClick() {
                                        detailViewModel.clickBlockAccountComment(
                                            commentItem.integUid,
                                            commentItem.userBlockYn == true
                                        )
                                    }
                                })
                            }
                            getString(R.string.a_modify_comment) -> {
                                dialogItems= Triple(it.getString(R.string.a_modify_comment),it.getString(R.string.se_a_want_modify_comment),object :
                                    CommonDialog.ClickListener {
                                    override fun onClick() {
                                        settingModifyMode(
                                            commentItem.content,
                                            commentItem.attachList,
                                            commentItem.anonymYn
                                        )
                                        detailViewModel.setReplyId(commentItem.replyId.toString())
                                    }
                                })
                            }
                            getString(R.string.a_delete_comment) -> {
                                dialogItems= Triple(it.getString(R.string.a_delete_comment),it.getString(R.string.se_a_want_delete_comment),object :
                                    CommonDialog.ClickListener {
                                    override fun onClick() {
                                        detailViewModel.clickCommentDelete(
                                            detailPostArg.postId.toString(),
                                            commentItem.replyId.toString()
                                        )
                                    }
                                })
                            }
                        }
                        if(dialogItems!=null){
                            showCheckDialog(dialogItems.first,dialogItems.second,dialogItems.third)
                        }
                        dismiss()
                    }
                })
            }.show(parentFragmentManager, "comment")
        }
    }

    private fun commentOptionSheetItem(isPieceBlock: Boolean?, isUserBlock: Boolean?, commentUid: String) =
     if (commentUid == detailViewModel.uId) {
            arrayListOf(
                BottomSheetItem(
                    R.drawable.icon_outline_post_n, getString(R.string.a_modify_comment), null, null
                ),
                BottomSheetItem(
                    R.drawable.icon_outline_trash, getString(R.string.a_delete_comment),null,null
                ),
            )
        } else {
            arrayListOf(
                BottomSheetItem(
                    R.drawable.icon_outline_siren,getString(R.string.s_to_report),null,null
                ),
                BottomSheetItem(
                    R.drawable.icon_outline_hide,
                    if (isPieceBlock == true) getString(R.string.a_see_comment)
                    else getString(R.string.a_block_comment),
                    null,
                    null
                ),
                BottomSheetItem(
                    R.drawable.icon_outline_blockaccount,
                    if (isUserBlock == true) getString(R.string.c_release_account_block)
                    else getString(R.string.g_block_account),
                    null,
                    null
                ),
            )
        }

    private fun reportBottomSheet(
        sheetTextItem: List<BottomSheetItem>,
        type: String,
        targetId: Int,
        targetUId: String
    ) {
        activity?.let {
            CustomBottomSheet().apply {
                setBottomItems(sheetTextItem as ArrayList<BottomSheetItem>)
                setOnCliCkListener(object : BottomSheetAdapter.OnItemClickListener {
                    override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                        var dialogItems : Pair<String,String>? =null
                        when (type) {
                            "POST"->{
                                dialogItems= Pair(it.getString(R.string.a_report_post),it.getString(R.string.se_s_want_selected_post_report))
                            }
                            "REPLY"->{
                                dialogItems = Pair(it.getString(R.string.a_report_comment), it.getString(R.string.se_s_want_selected_comment_report))
                            }
                        }
                        if(dialogItems!=null){
                            showCheckDialog(dialogItems.first,dialogItems.second,object :
                                CommonDialog.ClickListener {
                                override fun onClick() {
                                    detailViewModel.callReportPostComment(
                                        targetId,type,targetUId,pos
                                    )
                                }
                            })
                        }
                        dismiss()
                    }
                })
            }.show(childFragmentManager, "reportBottomDialog")
        }
    }

    private fun showCheckDialog(title:String,message:String,clickListener :CommonDialog.ClickListener){
        val commonDialog = CommonDialog.Builder()
        commonDialog.title =title
        commonDialog.message =message
        commonDialog.setPositiveButtonClickListener(clickListener)
        commonDialog.positiveButtonString = getString(R.string.h_confirm)
        commonDialog.negativeButtonString = getString(R.string.c_cancel)
        commonDialog.build().show(parentFragmentManager, "dialogComment")
    }

    private fun settingPlayer() =object :
        PostDetailImageAdapter.OnAttachListClickListener {
        override fun onFullScreen(isFull: Boolean, isPort: Boolean) {
            settingFullScreen(isFull,isPort)
        }

        override fun onImageClick(pos: Int) {
            val sendAttachList :Array<String> = detailViewModel.attachImageList!!.toTypedArray()
            findNavController().navigate(R.id.action_detailPost_to_imageListViewerFragment,
                arguments.apply {
                    this?.putStringArray("imageUrlList",sendAttachList)
                    this?.putInt("imagePos",pos)
                }
            )
        }
    }

    private fun settingFullScreen(isFull :Boolean,isVideoPort:Boolean){
        if (isFull) {
            activity?.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        } else {
            activity?.requestedOrientation =
                if(isVideoPort) ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }
    }

    private fun registerCommunityComment(){
        if (detailViewModel.isModifyMode.value == true) {
            if (imageFile == null) {
                //image 포함인지 아닌지 확인
                detailViewModel.clickCommentModify(
                    detailPostArg.postId.toString(),
                    detailViewModel.replyId.value.toString()
                )
            } else {
                //image 포함 댓글
                detailViewModel.sendImageToCloudFlare(
                    resources.getString(R.string.cloudFlareKey),
                    imageFile!!
                )
            }
        } else {
            if (imageFile == null) {
                detailViewModel.clickCommentSendBtn(
                    detailPostArg.postId,
                )
            } else {
                detailViewModel.sendImageToCloudFlare(
                    resources.getString(R.string.cloudFlareKey),
                    imageFile!!
                )
            }
        }
    }

    private fun setTopScrollButtonVisibility(visibility:Int){
        binding.scrollUpBtn.visibility = visibility
    }

    private val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        activity?.let{
            val visible = it.isKeyboardShown()
            if(!visible && detailViewModel.commentContent.value.isNullOrBlank()){
                binding.edtCommentInput.clearFocus()
            }
        }
    }

    private val postListScrollListener = object :RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if(!recyclerView.canScrollVertically(-1)){
                setTopScrollButtonVisibility(View.GONE)
            }else{
                setTopScrollButtonVisibility(View.VISIBLE)
            }
        }
    }

    private fun getCommentImageResult() = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data
            data ?: return@registerForActivityResult
            context?.let { context ->
                imageFile = data.data!!.asMultipart(
                    "file",
                    "image_${Date().time}",
                    context.contentResolver
                )
            }
            detailViewModel.addOnImageList(data.data)
        }
    }

    override fun onResume() {
        super.onResume()
        if (communityDetailPostAdapter.itemCount > 0) {
            when(val adapterVH = binding.rcPostList.findViewHolderForAdapterPosition(0)){
                is NoticePostVH ->{
                    adapterVH.getImageAdapter().playerStateChange(adapterVH,"prepare",settingPlayer())
                }
                is CommunityPostDetailVH ->{
                    adapterVH.getImageAdapter().playerStateChange(adapterVH,"prepare",settingPlayer())
                }
            }
        }
        activity?.addOnGlobalLayoutListener(globalLayoutListener)
        binding.rcPostList.addOnScrollListener(postListScrollListener)
    }

    override fun onPause() {
        super.onPause()
        activity?.removeOnGlobalLayoutListener(globalLayoutListener)
        binding.rcPostList.clearOnScrollListeners()
        if (communityDetailPostAdapter.itemCount > 0) {
            when(val adapterVH = binding.rcPostList.findViewHolderForAdapterPosition(0)){
                is NoticePostVH ->{
                    adapterVH.getImageAdapter().playerStateChange(adapterVH,"pause",null)
                }
                is CommunityPostDetailVH ->{
                    adapterVH.getImageAdapter().playerStateChange(adapterVH,"pause",null)
                }
            }
        }
    }

    override fun onDestroy() {
        if (communityDetailPostAdapter.itemCount > 0) {
            when(val adapterVH = binding.rcPostList.findViewHolderForAdapterPosition(0)){
                is NoticePostVH ->{
                    adapterVH.getImageAdapter().playerStateChange(adapterVH,"stop",null)
                }
                is CommunityPostDetailVH ->{
                    adapterVH.getImageAdapter().playerStateChange(adapterVH,"stop",null)
                }
            }
        }
        super.onDestroy()
    }

}