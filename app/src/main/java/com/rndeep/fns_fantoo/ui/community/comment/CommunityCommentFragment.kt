package com.rndeep.fns_fantoo.ui.community.comment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.CommunityReplyData
import com.rndeep.fns_fantoo.data.remote.model.community.DetailAttachList
import com.rndeep.fns_fantoo.databinding.FragmentCommentBinding
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetAdapter
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetItem
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.CustomBottomSheet
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.utils.*
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class CommunityCommentFragment :
    BaseFragment<FragmentCommentBinding>(FragmentCommentBinding::inflate) {

    private val getArgs: CommunityCommentFragmentArgs by navArgs()

    private val commentViewModel: CommunityCommentViewModel by viewModels()

    private lateinit var getImageResult: ActivityResultLauncher<Intent>

    //댓글 어댑터
    private val postDetailCommunityCommentAdapter = CommunityCommentAdapter()

    //Multipart
    private var cloudFlarePart: MultipartBody.Part? = null

    //댓글 클릭 위치
    private var commentClickPos: Int? = null

    private lateinit var dividerDeco :CustomDividerDecoration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getImageResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data = it.data
                    data ?: return@registerForActivityResult
                    context?.let { context ->
                        cloudFlarePart = data.data!!.asMultipart(
                            "file",
                            "image_${Date().time}",
                            context.contentResolver
                        )
                    }
                    commentViewModel.addOnImageList(data.data)
                }
            }
        dividerDeco=CustomDividerDecoration(
            0.5f,
            0f,
            requireContext().getColor(R.color.gray_400_opacity12),
            false
        )
    }

    override fun initUi() {
        settingInputComment()
        binding.rcCommentList.layoutManager = LinearLayoutManager(requireContext())
        binding.rcCommentList.addSingleItemDecoRation(dividerDeco)
        postDetailCommunityCommentAdapter.setIsLogin(commentViewModel.isLoginUser())
        binding.rcCommentList.adapter = postDetailCommunityCommentAdapter

        //댓글 이미지 가져오기
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


        binding.ivCommentImage.clipToOutline = true
        settingObserve()

        commentViewModel.getDetailComment(getArgs.postId, getArgs.replyId)
    }

    override fun initUiActionEvent() {
        binding.commentToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.ivCommentImageBack.setOnClickListener {
            commentViewModel.setAttachItem(null)
            binding.clCommentImageContainer.visibility = View.GONE
        }

        binding.edtCommentInput.doOnTextChanged { text, _, _, _ ->
            commentViewModel.setCommentText(text.toString())
        }

        //익명 여부
        binding.ivAnonymous.setOnClickListener {
            commentViewModel.setAnonymYN(!(commentViewModel.anonymYN.value?:false))
        }

        //댓글 기능 클릭
        postDetailCommunityCommentAdapter.setCommunityCommentReplyClickListener(object :
            CommunityCommentAdapter.OnCommunityCommentReplyClickListener {
            override fun onReplyOptionClick(replyData: CommunityReplyData, pos: Int) {
                activity?.let {
                    CustomBottomSheet().apply {
                        setBottomItems(
                            sheetItem(
                                replyData.pieceBlockYn,
                                replyData.userBlockYn,
                                replyData.integUid
                            )
                        )
                        setOnCliCkListener(object : BottomSheetAdapter.OnItemClickListener {
                            override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                                var dialogItems : Triple<String,String,CommonDialog.ClickListener>?= null
                                when (name) {
                                    it.getString(R.string.s_report) -> {
                                        //신고
                                        commentViewModel.getReportMessageList(replyData.replyId,replyData.integUid)
                                    }
                                    it.getString(R.string.a_block_comment) -> {
                                        dialogItems= Triple(it.getString(R.string.a_block_comment),it.getString(R.string.se_s_want_selected_comment_block),object :
                                            CommonDialog.ClickListener {
                                            override fun onClick() {
                                                commentViewModel.clickBlockComment(
                                                    replyData.replyId.toString(),
                                                    replyData.integUid,
                                                    replyData.pieceBlockYn == true
                                                )
                                            }
                                        })
                                    }
                                    it.getString(R.string.a_see_comment) -> {
                                        dialogItems= Triple(it.getString(R.string.a_see_comment),it.getString(R.string.se_a_want_see_comment),object :
                                            CommonDialog.ClickListener {
                                            override fun onClick() {
                                                commentViewModel.clickBlockComment(
                                                    replyData.replyId.toString(),
                                                    replyData.integUid,
                                                    replyData.pieceBlockYn == true
                                                )
                                            }
                                        })
                                    }
                                    it.getString(R.string.g_block_account) -> {
                                        dialogItems= Triple(it.getString(R.string.g_block_account),it.getString(R.string.se_s_do_you_want_block_select_user),object :
                                            CommonDialog.ClickListener {
                                            override fun onClick() {
                                                commentViewModel.clickBlockAccount(
                                                    replyData.integUid,
                                                    replyData.userBlockYn == true
                                                )
                                            }
                                        })
                                    }
                                    it.getString(R.string.c_release_account_block) -> {
                                        dialogItems= Triple(it.getString(R.string.c_release_account_block), it.getString(R.string.se_s_do_you_want_release_block_select_user),object :
                                            CommonDialog.ClickListener {
                                            override fun onClick() {
                                                commentViewModel.clickBlockAccount(
                                                    replyData.integUid,
                                                    replyData.userBlockYn == true
                                                )
                                            }
                                        })
                                    }
                                    getString(R.string.a_modify_comment) -> {
                                        dialogItems= Triple(it.getString(R.string.a_modify_comment), it.getString(R.string.se_a_want_modify_comment),object :
                                            CommonDialog.ClickListener {
                                            override fun onClick() {
                                                settingModifyComment(
                                                    replyData.content,
                                                    replyData.attachList,
                                                    replyData.anonymYn
                                                )
                                                commentViewModel.modifyReplyId=replyData.replyId
                                            }
                                        })
                                    }
                                    getString(R.string.a_delete_comment) -> {
                                        dialogItems= Triple(it.getString(R.string.a_delete_comment),it.getString(R.string.se_a_want_delete_comment),object :
                                            CommonDialog.ClickListener {
                                            override fun onClick() {
                                                commentViewModel.clickCommentDelete(
                                                    getArgs.postId.toString(),
                                                    replyData.replyId.toString()
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

            override fun onTranslateClick(replyData: CommunityReplyData, pos: Int) {
                commentClickPos=pos
                commentViewModel.clickTranslateComment(replyData.content,replyData.translateYn?:false,pos)
            }

            override fun onReplyLikeClick(replyItem: CommunityReplyData, pos: Int) {
                if(!commentViewModel.isLoginUser()){
                    activity?.showErrorSnackBar(binding.root,ConstVariable.ERROR_PLEASE_LATER_LOGIN)
                    return
                }
                commentClickPos = pos
                commentViewModel.clickLikeDisLikeComment(
                    replyItem.myLikeYn == true,
                    ConstVariable.LIKE_CLICK,
                    replyItem.replyId.toString()
                )
            }

            override fun onReplyDisListClick(replyItem: CommunityReplyData, pos: Int) {
                if(!commentViewModel.isLoginUser()){
                    activity?.showErrorSnackBar(binding.root,ConstVariable.ERROR_PLEASE_LATER_LOGIN)
                    return
                }
                commentClickPos = pos
                commentViewModel.clickLikeDisLikeComment(
                    replyItem.myDisLikeYn == true,
                    ConstVariable.DISLIKE_CLICK,
                    replyItem.replyId.toString()
                )
            }

            override fun onImageClick(imageUrl: String) {
                findNavController().navigate(
                    CommunityCommentFragmentDirections.actionCommentFragmentToProfileImageViewerFragment3(
                        imageUrl
                    )
                )
            }

        })

        //댓글 입력
        binding.btnCommentRegister.setOnClickListener {
            if (commentViewModel.commentInputing.value == true) {
                activity?.showCustomSnackBar(binding.root, getString(R.string.se_s_sending_comment_to_server))
                return@setOnClickListener
            }
            if(commentViewModel.commentText.value.isNullOrBlank()){
                activity?.showErrorSnackBar(binding.root,ConstVariable.ERROR_FE3045)
                return@setOnClickListener
            }
            commentViewModel.setCommentItem()
            commentViewModel.settingCommentInputingState(true)
            if (commentViewModel.isModifyMode.value == true &&
                commentViewModel.modifyReplyId != null
            ) {
                modifyCommunitySendComment()
            } else {
                defaultCommunitySendComment()
            }
        }

        binding.edtCommentInput.setOnFocusChangeListener { view, b ->
            if(b && !commentViewModel.isLoginUser()){
                binding.edtCommentInput.clearFocus()
                activity?.showErrorSnackBar(binding.root,ConstVariable.ERROR_PLEASE_LATER_LOGIN)
                return@setOnFocusChangeListener
            }else if(b && commentViewModel.commentState!=1){
                binding.edtCommentInput.clearFocus()
                activity?.showErrorSnackBar(binding.root,ConstVariable.ERROR_FE2008)
                return@setOnFocusChangeListener
            }
            commentLayoutFocusChange(b)
        }

        //수정 취소
        binding.btnModifyCancel.setOnClickListener {
            settingInputComment()
        }

    }

    private fun settingObserve() {
        //댓글 데이터
        commentViewModel.communityReplyLiveData.observe(this) {
            if(it.isNotEmpty()){
                if(it[0].activeStatus==2 && it.size==1){
                    findNavController().popBackStack()
                    return@observe
                }
            }
            postDetailCommunityCommentAdapter.submitList(it)
        }

        //댓글 텍스트
        commentViewModel.commentText.observe(this) {
            var buttonDrawable: Drawable = binding.btnCommentRegister.background
            buttonDrawable = DrawableCompat.wrap(buttonDrawable)
            if (it?.isNotEmpty() == true) {
                DrawableCompat.setTint(
                    buttonDrawable,
                    requireContext().getColor(R.color.state_active_primary_default)
                )
                binding.btnCommentRegister.background = buttonDrawable
                binding.btnCommentRegister.isClickable = true
            } else {
                DrawableCompat.setTint(
                    buttonDrawable,
                    requireContext().getColor(R.color.state_disabled_gray_200)
                )
                binding.btnCommentRegister.background = buttonDrawable
                binding.btnCommentRegister.isClickable = false
            }
        }

        //댓글 이미지 추가
        commentViewModel.imageUrlLiveData.observe(viewLifecycleOwner) {
            it ?: return@observe
            binding.clCommentImageContainer.visibility = View.VISIBLE
            setImageWithPlaceHolder(binding.ivCommentImage,it.toString())
        }

        //클라우드 플레어 키값
        commentViewModel.cloudFlareKeyLiveData.observe(this) {
            if(it == ConstVariable.ERROR_COMMENT_IMAGE_UPLOAD_FAIL){
                activity?.showCustomSnackBar(binding.root,"업로드가 실패했습니다. 네트워크 상태를 확인해 주세요.")
                commentViewModel.setAttachItem(null)
                binding.clCommentImageContainer.visibility = View.GONE
            }else{
                commentViewModel.setImageDataInCommentItem(it,"image")
                if (commentViewModel.isModifyMode.value == true &&
                    commentViewModel.modifyReplyId != null
                ) {
                    commentViewModel.clickCommentModify(
                        getArgs.postId.toString(),
                        commentViewModel.modifyReplyId.toString(),
                    )
                } else {
                    commentViewModel.inputCommentReply(
                        getArgs.postId.toString(),
                        getArgs.replyId.toString(),
                    )
                }
            }
        }

        //댓글 입력 결과
        commentViewModel.inputCommentReplyLiveData.observe(this) {
            hideSoftInput(binding.root)
            binding.edtCommentInput.clearFocus()
            commentViewModel.settingCommentInputingState(false)
            settingInputComment()
            commentViewModel.initNextId()
            commentViewModel.getDetailComment(getArgs.postId, getArgs.replyId)
        }

        //댓글 익명 여부 상태
        commentViewModel.anonymYN.observe(this) {
            if (it) {
                binding.ivAnonymous.setColorFilter(requireContext().getColor(R.color.state_active_primary_default))
            } else {
                binding.ivAnonymous.setColorFilter(requireContext().getColor(R.color.state_active_gray_700))
            }
        }

        //신고
        commentViewModel.reportMessageList.observe(viewLifecycleOwner){
            reportBottomSheet(it.first,it.third,it.second)
        }

        commentViewModel.reportCommentResultLiveData.observe(viewLifecycleOwner){
            context?.let {
                CommonDialog.Builder().apply {
                    title = it.getString(R.string.s_report_complete)
                    message = it.getString(R.string.se_s_report_complete)
                    positiveButtonString = it.getString(R.string.h_confirm)
                }.build().show(parentFragmentManager, "reportComplete")
            }
        }

        //erro 코드
        commentViewModel.errorCodeLiveData.observe(this) {
            activity?.showErrorSnackBar(binding.root, it.toString())
        }

        //댓글 삭제 경과
        commentViewModel.deleteResultLiveData.observe(this) {
            commentViewModel.initNextId()
            commentViewModel.getDetailComment(getArgs.postId, getArgs.replyId)
        }

        //계정 차단 결과
        commentViewModel.blockAccountResultLiveData.observe(this) {
            commentViewModel.initNextId()
            commentViewModel.getDetailComment(getArgs.postId, getArgs.replyId)
        }

        //댓글 차단 결과
        commentViewModel.blockCommentResultLiveData.observe(this) {
            commentViewModel.initNextId()
            commentViewModel.getDetailComment(getArgs.postId, getArgs.replyId)
        }

        //좋아요 싫어요 결과 처리
        commentViewModel.likeDislikeResultLiveData.observe(this) {
            commentClickPos ?: return@observe
            val item = postDetailCommunityCommentAdapter.currentList[commentClickPos!!]
            if(item is CommunityReplyData){
                when (it.first) {
                    ConstVariable.LIKE_CLICK -> {
                        if (item.myLikeYn == null) {
                            item.myLikeYn = true
                        } else {
                            item.myLikeYn = !item.myLikeYn!!
                        }
                        item.myDisLikeYn = false
                    }
                    ConstVariable.DISLIKE_CLICK -> {
                        if (item.myDisLikeYn == null) {
                            item.myDisLikeYn = true
                        } else {
                            item.myDisLikeYn = !item.myDisLikeYn!!
                        }
                        item.myLikeYn = false
                    }
                }
                item.likeCnt = it.second
                item.dislikeCnt = it.third
                postDetailCommunityCommentAdapter.notifyItemChanged(
                    commentClickPos!!,
                    ConstVariable.PAYLOAD_LIKE_CLICK
                )
            }
            commentClickPos = null
        }

        commentViewModel.translateComment.observe(this){
            commentClickPos ?: return@observe
            val item =postDetailCommunityCommentAdapter.currentList[commentClickPos!!]
            if(item is CommunityReplyData){
                item.content= it.second
                item.translateYn=it.first
            }
            postDetailCommunityCommentAdapter.notifyItemChanged(commentClickPos!!,ConstVariable.PAYLOAD_TRANSLATE_CLICK)
            commentClickPos=null
        }

        //댓글 입력 상태
        commentViewModel.commentInputing.observe(this){
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
        commentViewModel.setInitAttachImage()
        cloudFlarePart = null
        val color = requireContext().getColor(R.color.state_enable_gray_400)
        binding.ivImageSelect.imageTintList = ColorStateList.valueOf(color)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun settingModifyComment(commentText: String, imageId: List<DetailAttachList>?,isAnonmyYn :Boolean) {
        commentViewModel.setModifyMode(true)
        binding.btnModifyCancel.visibility = View.VISIBLE
        binding.btnCommentRegister.setImageDrawable(requireContext().getDrawable(R.drawable.icon_fill_check))
        var buttonDrawable: Drawable = binding.btnCommentRegister.background
        buttonDrawable = DrawableCompat.wrap(buttonDrawable)
        DrawableCompat.setTint(
            buttonDrawable,
            requireContext().getColor(R.color.state_active_primary_default)
        )
        if(isAnonmyYn){
            commentViewModel.setAnonymYN(isAnonmyYn)
            binding.ivAnonymous.visibility=View.VISIBLE
            binding.ivAnonymous.isClickable=false
        }else{
            binding.ivAnonymous.visibility=View.GONE
        }
        binding.btnCommentRegister.background = buttonDrawable
        binding.edtCommentInput.setText(commentText)
        binding.edtCommentInput.requestFocus()
        cloudFlarePart = null
        imageId?.let {
            commentViewModel.setAttachItem(it)
            if (it.isNotEmpty()) {
                binding.clCommentImageContainer.visibility = View.VISIBLE
                commentViewModel.addOnImageList(
                    Uri.parse(
                        requireContext().getString(
                            R.string.imageUrlBase,
                            it[0].id
                        )
                    )
                )
            } else {
                binding.clCommentImageContainer.visibility = View.GONE
                commentViewModel.addOnImageList(null)
            }
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun settingInputComment() {
        commentViewModel.setModifyMode(false)
        commentViewModel.setAnonymYN(false)
        binding.ivAnonymous.isClickable=true
        binding.btnModifyCancel.visibility = View.GONE
        binding.btnCommentRegister.setImageDrawable(requireContext().getDrawable(R.drawable.icon_outline_send))
        var buttonDrawable: Drawable = binding.btnCommentRegister.background
        buttonDrawable = DrawableCompat.wrap(buttonDrawable)
        DrawableCompat.setTint(
            buttonDrawable,
            requireContext().getColor(R.color.state_disabled_gray_200)
        )
        binding.btnCommentRegister.background = buttonDrawable
        binding.edtCommentInput.setText("")
        binding.clCommentImageContainer.visibility = View.GONE
        commentViewModel.addOnImageList(null)
        commentViewModel.setAttachItem(null)
        cloudFlarePart = null
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

    private fun showCheckDialog(title:String,message:String,clickListener :CommonDialog.ClickListener){
        val commonDialog = CommonDialog.Builder()
        commonDialog.title =title
        commonDialog.message =message
        commonDialog.setPositiveButtonClickListener(clickListener)
        commonDialog.positiveButtonString = getString(R.string.h_confirm)
        commonDialog.negativeButtonString = getString(R.string.c_cancel)
        commonDialog.build().show(parentFragmentManager, "dialogComment")
    }

    private fun defaultCommunitySendComment(){
        if (cloudFlarePart == null) {
            commentViewModel.inputCommentReply(
                getArgs.postId.toString(),
                getArgs.replyId.toString(),
            )
        } else {
            commentViewModel.sendImageToCloudFlare(
                getString(R.string.cloudFlareKey),
                cloudFlarePart!!
            )
        }
    }

    private fun modifyCommunitySendComment(){
        if (cloudFlarePart == null) {
            commentViewModel.clickCommentModify(
                getArgs.postId.toString(),
                commentViewModel.modifyReplyId.toString(),
            )
        } else {
            commentViewModel.sendImageToCloudFlare(
                getString(R.string.cloudFlareKey),
                cloudFlarePart!!
            )
        }
    }

    private fun sheetItem(isPieceBlock: Boolean?, isUserBlock: Boolean?, commentUid: String) =
        if (commentUid == commentViewModel.uId) {
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
        } else {
            arrayListOf(
                BottomSheetItem(
                    R.drawable.icon_outline_siren,
                    getString(R.string.s_report),
                    null,
                    null
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
        targetId: Int,
        targetUId: String
    ) {
        activity?.let {
            CustomBottomSheet().apply {
                setBottomItems(sheetTextItem as ArrayList<BottomSheetItem>)
                setOnCliCkListener(object : BottomSheetAdapter.OnItemClickListener {
                    override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                        showCheckDialog(it.getString(R.string.a_report_comment),it.getString(R.string.se_s_want_selected_comment_report),object :
                            CommonDialog.ClickListener {
                            override fun onClick() {
                                commentViewModel.callReportPostComment(
                                    targetId,targetUId,pos
                                )
                            }
                        })
                        dismiss()
                    }
                })
            }.show(childFragmentManager, "reportBottomDialog")
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.addOnGlobalLayoutListener(globalLayoutListener)
    }

    override fun onPause() {
        super.onPause()
        commentViewModel.initNextId()
        activity?.removeOnGlobalLayoutListener(globalLayoutListener)
    }

    private val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        activity?.let{
            val visible = it.isKeyboardShown()
            if(!visible && commentViewModel.commentText.value.isNullOrBlank()){
                binding.edtCommentInput.clearFocus()
            }
        }
    }
}