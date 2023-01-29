package com.rndeep.fns_fantoo.ui.community

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.post.BoardPostData
import com.rndeep.fns_fantoo.data.remote.model.post.PostListData
import com.rndeep.fns_fantoo.databinding.FragmentCommunityTabBinding
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetAdapter
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.ui.editor.EditorInfo
import com.rndeep.fns_fantoo.ui.editor.EditorType
import com.rndeep.fns_fantoo.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CommunityTabFragment :
    BaseFragment<FragmentCommunityTabBinding>(FragmentCommunityTabBinding::inflate) {

    private val communityViewModel: CommunityTabPostViewModel by viewModels()

    //community 기본 어댑터
    private val communityAdapter = CommunityTabAdapter()

    override fun initUi() {
        activity?.let {
            activity?.window?.statusBarColor = it.getColor(R.color.gray_25)
            it.setDarkStatusBarIcon()
        }
        binding.rcCommunity.layoutManager = LinearLayoutManager(context)
        binding.rcCommunity.adapter = communityAdapter
        initView()
        settingObserver()
        if (communityViewModel.communityTotalItems.value == null) {
            communityViewModel.changeLoadingState(true)
        }

        communityViewModel.getInitItem()
    }

    override fun initUiActionEvent() {
        //툴바 선택
        binding.communityToolbar.setOnMenuItemClickListener { itemMenu ->
            when (itemMenu.itemId) {
                R.id.menu_club_search -> {
                    findNavController().navigate(CommunityTabFragmentDirections.actionCommunityTabFragmentToPostsearch())
                    true
                }
                R.id.menu_myInfo -> {
                    if (communityViewModel.isUser()) {
                        findNavController().navigate(R.id.action_communityTabFragment_to_communityMyPageFragment)
                    } else {
                        activity?.showErrorSnackBar(binding.root, ConstVariable.ERROR_NOT_MEMBER)
                    }
                    true
                }
                else -> {
                    false
                }
            }
        }

        //공지 선택
        communityAdapter.setOnBoardNoticeClickListener(object :
            BoardListeners.OnBoardNoticeClickListener {
            override fun onNoticeClick(v: View, postId: Int, clubId: String?) {
                findNavController().navigate(
                    CommunityTabFragmentDirections.actionCommunityTabFragmentToCommunitypost(
                        ConstVariable.TYPE_COMMUNITY_NOTICE,
                        null,
                        postId
                    )
                )
            }

            override fun onNoticeMore() {
            }

        })

        //post clickListener 들
        communityAdapter.setOnOptionsClickListener(object :
            BoardListeners.OnBoardPostClickListener {
            override fun onLikeClick(
                dbId: Int,
                postItem: PostListData,
                postType: String,
                changePos: Int,
                holder: RecyclerView.ViewHolder
            ) {
                if(postItem is BoardPostData){
                    communityViewModel.requestLikeUpdate(
                        postItem.postId.toString(),
                        ConstVariable.LIKE_CLICK,
                        changePos,
                        postType,
                        dbId,
                        postItem.likeYn == true
                    )
                }
            }

            override fun onDisLikeClick(
                dbId: Int,
                postItem: PostListData,
                postType: String,
                changePos: Int,
                holder: RecyclerView.ViewHolder
            ) {
                if(postItem is BoardPostData){
                    communityViewModel.requestLikeUpdate(
                        postItem.postId.toString(),
                        ConstVariable.DISLIKE_CLICK,
                        changePos,
                        postType,
                        dbId,
                        postItem.dislikeYn == true
                    )
                }
            }

            override fun onHonorClick(
                dbId: Int,
                postItem: PostListData,
                postType: String,
                changePos: Int,
                holder: RecyclerView.ViewHolder
            ) {
//                communityViewModel.requestHonorUpdate(
//                    postItem.postId.toString(),
//                    changePos,
//                    postType,
//                    dbId,
//                    postItem.honorYn == true
//                )
            }

            override fun onOptionClick(
                dbId: Int,
                postAuthId: String,
                postType: String,
                changePos: Int,
                postId: Int,
                isPieceBlockYn: Boolean?,
                isUserBlockYN: Boolean?,
                code: String
            ) {
                activity?.showBottomSheetOfPost(
                    postAuthId == communityViewModel.uId,
                    communityViewModel.isUser(),
                    postType,
                    getBottomSheetClickListener(dbId, ConstVariable.DB_COMMUNITYPAGE, changePos),
                    isPieceBlockYn,
                    isUserBlockYN
                )
            }

            override fun onPostClick(
                categoryId: String,
                postId: Int,
                postType: String,
                clubId: String?
            ) {
                val action =
                    CommunityTabFragmentDirections.actionCommunityTabFragmentToCommunitypost(
                        postType, categoryId, postId
                    )
                findNavController().navigate(action)
            }

            override fun onProfileClick(postItem: PostListData) {
                if(postItem is BoardPostData){
                    val boardAction =
                        CommunityTabFragmentDirections.actionCommunityTabFragmentToCommunityboard(
                            BoardInfo(postItem.title?:"", postItem.code, ConstVariable.BOARD_COMMON_TYPE)
                        )
                    findNavController().navigate(boardAction)
                }
            }
        })

        //글작성 floating button
        binding.fabEditButton.setOnClickListener {
            if(communityViewModel.isUser()){
                val editorInfo = EditorInfo(EditorType.COMMUNITY, null, null, null)
                val direction =
                    CommunityTabFragmentDirections.actionCommunityTabFragmentToEditorFragment(
                        editorInfo
                    )
                findNavController().navigate(direction)
            }else{
                activity?.showErrorSnackBar(binding.root,ConstVariable.ERROR_NOT_MEMBER)
            }
        }

        //새로고침
        binding.swipeRefresh.setOnRefreshListener {
            communityViewModel.getInitItem()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun settingObserver() {
        communityViewModel.loadingStateLiveData.observe(viewLifecycleOwner) {
            binding.fantooLoadingView.visibility = if (it) View.VISIBLE else View.GONE
        }

        communityViewModel.communityTotalItems.observe(viewLifecycleOwner) {
            communityViewModel.changeLoadingState(false)
            if (it.isEmpty()) {
                return@observe
            }
            val state = binding.rcCommunity.layoutManager?.onSaveInstanceState()
            communityAdapter.submitList(it) {
                if(binding.swipeRefresh.isRefreshing){
                    binding.swipeRefresh.isRefreshing = false
                }
                binding.rcCommunity.layoutManager?.onRestoreInstanceState(state)
            }
        }

        //상단 게시판 아이템
        communityViewModel.communityBoardItems.observe(viewLifecycleOwner) {
            communityAdapter.setBoardItem(it.categoryBoardList)
        }

        //상단 공지 리스트
        communityViewModel.communityNoticeItemLiveData.observe(viewLifecycleOwner) {
            communityAdapter.setNoticeItems(it)
        }

        //좋아요 결과
        communityViewModel.likeDislikeResultLiveData.observe(this) {
            communityAdapter.currentList[it.third].boardPostItem =
                it.second[it.third].boardPostItem!!
            communityAdapter.notifyItemChanged(it.third, ConstVariable.PAYLOAD_LIKE_CLICK)
        }

        //아너 결과
        communityViewModel.honorResultLiveData.observe(this) {
            communityAdapter.currentList[it.second].boardPostItem =
                it.first[it.second].boardPostItem!!
            communityAdapter.notifyItemChanged(it.second, ConstVariable.PAYLOAD_HONOR_CLICK)
        }

        //에러 코드
        communityViewModel.errorMessageLiveData.observe(this) {
            activity?.showErrorSnackBar(binding.root, it.toString())
        }

        //저장하기
        communityViewModel.savePostResult.observe(viewLifecycleOwner) {
            val saveMessage= if (it) {
                getString(R.string.se_j_done_save)
            }else{
                getString(R.string.se_a_failed_save_by_error)
            }
            val customDialog = CommonDialog.Builder().apply {
                title = saveMessage
                setPositiveButtonText(getString(R.string.h_confirm))
            }
            customDialog.build().show(this.parentFragmentManager, "save")
        }
        //공유하기
        communityViewModel.savePostSharePostResult.observe(viewLifecycleOwner) {
            if (it) {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_TEXT, "공유 텍스트(Deep Link 받아오기)")
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.g_share_post))
                val chooserIntent = Intent.createChooser(shareIntent, null)
                startActivity(chooserIntent)

            }
        }
        //가입하기
        communityViewModel.savePostJoinClubResult.observe(viewLifecycleOwner) {
            if (it) {
                val customDialog = CommonDialog.Builder().apply {
                    title = getString(R.string.k_club_with_arg, "아이유")
                    message = getString(R.string.se_g_join_complete)
                    setPositiveButtonText(getString(R.string.h_confirm))
                }
                customDialog.build().show(this.parentFragmentManager, "join")
            }
        }
        //신고하기
        communityViewModel.savePostReportResult.observe(viewLifecycleOwner) {
            if (it) {
                val customDialog = CommonDialog.Builder().apply {
                    title = getString(R.string.g_post_with_arg, "아이유 안티")
                    message = getString(R.string.se_s_report_complete)
                    setPositiveButtonText(getString(R.string.h_confirm))
                }
                customDialog.build().show(this.parentFragmentManager, "report")
            }
        }
        //게시글 숨기기
        communityViewModel.pieceBlockResultLiveData.observe(this) {
            communityViewModel.getInitItem()
        }
        //이 사용자 차단하기
        communityViewModel.accountBlockResultLiveData.observe(this) {
            communityViewModel.getInitItem()
        }

        //에러 상태
        communityViewModel.errorResultLiveData.observe(this) {
            it ?: return@observe
            activity?.showErrorSnackBar(binding.root, it)
        }

    }

    private fun initView() {
        if (binding.rcCommunity.itemAnimator is SimpleItemAnimator) {
            (binding.rcCommunity.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                false
            binding.rcCommunity.itemAnimator = null
        }

        binding.rcCommunity.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val itemPosition = parent.getChildAdapterPosition(view)
                if (itemPosition == RecyclerView.NO_POSITION) return
                if (communityAdapter.currentList[itemPosition].type == ConstVariable.TYPE_CLUB ||
                    communityAdapter.currentList[itemPosition].type == ConstVariable.TYPE_COMMUNITY ||
                    communityAdapter.currentList[itemPosition].type == ConstVariable.TYPE_AD
                ) {
                    outRect.bottom = SizeUtils.getDpValue(1f, requireContext()).toInt()
                }
                if (communityAdapter.currentList[itemPosition].type == ConstVariable.TYPE_WEEK_TOP) {
                    outRect.top = SizeUtils.getDpValue(11f, requireContext()).toInt()
                }
            }
        })

    }


    //바텀 Sheet 클릭 리스너
    fun getBottomSheetClickListener(pk_id: Int, type: String, parentPos: Int) =
        object : BottomSheetAdapter.OnItemClickListener {
            override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                when (name) {
//                    getString(R.string.j_to_save) -> {
//                        //api 통신 작성
//                        communityViewModel.requestSavePost(pk_id, type)
//                    }
//                    getString(R.string.g_to_share) -> {
//                        //api 통신 작성
//                        communityViewModel.requestSharePost(pk_id, type)
//                    }
//                    getString(R.string.g_to_join) -> {
//                        //api 통신 작성
//                        communityViewModel.requestJoinClub(pk_id, type)
//                    }
//                    getString(R.string.s_to_report) -> {
//                        //api 통신 작성
//                        communityViewModel.requestReportPost(pk_id, type)
//                    }
//                    getString(R.string.g_hide_post), getString(R.string.g_unhide_post) -> {
//                        //api 통신 작성
//                        communityViewModel.requestBlockPiecePost(pk_id, parentPos)
//                    }
//                    getString(R.string.a_block_this_user), getString(R.string.a_unblock_this_user) -> {
//                        //api 통신 작성
//                        communityViewModel.requestBlockPostUser(pk_id)
//                    }
                }
            }
        }

    fun scrollTop() {
        binding.rcCommunity.anchorSmoothScrollToPosition(0, 3)
    }

}