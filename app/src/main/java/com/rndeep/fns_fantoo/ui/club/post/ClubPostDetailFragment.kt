package com.rndeep.fns_fantoo.ui.club.post

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.ClubReplyData
import com.rndeep.fns_fantoo.data.remote.model.CommunityReplyData
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostAttachList
import com.rndeep.fns_fantoo.databinding.FragmentDetailPostBinding
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetAdapter
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetItem
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.CustomBottomSheet
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.ui.common.post.PostDetailImageAdapter
import com.rndeep.fns_fantoo.ui.common.post.PostDetailReplayAdapter
import com.rndeep.fns_fantoo.ui.community.postdetail.data.DetailPostItem
import com.rndeep.fns_fantoo.ui.community.postdetail.viewholder.CommunityPostDetailVH
import com.rndeep.fns_fantoo.ui.community.postdetail.viewholder.NoticePostVH
import com.rndeep.fns_fantoo.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ClubPostDetailFragment :
    BaseFragment<FragmentDetailPostBinding>(FragmentDetailPostBinding::inflate) {

    private val detailViewModel: ClubPostDetailViewModel by viewModels()

    private val clubDetailArg: ClubPostDetailFragmentArgs by navArgs()

    private val detailPostAdapter = ClubPostDetailAdapter()

    //????????? intent
    private lateinit var getImageResult: ActivityResultLauncher<Intent>
    private var modifyImageId: String? = null
    private var replyId: Int? = null

    //?????? ?????????
    private var isCommentInputing = false
    private var moveCommentBottom = false

    private var moveReply = false
    private var lastLoading = false

    private var commentPos :Int? =null
    private var commentReplyPos :Int? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getImageResult = getCommentImageResult()
        initStatusBar()
    }

    override fun initUi() {
        initToolbar()
        //?????? ?????? ?????????
        settingInputMode()

        settingObserver()
        binding.rcPostList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        detailPostAdapter.isLoginUser(detailViewModel.uId != null)
        binding.rcPostList.adapter = detailPostAdapter

        binding.rcPostList.checkLastItemVisible {
            if (it && !lastLoading && detailPostAdapter.itemCount > 4) {
                lastLoading = true
                when (clubDetailArg.previewType) {
                    ConstVariable.TYPE_CLUB, ConstVariable.TYPE_CLUB_NOTICE -> {
                        detailViewModel.getClubCommentList(
                            clubDetailArg.clubId,
                            clubDetailArg.categoryCode,
                            clubDetailArg.postId,
                            "Add"
                        )
                    }
                }
            }
        }

        //?????? ?????? ??????
        detailPostAdapter.setOnFullScreenClickListener(object :
            PostDetailImageAdapter.OnFullScreenClickListener {
            override fun onFullScreen(isFull: Boolean, isPort: Boolean) {
                settingFullScreen(isFull, isPort)
            }
        })

        detailPostAdapter.setOnPostDetailOptionsClickListener(object :
            ClubPostDetailAdapter.OnClubPostDetailOptionsClickListener {
            override fun onTransLateClick(originItem: List<String>, transYn: Boolean) {
                detailViewModel.clickTranslatePost(originItem, transYn)
            }

            override fun onShareClick() {

            }

            override fun onCategoryTextClick(clubId: String, categoryCode: String) {
                if(navController.previousBackStackEntry?.destination?.id == R.id.clubDetailPageFragment){
                    findNavController().navigate(R.id.action_clubPostDetailFragment_to_club_detail_page2,
                        arguments.apply {
                            this?.putString("clubId", clubId)
                            this?.putString("clubCategoryCode", categoryCode)
                        },
                        NavOptions.Builder().setPopUpTo(R.id.clubDetailPageFragment,true).build()
                    )
                }else{
                    findNavController().navigate(R.id.action_clubPostDetailFragment_to_club_detail_page2,
                        arguments.apply {
                            this?.putString("clubId", clubId)
                            this?.putString("clubCategoryCode", categoryCode)
                        },
                        NavOptions.Builder().setPopUpTo(R.id.clubPostDetailFragment,true).build()
                    )
                }
            }
            override fun onProfileClick(
                clubId: String,
                memberId: Int,
                nickName: String,
                isBlock :Boolean,
                userPhoto: String
            ) {
                if(!detailViewModel.getIsLogin()){
                    activity?.showErrorSnackBar(binding.root,ConstVariable.ERROR_PLEASE_LATER_LOGIN)
                    return
                }
                if(!detailViewModel.isMember){
                    findNavController().navigate(R.id.action_detailPost_to_profile,
                        arguments.apply {
                            this?.putString("nickname",nickName)
                            this?.putString("clubId",clubId)
                            this?.putBoolean("isBlock",isBlock)
                            this?.putString("memberId",memberId.toString())
                            this?.putString("userPhoto",userPhoto)
                        })
                }else{
                    findNavController().navigate(R.id.action_clubPostDetailFragment_to_club_member,
                        arguments.apply {
                            this?.putString("clubId",clubId)
                            this?.putString("integUid",detailViewModel.uId)
                            this?.putInt("memberId",memberId)
                            this?.putString("myMemberId","0")
                        })
                }
            }
        })
        detailPostAdapter.setOnCommentClickListener(object :
            ClubPostDetailAdapter.OnClubCommentClickListener {
            override fun onCommentClick(commentItem: ClubReplyData) {
                findNavController().navigate(
                    ClubPostDetailFragmentDirections.actionClubPostDetailFragmentToClubComment(
                        detailViewModel.isMember,
                        commentItem.postId,
                        commentItem.replyId,
                        commentItem.categoryCode,
                        commentItem.clubId
                    )
                )
            }

            override fun onOptionsClick(commentItem: ClubReplyData, pos: Int) {
                showCommentBottomSheet(commentItem)
            }

            override fun onProfileClick(commentItem: ClubReplyData) {
                if(!detailViewModel.getIsLogin()){
                    activity?.showErrorSnackBar(binding.root,ConstVariable.ERROR_PLEASE_LATER_LOGIN)
                    return
                }
                if(!detailViewModel.isMember){
                    findNavController().navigate(R.id.action_detailPost_to_profile,
                        arguments.apply {
                            this?.putString("nickname",commentItem.nickname)
                            this?.putString("clubId",commentItem.clubId)
                            this?.putBoolean("isBlock",commentItem.status==1)
                            this?.putString("memberId",commentItem.memberId.toString())
                            this?.putString("userPhoto",commentItem.profileImg)
                        })
                }else{
                    findNavController().navigate(R.id.action_clubPostDetailFragment_to_club_member,
                        arguments.apply {
                            this?.putString("clubId",commentItem.clubId)
                            this?.putString("integUid",detailViewModel.uId)
                            this?.putInt("memberId",commentItem.memberId)
                            this?.putString("myMemberId","0")
                        })
                }
            }

            override fun onTranslateClick(commentItem: ClubReplyData, pos: Int) {
                commentPos = pos
                detailViewModel.clickTransComment(commentItem.content,pos+1,pos+1,commentItem.translateYn?:false)
            }

            override fun onCommentImageClick(imageUrl: String) {
                findNavController().navigate(
                    ClubPostDetailFragmentDirections.actionClubPostDetailFragmentToProfileImageViewerFragment5(
                        imageUrl
                    )
                )
            }
        })

        detailPostAdapter.setOnReplyClickListener(object : PostDetailReplayAdapter.OnReplyClickListener{
            override fun onLikeClick(
                commentItem: CommunityReplyData,
                parentPosition: Int,
                position: Int
            ) {}

            override fun onDisLikeClick(
                commentItem: CommunityReplyData,
                parentPosition: Int,
                position: Int
            ) {}

            override fun onTranslate(
                comment: String,
                parentPosition: Int,
                position: Int,
                translateYn: Boolean
            ) {
                commentPos = parentPosition
                commentReplyPos = position
                detailViewModel.clickTransComment(comment,parentPosition+1,position+1,translateYn)
            }

            override fun onProfileClick(
                nickName: String,
                clubId: String?,
                memberId: String?,
                isBlock: Boolean,
                targetUid: String?,
                userPhoto: String
            ) {
                if(!detailViewModel.getIsLogin()){
                    activity?.showErrorSnackBar(binding.root,ConstVariable.ERROR_PLEASE_LATER_LOGIN)
                    return
                }
                if(!detailViewModel.isMember){
                    findNavController().navigate(R.id.action_detailPost_to_profile,
                        arguments.apply {
                            this?.putString("nickname",nickName)
                            this?.putString("clubId",clubId)
                            this?.putBoolean("isBlock",isBlock)
                            this?.putString("memberId",memberId)
                            this?.putString("userPhoto",userPhoto)
                        })
                }else{
                    findNavController().navigate(R.id.action_clubPostDetailFragment_to_club_member,
                        arguments.apply {
                            this?.putString("clubId",clubId)
                            this?.putString("integUid",detailViewModel.uId)
                            this?.putInt("memberId",memberId?.toInt()?:-1)
                            this?.putString("myMemberId","0")
                        })
                }
            }

            override fun onImageClick(imageUrl: String) {
                findNavController().navigate(
                    ClubPostDetailFragmentDirections.actionClubPostDetailFragmentToProfileImageViewerFragment5(
                        imageUrl
                    )
                )
            }

        })

        //if (clubDetailArg.replyId != -1) moveReply = true
        //comment???????????? ????????? ?????? post???????????? ???????????? ?????? ??????
        val replyId = arguments?.get("replyId")
        if(replyId != null && replyId != -1){
            moveReply = true
            arguments?.remove("replyId")
        }
        getPostInfoData()
    }

    override fun initUiActionEvent() {
        //??????????????? ??????
        binding.scrollUpBtn.setOnClickListener {
            binding.rcPostList.anchorSmoothScrollToPosition(0)
        }

        //?????? ??????
        binding.postDetailToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_post_bookmark -> {
                    if (detailPostAdapter.itemCount > 0) {
                        val postItem = detailPostAdapter.currentList[0]
                        if (postItem is DetailPostItem.ClubPostDetail) {
                            detailViewModel.changeClubPostBookmarkYn(
                                clubDetailArg.clubId,
                                clubDetailArg.categoryCode,
                                clubDetailArg.postId
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
                    if (detailPostAdapter.itemCount > 0) {
                        val postItem = detailPostAdapter.currentList[0]
                        if (postItem is DetailPostItem.ClubPostDetail) {
                            postOptionSheet(postItem)
                        }
                    }
                    true
                }
                else -> {
                    false
                }
            }
        }
        //?????? ??????
        binding.btnCommentRegister.setOnClickListener {
            if (isCommentInputing) {
                activity?.showCustomSnackBar(
                    binding.root,
                    getString(R.string.se_s_sending_comment_to_server)
                )
                return@setOnClickListener
            }
            sendingText(true)
            isCommentInputing = true
            when (clubDetailArg.previewType) {
                ConstVariable.TYPE_CLUB, ConstVariable.TYPE_CLUB_NOTICE -> {
                    detailViewModel.commentText = binding.edtCommentInput.text.toString()
                    if (detailViewModel.getModifyMode() && replyId != null) {
                        if (modifyImageId != null) {
                            detailViewModel.sendPostModifyComment(
                                clubDetailArg.clubId,
                                clubDetailArg.categoryCode,
                                clubDetailArg.postId,
                                replyId!!,
                                modifyImageId
                            )
                        } else if (detailViewModel.getMultiPartBody() != null) {
                            detailViewModel.sendImageToCloudFlare(
                                getString(R.string.cloudFlareKey)
                            )
                        } else {
                            detailViewModel.sendPostModifyComment(
                                clubDetailArg.clubId,
                                clubDetailArg.categoryCode,
                                clubDetailArg.postId,
                                replyId!!,
                                null
                            )
                        }
                    } else {
                        moveCommentBottom = true
                        if (detailViewModel.getMultiPartBody() == null) {
                            detailViewModel.sendPostComment(
                                clubDetailArg.clubId,
                                clubDetailArg.categoryCode,
                                clubDetailArg.postId,
                                null
                            )
                        } else {
                            detailViewModel.sendImageToCloudFlare(
                                getString(R.string.cloudFlareKey)
                            )
                        }
                    }
                    hideSoftInput(binding.root)
                }
            }
        }
        //?????? ?????? ??????
        binding.btnModifyCancel.setOnClickListener {
            settingInputMode()
        }
        //?????? ????????? ????????????
        binding.ivImageSelect.setOnClickListener {
            if(!binding.edtCommentInput.hasFocus()){
                binding.edtCommentInput.requestFocus()
                return@setOnClickListener
            }
            val imageIntent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
            }
            getImageResult.launch(Intent.createChooser(imageIntent, "GET Album"))
        }
        //????????? ?????????
        binding.ivCommentImageBack.setOnClickListener {
            binding.clCommentImageContainer.visibility = View.GONE
            detailViewModel.setInitAttachImaga()
            modifyImageId = null
            val color = requireContext().getColor(R.color.state_enable_gray_400)
            binding.ivImageSelect.imageTintList = ColorStateList.valueOf(color)
        }

        binding.edtCommentInput.setOnFocusChangeListener{ _ ,b->
            if(b && !detailViewModel.getIsLogin()){
                binding.edtCommentInput.clearFocus()
                activity?.showErrorSnackBar(binding.root,ConstVariable.ERROR_PLEASE_LATER_LOGIN)
                return@setOnFocusChangeListener
            }
            commentLayoutFocusChange(b)
        }

        //?????? ??????
        binding.edtCommentInput.doOnTextChanged { text, _, _, _ ->
            detailViewModel.setCommentContent(text.toString())
        }
    }

    private fun settingObserver() {
        detailViewModel.detailPostErrorLiveData.observe(this) {
            if (it == "FE3001") findNavController().popBackStack()
            context?.showErrorSnackBar(binding.root, it)
        }
        //????????? ??? ?????? ?????????
        detailViewModel.postDetailDataLiveData.observe(this) {
            val state = binding.rcPostList.layoutManager?.onSaveInstanceState()
            detailPostAdapter.submitList(it) {
                binding.rcPostList.layoutManager?.onRestoreInstanceState(state)
            }

            if (moveCommentBottom) {
                binding.rcPostList.smoothScrollToPosition(it.size - 1)
            }
            if (moveReply) {
                findNavController().navigate(
                    ClubPostDetailFragmentDirections.actionClubPostDetailFragmentToClubComment(
                        detailViewModel.isMember,
                        clubDetailArg.postId,
                        clubDetailArg.replyId,
                        clubDetailArg.categoryCode,
                        clubDetailArg.clubId
                    )
                )
                moveReply = false
            }
            moveCommentBottom = false
            lastLoading = false
        }
        //?????? ??????
        detailViewModel.postToolbarNameLiveData.observe(this) {
            binding.postDetailToolbar.title = it
        }
        //?????? ????????? ??????
        detailViewModel.commentContent.observe(this) {
            val buttonColor = if (it?.isNotEmpty() == true) {
                binding.btnCommentRegister.isClickable = true
                R.color.state_active_primary_default
            } else {
                binding.btnCommentRegister.isClickable = false
                R.color.state_disabled_gray_200
            }
            changeTextButtonColor(buttonColor)
            binding.btnCommentRegister.isClickable = it?.isNotEmpty() ?: false
        }
        //?????? ??????
        detailViewModel.translatePostData.observe(this) {
            when (val adapterItem = detailPostAdapter.currentList[0]) {
                is DetailPostItem.ClubPostDetail -> {
                    adapterItem.item.translateYn = it.first
                    adapterItem.item.subject = it.second.item.subject
                    adapterItem.item.content = it.second.item.content
                    detailPostAdapter.notifyItemChanged(0, ConstVariable.PAYLOAD_TRANSLATE_CLICK)
                }
                else -> {}
            }
        }
        //?????? ????????? ??????
        detailViewModel.imageUrlLiveData.observe(viewLifecycleOwner) {
            it ?: return@observe
            binding.clCommentImageContainer.visibility = View.VISIBLE
            setImageWithPlaceHolder(
                binding.ivCommentImage,
                it.toString(),
                maskingId = R.drawable.border_radius_8
            )
            val color = requireContext().getColor(R.color.state_active_gray_900)
            binding.ivImageSelect.imageTintList = ColorStateList.valueOf(color)
        }
        //????????? ?????? ??????
        detailViewModel.bookmarkClickResultLiveData.observe(this) {
            settingToolbarBookmark(it)
        }
        //?????? ??????
        detailViewModel.reportCommentResultLiveData.observe(this) {
            context?.let {
                CommonDialog.Builder().apply {
                    title = it.getString(R.string.s_report_complete)
                    message = it.getString(R.string.se_s_report_complete)
                    positiveButtonString = it.getString(R.string.h_confirm)
                }.build().show(parentFragmentManager, "reportComplete")
            }
        }
        //?????? ??????
        detailViewModel.blockCommentResultLiveData.observe(this) {
            detailViewModel.setInitReplyNextId()
            detailViewModel.getClubCommentList(
                clubDetailArg.clubId,
                clubDetailArg.categoryCode,
                clubDetailArg.postId,
                "init"
            )
        }
        //????????? ??????
        detailViewModel.blockPostResultLiveData.observe(this) {
            if (it) {
                activity?.showCustomSnackBar(binding.root, getString(R.string.se_g_post_blocked))
            } else {
                activity?.showCustomSnackBar(binding.root, getString(R.string.se_g_post_unblocked))
            }
            getPostInfoData()
        }
        //????????? ??????
        detailViewModel.deletePostResultLiveData.observe(this) {
            if (it) {
                findNavController().popBackStack()
                activity?.showCustomSnackBar(binding.root, getString(R.string.se_g_post_deleted))
            } else {
                activity?.showCustomSnackBar(binding.root, getString(R.string.error_code_2001))
            }
        }
        //?????? ?????? ??????
        detailViewModel.blockAccountResultLiveData.observe(this) {
            getPostInfoData()
        }
        //?????? ?????? ??????
        detailViewModel.resultCommentInput.observe(this) {
            if (it) {
                detailViewModel.setInitReplyNextId()
                detailViewModel.getClubCommentAllList(
                    clubDetailArg.clubId,
                    clubDetailArg.categoryCode,
                    clubDetailArg.postId,
                    "init"
                )
                moveCommentBottom = true
            } else {
                activity?.showCustomSnackBar(binding.root, "?????? ?????? ??????")
            }
            isCommentInputing = false
            settingInputMode()
        }
        //?????? ??????
        detailViewModel.transCommentReply.observe(this){
            if(detailPostAdapter.currentList[commentPos!!] is DetailPostItem.ClubPostComment){
                val adapterItem = detailPostAdapter.currentList[commentPos!!] as DetailPostItem.ClubPostComment
                if(commentReplyPos!=null){
                    val replyItem =adapterItem.item.childReplyList
                    if(!replyItem.isNullOrEmpty()){
                        val item = replyItem[commentReplyPos!!]
                        item.content = it.second
                        item.translateYn = it.first
                    }
                    detailPostAdapter.replyPosition(commentReplyPos!!)
                    detailPostAdapter.notifyItemChanged(commentPos!!,ConstVariable.PAYLOAD_TRANSLATE_CLICK)
                }else{
                    adapterItem.item.content=it.second
                    adapterItem.item.translateYn = it.first
                    detailPostAdapter.notifyItemChanged(commentPos!!,ConstVariable.PAYLOAD_TRANSLATE_CLICK)
                }
            }
            commentPos=null
            commentReplyPos=null
        }

        //?????? ?????? ??????
        detailViewModel.resultModifySomment.observe(this) {
            detailViewModel.setInitReplyNextId()
            detailViewModel.getClubCommentList(
                clubDetailArg.clubId,
                clubDetailArg.categoryCode,
                clubDetailArg.postId,
                "init"
            )
            settingInputMode()
            detailViewModel.setModifyMode(false)
            isCommentInputing = false
        }
        //?????? ?????? ????????? ??????
        detailViewModel.cloudFlareUploadLiveData.observe(this) {
            if (detailViewModel.getModifyMode()) {
                detailViewModel.sendPostModifyComment(
                    clubDetailArg.clubId,
                    clubDetailArg.categoryCode,
                    clubDetailArg.postId,
                    replyId!!,
                    it
                )
            } else {
                detailViewModel.sendPostComment(
                    clubDetailArg.clubId, clubDetailArg.categoryCode, clubDetailArg.postId, it
                )
            }
        }
        //?????? ????????? bottomSheet
        detailViewModel.reportMessageList.observe(this) {
            reportBottomSheet(it.first, it.second, it.third)
        }
        detailViewModel.bookMarkExists.observe(this) {
            if (!it) {
                binding.postDetailToolbar.menu.removeItem(R.id.menu_post_bookmark)
            }
        }

        detailViewModel.reportClubPostData.observe(this) {
            if (it) {
                activity?.showCustomSnackBar(binding.root, getString(R.string.se_s_report_complete))
            }
        }
    }

    private fun initStatusBar() {
        activity?.let {
            it.setStatusBarBack()
            it.window?.statusBarColor = Color.WHITE
            it.setDarkStatusBarIcon()
        }
    }

    private fun initToolbar() {
        binding.postDetailToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        when (clubDetailArg.previewType) {
            ConstVariable.TYPE_CLUB_CHALLENGE, ConstVariable.TYPE_CLUB_NOTICE -> {
                binding.postDetailToolbar.menu.removeItem(R.id.menu_post_bookmark)
            }
        }
    }

    private fun settingToolbarBookmark(isBookMark: Boolean) {
        context?.let { context ->
            val bookmarkMenu: MenuItem? =
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

            bookmarkMenu?.icon = bookmarkDrawable
        }
    }

    private fun getPostInfoData() {
        detailPostAdapter.setPostType(clubDetailArg.previewType)
        detailViewModel.setInitReplyNextId()
        when (clubDetailArg.previewType) {
            ConstVariable.TYPE_CLUB -> {
                detailViewModel.getClubPostBookMark(
                    clubDetailArg.clubId,
                    clubDetailArg.categoryCode,
                    clubDetailArg.postId
                )
                detailViewModel.getClubPostData(
                    clubDetailArg.clubId,
                    clubDetailArg.categoryCode,
                    clubDetailArg.postId
                )
            }
            ConstVariable.TYPE_CLUB_NOTICE -> {
//                binding.rlCommentContainer.visibility = View.GONE
                detailViewModel.requestClubNoticeDetailItem(
                    clubDetailArg.categoryCode,
                    clubDetailArg.clubId,
                    clubDetailArg.postId
                )
            }
            ConstVariable.TYPE_CLUB_CHALLENGE -> {
                binding.rlCommentContainer.visibility = View.GONE
                detailViewModel.getClubChallengePostDetailData(clubDetailArg.postId)
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun settingInputMode() {
        sendingText(false)
        detailViewModel.setModifyMode(false)
        binding.ivAnonymous.visibility=View.GONE
        binding.btnModifyCancel.visibility = View.GONE
        binding.btnCommentRegister.setImageDrawable(requireContext().getDrawable(R.drawable.icon_outline_send))
        changeTextButtonColor(R.color.state_disabled_gray_200)
        binding.edtCommentInput.setText("")
        binding.clCommentImageContainer.visibility = View.GONE
        detailViewModel.setInitAttachImaga()
        replyId = null
        modifyImageId = null
    }

    private fun settingModifyMode(
        content: String, attachItem: List<ClubPostAttachList>?, modifyReplyId: Int
    ) {
        detailViewModel.setModifyMode(true)
        binding.btnModifyCancel.visibility = View.VISIBLE
        binding.btnCommentRegister.setImageDrawable(requireContext().getDrawable(R.drawable.icon_outline_send))
        changeTextButtonColor(R.color.state_active_primary_default)
        binding.edtCommentInput.setText(content)
        binding.edtCommentInput.requestFocus()
        detailViewModel.setInitAttachImaga()
        replyId = modifyReplyId
        if (!attachItem.isNullOrEmpty()) {
            binding.clCommentImageContainer.visibility = View.VISIBLE
            setImageWithPlaceHolder(
                binding.ivCommentImage,
                getString(R.string.imageUrlBase, attachItem[0].attach),
                maskingId = R.drawable.border_radius_8
            )
            modifyImageId = attachItem[0].attach
        } else {
            binding.clCommentImageContainer.visibility = View.GONE
        }
    }

    private fun commentLayoutFocusChange(isFocus :Boolean){
        if(isFocus){
            (binding.ivImageSelect.layoutParams as ConstraintLayout.LayoutParams).topToBottom=binding.edtCommentInput.id
            (binding.edtCommentInput.layoutParams as ConstraintLayout.LayoutParams).startToEnd=
                ConstraintLayout.LayoutParams.UNSET
            (binding.edtCommentInput.layoutParams as ConstraintLayout.LayoutParams).startToStart=
                ConstraintLayout.LayoutParams.PARENT_ID
            (binding.edtCommentInput.layoutParams as ConstraintLayout.LayoutParams).width=
                ConstraintLayout.LayoutParams.MATCH_PARENT
            (binding.edtCommentInput.layoutParams as ConstraintLayout.LayoutParams).bottomToBottom=
                ConstraintLayout.LayoutParams.UNSET
            binding.ivAnonymous.visibility=View.VISIBLE
            binding.btnCommentRegister.visibility=View.VISIBLE
        }else{
            (binding.ivImageSelect.layoutParams as ConstraintLayout.LayoutParams).topToBottom=
                ConstraintLayout.LayoutParams.UNSET
            (binding.edtCommentInput.layoutParams as ConstraintLayout.LayoutParams).startToStart=
                ConstraintLayout.LayoutParams.UNSET
            (binding.edtCommentInput.layoutParams as ConstraintLayout.LayoutParams).startToEnd=binding.ivImageSelect.id
            (binding.edtCommentInput.layoutParams as ConstraintLayout.LayoutParams).width=0
            (binding.edtCommentInput.layoutParams as ConstraintLayout.LayoutParams).bottomToBottom=binding.ivImageSelect.id
            binding.ivAnonymous.visibility=View.GONE
            binding.btnCommentRegister.visibility=View.GONE
        }
    }

    private fun sendingText(isSending: Boolean) {
        binding.edtCommentInput.isEnabled = !isSending
        binding.ivImageSelect.isEnabled = !isSending
        binding.ivCommentImageBack.isEnabled = !isSending
        binding.btnCommentRegister.isEnabled = !isSending
    }

    private fun changeTextButtonColor(@ColorRes color: Int) {
        var buttonDrawable: Drawable = binding.btnCommentRegister.background
        buttonDrawable = DrawableCompat.wrap(buttonDrawable)
        DrawableCompat.setTint(
            buttonDrawable,
            requireContext().getColor(color)
        )
        binding.btnCommentRegister.background = buttonDrawable
    }

    private fun settingFullScreen(isFull: Boolean, isVideoPort: Boolean) {
        if (isFull) {
            activity?.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        } else {
            activity?.requestedOrientation =
                if (isVideoPort) ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }
    }

    private fun getCommentImageResult() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                data ?: return@registerForActivityResult
                context?.let { context ->
                    data.data!!.asMultipart(
                        "file",
                        "image_${Date().time}",
                        context.contentResolver
                    ).run {
                        if (this == null) {
                            activity?.showErrorSnackBar(
                                binding.root,
                                ConstVariable.ERROR_IMAGE_UPLOAD_FAIL
                            )
                        } else {
                            modifyImageId = null
                            detailViewModel.addOnImageList(data.data, this)
                        }
                    }
                }

            }
        }

    private fun reportBottomSheet(
        sheetTextItem: List<BottomSheetItem>,
        type: String,
        replyId: Int?
    ) {
        activity?.let {
            CustomBottomSheet().apply {
                setBottomItems(sheetTextItem as ArrayList<BottomSheetItem>)
                setOnCliCkListener(object : BottomSheetAdapter.OnItemClickListener {
                    override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                        when (type) {
                            "post" -> {
                                detailViewModel.clickReportClubPost(
                                    pos,
                                    clubDetailArg.clubId,
                                    clubDetailArg.categoryCode,
                                    clubDetailArg.postId
                                )
                            }
                            "comment" -> {
                                detailViewModel.clickReportClubComment(
                                    pos,
                                    clubDetailArg.clubId,
                                    clubDetailArg.categoryCode,
                                    clubDetailArg.postId,
                                    replyId!!
                                )
                            }
                        }
                        dismiss()
                    }
                })
            }.show(childFragmentManager, "reportBottomDialog")
        }
    }

    private fun postOptionSheet(postItem: DetailPostItem.ClubPostDetail) {
        activity?.let {
            val fm = childFragmentManager
            CustomBottomSheet().apply {
                setBottomItems(
                    when (clubDetailArg.previewType) {
                        ConstVariable.TYPE_CLUB_CHALLENGE -> {
                            getChallengePostSheetItem()
                        }
                        ConstVariable.TYPE_CLUB_NOTICE -> {
                            getNoticePostSheetItem(
                                postItem.item.deleteTyep == 2,
                                detailViewModel.isMember,
                                !detailViewModel.getIsLogin(),
                            )
                        }
                        else -> {
                            getPostOptionsText(
                                postItem.item.deleteTyep == 1,
                                postItem.item.deleteTyep == 2,
                                postItem.item.blockType == 1,
                                postItem.item.blockType == 2,
                                detailViewModel.isMember,
                                !detailViewModel.getIsLogin()
                            )
                        }
                    }
                )
                setOnCliCkListener(object : BottomSheetAdapter.OnItemClickListener {
                    override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                        var dialogItems: Triple<String, String, CommonDialog.ClickListener>? = null
                        when (name) {
                            getString(R.string.j_save_cancel), getString(R.string.j_to_save) -> {
                                detailViewModel.changeClubPostBookmarkYn(
                                    postItem.item.clubId,
                                    postItem.item.categoryCode,
                                    postItem.item.postId
                                )
                            }
                            getString(R.string.g_to_share) -> {

                            }
                            getString(R.string.bottom_sheet_edit) -> {

                            }
                            getString(R.string.bottom_sheet_delete) -> {
                                dialogItems = Triple(
                                    getString(R.string.bottom_sheet_delete),
                                    getString(R.string.se_h_want_to_delete_post),
                                    object : CommonDialog.ClickListener {
                                        override fun onClick() {
                                            if (postItem.item.deleteTyep == 2) {
                                                PostDeleteBottomSheet().apply {
                                                    setOnDeleteClickListener(object :
                                                        PostDeleteBottomSheet.DeleteSheetClickListener {
                                                        override fun onDeleteClick(
                                                            deleteReason: String,
                                                            isOpenReason: Boolean
                                                        ) {
                                                            detailViewModel.clickDeleteClubPost(
                                                                postItem.item.clubId,
                                                                postItem.item.categoryCode,
                                                                postItem.item.postId,
                                                                if (isOpenReason) deleteReason
                                                                else null
                                                            )
                                                        }
                                                    })
                                                }.show(fm, "deleteDialog")
                                            } else {
                                                detailViewModel.clickDeleteClubPost(
                                                    postItem.item.clubId,
                                                    postItem.item.categoryCode,
                                                    postItem.item.postId,
                                                    null
                                                )
                                            }
                                        }
                                    })
                            }
                            getString(R.string.s_to_report) -> {
                                detailViewModel.getReportMessageList("post", null)
                            }
                            it.getString(R.string.a_block_this_user), getString(R.string.a_unblock_this_user) -> {
                                dialogItems = Triple(
                                    if (name == it.getString(R.string.a_block_this_user)) it.getString(
                                        R.string.g_block_account
                                    )
                                    else it.getString(R.string.c_release_account_block),
                                    if (name == it.getString(R.string.a_block_this_user)) getString(
                                        R.string.se_s_blocked_select_user
                                    )
                                    else getString(R.string.se_s_block_released_select_user),
                                    object : CommonDialog.ClickListener {
                                        override fun onClick() {
                                            detailViewModel.clickBlockAccountComment(
                                                postItem.item.clubId,
                                                postItem.item.memberId
                                            )
                                        }
                                    })
                            }
                            it.getString(R.string.g_block_post), getString(R.string.g_unblock_post) -> {
                                dialogItems = Triple(
                                    if (name == it.getString(R.string.g_block_post)) getString(R.string.a_block_post)
                                    else getString(R.string.a_see_post),
                                    if (name == it.getString(R.string.g_block_post)) getString(R.string.se_a_want_to_block_post)
                                    else getString(R.string.se_a_want_to_unblock_post),
                                    object : CommonDialog.ClickListener {
                                        override fun onClick() {
                                            detailViewModel.clickBlockClubPost(
                                                postItem.item.clubId,
                                                postItem.item.categoryCode,
                                                postItem.item.postId
                                            )
                                        }
                                    })

                            }
                            getString(R.string.g_to_join) -> {
                                findNavController().navigate(
                                    ClubPostDetailFragmentDirections.actionClubPostDetailFragmentToClubJoinFragment3(
                                        postItem.item.clubId
                                    )
                                )
                            }
                        }
                        if (dialogItems != null) {
                            CommonDialog.Builder().apply {
                                title = dialogItems.first
                                message = dialogItems.second
                                positiveButtonString = getString(R.string.h_confirm)
                                negativeButtonString = getString(R.string.c_cancel)
                                positiveButtonClickListener = dialogItems.third
                            }.build().show(parentFragmentManager, "postOption")
                        }
                        dismiss()
                    }
                })
            }.show(childFragmentManager, "postDetailOPtions")
        }
    }

    fun getPostOptionsText(
        isMy: Boolean,
        isClubMaster: Boolean,
        isUserBlock: Boolean,
        isPostBlock: Boolean,
        isMember: Boolean,
        isGuest: Boolean
    ): ArrayList<BottomSheetItem> {
        return if (isGuest) {
            arrayListOf(
                BottomSheetItem(null, getString(R.string.g_to_share), null, null),
            )
        } else if (isMy) {
            arrayListOf(
                BottomSheetItem(null, getString(R.string.g_to_share), null, null),
                BottomSheetItem(null, getString(R.string.bottom_sheet_edit), null, null),
                BottomSheetItem(null, getString(R.string.bottom_sheet_delete), null, null),
            )
        } else if (isClubMaster) {
            arrayListOf(
                BottomSheetItem(
                    null, if (detailViewModel.bookmarkClickResultLiveData.value == true)
                        getString(R.string.j_save_cancel)
                    else getString(R.string.j_to_save), null, null
                ),
                BottomSheetItem(null, getString(R.string.g_to_share), null, null),
                BottomSheetItem(null, getString(R.string.bottom_sheet_delete), null, null),
            )
        } else if (isMember) {
            arrayListOf(
                BottomSheetItem(
                    null, if (detailViewModel.bookmarkClickResultLiveData.value == true)
                        getString(R.string.j_save_cancel)
                    else getString(R.string.j_to_save), null, null
                ),
                BottomSheetItem(null, getString(R.string.g_to_share), null, null),
                BottomSheetItem(null, getString(R.string.s_to_report), null, null),
                BottomSheetItem(
                    null,
                    if (isUserBlock) getString(R.string.a_unblock_this_user) else getString(R.string.a_block_this_user),
                    null,
                    null
                ),
                BottomSheetItem(
                    null,
                    if (isPostBlock) getString(R.string.g_unblock_post) else getString(R.string.g_block_post),
                    null,
                    null
                )
            )
        } else {
            arrayListOf(
                BottomSheetItem(null, getString(R.string.g_to_join), null, null),
                BottomSheetItem(null, getString(R.string.g_to_share), null, null),
                BottomSheetItem(null, getString(R.string.s_to_report), null, null),
            )
        }

    }

    fun getNoticePostSheetItem(
        isClubMaster: Boolean,
        isMember: Boolean,
        isGuest: Boolean,
    ) =
        if (isGuest) {
            arrayListOf()
        } else if (isClubMaster) {
            arrayListOf(
                BottomSheetItem(null, getString(R.string.g_to_share), null, null),
                BottomSheetItem(null, getString(R.string.bottom_sheet_edit), null, null),
                BottomSheetItem(null, getString(R.string.bottom_sheet_delete), null, null),
            )
        } else if (isMember) {
            arrayListOf(
                BottomSheetItem(null, getString(R.string.g_to_share), null, null),
            )
        } else {
            arrayListOf()
        }


    fun getChallengePostSheetItem() = arrayListOf(
        BottomSheetItem(null, getString(R.string.g_to_share), null, null),
    )

    private fun showCommentBottomSheet(commentItem: ClubReplyData) {
        activity?.let {
            CustomBottomSheet().apply {
                setBottomItems(
                    sheetItem(
                        commentItem.deleteType == 1,
                        commentItem.deleteType == 2,
                        commentItem.blockType == 1,
                        commentItem.blockType == 3,
                        detailViewModel.isMember,
                        detailViewModel.uId == null
                    )
                )
                setOnCliCkListener(object : BottomSheetAdapter.OnItemClickListener {
                    override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                        val commonDialog = CommonDialog.Builder()
                        when (name) {
                            it.getString(R.string.s_report) -> {
                                //??????
                                commonDialog.title =
                                    it.getString(R.string.a_report_comment)
                                commonDialog.message =
                                    it.getString(R.string.se_s_want_selected_comment_report)
                                commonDialog.setPositiveButtonClickListener(object :
                                    CommonDialog.ClickListener {
                                    override fun onClick() {
                                        detailViewModel.getReportMessageList(
                                            "comment",
                                            commentItem.replyId
                                        )
                                    }
                                })
                            }
                            it.getString(R.string.a_block_comment) -> {
                                //??????
                                commonDialog.title =
                                    it.getString(R.string.a_block_comment)
                                commonDialog.message =
                                    it.getString(R.string.se_s_want_selected_comment_block)
                                commonDialog.setPositiveButtonClickListener(object :
                                    CommonDialog.ClickListener {
                                    override fun onClick() {
                                        detailViewModel.clickBlockComment(
                                            commentItem.clubId,
                                            commentItem.categoryCode!!,
                                            commentItem.postId,
                                            commentItem.replyId
                                        )
                                    }
                                })
                            }
                            it.getString(R.string.a_see_comment) -> {
                                commonDialog.title =
                                    it.getString(R.string.a_see_comment)
                                commonDialog.message =
                                    it.getString(R.string.se_a_want_see_comment)
                                commonDialog.setPositiveButtonClickListener(object :
                                    CommonDialog.ClickListener {
                                    override fun onClick() {
                                        detailViewModel.clickBlockComment(
                                            commentItem.clubId,
                                            commentItem.categoryCode!!,
                                            commentItem.postId,
                                            commentItem.replyId
                                        )
                                    }
                                })
                            }
                            it.getString(R.string.g_block_account) -> {
                                //?????? ??????
                                commonDialog.title = it.getString(R.string.g_block_account)
                                commonDialog.message =
                                    it.getString(R.string.se_s_do_you_want_block_select_user)
                                commonDialog.setPositiveButtonClickListener(object :
                                    CommonDialog.ClickListener {
                                    override fun onClick() {
                                        detailViewModel.clickBlockAccountComment(
                                            commentItem.clubId,
                                            commentItem.memberId
                                        )
                                    }
                                })
                            }
                            it.getString(R.string.c_release_account_block) -> {
                                commonDialog.title = it.getString(R.string.c_release_account_block)
                                commonDialog.message =
                                    it.getString(R.string.se_s_do_you_want_release_block_select_user)
                                commonDialog.setPositiveButtonClickListener(object :
                                    CommonDialog.ClickListener {
                                    override fun onClick() {
                                        detailViewModel.clickBlockAccountComment(
                                            commentItem.clubId,
                                            commentItem.memberId
                                        )
                                    }
                                })
                            }
                            getString(R.string.a_modify_comment) -> {
                                //?????? ??????
                                commonDialog.title =
                                    it.getString(R.string.a_modify_comment)
                                commonDialog.message =
                                    it.getString(R.string.se_a_want_modify_comment)
                                commonDialog.setPositiveButtonClickListener(object :
                                    CommonDialog.ClickListener {
                                    override fun onClick() {
                                        settingModifyMode(
                                            commentItem.content,
                                            commentItem.attachList,
                                            commentItem.replyId
                                        )
                                    }
                                })
                            }
                            getString(R.string.a_delete_comment) -> {
                                //?????? ??????
                                commonDialog.title =
                                    it.getString(R.string.a_delete_comment)
                                commonDialog.message =
                                    it.getString(R.string.se_a_want_delete_comment)
                                commonDialog.setPositiveButtonClickListener(object :
                                    CommonDialog.ClickListener {
                                    override fun onClick() {
                                        detailViewModel.clickDeleteComment(
                                            clubDetailArg.clubId,
                                            clubDetailArg.categoryCode,
                                            clubDetailArg.postId,
                                            commentItem.replyId
                                        )
                                    }
                                })
                            }
                        }
                        commonDialog.positiveButtonString = it.getString(R.string.h_confirm)
                        commonDialog.negativeButtonString = it.getString(R.string.c_cancel)
                        commonDialog.build().show(parentFragmentManager, "dialogComment")
                        dismiss()
                    }
                })
            }.show(parentFragmentManager, "comment")
        }
    }

    private fun sheetItem(
        isMy: Boolean,
        isClubMaster: Boolean,
        isUserBlock: Boolean,
        isPostBlock: Boolean,
        isMember: Boolean,
        isGuest: Boolean
    ) =
        if (isGuest) {
            arrayListOf(

            )
        } else if (isMy) {
            arrayListOf(
                BottomSheetItem(
                    R.drawable.icon_outline_post_n,
                    getString(R.string.a_modify_comment),
                    null,
                    null
                ),
                BottomSheetItem(
                    R.drawable.icon_outline_trash,
                    getString(R.string.a_delete_comment),
                    null,
                    null
                ),
            )
        } else if (isClubMaster) {
            arrayListOf(
                BottomSheetItem(
                    R.drawable.icon_outline_trash,
                    getString(R.string.a_delete_comment),
                    null,
                    null
                ),
            )
        } else if (isMember) {
            arrayListOf(
                BottomSheetItem(
                    R.drawable.icon_outline_siren,
                    getString(R.string.s_report),
                    null,
                    null
                ),
                BottomSheetItem(
                    R.drawable.icon_outline_hide,
                    if (isPostBlock) getString(R.string.a_see_comment)
                    else getString(R.string.a_block_comment),
                    null,
                    null
                ),
                BottomSheetItem(
                    R.drawable.icon_outline_blockaccount,
                    if (isUserBlock) getString(R.string.c_release_account_block)
                    else getString(R.string.g_block_account),
                    null,
                    null
                ),
            )
        } else {
            arrayListOf(
                BottomSheetItem(
                    R.drawable.icon_outline_siren,
                    getString(R.string.s_report),
                    null,
                    null
                )
            )
        }


    private fun settingPlayer() = object :
        PostDetailImageAdapter.OnFullScreenClickListener {
        override fun onFullScreen(isFull: Boolean, isPort: Boolean) {
            settingFullScreen(isFull, isPort)
        }
    }

    override fun onResume() {
        super.onResume()
        if (detailPostAdapter.itemCount > 0) {
            when (val adapterVH = binding.rcPostList.findViewHolderForAdapterPosition(0)) {
                is NoticePostVH -> {
                    adapterVH.getImageAdapter()
                        .playerStateChange(adapterVH, "prepare", settingPlayer())
                }
                is CommunityPostDetailVH -> {
                    adapterVH.getImageAdapter()
                        .playerStateChange(adapterVH, "prepare", settingPlayer())
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        detailViewModel.setInitReplyNextId()
        if (detailPostAdapter.itemCount > 0) {
            when (val adapterVH = binding.rcPostList.findViewHolderForAdapterPosition(0)) {
                is NoticePostVH -> {
                    adapterVH.getImageAdapter().playerStateChange(adapterVH, "pause", null)
                }
                is CommunityPostDetailVH -> {
                    adapterVH.getImageAdapter().playerStateChange(adapterVH, "pause", null)
                }
            }
        }
    }

    override fun onDestroy() {
        if (detailPostAdapter.itemCount > 0) {
            when (val adapterVH = binding.rcPostList.findViewHolderForAdapterPosition(0)) {
                is NoticePostVH -> {
                    adapterVH.getImageAdapter().playerStateChange(adapterVH, "stop", null)
                }
                is CommunityPostDetailVH -> {
                    adapterVH.getImageAdapter().playerStateChange(adapterVH, "stop", null)
                }
            }
        }
        super.onDestroy()
    }
}