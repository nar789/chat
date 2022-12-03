package com.rndeep.fns_fantoo.ui.community.board

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isEmpty
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.Country
import com.rndeep.fns_fantoo.data.remote.model.post.BoardPostData
import com.rndeep.fns_fantoo.data.remote.model.post.PostListData
import com.rndeep.fns_fantoo.databinding.FragmentCommunityBoardBinding
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.ui.common.LanguageBottomSheet
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetAdapter
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetItem
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.CustomBottomSheet
import com.rndeep.fns_fantoo.ui.community.BoardInfo
import com.rndeep.fns_fantoo.ui.editor.EditorInfo
import com.rndeep.fns_fantoo.ui.editor.EditorType
import com.rndeep.fns_fantoo.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommunityBoardFragment :
    BaseFragment<FragmentCommunityBoardBinding>(FragmentCommunityBoardBinding::inflate) {
    private val boardFragmentViewModel: CommunityBoardViewModel by viewModels()

    //받은 아이템
    val getItem: CommunityBoardFragmentArgs by navArgs()

    lateinit var boardItem: BoardInfo

    //트랜드 global filter 팝업
    var globalSheetData = arrayListOf<BottomSheetItem>()

    //게시판 리스트 어댑터
    private var boardPostAdapter = CommunityBoardPostListAdapter()

    //bttomSheet 아이템
    private lateinit var authMemberSheetData: ArrayList<BottomSheetItem>
    private lateinit var noMemberSheetData: ArrayList<BottomSheetItem>

    private var isBookMark = false

    private var bottomDetect = false

    var scrollY = 0
    var isRolling = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        boardItem = getItem.boardInfo

        globalSheetData = arrayListOf(
            BottomSheetItem(
                null,
                resources.getString(R.string.en_global),
                null,
                true
            ),
            BottomSheetItem(
                null,
                resources.getString(R.string.n_setting_to_my_language),
                null,
                false
            ),
            BottomSheetItem(
                null,
                resources.getString(R.string.d_other_language_select),
                null,
                false
            ),
        )

        authMemberSheetData = arrayListOf(
            BottomSheetItem(
                R.drawable.icon_outline_share,
                getString(R.string.g_to_share),
                null,
                null
            ),
            BottomSheetItem(
                R.drawable.outline_icon_edit,
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

        noMemberSheetData = arrayListOf(
            BottomSheetItem(
                R.drawable.icon_outline_share,
                getString(R.string.g_to_share),
                null,
                null
            ),
        )
    }

    override fun initUi() {
        initToolbar()
        initView()
        settingObserver()

        //게시판 리스트 가져오기
        //(화면 이동시 리스트를 계속 가져와 새로고침 되는 이슈로 인해 item이 없을 경우에만 초기 데이터 불러옴)
        binding.btnScrollUp.alpha = 0f
        boardFragmentViewModel.getBoardPostItem(
            boardItem.boardId!!,
            boardFragmentViewModel.getCurrentBoardCategoryCode(),
            if (boardPostAdapter.itemCount < 10) 10 else boardPostAdapter.itemCount - 1
        )

        //게시글 스크롤 상태 확인
        binding.rcBoardPostList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isRolling = true
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isRolling = false
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isRolling) {
                    scrollY += dy
                    binding.btnScrollUp.alpha = scrollY / 2000f
                }
            }
        })
    }

    override fun initUiActionEvent() {
        //공지 선택
        boardPostAdapter.setOnCommunityNoticeClickListener(object :
            BoardListeners.OnBoardNoticeClickListener {
            override fun onNoticeClick(v: View, postId: Int, clubId: String?) {
                findNavController().navigate(
                    CommunityBoardFragmentDirections.actionCommunityBoardFragment3ToCommunitypost(
                        ConstVariable.TYPE_COMMUNITY_NOTICE, boardItem.boardId, postId
                    )
                )
            }

            override fun onNoticeMore() {
                findNavController().navigate(
                    CommunityBoardFragmentDirections.actionCommunityBoardFragment3ToNotice(
                        boardItem
                    )
                )
            }
        })

        //카테고리 선택
        boardPostAdapter.setOnCommunityCategoryClickListener(object :
            BoardListeners.OnBoardCategoryClickListener {
            override fun onCategoryClick(v: View, id: String, position: Int) {
                boardFragmentViewModel.changeLoadingState(true)
                boardFragmentViewModel.setInitCurrentNextId()
                boardFragmentViewModel.callBoardPostItem(boardItem.boardId!!, id, 10)
                boardPostAdapter.selectCatePos(position)
                scrollY = 0
                binding.btnScrollUp.alpha = 0f
            }
        })

        //언어 필터 선택
        boardPostAdapter.setOnCommunityFilterClickListener(object :
            CommunityBoardPostListAdapter.OnBoardFilterClickListener {
            override fun onFilterClick(v: View, id: String) {
                showGlobalBottomDialog()
            }
        })

        //게시글 선택
        boardPostAdapter.setOnBoardPostClickListener(object :
            BoardListeners.OnBoardPostClickListener {
            override fun onLikeClick(
                dbId: Int,
                postItem: PostListData,
                postType: String,
                changePos: Int,
                holder: RecyclerView.ViewHolder
            ) {
                if(postItem is BoardPostData){
                    boardFragmentViewModel.requestPostLike(
                        dbId,
                        ConstVariable.LIKE_CLICK,
                        postItem.postId.toString(),
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
                    boardFragmentViewModel.requestPostLike(
                        dbId,
                        ConstVariable.DISLIKE_CLICK,
                        postItem.postId.toString(),
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
//                val res = boardFragmentViewModel.requestHonor(dbId)
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
                showPostBottomDialog(
                    dbId,
                    postAuthId,
                    postId.toString(),
                    code,
                    isPieceBlockYn == true,
                    isUserBlockYN == true
                )

            }

            override fun onPostClick(
                categoryId: String,
                postId: Int,
                postType: String,
                clubId: String?
            ) {
                findNavController().navigate(
                    CommunityBoardFragmentDirections.actionCommunityBoardFragment3ToCommunitypost(
                        postType,
                        categoryId,
                        postId
                    )
                )
            }

            override fun onProfileClick(title: String, boardCode: String) {}


        })

        //recyclerview 마지막 아이템 확인
        binding.rcBoardPostList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter?.itemCount?.minus(1)

                if (lastItemPosition == itemTotalCount) {
                    if (!bottomDetect && boardPostAdapter.itemCount > 8) {
                        bottomDetect = true
                        boardFragmentViewModel.changeLoadingState(true)
                        boardFragmentViewModel.callBoardPostItem(
                            boardItem.boardId!!,
                            boardFragmentViewModel.getCurrentBoardCategoryCode(),
                            10
                        )
                    }
                } else {
                    bottomDetect = false
                }
            }
        })

        //글쓰기로 이동
        binding.fabBoardPostEdit.setOnClickListener {
            val editorInfo = EditorInfo(EditorType.COMMUNITY, null, null)
            findNavController().navigate(CommunityBoardFragmentDirections.actionCommunityBoardFragment3ToEditorFragment3(editorInfo))
        }

        //스크롤 위로
        binding.btnScrollUp.setOnClickListener {
            binding.rcBoardPostList.anchorSmoothScrollToPosition(0, 3)
            scrollY = 0
            binding.btnScrollUp.alpha = 0f
        }

        //refresh Item
        binding.swipeRefresh.setOnRefreshListener {
            boardFragmentViewModel.getBoardPostItem(
                boardItem.boardId!!,
                boardFragmentViewModel.getCurrentBoardCategoryCode(),
                if (boardPostAdapter.itemCount < 10) 10 else boardPostAdapter.itemCount - 1
            )
        }
    }

    private fun initToolbar() {
        //뒤로가기
        binding.communityBoardToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        //게시판 제목
        binding.communityBoardToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                //즐겨찾기
                R.id.menu_board_bookmark -> {
                    boardFragmentViewModel.bookmarkChange(isBookMark, boardItem.boardId!!)
                    true
                }
                //검색
                R.id.menu_board_search -> {
                    val action =
                        CommunityBoardFragmentDirections.actionCommunityBoardFragment3ToPostsearch()
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }
    }

    private fun initView() {
        binding.rcBoardPostList.layoutManager = LinearLayoutManager(requireContext())
        binding.rcBoardPostList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val itemPosition = parent.getChildAdapterPosition(view)
                if (itemPosition == RecyclerView.NO_POSITION) return
                if (boardPostAdapter.getItem(itemPosition).type == ConstVariable.TYPE_COMMUNITY) {
                    outRect.bottom = SizeUtils.getDpValue(8f, requireContext()).toInt()
                }
                if (boardPostAdapter.getItem(itemPosition).type == ConstVariable.TYPE_COMMUNITY_NOTICE) {
                    outRect.bottom = SizeUtils.getDpValue(2f, requireContext()).toInt()
                }
            }
        })

        binding.rcBoardPostList.itemAnimator = null
        binding.rcBoardPostList.adapter = boardPostAdapter

        if (boardItem.boardId == "C_HOT") {
            binding.fabBoardPostEdit.visibility = View.GONE
            boardPostAdapter.setBoardViewType(ConstVariable.VIEW_COMMUNITY_PAGE_HOT)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun settingObserver() {
        boardFragmentViewModel.loadingStateLiveData.observe(viewLifecycleOwner) {
            binding.fantooLoadingView.visibility = if (it) View.VISIBLE else View.GONE
        }

        //notice
        boardFragmentViewModel.boardNoticeItems.observe(viewLifecycleOwner) {
            boardPostAdapter.setNoticeItems(it.subList(0, if (it.size > 2) 3 else it.size))
        }
        //카테고리 정보
        boardFragmentViewModel.categoryInfoLiveData.observe(viewLifecycleOwner) { boardInfo ->
            //게시판 제목
            val boardName = when (boardInfo.second) {
                "KR" -> {
                    boardInfo.first.codeNameKo
                }
                "EN" -> {
                    boardInfo.first.codeNameEn
                }
                else -> {
                    boardInfo.first.codeNameEn
                }
            }
            binding.communityBoardToolbar.title = boardName
            if (binding.communityBoardToolbar.menu.isEmpty()) {
                binding.communityBoardToolbar.inflateMenu(R.menu.community_board_menu)
            }
            changeBookmarkState(boardInfo.first.favorite == true)
            isBookMark = boardInfo.first.favorite == true
        }

        //서브 카테고리
        boardFragmentViewModel.boardCategoryItems.observe(viewLifecycleOwner) {
            boardPostAdapter.setCategoryItems(it)
        }

        //전체 아이템
        boardFragmentViewModel.boardPostDatas.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.rcBoardPostList.visibility = View.GONE
                binding.layoutNoListView.root.visibility = View.VISIBLE
            } else {
                binding.rcBoardPostList.visibility = View.VISIBLE
                binding.layoutNoListView.root.visibility = View.GONE

                val state = binding.rcBoardPostList.layoutManager?.onSaveInstanceState()
                boardPostAdapter.submitList(it) {
                    binding.rcBoardPostList.layoutManager?.onRestoreInstanceState(state)
                }
            }
            binding.swipeRefresh.isRefreshing = false
        }

        //더이상 아이템이 없음
        boardFragmentViewModel.noMorePostItem.observe(viewLifecycleOwner) {
            activity?.showCustomSnackBar(binding.root, getString(R.string.se_d_no_more_exist_post))
        }

        //좋아요 싫어요 결과
        boardFragmentViewModel.likeDisLikeResultLiveData.observe(this) {
            boardPostAdapter.submitList(it)
        }

        //차단 결과
        boardFragmentViewModel.boardPieceBlockLiveData.observe(this) {
            if (it == 200) {
                boardFragmentViewModel.getBoardPostItem(
                    boardItem.boardId!!,
                    boardFragmentViewModel.getCurrentBoardCategoryCode(),
                    if (boardPostAdapter.itemCount < 10) 10 else boardPostAdapter.itemCount - 1
                )
            } else {
                activity?.showErrorSnackBar(binding.root, it.toString())
            }
        }

        //삭제 결과
        boardFragmentViewModel.boardDeletePostResultLiveData.observe(this) {
            boardPostAdapter.submitList(it)
        }

        //북마크 등록 결과
        boardFragmentViewModel.bookmarkResultLiveData.observe(viewLifecycleOwner) { bookmarkResult ->
            if (bookmarkResult) {
                isBookMark = !isBookMark
                changeBookmarkState(isBookMark)
            }
        }

        //에러 상태
        boardFragmentViewModel.errorSnackBarMessageLiveData.observe(viewLifecycleOwner) { errorCode ->
            activity?.showErrorSnackBar(binding.root, errorCode)
        }

        //Global 상태
        boardFragmentViewModel.globalYnLiveData.observe(viewLifecycleOwner) {
            boardPostAdapter.setGlobalYN(!it)
        }

    }

    private fun changeBookmarkState(isBookmark: Boolean) {
        context?.let {
            val bookmarkMenu =
                binding.communityBoardToolbar.menu.findItem(R.id.menu_board_bookmark)
            val bookmarkDrawable = bookmarkMenu.icon

            DrawableCompat.setTint(
                bookmarkDrawable,
                ContextCompat.getColor(
                    it,
                    if (isBookmark) R.color.state_active_secondary_default else R.color.gray_200
                )
            )

            bookmarkMenu.setIcon(bookmarkDrawable)
        }
    }

    fun showGlobalBottomDialog() {
        activity?.let {
            val bottomSheet = CustomBottomSheet()
            bottomSheet.setTitleText(getString(R.string.a_language_filter))
            bottomSheet.setBottomItems(
                globalSheetData
            )
            bottomSheet.setOnCliCkListener(object : BottomSheetAdapter.OnItemClickListener {
                override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                    if (pos == oldPos) {
                        bottomSheet.dismiss()
                        return
                    }
                    when (name) {
                        resources.getString(R.string.en_global) -> {
                            boardFragmentViewModel.setGlobalState(true)
                            boardFragmentViewModel.getBoardPostItem(
                                getItem.boardInfo.boardId!!,
                                boardFragmentViewModel.getCurrentBoardCategoryCode(),
                                10,
                            )
                        }
                        resources.getString(R.string.n_setting_to_my_language) -> {
                            boardFragmentViewModel.setGlobalState(false)
                            boardFragmentViewModel.getBoardPostItem(
                                getItem.boardInfo.boardId!!,
                                boardFragmentViewModel.getCurrentBoardCategoryCode(),
                                10,
                            )

                        }
                        resources.getString(R.string.d_other_language_select) -> {
                            //api 통신 작성
                            showLanguageBottomSheet()
                        }
                    }
                    for (a in globalSheetData) {
                        a.isChecked = a.itemName == name
                    }
                    bottomSheet.dismiss()
                }
            })
            bottomSheet.show(it.supportFragmentManager, "Tag")
        }
    }

    fun showLanguageBottomSheet(){
        LanguageBottomSheet().apply {
            itemClickListener = object: LanguageBottomSheet.ItemClickListener{
                override fun onItemClick(item: Country) {
                    boardFragmentViewModel.setGlobalState(false)
                    boardFragmentViewModel.getBoardPostItem(
                        getItem.boardInfo.boardId!!,
                        boardFragmentViewModel.getCurrentBoardCategoryCode(),
                        10,
                    )
                    dismiss()
                }
            }
        }.show(parentFragmentManager,"langSheet")
    }

    fun showPostBottomDialog(
        dbId: Int,
        postAuthId: String,
        postId: String,
        code: String,
        pieceBlock: Boolean,
        userBlock: Boolean
    ) {
        val boardBottomSheet = CustomBottomSheet()

        if (boardFragmentViewModel.isMember()) {
            if (postAuthId == boardFragmentViewModel.uId) {
                boardBottomSheet.setBottomItems(authMemberSheetData)
            } else {
                boardBottomSheet.setBottomItems(getPostOptionsData(userBlock, pieceBlock))
            }
        } else {
            boardBottomSheet.setBottomItems(noMemberSheetData)
        }

        boardBottomSheet.setOnCliCkListener(object :
            BottomSheetAdapter.OnItemClickListener {
            override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                when (name) {
                    getString(R.string.j_to_save) -> {

                    }
                    getString(R.string.g_to_share) -> {

                    }
                    getString(R.string.s_to_report) -> {

                    }
                    getString(R.string.g_hide_post), getString(R.string.g_unhide_post) -> {
                        boardFragmentViewModel.pieceBlockPost(
                            postAuthId, ConstVariable.LIKE_DISLIKE_TYPE_POST, postId, pieceBlock
                        )
                    }
                    getString(R.string.a_block_this_user), getString(R.string.a_unblock_this_user) -> {
                        boardFragmentViewModel.userBlockPost(postAuthId, userBlock)
                    }
                    getString(R.string.bottom_sheet_edit) -> {

                    }
                    getString(R.string.bottom_sheet_delete) -> {
                        boardFragmentViewModel.callDeletePost(code, postId, dbId)
                    }
                }
                boardBottomSheet.dismiss()
            }
        })
        boardBottomSheet.show(parentFragmentManager, "postOptions")
    }

    private fun getPostOptionsData(isUserBlock: Boolean, isPieceBlock: Boolean) = arrayListOf(
        BottomSheetItem(
            R.drawable.icon_outline_save,
            getString(R.string.j_to_save),
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
            if (isPieceBlock) getString(R.string.g_unhide_post) else getString(R.string.g_hide_post),
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