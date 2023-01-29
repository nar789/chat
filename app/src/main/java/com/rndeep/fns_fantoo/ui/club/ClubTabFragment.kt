package com.rndeep.fns_fantoo.ui.club

import android.content.Intent
import android.graphics.Rect
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.post.PostListData
import com.rndeep.fns_fantoo.databinding.FragmentClubTabBinding
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetAdapter
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.ui.common.recommendclub.RecommendClubListAdapter
import com.rndeep.fns_fantoo.ui.common.recommendclub.RecommendClubSortAdapter
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class ClubTabFragment : BaseFragment<FragmentClubTabBinding>(FragmentClubTabBinding::inflate) {

    private val clubViewModel: ClubTabViewModel by viewModels()

    //ClubCategory variable
    private val clubRcAdapter = ClubTabAdapter()

    override fun initUi() {
        activity?.let {
            //클럽 디테일 페이지에서 상단바 투명일시 되돌리는 역할
            activity?.window?.statusBarColor = it.getColor(R.color.gray_25)
            it.setDarkStatusBarIcon()
        }
        initToolbar()
        settingObserver()
        initView()
        if (clubViewModel.clubPostListLiveData.value == null) {
            clubViewModel.changeLoadingState(true)
        }
        clubViewModel.getInitClubData()
    }

    override fun initUiActionEvent() {
        clubRcAdapter.setOnOptionsClickListener(object : BoardListeners.OnBoardPostClickListener {
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
                //상단 오른쪽 클릭 처리
//                activity?.showBottomSheetOfPost(
//                    postAuthId == clubViewModel.uId,
//                    clubViewModel.isUser(),
//                    postType,
//                    getBottomSheetClickListener(dbId, ConstVariable.DB_CLUBPAGE),
//                    isPieceBlockYn,
//                    isUserBlockYN
//                )
            }

            override fun onPostClick(
                categoryId: String,
                postId: Int,
                postType: String,
                clubId: String?
            ) {
                val action =
                    ClubTabFragmentDirections.actionClubTabFragmentToClubPost(
                        previewType = postType,
                        categoryCode = categoryId,
                        clubId= clubId?:"-1",
                        postId = postId,
                    )
                findNavController().navigate(action)
            }

            override fun onProfileClick(postItem: PostListData) {}
        })

        //추천 클럽 카테고리 클릭
        clubRcAdapter.setOnCategoryClickListener(object :RecommendClubSortAdapter.RecommendCategoryClickListener{
            override fun onCategoryClick(categoryCode: String) {
                clubViewModel.categoryHotClub(categoryCode)
            }
        })


        //추천 클럽 클릭 리스너
        clubRcAdapter.setOnRecommendClubClickListener(object :
            RecommendClubListAdapter.OnRecommendClubClickListener {
            override fun onClubClick(v: View, id: Int, name: String) {
                try {
                    val action =
                        ClubTabFragmentDirections.actionClubTabFragmentToClubPageDetail(id.toString())
                    findNavController().navigate(action)
                }catch (e: Exception){
                    Timber.e("${e.printStackTrace()}")
                }
            }

            override fun onJoinClick(v: View, id: Int, name: String) {
                //클럽 가입
                if(clubViewModel.isUser()){
                    val action =
                        ClubTabFragmentDirections.actionClubTabFragmentToClubJoinFragment(id.toString())
                    findNavController().navigate(action)
                }else{
                    activity?.showErrorSnackBar(binding.root,ConstVariable.ERROR_NOT_MEMBER)
                }
            }
        })

        //새로고침
        binding.swipeRefresh.setOnRefreshListener {
            clubViewModel.getInitClubData()
        }
    }

    private fun initToolbar() {
        binding.clubToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_club_search -> {
                    findNavController().navigate(R.id.action_clubTabFragment_to_clubSearchFragment)
                    true
                }
                R.id.menu_club_create -> {
                    if (!clubViewModel.isUser()) {
                        activity?.showErrorSnackBar(binding.root, ConstVariable.ERROR_NOT_MEMBER)
                        return@setOnMenuItemClickListener true
                    }
                    findNavController().navigate(R.id.action_clubTabFragment_to_clubCreateFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun settingObserver() {
        clubViewModel.loadingStateLiveData.observe(viewLifecycleOwner) {
            binding.fantooLoadingView.visibility = if (it) View.VISIBLE else View.GONE
        }
        //내 클럽
        clubViewModel.myClubListLiveData.observe(viewLifecycleOwner) {
            clubRcAdapter.setMyClubItem(it)
        }
        //챌린지
        clubViewModel.challengeItemLiveData.observe(viewLifecycleOwner) {
            clubRcAdapter.setChallengeItem(it)
        }
        //인기 카테고리
        clubViewModel.clubRecommendCategoryList.observe(viewLifecycleOwner){
            clubRcAdapter.setHotClubCategory(it)
        }
        //핫 인기
        clubViewModel.hotRecommendClubLiveData.observe(viewLifecycleOwner) {
            it.title = getString(R.string.a_recomment_poplular_club)
            it.subText = getString(R.string.se_a_recommend_hottest_club)
            clubRcAdapter.setHotRecommendItem(it)
        }
        //신규 클럽
        clubViewModel.newRecommendClubLiveData.observe(viewLifecycleOwner) {
            it.title = getString(R.string.s_recommend_new_club)
            it.subText = getString(R.string.se_n_find_new_club_for_me)
            clubRcAdapter.setNewRecommendItem(it)
        }
        clubViewModel.clubPostTimeData.observe(viewLifecycleOwner){
            clubRcAdapter.setPostTime(it)
        }
        //전체 리스트
        clubViewModel.clubPostListLiveData.observe(viewLifecycleOwner) {
            clubViewModel.changeLoadingState(false)
            val state = binding.rcClub.layoutManager?.onSaveInstanceState()
            clubRcAdapter.submitList(it) {
                binding.rcClub.layoutManager?.onRestoreInstanceState(state)
                binding.swipeRefresh.isRefreshing = false
            }
        }

        //저장하기
        clubViewModel.savePostResult.observe(viewLifecycleOwner) {
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
        clubViewModel.savePostSharePostResult.observe(viewLifecycleOwner) {
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
        clubViewModel.savePostJoinClubResult.observe(viewLifecycleOwner) {
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
        clubViewModel.savePostReportResult.observe(viewLifecycleOwner) {
            if (it) {
                val customDialog = CommonDialog.Builder().apply {
                    title = getString(R.string.g_post_with_arg, "아이유 안티")
                    message = getString(R.string.se_s_report_complete)
                    setPositiveButtonText(getString(R.string.h_confirm))
                }
                customDialog.build().show(this.parentFragmentManager, "report")
            }
        }
    }

    private fun initView() {
        if (binding.rcClub.itemAnimator is SimpleItemAnimator) {
            (binding.rcClub.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                false
            binding.rcClub.itemAnimator = null
        }

        binding.rcClub.layoutManager = LinearLayoutManager(context)
        binding.rcClub.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val itemPosition = parent.getChildAdapterPosition(view)
                if (itemPosition == RecyclerView.NO_POSITION) return
                if (clubRcAdapter.currentList[itemPosition].type == ConstVariable.TYPE_CLUB || clubRcAdapter.currentList[itemPosition].type == ConstVariable.TYPE_COMMUNITY
                    || clubRcAdapter.currentList[itemPosition].type == ConstVariable.TYPE_AD || clubRcAdapter.currentList[itemPosition].type == ConstVariable.TYPE_CLUB_TOP_10
                ) {
                    outRect.bottom = SizeUtils.getDpValue(1f, requireContext()).toInt()
                }
                if (clubRcAdapter.currentList[itemPosition].type == ConstVariable.TYPE_HOME_RECOMMEND_CLUB) {
                    outRect.top = SizeUtils.getDpValue(22f, requireContext()).toInt()
                }
            }
        })
        binding.rcClub.adapter = clubRcAdapter
        val animator = binding.rcClub.itemAnimator
        if (animator is SimpleItemAnimator) {
            (animator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }

    fun getBottomSheetClickListener(pk_id: Int, type: String) =
        object : BottomSheetAdapter.OnItemClickListener {
            override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
//                when (name) {
//                    getString(R.string.j_to_save) -> {
//                        //api 통신 작성
//                        clubViewModel.requestSavePost(pk_id, type)
//                    }
//                    getString(R.string.g_to_share) -> {
//                        //api 통신 작성
//                        clubViewModel.requestSharePost(pk_id, type)
//                    }
//                    getString(R.string.g_to_join) -> {
//                        //api 통신 작성
//                        clubViewModel.requestJoinClub(pk_id, type)
//                    }
//                    getString(R.string.s_to_report) -> {
//                        //api 통신 작성
//                        clubViewModel.requestReportPost(pk_id, type)
//                    }
//                    getString(R.string.g_hide_post), getString(R.string.g_unhide_post) -> {
//                        //api 통신 작성
//                    }
//                    getString(R.string.a_block_this_user), getString(R.string.a_unblock_this_user) -> {
//                        //api 통신 작성
//                    }
//                }
            }
        }

    fun scrollTop() {
        binding.rcClub.anchorSmoothScrollToPosition(0, 3)
    }


}