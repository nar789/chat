package com.rndeep.fns_fantoo.ui.club.comment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowInsetsAnimation
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.ClubReplyData
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostAttachList
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
class ClubCommentFragment:
    BaseFragment<FragmentCommentBinding>(FragmentCommentBinding::inflate) {

    private val getArgs : ClubCommentFragmentArgs by navArgs()

    private val commentViewModel: ClubCommentViewModel by viewModels()

    private lateinit var getImageResult: ActivityResultLauncher<Intent>
    //댓글 어댑터
    private val postDetailCommentAdapter = ClubCommentAdapter()
    //Multipart
    private var cloudFlarePart: MultipartBody.Part? = null

    private lateinit var dividerDeco :CustomDividerDecoration

    private var changeReplyPos : Int? =null

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
        postDetailCommentAdapter.isLoginUser(commentViewModel.isUser)
        binding.rcCommentList.adapter = postDetailCommentAdapter
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

        commentViewModel.getClubDetailComment(getArgs.clubId,getArgs.categoryCode?:"",getArgs.postId,getArgs.replyId)
    }

    override fun initUiActionEvent() {
        binding.commentToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.ivCommentImageBack.setOnClickListener {
            binding.clCommentImageContainer.visibility = View.GONE
            commentViewModel.setAttachItem(null)
            commentViewModel.addOnImageList(null)
        }

        binding.edtCommentInput.doOnTextChanged { text, _, _, _ ->
            commentViewModel.setCommentText(text.toString())
        }

        binding.edtCommentInput.setOnFocusChangeListener { view, b ->
            if(b && !commentViewModel.isUser){
                binding.edtCommentInput.clearFocus()
                activity?.showErrorSnackBar(binding.root,ConstVariable.ERROR_PLEASE_LATER_LOGIN)
                return@setOnFocusChangeListener
            }
            if(b && commentViewModel.commentState!=0){
                binding.edtCommentInput.clearFocus()
                activity?.showErrorSnackBar(binding.root,ConstVariable.ERROR_FE2008)
                return@setOnFocusChangeListener
            }
            commentLayoutFocusChange(b)
        }

        //댓글 기능 클릭
        postDetailCommentAdapter.setClubCommentReplyClickListener(object :
            ClubCommentAdapter.ClubCommentReplyClickListener {
            override fun onReplyOptionClick(replyData: ClubReplyData, pos: Int) {
                activity?.let {
                    CustomBottomSheet().apply {
                        setBottomItems(
                            sheetItem(
                                replyData.deleteType==1,
                                replyData.deleteType==2,
                                replyData.blockType==1,
                                replyData.blockType==3,
                                getArgs.isMember,
                                !commentViewModel.isUser
                            )
                        )
                        setOnCliCkListener(object : BottomSheetAdapter.OnItemClickListener {
                            override fun onItemClick(name: String, _pos: Int, _oldPos: Int?) {
                                val commonDialog = CommonDialog.Builder()
                                when (name) {
                                    it.getString(R.string.s_report) -> {
                                        //신고
                                        commonDialog.title =
                                            it.getString(R.string.a_report_comment)
                                        commonDialog.title =
                                            it.getString(R.string.se_s_want_selected_comment_report)
                                        commonDialog.setPositiveButtonClickListener(object :
                                            CommonDialog.ClickListener {
                                            override fun onClick() {
                                                commentViewModel.getReportMessage(replyData.parentReplyId==replyData.replyId,replyData.replyId)
                                            }
                                        })
                                    }
                                    it.getString(R.string.a_block_comment) -> {
                                        //게시글 차단
                                        commonDialog.title =
                                            it.getString(R.string.a_block_comment)
                                        commonDialog.title =
                                            it.getString(R.string.se_s_want_selected_comment_block)
                                        commonDialog.setPositiveButtonClickListener(object :
                                            CommonDialog.ClickListener {
                                            override fun onClick() {
                                                commentViewModel.clickBlockComment(
                                                    replyData.clubId,
                                                    replyData.categoryCode!!,
                                                    replyData.postId,
                                                    replyData.parentReplyId,
                                                    replyData.replyId
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
                                                commentViewModel.clickBlockComment(
                                                    replyData.clubId,
                                                    replyData.categoryCode!!,
                                                    replyData.postId,
                                                    replyData.parentReplyId,
                                                    replyData.replyId
                                                )
                                            }
                                        })
                                    }
                                    it.getString(R.string.g_block_account) -> {
                                        //계정 차단
                                        commonDialog.title = it.getString(R.string.g_block_account)
                                        commonDialog.message =
                                            it.getString(R.string.se_s_do_you_want_block_select_user)
                                        commonDialog.setPositiveButtonClickListener(object :
                                            CommonDialog.ClickListener {
                                            override fun onClick() {
                                                commentViewModel.clickBlockAccount(
                                                    replyData.clubId,
                                                    replyData.memberId
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
                                                commentViewModel.clickBlockAccount(
                                                    replyData.clubId,
                                                    replyData.memberId
                                                )
                                            }
                                        })
                                    }
                                    getString(R.string.a_modify_comment) -> {
                                        //댓글 수정
                                        commonDialog.title =
                                            it.getString(R.string.a_modify_comment)
                                        commonDialog.message =
                                            it.getString(R.string.se_a_want_modify_comment)
                                        commonDialog.setPositiveButtonClickListener(object :
                                            CommonDialog.ClickListener {
                                            override fun onClick() {
                                                settingModifyComment(
                                                    replyData.content,
                                                    replyData.attachList
                                                )
                                                commentViewModel.modifyReplyId=replyData.replyId
                                            }
                                        })
                                    }
                                    getString(R.string.a_delete_comment) -> {
                                        //댓글 삭제
                                        commonDialog.title =
                                            it.getString(R.string.a_delete_comment)
                                        commonDialog.message =
                                            it.getString(R.string.se_a_want_delete_comment)
                                        commonDialog.setPositiveButtonClickListener(object :
                                            CommonDialog.ClickListener {
                                            override fun onClick() {
                                                commentViewModel.clickCommentDelete(
                                                    getArgs.clubId,
                                                    getArgs.categoryCode?:"",
                                                    getArgs.postId,
                                                    replyData.replyId,
                                                    replyData.parentReplyId
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

            override fun onTranslateClick(replyData: ClubReplyData, pos: Int) {
                changeReplyPos = pos
                commentViewModel.clickTranslateReply(replyData.content,pos,replyData.translateYn?:false)
            }

            override fun onCommentImageClick(imageUrl: String) {
                findNavController().navigate(
                    ClubCommentFragmentDirections.actionClubCommentFragment2ToProfileImageViewerFragment4(
                        imageUrl
                    )
                )
            }
        })

        //댓글 입력
        binding.btnCommentRegister.setOnClickListener {
            if (commentViewModel.commentInputing.value == true) {
                activity?.showCustomSnackBar(binding.root,getString(R.string.se_s_sending_comment_to_server))
                return@setOnClickListener
            }
            if (commentViewModel.commentText.value.isNullOrBlank()) {
                activity?.showErrorSnackBar(binding.root, ConstVariable.ERROR_FE3045)
                return@setOnClickListener
            }
            commentViewModel.settingCommentInputingState(true)
            commentViewModel.setCommentItem()
            if (commentViewModel.isModifyMode.value == true &&
                commentViewModel.modifyReplyId != null
            ) {
                modifyClubSendComment()
            } else {
                defaultClubSendComment()
            }
        }

        //수정 취소
        binding.btnModifyCancel.setOnClickListener {
            settingInputComment()
        }

    }

    private fun settingObserve() {
        //댓글 데이터
        commentViewModel.clubReplyLiveData.observe(this){
            if(it.isNotEmpty()){
                if((it[0].status==2 || it[0].status==3) && it.size==1){
                    findNavController().popBackStack()
                    return@observe
                }
            }
            postDetailCommentAdapter.submitList(it)
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
            binding.ivCommentImage.setImageURI(it)
        }

        //클라우드 플레어 키값
        commentViewModel.cloudFlareKeyLiveData.observe(this) {
            if(it == ConstVariable.ERROR_COMMENT_IMAGE_UPLOAD_FAIL){
                activity?.showCustomSnackBar(binding.root,"업로드가 실패했습니다. 네트워크 상태를 확인해 주세요.")
                binding.clCommentImageContainer.visibility = View.GONE
                commentViewModel.setAttachItem(null)
                commentViewModel.addOnImageList(null)
            }else{
                commentViewModel.setImageDataInCommentItem(it,"image")
                if (commentViewModel.isModifyMode.value == true &&
                    commentViewModel.modifyReplyId != null
                ) {
                    commentViewModel.modifyClubCommentReply(
                        getArgs.clubId,
                        getArgs.categoryCode?:"",
                        getArgs.postId,
                        getArgs.replyId,
                        commentViewModel.modifyReplyId!!,
                    )
                } else {
                    commentViewModel.inputClubCommentReply(
                        getArgs.clubId,
                        getArgs.categoryCode.toString(),
                        getArgs.postId,
                        getArgs.replyId,
                    )
                }
            }
        }

        //댓글 입력 결과
        commentViewModel.resultInputComment.observe(this){
            settingInputComment()
            commentViewModel.initNextId()
            commentViewModel.getClubDetailComment(getArgs.clubId,getArgs.categoryCode?:"",getArgs.postId, getArgs.replyId)
            commentViewModel.setModifyMode(false)
            commentViewModel.settingCommentInputingState(false)
        }

        //erro 코드
        commentViewModel.errorCodeLiveData.observe(this) {
            activity?.showErrorSnackBar(binding.root, it)
        }

        //댓글 삭제 경과
        commentViewModel.deleteResultLiveData.observe(this) {
            commentViewModel.initNextId()
            commentViewModel.getClubDetailComment(getArgs.clubId,getArgs.categoryCode?:"",getArgs.postId,getArgs.replyId)
        }

        //계정 차단 결과
        commentViewModel.blockAccountResultLiveData.observe(this) {
            commentViewModel.initNextId()
            commentViewModel.getClubDetailComment(getArgs.clubId,getArgs.categoryCode?:"",getArgs.postId,getArgs.replyId)
        }

        //댓글 차단 결과
        commentViewModel.blockCommentResultLiveData.observe(this) {
            if(!it){
                return@observe
            }
            activity?.showCustomSnackBar(binding.root,getString(R.string.se_s_selected_comment_blocked))
            commentViewModel.initNextId()
            commentViewModel.getClubDetailComment(getArgs.clubId,getArgs.categoryCode?:"",getArgs.postId,getArgs.replyId)
        }

        //댓글 번역
        commentViewModel.translateReply.observe(this){
            if(changeReplyPos !=null){
                val adapterItem = postDetailCommentAdapter.currentList[changeReplyPos!!]
                if(adapterItem is ClubReplyData){
                    adapterItem.content=it.second
                    adapterItem.translateYn=it.first
                }
                postDetailCommentAdapter.notifyItemChanged(changeReplyPos!!,ConstVariable.PAYLOAD_TRANSLATE_CLICK)
            }
            changeReplyPos=null
        }

        //신고 bottomSheet
        commentViewModel.reportMessageItems.observe(this){
            reportBottomSheet(it.first,it.second,it.third)
        }
        commentViewModel.reportResultLiveData.observe(this){
            commentViewModel.initNextId()
            commentViewModel.getClubDetailComment(getArgs.clubId,getArgs.categoryCode?:"",getArgs.postId,getArgs.replyId)
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
        commentViewModel.setAttachItem(null)
        commentViewModel.addOnImageList(null)
        val color = requireContext().getColor(R.color.state_enable_gray_400)
        binding.ivImageSelect.imageTintList = ColorStateList.valueOf(color)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun settingModifyComment(commentText: String, imageId: List<ClubPostAttachList>?) {
        commentViewModel.setModifyMode(true)
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
        cloudFlarePart = null
        imageId?.let {
            commentViewModel.setAttachItem(it)
            if (it.isNotEmpty()) {
                binding.clCommentImageContainer.visibility = View.VISIBLE
                setImageWithPlaceHolder(binding.ivCommentImage,requireContext().getString(R.string.imageUrlBase,it[0].attach))
            } else {
                binding.clCommentImageContainer.visibility = View.GONE
                commentViewModel.addOnImageList(null)
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun settingInputComment() {
        hideSoftInput(binding.root)
        binding.ivAnonymous.visibility=View.GONE
        commentViewModel.setModifyMode(false)
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
        binding.edtCommentInput.clearFocus()
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

    private fun defaultClubSendComment(){
        if (cloudFlarePart == null) {
            commentViewModel.inputClubCommentReply(
                getArgs.clubId,
                getArgs.categoryCode.toString(),
                getArgs.postId,
                getArgs.replyId,
            )
        } else {
            commentViewModel.sendImageToCloudFlare(
                getString(R.string.cloudFlareKey),
                cloudFlarePart!!
            )
        }
    }

    private fun modifyClubSendComment() {
        if (cloudFlarePart == null) {
            commentViewModel.modifyClubCommentReply(
                getArgs.clubId,
                getArgs.categoryCode?:"",
                getArgs.postId,
                getArgs.replyId,
                commentViewModel.modifyReplyId!!,
            )
        } else {
            commentViewModel.sendImageToCloudFlare(
                getString(R.string.cloudFlareKey),
                cloudFlarePart!!
            )
        }
    }

    private fun reportBottomSheet(sheetTextItem :List<BottomSheetItem>, isParentComment:Boolean, childReplyId:Int){
        activity?.let {
            CustomBottomSheet().apply {
                setBottomItems(sheetTextItem as ArrayList<BottomSheetItem>)
                setOnCliCkListener(object : BottomSheetAdapter.OnItemClickListener {
                    override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                            if(isParentComment){
                                commentViewModel.clickReportComment(
                                    pos,
                                    getArgs.clubId,
                                    getArgs.categoryCode!!,
                                    getArgs.postId,
                                    getArgs.replyId
                                )
                            }else{
                                commentViewModel.clickReportCommentReply(
                                    pos,
                                    getArgs.clubId,
                                    getArgs.categoryCode!!,
                                    getArgs.postId,
                                    childReplyId,
                                    getArgs.replyId,
                                )
                            }
                        dismiss()
                    }
                })
            }.show(childFragmentManager, "reportBottomDialog")
        }
    }

    private fun sheetItem(
        isMy: Boolean,
        isClubMaster: Boolean,
        isUserBlock: Boolean,
        isCommentBlock: Boolean,
        isMember: Boolean,
        isGuest: Boolean
    ) =
        if(isGuest){
            arrayListOf(

            )
        }else if (isMy) {
            arrayListOf(
                BottomSheetItem(
                    R.drawable.icon_outline_post_n,
                    getString(R.string.a_modify_comment),
                    null,
                    null
                ),
                BottomSheetItem(
                    R.drawable.icon_outline_delate,
                    getString(R.string.a_delete_comment),
                    null,
                    null
                ),
            )
        }else if(isClubMaster){
            arrayListOf(
                BottomSheetItem(
                    R.drawable.icon_outline_delate,
                    getString(R.string.a_delete_comment),
                    null,
                    null
                ),
            )
        }else if(isMember){
            arrayListOf(
                BottomSheetItem(
                    R.drawable.icon_outline_siren,
                    getString(R.string.s_report),
                    null,
                    null
                ),
                BottomSheetItem(
                    R.drawable.icon_outline_hide,
                    if (isCommentBlock) getString(R.string.a_see_comment)
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
        }else {
            arrayListOf(
                BottomSheetItem(
                    R.drawable.icon_outline_siren,
                    getString(R.string.s_report),
                    null,
                    null
                )
            )
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