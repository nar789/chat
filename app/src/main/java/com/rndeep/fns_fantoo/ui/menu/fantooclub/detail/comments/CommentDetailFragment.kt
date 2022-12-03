package com.rndeep.fns_fantoo.ui.menu.fantooclub.detail.comments

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.ComposeComment
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.ComposeCommentReply
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.FantooClubComment
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.FantooClubPostAttach
import com.rndeep.fns_fantoo.databinding.FragmentCommentDetailBinding
import com.rndeep.fns_fantoo.ui.login.LoginMainActivity
import com.rndeep.fns_fantoo.ui.menu.*
import com.rndeep.fns_fantoo.ui.menu.fantooclub.contents.LikeType
import com.rndeep.fns_fantoo.ui.menu.fantooclub.detail.ComposeCommentMode
import com.rndeep.fns_fantoo.ui.menu.fantooclub.detail.ContentsDetailFragment
import com.rndeep.fns_fantoo.ui.menu.fantooclub.more.MoreBottomSheet
import com.rndeep.fns_fantoo.ui.menu.fantooclub.more.report.ReportBottomSheet
import com.rndeep.fns_fantoo.ui.menu.fantooclub.more.report.ReportMessageItem
import com.rndeep.fns_fantoo.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

/**
 * Comment Detail UI
 */
@AndroidEntryPoint
class CommentDetailFragment : Fragment() {

    private lateinit var binding: FragmentCommentDetailBinding
    private lateinit var commentDetailAdapter: CommentDetailAdapter
    private lateinit var comment: FantooClubComment
    private lateinit var moreMenu: MoreBottomSheet
    private lateinit var reportMenu: ReportBottomSheet
    private lateinit var loginDialog: MenuDialogFragment
    private lateinit var msgDialog: MenuDialogFragment
    private var commentImage: Uri? = null

    private val viewModel: CommentDetailViewModel by viewModels()
    private val args: CommentDetailFragmentArgs by navArgs()

    private val startForResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.apply {
                    Timber.d("image : ${result.data}")
                    setAttachImage(this.data.toString())
                    commentImage = this.data
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentDetailBinding.inflate(inflater, container, false).apply {

            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            commentDetailAdapter = CommentDetailAdapter { comments, clickType ->
                if (!viewModel.getIsLogin()) {
                    return@CommentDetailAdapter
                }
                when (clickType) {
                    CommentsClickType.MORE -> {
                        showMoreMenu(comments)
                    }
                    CommentsClickType.ITEM -> {
                        Timber.d("click comment item")
                    }
                    CommentsClickType.LIKE -> {
                        Timber.d("click comment like")
                        setLikeAndDislike(comments, LikeType.LIKE)
                    }
                    CommentsClickType.DISLIKE -> {
                        Timber.d("click comment dislike")
                        setLikeAndDislike(comments, LikeType.DISLIKE)
                    }
                    CommentsClickType.HONOR -> {
                        Timber.d("click comment honor")
                    }
                    CommentsClickType.TRANSLATE -> {
                        Timber.d("click comment translate")
                    }
                }
            }
            commentsList.run {
                adapter = commentDetailAdapter
//                setHasFixedSize(true)
                val divider =
                    DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                ContextCompat.getDrawable(requireContext(), R.drawable.divider_line)
                    ?.let { divider.setDrawable(it) }
                addItemDecoration(divider)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        comment = args.comment
        Timber.d("args comment: $comment")

        binding.picture.setOnClickListener {
            showPhotoPicker()
        }

        binding.cancel.setOnClickListener {
            binding.attachBg.visibility = View.GONE
            val color = requireContext().getColor(R.color.state_enable_gray_400)
            binding.picture.imageTintList = ColorStateList.valueOf(color)
            commentImage = null
        }

        binding.input.doOnTextChanged { text, _, _, _ ->
            viewModel.setReplyContent(text.toString())
        }

        binding.fab.setOnClickListener {
            binding.scroll.smoothScrollTo(0, 0)
        }

        viewModel.sendBtnEnabled.observe(viewLifecycleOwner) { enabled ->
            val color = when (enabled) {
                true -> requireContext().getColor(R.color.state_active_primary_default)
                else -> requireContext().getColor(R.color.state_disabled_gray_200)
            }
            binding.save.isEnabled = enabled
            binding.save.setBackgroundColor(color)
        }

        binding.save.setOnClickListener {
            if (commentImage == null) {
                if (viewModel.getFantooClubComposeComment().fantooClubComment?.attaches.isNullOrEmpty()) {
                    composeCommentReply(null)
                } else {
                    composeCommentReply(viewModel.getFantooClubComposeComment().fantooClubComment?.attaches)
                }
            } else {
                commentImage!!.asMultipart(
                    "file",
                    "image_${Date().time}",
                    requireContext().contentResolver
                )
                    ?.let { it1 -> viewModel.uploadImage(it1) }
            }
        }

        binding.modifyCancel.setOnClickListener {
            initComposeCommentReply()
        }

        binding.scroll.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val scrolled = scrollY > SCROLL_HEIGHT
            viewModel.setIsScroll(scrolled)
        }

        binding.inputHint.setOnClickListener {
            binding.input.visibility = View.VISIBLE
            binding.save.visibility = View.VISIBLE
            binding.inputHint.visibility = View.GONE
            showKeyboard(binding.input)
            binding.input.requestFocus()
        }

        viewModel.isScroll.observe(viewLifecycleOwner) { scroll ->
            val visibility = if (scroll) View.VISIBLE else View.GONE
            binding.fab.visibility = visibility
        }

        viewModel.comments.observe(viewLifecycleOwner) { list ->
            Timber.d("observe comments : $list")
            list?.let {
                commentDetailAdapter.submitList(it)
            }
        }

        viewModel.composeSuccess.observe(viewLifecycleOwner) {
            Timber.d("composeSuccess : $it")
            if (it) {
                getCommentReplies(comment)
            }
        }

        viewModel.uploadImageSuccess.observe(viewLifecycleOwner) { uploadImage ->
            Timber.d("uploadImageSuccess : $uploadImage")
            uploadImage?.let { attaches ->
                Timber.d("uploadImageSuccess , attaches: $attaches")
                composeCommentReply(attaches)
            }
        }

        viewModel.commentLike.observe(viewLifecycleOwner) { like ->
            Timber.d("commentLike : $like")
            like?.let {
                getCommentReplies(comment)
            }
        }

        viewModel.errorMsg.observe(viewLifecycleOwner) { msg ->
            Timber.d("errorMsg : $msg")
            msg?.let {
                if ("FE1013" == it) {
                    showLoginMessage()
                } else {
                    showErrorMessage(msg)
                }
            }
        }

        viewModel.fetchFantooClubFollow(comment.clubId)
        getCommentReplies(comment)
    }

    private fun getCommentReplies(comment: FantooClubComment) {
        viewModel.fetchFantooClubCommentReplies(
            comment.clubId,
            comment.categoryCode,
            comment.postId.toString(),
            comment.parentReplyId.toString()
        )
    }

    private fun initComposeCommentReply() {
        binding.input.setText("")
        binding.save.isEnabled = false
        binding.modifyCancel.visibility = View.GONE
        binding.attachBg.visibility = View.GONE
        binding.input.visibility = View.GONE
        binding.inputHint.visibility = View.VISIBLE
        val color = requireContext().getColor(R.color.state_enable_gray_400)
        binding.picture.imageTintList = ColorStateList.valueOf(color)
        binding.save.setBackgroundColor(color)
        binding.save.visibility = View.GONE
        commentImage = null
        viewModel.setFantooClubComposeComment(null, ComposeCommentMode.NEW)
        dismissKeyboard(binding.input)
    }

    private fun composeCommentReply(attaches: List<FantooClubPostAttach>?) {
        if (!viewModel.getIsLogin()) {
            showLoginMessage()
            initComposeCommentReply()
            return
        }
        val composeCommentReply =
            ComposeCommentReply(
                attaches,
                comment.postId,
                comment.replyId,
                binding.input.text.toString(),
                viewModel.getUid(),
                comment.langCode,
                comment.parentReplyId
            )
        Timber.d("save click body: $composeCommentReply ")
        when (viewModel.getFantooClubComposeComment().commentMode) {
            ComposeCommentMode.NEW -> {
                viewModel.composeFantooClubCommentReply(
                    comment.clubId,
                    comment.categoryCode,
                    comment.postId.toString(),
                    comment.parentReplyId.toString(),
                    composeCommentReply
                )
            }
            ComposeCommentMode.EDIT -> {
                val replyId = viewModel.getFantooClubComposeComment().fantooClubComment?.replyId
                if (comment.parentReplyId == replyId) {
                    val composeComment =
                        ComposeComment(
                            attaches,
                            binding.input.text.toString(),
                            viewModel.getUid(),
                            comment.langCode
                        )
                    viewModel.modifyFantooClubComment(
                        comment.clubId,
                        comment.categoryCode,
                        comment.postId.toString(),
                        comment.parentReplyId.toString(),
                        composeComment
                    )
                } else {
                    viewModel.modifyFantooClubCommentReply(
                        comment.clubId,
                        comment.categoryCode,
                        comment.postId.toString(),
                        comment.parentReplyId.toString(),
                        replyId.toString(),
                        composeCommentReply
                    )
                }
            }
        }
        initComposeCommentReply()
    }

    private fun deleteComment(comment: FantooClubComment, commentsDetailType: CommentsDetailType) {
        Timber.d("delete item: $comment, type: $commentsDetailType")
        when (commentsDetailType) {
            CommentsDetailType.COMMENT -> {
                viewModel.deleteFantooClubComment(
                    comment.clubId,
                    comment.categoryCode,
                    comment.postId.toString(),
                    comment.replyId.toString()
                )
            }
            CommentsDetailType.REPLY -> {
                viewModel.deleteFantooClubCommentReply(
                    comment.clubId,
                    comment.categoryCode,
                    comment.postId.toString(),
                    comment.parentReplyId.toString(),
                    comment.replyId.toString()
                )
            }
        }
    }

    private fun reportComment(
        comment: FantooClubComment,
        reportMessageId: Int,
        commentsDetailType: CommentsDetailType
    ) {
        Timber.d("report item: $comment, type: $commentsDetailType")
        when (commentsDetailType) {
            CommentsDetailType.COMMENT -> {
                viewModel.reportFantooClubComment(
                    comment.clubId,
                    comment.categoryCode,
                    comment.postId.toString(),
                    comment.replyId.toString(),
                    reportMessageId
                )
            }
            CommentsDetailType.REPLY -> {
                viewModel.reportFantooClubCommentReply(
                    comment.clubId,
                    comment.categoryCode,
                    comment.postId.toString(),
                    comment.parentReplyId.toString(),
                    comment.replyId.toString(),
                    reportMessageId
                )
            }
        }
    }

    private fun blockComment(comment: FantooClubComment, commentsDetailType: CommentsDetailType) {
        Timber.d("block item: $comment, type: $commentsDetailType")
        when (commentsDetailType) {
            CommentsDetailType.COMMENT -> {
                viewModel.blockFantooClubComment(
                    comment.clubId,
                    comment.categoryCode,
                    comment.postId.toString(),
                    comment.replyId.toString()
                )
            }
            CommentsDetailType.REPLY -> {
                viewModel.blockFantooClubCommentReply(
                    comment.clubId,
                    comment.categoryCode,
                    comment.postId.toString(),
                    comment.parentReplyId.toString(),
                    comment.replyId.toString()
                )
            }
        }
    }

    private fun setLikeAndDislike(comment: FantooClubComment, likeType: LikeType) {
        Timber.d("setLikeAndDislike item: $comment, type: $likeType")
        if (comment.parentReplyId == comment.replyId) {
            viewModel.setFantooClubCommentLikeAndDislike(
                comment.clubId,
                likeType.type,
                comment.categoryCode,
                comment.postId.toString(),
                comment.replyId.toString()
            )
        } else {
            viewModel.setFantooClubCommentReplyLikeAndDislike(
                comment.clubId,
                likeType.type,
                comment.categoryCode,
                comment.postId.toString(),
                comment.parentReplyId.toString(),
                comment.replyId.toString()
            )
        }
    }

    private fun showPhotoPicker() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_PICK
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
        }
        startForResult.launch(Intent.createChooser(intent, "Get Album"))
    }

    private fun setAttachImage(url: String) {
        val option = MultiTransformation(CenterCrop(), RoundedCorners(24))
        Glide.with(requireContext())
            .load(url)
            .centerCrop()
            .apply(RequestOptions.bitmapTransform(option))
            .into(binding.attachImg)
        binding.attachBg.visibility = View.VISIBLE
        val color = requireContext().getColor(R.color.state_active_gray_900)
        binding.picture.imageTintList = ColorStateList.valueOf(color)
    }

    private fun showMoreMenu(comment: FantooClubComment) {
        Timber.d("deleteType: ${comment.deleteType}, status: ${comment.status}, isFollow: ${viewModel.getIsFollow()}")
        val moreMenuItem =
            when {
                !viewModel.getIsFollow() -> {
                    viewModel.reportCommentMenuItems
                }
                comment.status == ContentsDetailFragment.TYPE_BLOCKED_COMMENT -> {
                    viewModel.blockedCommentMenuItems
                }
                comment.deleteType == ContentsDetailFragment.TYPE_DISABLE_DELETE_COMMENT -> {
                    viewModel.commentMenuItems
                }
                else -> {
                    viewModel.myCommentMenuItems
                }
            }
        val commentsDetailType =
            if (comment.replyId == comment.parentReplyId) CommentsDetailType.COMMENT else CommentsDetailType.REPLY
        moreMenu = MoreBottomSheet(moreMenuItem) {
            Timber.d("item : $it")
            showMoreMessage(it.icon, comment, commentsDetailType)
            moreMenu.dismiss()
        }
        moreMenu.show(requireActivity().supportFragmentManager, MoreBottomSheet.TAG)
    }

    private fun showMoreMessage(icon: Int, comment: FantooClubComment, commentsDetailType: CommentsDetailType) {
        var title = ""
        var msg = ""
        when (icon) {
            R.drawable.icon_outline_edit -> {
                title = requireContext().getString(R.string.a_modify_comment)
                msg = requireContext().getString(R.string.se_a_want_modify_comment)
            }
            R.drawable.icon_outline_trash -> {
                title = requireContext().getString(R.string.a_delete_comment)
                msg = requireContext().getString(R.string.se_a_want_delete_comment)
            }
            R.drawable.icon_outline_siren -> {
                title = requireContext().getString(R.string.a_report_comment)
                msg = requireContext().getString(R.string.se_s_want_selected_comment_report)
            }
            R.drawable.icon_outline_hide -> {
                if (comment.status == ContentsDetailFragment.TYPE_BLOCKED_COMMENT) {
                    title = requireContext().getString(R.string.a_see_comment)
                    msg = requireContext().getString(R.string.se_a_want_see_comment)
                } else {
                    title = requireContext().getString(R.string.a_block_comment)
                    msg = requireContext().getString(R.string.se_s_want_selected_comment_block)
                }
            }
        }

        val message = DialogMessage(
            DialogTitle(
                title,
                msg,
                null
            ),
            DialogButton(getString(R.string.h_confirm), getString(R.string.c_cancel), true),
            isCompleted = false
        )
        msgDialog = MenuDialogFragment(message) { clickType ->
            when (clickType) {
                DialogClickType.OK -> {
                    Timber.d("ok")
                    when (icon) {
                        R.drawable.icon_outline_edit -> {
                            setModifyCommentReply(comment)
                        }
                        R.drawable.icon_outline_trash -> {
                            deleteComment(comment, commentsDetailType)
                        }
                        R.drawable.icon_outline_siren -> {
                            showReportMessages(comment, commentsDetailType)
                        }
                        R.drawable.icon_outline_hide -> {
                            blockComment(comment, commentsDetailType)
                        }
                    }
                }
                DialogClickType.CANCEL -> {
                    Timber.d("cancel")
                }
            }
            msgDialog.dismiss()
        }
        msgDialog.show(requireActivity().supportFragmentManager, MenuDialogFragment.DIALOG_MENU)
    }

    private fun showReportMessages(
        comment: FantooClubComment,
        commentsDetailType: CommentsDetailType
    ) {
        Timber.d("showReportMessages")
        val reportMenuItem = mutableListOf<ReportMessageItem>()
        if (viewModel.getReportMessages().isNullOrEmpty()) {
            viewModel.fetchReportMessages()
            return
        } else {
            viewModel.getReportMessages()?.let { items ->
                items.forEach { item ->
                    reportMenuItem.add(ReportMessageItem(item.reportMessageId, item.message, false))
                }
            }
        }
        reportMenu = ReportBottomSheet(reportMenuItem) { reportMessage ->
            Timber.d("item : $reportMessage")
            reportComment(comment, reportMessage.id, commentsDetailType)
            reportMenu.dismiss()
        }
        reportMenu.show(requireActivity().supportFragmentManager, ReportBottomSheet.TAG)
    }

    private fun setModifyCommentReply(comment: FantooClubComment) {
        Timber.d("setModifyCommentReply, comment: $comment")
        binding.modifyCancel.visibility = View.VISIBLE
        binding.save.visibility = View.VISIBLE
        binding.input.visibility = View.VISIBLE
        binding.inputHint.visibility = View.GONE
        binding.input.setText(comment.content)

        if (comment.attaches.isNotEmpty()) {
            getImageUrlFromCDN(comment.attaches[0].attach)?.let { setAttachImage(it) }
        }

        viewModel.setFantooClubComposeComment(comment, ComposeCommentMode.EDIT)
    }

    private fun showLoginMessage() {
        val message = DialogMessage(
            DialogTitle(
                getString(R.string.r_need_login),
                getString(R.string.se_r_need_login),
                null
            ),
            DialogButton(getString(R.string.h_confirm), getString(R.string.c_cancel), true),
            isCompleted = false
        )
        loginDialog = MenuDialogFragment(message) { clickType ->
            when (clickType) {
                DialogClickType.OK -> {
                    Timber.d("ok")
                    goToLoginPage()
                }
                DialogClickType.CANCEL -> {
                    Timber.d("cancel")
                }
            }
            loginDialog.dismiss()
        }
        openLoginDialog()
    }

    private fun showErrorMessage(msg: String) {
        val message = DialogMessage(
            DialogTitle(
                msg,
                null,
                null
            ),
            DialogButton(getString(R.string.h_confirm), null, false),
            isCompleted = false
        )
        msgDialog = MenuDialogFragment(message) { clickType ->
            when (clickType) {
                DialogClickType.OK -> {
                    Timber.d("ok")
                    msgDialog.dismiss()
                }
                else -> {}
            }
        }
        msgDialog.show(requireActivity().supportFragmentManager, MenuDialogFragment.DIALOG_MENU)
    }

    private fun openLoginDialog() {
        Timber.d("openDialog")
        loginDialog.show(
            requireActivity().supportFragmentManager,
            MenuDialogFragment.DIALOG_MENU
        )
    }

    private fun goToLoginPage() {
        val intent = Intent(requireContext(), LoginMainActivity::class.java)
        intent.putExtra(ConstVariable.INTENT.EXTRA_NAV_START_DESTINATION_ID, R.id.loginFragment)
        startActivity(intent)
        requireActivity().finish()
    }

    companion object {
        const val SCROLL_HEIGHT = 750
    }
}