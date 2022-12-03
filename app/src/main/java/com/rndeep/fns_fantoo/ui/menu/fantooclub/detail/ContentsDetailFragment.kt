package com.rndeep.fns_fantoo.ui.menu.fantooclub.detail

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
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.util.Util
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.ComposeComment
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.FantooClubComment
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.FantooClubPostAttach
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.FantooClubPost
import com.rndeep.fns_fantoo.databinding.FragmentContentsDetailBinding
import com.rndeep.fns_fantoo.ui.editor.HashtagAdapter
import com.rndeep.fns_fantoo.ui.login.LoginMainActivity
import com.rndeep.fns_fantoo.ui.menu.*
import com.rndeep.fns_fantoo.ui.menu.fantooclub.contents.LikeType
import com.rndeep.fns_fantoo.ui.menu.fantooclub.detail.comments.CommentsAdapter
import com.rndeep.fns_fantoo.ui.menu.fantooclub.detail.comments.CommentsClickType
import com.rndeep.fns_fantoo.ui.menu.fantooclub.more.MoreBottomSheet
import com.rndeep.fns_fantoo.ui.menu.fantooclub.more.MoreMenuItem
import com.rndeep.fns_fantoo.ui.menu.fantooclub.more.report.ReportBottomSheet
import com.rndeep.fns_fantoo.ui.menu.fantooclub.more.report.ReportMessageItem
import com.rndeep.fns_fantoo.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

/**
 * FantooTv Detail UI
 */
@AndroidEntryPoint
class ContentsDetailFragment : Fragment() {

    private lateinit var binding: FragmentContentsDetailBinding
    private lateinit var hashtagAdapter: HashtagAdapter
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var contents: FantooClubPost
    private lateinit var moreMenu: MoreBottomSheet
    private lateinit var reportMenu: ReportBottomSheet
    private lateinit var loginDialog: MenuDialogFragment
    private lateinit var msgDialog: MenuDialogFragment
    private var commentImage: Uri? = null

    private val viewModel: ContentsDetailViewModel by viewModels()
    private val videoInfoViewModel: VideoInfoViewModel by activityViewModels()
    private val args: ContentsDetailFragmentArgs by navArgs()

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

        contents = args.contents
        Timber.d("onCreateView, args.contents: $contents")

        binding = FragmentContentsDetailBinding.inflate(inflater, container, false).apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            hashtagAdapter = HashtagAdapter { hashtag ->
            }
            hashtagList.run {
                adapter = hashtagAdapter
                setHasFixedSize(true)
                val space = resources.getDimensionPixelSize(R.dimen.spacing_eight)
                addItemDecoration(SpaceDecoration(end = space))
            }
            contents.hashtagList?.let { list ->
                viewModel.setHashtags(list)
                hashtagList.visibility = View.VISIBLE
            }

            commentsAdapter = CommentsAdapter { comment, clickType ->
                if (!viewModel.getIsLogin()) {
                    showLoginMessage()
                    return@CommentsAdapter
                }
                when (clickType) {
                    CommentsClickType.MORE -> {
                        showMoreMenu(comment)
                    }
                    CommentsClickType.ITEM -> {
                        Timber.d("click comment item")
                        navigateToCommentDetail(comment)
                    }
                    CommentsClickType.LIKE -> {
                        Timber.d("click comment like")
                        viewModel.setFantooClubCommentLikeAndDislike(
                            comment.clubId,
                            LikeType.LIKE.type,
                            comment.categoryCode,
                            comment.postId.toString(),
                            comment.replyId.toString()
                        )
                    }
                    CommentsClickType.DISLIKE -> {
                        Timber.d("click comment dislike")
                        viewModel.setFantooClubCommentLikeAndDislike(
                            comment.clubId,
                            LikeType.DISLIKE.type,
                            comment.categoryCode,
                            comment.postId.toString(),
                            comment.replyId.toString()
                        )
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
                adapter = commentsAdapter
                setHasFixedSize(true)
                val divider =
                    DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                ContextCompat.getDrawable(requireContext(), R.drawable.divider_line)
                    ?.let { divider.setDrawable(it) }
                addItemDecoration(divider)
            }

        }

        initView()
        contents.attachList?.let { attaches ->
            if (attaches.isNotEmpty()) {
                getVideoUrlFromCDN(attaches[0].attach)?.let { videoInfoViewModel.setLoadUri(it) }
                Timber.d("video url: ${videoInfoViewModel.getLoadUri()}")
                videoInfoViewModel.initializePlayer(requireContext(), binding.playerView)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBar(R.color.gray_25, true)

        binding.share.setOnClickListener {
            contents.subject?.let { title ->
                contents.link?.let { link -> startShare(title, link) }
            }
        }

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
                composeComment(null)
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
            initComposeComment()
        }

        binding.scroll.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val scrolled = scrollY > binding.playerView.height
            viewModel.setIsScroll(scrolled)
        }

        binding.translate.setOnClickListener {
            val commentsId = 0L
            viewModel.startTranslate(commentsId)
        }

        binding.like.setOnClickListener {
            Timber.d("click detail like")
            setLikeAndDislike(LikeType.LIKE)
        }

        binding.dislike.setOnClickListener {
            Timber.d("click detail dislike")
            setLikeAndDislike(LikeType.DISLIKE)
        }

        binding.playerView.setFullscreenButtonClickListener {
            navigateToFullScreen()
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

        viewModel.hashtags.observe(viewLifecycleOwner) { list ->
            Timber.d("observe hashtags : $list")
            hashtagAdapter.submitList(list.toList())
        }

        viewModel.comments.observe(viewLifecycleOwner) { list ->
            Timber.d("observe comments : $list")
            list?.let {
                commentsAdapter.submitList(it)

                if (it.isNotEmpty()) {
                    binding.divider.visibility = View.VISIBLE
                    binding.commentsList.visibility = View.VISIBLE
                }
            }
        }

        viewModel.detailLike.observe(viewLifecycleOwner) { detailLike ->
            Timber.d("detailLike : $detailLike")
            detailLike?.let { like ->
                like.likeYn?.let {
                    if (it) {
                        binding.like.setColorFilter(requireContext().getColor(R.color.state_active_gray_700))
                        binding.dislike.setColorFilter(requireContext().getColor(R.color.state_disabled_gray_200))
                    } else {
                        binding.like.setColorFilter(requireContext().getColor(R.color.state_disabled_gray_200))
                        binding.dislike.setColorFilter(requireContext().getColor(R.color.state_active_gray_700))
                    }
                } ?: kotlin.run {
                    binding.like.setColorFilter(requireContext().getColor(R.color.state_disabled_gray_200))
                    binding.dislike.setColorFilter(requireContext().getColor(R.color.state_disabled_gray_200))
                }
                val count = like.likeCount - like.dislikeCount
                binding.likeCount.text = count.toString()
            }
        }

        viewModel.commentLike.observe(viewLifecycleOwner) { like ->
            Timber.d("commentLike : $like")
            like?.let {
                getComments()
            }
        }

        viewModel.composeSuccess.observe(viewLifecycleOwner) {
            Timber.d("composeSuccess : $it")
            if (it) {
                getComments()
            }
        }

        viewModel.uploadImageSuccess.observe(viewLifecycleOwner) { uploadImage ->
            Timber.d("uploadImageSuccess : $uploadImage")
            uploadImage?.let { attaches ->
                Timber.d("uploadImageSuccess , attaches: $attaches")
                composeComment(attaches)
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

        viewModel.fetchFantooClubFollow(contents.clubId)
        getComments()
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || videoInfoViewModel.getExoPlayer() == null) {
            videoInfoViewModel.initializePlayer(requireContext(), binding.playerView)
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT > 23) {
            videoInfoViewModel.releasePlayer()
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (Util.SDK_INT > 23) {
            videoInfoViewModel.releasePlayer()
        }
        videoInfoViewModel.initVideoInfo()
    }

    private fun initView() {
        binding.title.text = contents.subject
        binding.nickname.text = getString(R.string.p_fantoo_tv)

        binding.body.text = contents.content
        binding.body.visibility = View.VISIBLE

        contents.hashtagList?.let { hashtag ->

        }
        contents.categoryName2.let { name ->
            binding.category.text = name
        }
        binding.createdAt.text = TimeUtils.diffTimeWithCurrentTime(contents.createDate)

        contents.like?.let { like ->
            like.likeYn?.let {
                if (it) {
                    binding.like.setColorFilter(requireContext().getColor(R.color.state_active_gray_700))
                    binding.dislike.setColorFilter(requireContext().getColor(R.color.state_disabled_gray_200))
                } else {
                    binding.like.setColorFilter(requireContext().getColor(R.color.state_disabled_gray_200))
                    binding.dislike.setColorFilter(requireContext().getColor(R.color.state_active_gray_700))
                }
            } ?: kotlin.run {
                binding.like.setColorFilter(requireContext().getColor(R.color.state_disabled_gray_200))
                binding.dislike.setColorFilter(requireContext().getColor(R.color.state_disabled_gray_200))
            }
            val count = like.likeCount - like.dislikeCount
            binding.likeCount.text = count.toString()
        }

        binding.honorCount.visibility = View.GONE
        binding.honor.visibility = View.GONE

        binding.commentCount.text = contents.replyCount.toString()
        binding.commentCount.visibility = View.VISIBLE
        binding.comment.visibility = View.VISIBLE
    }

    private fun initComposeComment() {
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

    private fun setModifyReply(comment: FantooClubComment) {
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

    private fun navigateToFullScreen() {
        val directions =
            ContentsDetailFragmentDirections.actionContentsDetailFragmentToFullScreenViewerFragment()
        findNavController().navigate(directions)
    }

    private fun navigateToCommentDetail(comment: FantooClubComment) {
        Timber.d("navigateToCommentDetail, comment: $comment")
        val directions =
            ContentsDetailFragmentDirections.actionContentsDetailFragmentToCommentDetailFragment(
                comment
            )
        findNavController().navigate(directions)
    }

    private fun setSavedIcon(saved: Boolean) {
        var icon =
            AppCompatResources.getDrawable(requireContext(), R.drawable.icon_outline_favorite)
        var color = requireContext().getColor(R.color.state_enable_gray_900)
        if (saved) {
            icon = AppCompatResources.getDrawable(requireContext(), R.drawable.icon_fill_bookmark_1)
            color = requireContext().getColor(R.color.state_active_primary_default)
        }
        binding.toolbar.menu.getItem(0).icon = icon
        DrawableCompat.setTint(binding.toolbar.menu.getItem(0).icon, color)
    }

    private fun showMoreMenu(comment: FantooClubComment) {
        Timber.d("deleteType: ${comment.deleteType}, status: ${comment.status}, isFollow: ${viewModel.getIsFollow()}")
        val moreMenuItem =
            when {
                !viewModel.getIsFollow() -> {
                    viewModel.reportCommentMenuItems
                }
                comment.status == TYPE_BLOCKED_COMMENT -> {
                    viewModel.blockedCommentMenuItems
                }
                comment.deleteType == TYPE_DISABLE_DELETE_COMMENT -> {
                    viewModel.commentMenuItems
                }
                else -> {
                    viewModel.myCommentMenuItems
                }
            }
        moreMenu = MoreBottomSheet(moreMenuItem) {
            Timber.d("item : $it")
            showMoreMessage(it.icon, comment)
            moreMenu.dismiss()
        }
        moreMenu.show(requireActivity().supportFragmentManager, MoreBottomSheet.TAG)
    }

    private fun showReportMessages(comment: FantooClubComment) {
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
            reportComment(comment, reportMessage.id)
            reportMenu.dismiss()
        }
        reportMenu.show(requireActivity().supportFragmentManager, ReportBottomSheet.TAG)
    }

    private fun showMoreMessage(icon: Int, comment: FantooClubComment) {
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
                if (comment.status == TYPE_BLOCKED_COMMENT) {
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
                            setModifyReply(comment)
                        }
                        R.drawable.icon_outline_trash -> {
                            deleteComment(comment)
                        }
                        R.drawable.icon_outline_siren -> {
                            showReportMessages(comment)
                        }
                        R.drawable.icon_outline_hide -> {
                            blockComment(comment)
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

    private fun getComments() {
        viewModel.fetchFantooClubComments(
            contents.clubId,
            contents.categoryCode,
            contents.postId.toString()
        )
    }

    private fun setLikeAndDislike(likeType: LikeType) {
        if (!viewModel.getIsLogin()) {
            showLoginMessage()
            return
        }
        viewModel.setFantooClubPostLikeAndDislike(
            contents.clubId,
            likeType.type,
            contents.categoryCode,
            contents.postId.toString()
        )
    }

    private fun composeComment(attaches: List<FantooClubPostAttach>?) {
        if (!viewModel.getIsLogin()) {
            showLoginMessage()
            initComposeComment()
            return
        }
        val composeComment =
            ComposeComment(
                attaches,
                binding.input.text.toString(),
                viewModel.getUid(),
                contents.langCode
            )
        Timber.d("save click body: $composeComment ")
        when (viewModel.getFantooClubComposeComment().commentMode) {
            ComposeCommentMode.NEW -> {
                viewModel.composeFantooClubComment(
                    contents.clubId,
                    contents.categoryCode,
                    contents.postId.toString(),
                    composeComment
                )
            }
            ComposeCommentMode.EDIT -> {
                val replyId =
                    viewModel.getFantooClubComposeComment().fantooClubComment?.replyId.toString()
                viewModel.modifyFantooClubComment(
                    contents.clubId,
                    contents.categoryCode,
                    contents.postId.toString(),
                    replyId,
                    composeComment
                )
            }
        }
        initComposeComment()
    }

    private fun deleteComment(comment: FantooClubComment) {
        Timber.d("delete item: $comment")
        viewModel.deleteFantooClubComment(
            comment.clubId,
            comment.categoryCode,
            comment.postId.toString(),
            comment.replyId.toString()
        )
    }

    private fun reportComment(comment: FantooClubComment, reportMessageId: Int) {
        Timber.d("report item: $comment")
        viewModel.reportFantooClubComment(
            comment.clubId,
            comment.categoryCode,
            comment.postId.toString(),
            comment.replyId.toString(),
            reportMessageId
        )
    }

    private fun blockComment(comment: FantooClubComment) {
        Timber.d("block item: $comment")
        viewModel.blockFantooClubComment(
            comment.clubId,
            comment.categoryCode,
            comment.postId.toString(),
            comment.replyId.toString()
        )
    }

    companion object {
        const val TYPE_DISABLE_DELETE_COMMENT = 0
        const val TYPE_BLOCKED_COMMENT = 4
    }
}