package com.rndeep.fns_fantoo.ui.club.detail.home

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TabClubPageHomeListBinding
import com.rndeep.fns_fantoo.ui.club.detail.ClubPageListAdapter
import com.rndeep.fns_fantoo.data.remote.model.post.PostListData
import com.rndeep.fns_fantoo.ui.club.detail.ClubPageFragmentDirections
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetAdapter
import com.rndeep.fns_fantoo.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ClubPageHomeFragment : Fragment() {

    private var _binding: TabClubPageHomeListBinding? = null
    private val binding get() = _binding!!
    private val clubPageHomeViewModel: ClubPageHomeViewModel by viewModels()

    private val clubPageListAdapter = ClubPageListAdapter()

    //게시판의 타입명
    private lateinit var postType: String

    private var isAddLoading = false

    private lateinit var clubID: String
    private lateinit var categoryCode: String
    private lateinit var joinMemberId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clubID = arguments?.getString(ConstVariable.ClubDetail.KEY_DETAIL_CLUB_ID).toString()
        categoryCode = arguments?.getString(ConstVariable.ClubDetail.KEY_CATEGORY_CODE).toString()
        joinMemberId = arguments?.getString(ConstVariable.ClubDetail.KEY_JOIN_MEMBER_ID).toString()
        postType = ConstVariable.DB_CLUB_DETAIL_HOME
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = TabClubPageHomeListBinding.inflate(inflater, container, false)
        initView()
        settingObserve()
        clubPageHomeViewModel.initNextIds()
        clubPageHomeViewModel.getClubDetailHomePostItem(postType, clubID, categoryCode)

        return binding.root
    }

    private fun initView() {
        binding.rcClubPagePostList.layoutManager = LinearLayoutManager(context)
        binding.rcClubPagePostList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val itemPosition = parent.getChildAdapterPosition(view)
                if (itemPosition == RecyclerView.NO_POSITION) return
                if (clubPageListAdapter.currentList[itemPosition].type == ConstVariable.TYPE_CLUB || clubPageListAdapter.currentList[itemPosition].type == ConstVariable.TYPE_COMMUNITY
                    || clubPageListAdapter.currentList[itemPosition].type == ConstVariable.TYPE_AD || clubPageListAdapter.currentList[itemPosition].type == ConstVariable.TYPE_COMMUNITY_NOTICE
                ) {
                    outRect.bottom = SizeUtils.getDpValue(-10f, requireContext()).toInt()
                }
            }
        })
        binding.rcClubPagePostList.checkLastItemVisible {
            if (it && clubPageListAdapter.itemCount > 10 && !isAddLoading) {
                clubPageHomeViewModel.addClubHomeData(postType, clubID, categoryCode)
                isAddLoading = true
            }
        }

        binding.rcClubPagePostList.adapter = clubPageListAdapter

        clubPageListAdapter.setListOrFeed(ConstVariable.POST_TYPE_FEED)
        clubPageListAdapter.setViewType(ConstVariable.VIEW_CLUB_PAGE_HOME)
        clubPageListAdapter.setOnOptionsClickListener(object :
            BoardListeners.OnBoardPostClickListener {
            override fun onLikeClick(
                dbId: Int,
                postItem: PostListData,
                postType: String,
                changePos: Int,
                holder: RecyclerView.ViewHolder
            ) {
            }

            override fun onDisLikeClick(
                dbId: Int,
                postItem: PostListData,
                postType: String,
                changePos: Int,
                holder: RecyclerView.ViewHolder
            ) {
            }

            override fun onHonorClick(
                dbId: Int,
                postItem: PostListData,
                postType: String,
                changePos: Int,
                holder: RecyclerView.ViewHolder
            ) {
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
                    postAuthId == clubPageHomeViewModel.uId,
                    clubPageHomeViewModel.isUser(),
                    postType,
                    getBottomSheetClickListener(dbId, postType),
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
                findNavController().navigate(
                    ClubPageFragmentDirections.actionClubDetailPageFragmentToClubPost(
                        postType, categoryId, clubId ?: "-1", postId, joinMemberId.toInt()
                    )
                )
            }

            override fun onProfileClick(postItem: PostListData) {}
        })

        //공지 클릭
        clubPageListAdapter.setOnNoticeItemClickListener(object :
            BoardListeners.OnBoardNoticeClickListener {
            override fun onNoticeClick(v: View, postId: Int, clubId: String?) {
                findNavController().navigate(
                    ClubPageFragmentDirections.actionClubDetailPageFragmentToClubPost(
                        ConstVariable.TYPE_CLUB_NOTICE, "notice", clubID, postId, joinMemberId.toInt()
                    )
                )
            }

            override fun onNoticeMore() {
                val boardAction =
                    ClubPageFragmentDirections.actionClubDetailPageFragmentToClubNoticeFragment(
                        clubID
                    )
                findNavController().navigate(boardAction)
            }
        })

    }

    private fun settingObserve() {
        clubPageHomeViewModel.detailClubHomePostItems.observe(viewLifecycleOwner) {
            if ((it.size == 1 || it.isEmpty())) {
                binding.llNotPostData.visibility = View.VISIBLE
            } else {
                binding.llNotPostData.visibility = View.GONE
            }
            binding.llNotLoginView.visibility=View.GONE
            val state = binding.rcClubPagePostList.layoutManager?.onSaveInstanceState()
            clubPageListAdapter.submitList(it) {
                if (isAddLoading) isAddLoading = false
                binding.rcClubPagePostList.layoutManager?.onRestoreInstanceState(state)
            }
        }

        clubPageHomeViewModel.noticeClubHomeMutableData.observe(viewLifecycleOwner) {
            clubPageListAdapter.setNoticeListItem(it)
        }
        clubPageHomeViewModel.errorDataClub.observe(viewLifecycleOwner){
            if(it=="FE3001"){
                showJoinView(false)
            }else{
                activity?.showErrorSnackBar(binding.root,it)
            }
        }
    }

    fun getBottomSheetClickListener(pk_id: Int, type: String) =
        object : BottomSheetAdapter.OnItemClickListener {
            override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                when (name) {
                    getString(R.string.j_to_save) -> {
                        //api 통신 작성
                        clubPageHomeViewModel.requestSavePost(pk_id, type)
                    }
                    getString(R.string.g_to_share) -> {
                        //api 통신 작성
                        clubPageHomeViewModel.requestSharePost(pk_id, type)
                    }
                    getString(R.string.g_to_join) -> {
                        //api 통신 작성
                        clubPageHomeViewModel.requestJoinClub(pk_id, type)
                    }
                    getString(R.string.s_to_report) -> {
                        //api 통신 작성
                        clubPageHomeViewModel.requestReportPost(pk_id, type)
                    }
                    getString(R.string.g_hide_post), getString(R.string.g_unhide_post) -> {
                        //api 통신 작성
//                        clubPageHomeViewModel.requestHidePost(pk_id, type,clubPageHomeViewModel.detailClubHomePostItems)
                    }
                    getString(R.string.a_block_this_user), getString(R.string.a_unblock_this_user) -> {
                        //api 통신 작성
//                        clubPageHomeViewModel.requestBlockPostUser(pk_id, type,clubPageHomeViewModel.detailClubHomePostItems)
                    }
                }
            }
        }

    fun showJoinView(isOpen: Boolean) {
        if (isOpen) {
            binding.rcClubPagePostList.visibility = View.VISIBLE
            binding.llNotLoginView.visibility = View.GONE
        } else {
            binding.rcClubPagePostList.visibility = View.GONE
            binding.llNotLoginView.visibility = View.VISIBLE
            binding.llNotPostData.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}