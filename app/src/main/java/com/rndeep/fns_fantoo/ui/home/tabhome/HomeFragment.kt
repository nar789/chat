package com.rndeep.fns_fantoo.ui.home.tabhome

import android.content.Intent
import android.graphics.Rect
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.GenderType
import com.rndeep.fns_fantoo.data.remote.model.post.BoardPostData
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostData
import com.rndeep.fns_fantoo.data.remote.model.post.PostListData
import com.rndeep.fns_fantoo.databinding.FragmentHomeBinding
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.ui.common.recommendclub.RecommendClubListAdapter
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.ui.home.HomeTabFragmentDirections
import com.rndeep.fns_fantoo.utils.*
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetAdapter
import com.rndeep.fns_fantoo.ui.login.LoginMainActivity
import com.rndeep.fns_fantoo.ui.menu.MenuFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val homeViewModel: HomeViewModel by viewModels()

    private val homeRcAdapter = CategoryHomeAdapter()

    private var currentLast = true

    private val homeItemDeco: RecyclerView.ItemDecoration

    init {
        homeItemDeco = getHomeRecyclerViewDeco()
    }

    override fun initUi() {
        binding.rcHomeList.layoutManager = LinearLayoutManager(context)
        binding.rcHomeList.addSingleItemDecoRation(homeItemDeco)
        binding.rcHomeList.adapter = homeRcAdapter

        //초기 상태인지 아닌지 확인
        if (!homeViewModel.isUser()) {
            homeViewModel.getGuestData()
        }
        settingObserve()
    }

    override fun initUiActionEvent() {
        binding.rcHomeList.checkLastItemVisible {
            if (it) {
                //마지막 아이템
                if (!(currentLast) && homeRcAdapter.currentList.size > 8) {
                    homeViewModel.changeLoadingState(true)
                    currentLast = true
                    homeViewModel.getAddHomeData(1, requireContext())
                }
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            homeViewModel.isCheckProfile()
            binding.swipeRefresh.isRefreshing = false
//            homeViewModel.getInitHomeData(requireContext())
        }

        homeRcAdapter.setOnLoginClickListener(object : CategoryHomeAdapter.OnLoginClickListener {
            override fun onLoginCLick() {
                activity?.let { activity ->
                    context?.let {
                        val intent = Intent(it, LoginMainActivity::class.java)
                        intent.putExtra(
                            ConstVariable.INTENT.EXTRA_NAV_START_DESTINATION_ID,
                            R.id.loginFragment
                        )
                        startActivity(intent)
                        activity.finish()
                    }
                }
            }
        })

        // HomeCategory 하단 기능 선택
        homeRcAdapter.setOnOptionsClickListener(object : BoardListeners.OnBoardPostClickListener {
            override fun onLikeClick(
                dbId: Int,
                postItem: PostListData,
                postType: String,
                changePos: Int,
                holder: RecyclerView.ViewHolder
            ) {
                when (postItem) {
                    is BoardPostData -> {
                        homeViewModel.requestLikeUpdate(
                            postItem.postId.toString(),
                            ConstVariable.LIKE_CLICK,
                            changePos,
                            postType,
                            dbId,
                            postItem.likeYn == true
                        )
                    }
                    is ClubPostData -> {
                    }
                }
            }

            override fun onDisLikeClick(
                dbId: Int,
                postItem: PostListData,
                postType: String,
                changePos: Int,
                holder: RecyclerView.ViewHolder
            ) {
                when (postItem) {
                    is BoardPostData -> {
                        homeViewModel.requestLikeUpdate(
                            postItem.postId.toString(),
                            ConstVariable.DISLIKE_CLICK,
                            changePos,
                            postType,
                            dbId,
                            postItem.dislikeYn == true
                        )
                    }
                    is ClubPostData -> {
                    }
                }
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
                //상단 오른쪽 클릭 처리
                activity?.showBottomSheetOfPost(
                    postAuthId == homeViewModel.uId,
                    homeViewModel.isUser(),
                    postType,
                    getBottomSheetClickListener(dbId, ConstVariable.DB_HOMECATEGORY, changePos),
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
                when (postType) {
                    ConstVariable.TYPE_COMMUNITY -> {
                        val action =
                            HomeTabFragmentDirections.actionHomeTabFragmentToCommunitypost(
                                postType, categoryId, postId
                            )
                        findNavController().navigate(action)
                    }
                    ConstVariable.TYPE_CLUB -> {
                        val action =
                            HomeTabFragmentDirections.actionHomeTabFragmentToClubPost(
                                postType, categoryId, clubId ?: "-1", postId,
                            )
                        findNavController().navigate(action)
                    }
                }
            }

            override fun onProfileClick(postItem: PostListData) {}
        })

        //추천 클럽 선택
        homeRcAdapter.setOnRecommendClubClickListener(object :
            RecommendClubListAdapter.OnRecommendClubClickListener {
            override fun onClubClick(v: View, id: Int, name: String) {
                findNavController().navigate(HomeTabFragmentDirections.actionHomeTabFragmentToClubPageDetail(id.toString()))
            }

            override fun onJoinClick(v: View, id: Int, name: String) {
                findNavController().navigate(
                    HomeTabFragmentDirections.actionHomeTabFragmentToClubJoinFragment2(
                        id.toString()
                    )
                )
            }
        })

    }

    fun getBottomSheetClickListener(pk_id: Int, type: String, parentPos: Int) =
        object : BottomSheetAdapter.OnItemClickListener {
            override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                when (name) {
                    getString(R.string.j_to_save) -> {
                        //api 통신 작성
                        homeViewModel.requestSavePost(pk_id, type)
                    }
                    getString(R.string.g_to_share) -> {
                        //api 통신 작성
                        homeViewModel.requestSharePost(pk_id, type)
                    }
                    getString(R.string.g_to_join) -> {
                        //api 통신 작성
                        homeViewModel.requestJoinClub(pk_id, type)
                    }
                    getString(R.string.s_to_report) -> {
                        //api 통신 작성
                        homeViewModel.requestReportPost(pk_id, type)
                    }
                    getString(R.string.g_hide_post), getString(R.string.g_unhide_post) -> {
                        //api 통신 작성
                        when (type) {
                            ConstVariable.DB_HOMECATEGORY -> {
                                homeViewModel.requestBlockPiecePost(pk_id, parentPos)
                            }
                        }

                    }
                    getString(R.string.a_block_this_user), getString(R.string.a_unblock_this_user) -> {
                        //api 통신 작성
                        when (type) {
                            ConstVariable.DB_HOMECATEGORY -> {
                                homeViewModel.requestBlockPostUser(pk_id)
                            }
                        }
                    }
                }

            }
        }

    fun settingObserve() {
        //loading Data
        homeViewModel.loadingStateLiveData.observe(this) {
            binding.fantooLoadingView.visibility = if (it) View.VISIBLE else View.GONE
        }

        //home banner Item
        homeViewModel.bannerItemLiveData.observe(viewLifecycleOwner) {
            homeRcAdapter.setBannerItem(it)
            homeRcAdapter.notifyItemChanged(0)
        }

        if (homeViewModel.isUser()) {
            homeViewModel.myProfile.observe(viewLifecycleOwner) {
                if (it == null) {
                    homeViewModel.isCheckProfile()
                    return@observe
                }
                var count = MenuFragment.MINIMUM_PROFILE_COUNT
                if (!it.imageUrl.isNullOrEmpty()) count++
                if (it.gender != GenderType.UNKNOWN) count++
                if (!it.concern.isNullOrEmpty()) count++
                if (!it.birthday.isNullOrEmpty()) count++

                if (count == MenuFragment.COMPLETE_PROFILE_COUNT) {
                    homeViewModel.isCheckOrNeedCheck = false
                    if (homeRcAdapter.currentList.isEmpty()) {
                        homeViewModel.changeLoadingState(true)
                        homeViewModel.getInitHomeData(requireContext())
                    } else {
                        homeViewModel.getAllItem()
                    }
                } else {
                    if (homeViewModel.isCheckOrNeedCheck) {
                        homeViewModel.isCheckProfile()
                    } else {
                        homeRcAdapter.setEditProfileItem(it)
                        homeViewModel.getEditProfileView()
                    }
                }
            }
        }

        //home item observer
        homeViewModel.homeItemLiveData.observe(viewLifecycleOwner) {
            homeViewModel.changeLoadingState(false)
            if (it.isEmpty()) return@observe
            homeRcAdapter.submitList(it) {
                currentLast = false
            }
            binding.swipeRefresh.isRefreshing = false
        }
        //에러 코드
        homeViewModel.errorCodeLiveData.observe(this) {
            activity?.showErrorSnackBar(binding.root, it.toString())
            binding.swipeRefresh.isRefreshing = false
        }

        //추천 클럽 리스트
        homeViewModel.homeRecommendClubLiveData.observe(viewLifecycleOwner) {
            if (it.isEmpty()) return@observe
            it[0].title = getString(R.string.m_recomment_custom_club)
            it[0].subText = getString(R.string.se_n_recommend_clubs_on_interest)
            homeRcAdapter.setRecommendItem(it[0])
        }
        //저장하기
        homeViewModel.savePostResult.observe(viewLifecycleOwner) {
            val saveMessage = if (it) {
                getString(R.string.se_j_done_save)
            } else {
                getString(R.string.se_a_failed_save_by_error)
            }
            val customDialog = CommonDialog.Builder().apply {
                title = saveMessage
                setPositiveButtonText(getString(R.string.h_confirm))
            }
            customDialog.build().show(this.parentFragmentManager, "save")
        }
        //공유하기
        homeViewModel.savePostSharePostResult.observe(viewLifecycleOwner) {
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
        homeViewModel.savePostJoinClubResult.observe(viewLifecycleOwner) {
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
        homeViewModel.savePostReportResult.observe(viewLifecycleOwner) {
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
        homeViewModel.pieceBlockResultLiveData.observe(this) {
            homeRcAdapter.currentList[it.second].boardPostItem = it.first[it.second].boardPostItem!!
            homeRcAdapter.notifyItemChanged(it.second)
        }
        //이 사용자 차단하기
        homeViewModel.accountBlockResultLiveData.observe(this) {
            homeRcAdapter.submitList(it)
        }

        //좋아요 결과
        homeViewModel.likeDislikeResultLiveData.observe(this) {
            homeRcAdapter.currentList[it.third].boardPostItem = it.second[it.third].boardPostItem!!
            homeRcAdapter.notifyItemChanged(it.third, ConstVariable.PAYLOAD_LIKE_CLICK)

        }
        //honor 결과
        homeViewModel.honorResultLiveData.observe(this) {
            homeRcAdapter.currentList[it.second].boardPostItem = it.first[it.second].boardPostItem!!
            homeRcAdapter.notifyItemChanged(it.second, ConstVariable.PAYLOAD_HONOR_CLICK)
        }

        //에러 코드
        homeViewModel.errorMessageLiveData.observe(this) {
            activity?.showErrorSnackBar(binding.root, it.toString())
        }

    }

    fun getHomeRecyclerViewDeco() = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val itemPosition = parent.getChildAdapterPosition(view)
            if (itemPosition == RecyclerView.NO_POSITION) return
            when (homeRcAdapter.currentList[itemPosition].type) {
                ConstVariable.TYPE_CLUB, ConstVariable.TYPE_COMMUNITY, ConstVariable.TYPE_AD -> {
                    if (homeRcAdapter.itemCount != itemPosition + 1) {
                        if (homeRcAdapter.currentList[itemPosition + 1].type == ConstVariable.TYPE_HOME_RECOMMEND_CLUB) {
                            outRect.bottom = SizeUtils.getDpValue(0f, requireContext()).toInt()
                        } else {
                            outRect.bottom = SizeUtils.getDpValue(-10f, requireContext()).toInt()
                        }
                    } else {
                        outRect.bottom = SizeUtils.getDpValue(-10f, requireContext()).toInt()
                    }
                }
                ConstVariable.TYPE_HOME_RECOMMEND_CLUB -> {
                    outRect.top = SizeUtils.getDpValue(10f, requireContext()).toInt()
                    outRect.bottom = SizeUtils.getDpValue(10f, requireContext()).toInt()
                }
            }
        }
    }

    fun scrollTop() {
        binding.rcHomeList.anchorSmoothScrollToPosition(0, 5)
    }

    override fun onResume() {
        super.onResume()
        val bannerAdapterVH = binding.rcHomeList.findViewHolderForAdapterPosition(0)
        if (bannerAdapterVH is HomeCategoryBannerVH) {
            bannerAdapterVH.startBannerSlider()
        }
    }

    override fun onPause() {
        super.onPause()
        val bannerAdapterVH = binding.rcHomeList.findViewHolderForAdapterPosition(0)
        if (bannerAdapterVH is HomeCategoryBannerVH) {
            bannerAdapterVH.stopBannerSlider()
        }
    }
}