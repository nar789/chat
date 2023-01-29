package com.rndeep.fns_fantoo.ui.home.tabpopular

import android.content.Intent
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentPopularBinding
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.ui.common.recommendclub.RecommendClubListAdapter
import com.rndeep.fns_fantoo.data.local.model.CurationDataItem
import com.rndeep.fns_fantoo.data.local.model.TrendTagItem
import com.rndeep.fns_fantoo.data.remote.model.Country
import com.rndeep.fns_fantoo.data.remote.model.post.BoardPostData
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostData
import com.rndeep.fns_fantoo.data.remote.model.post.PostListData
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.ui.common.LanguageBottomSheet
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.ui.home.HomeTabFragmentDirections
import com.rndeep.fns_fantoo.utils.*
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetAdapter
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.CustomBottomSheet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PopularFragment : BaseFragment<FragmentPopularBinding>(FragmentPopularBinding::inflate) {

    private val popularViewModel: PopularViewModel by viewModels()

    private val popularRcAdapter = CategoryPopularAdapter()

    private var currentLast = false

    private val popularItemDeco: RecyclerView.ItemDecoration

    init {
        popularItemDeco = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val itemPosition = parent.getChildAdapterPosition(view)
                if (itemPosition == RecyclerView.NO_POSITION) return
                when (popularRcAdapter.currentList[itemPosition].type) {
                    ConstVariable.TYPE_CLUB, ConstVariable.TYPE_COMMUNITY, ConstVariable.TYPE_AD -> {
                        if (popularRcAdapter.itemCount != itemPosition + 1) {
                            if (popularRcAdapter.currentList[itemPosition + 1].type == ConstVariable.TYPE_POPULAR_CURATION) {
                                outRect.bottom = SizeUtils.getDpValue(0f, requireContext()).toInt()
                            } else if (popularRcAdapter.currentList[itemPosition + 1].type == ConstVariable.TYPE_HOME_RECOMMEND_CLUB) {
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
                    ConstVariable.TYPE_POPULAR_CURATION -> {
                        outRect.top = SizeUtils.getDpValue(10f, requireContext()).toInt()
                        outRect.bottom = SizeUtils.getDpValue(10f, requireContext()).toInt()
                    }
                }
            }
        }
    }

    override fun initUi() {
        binding.rcPopularList.layoutManager = LinearLayoutManager(context)
        binding.rcPopularList.addSingleItemDecoRation(popularItemDeco)
        binding.rcPopularList.adapter = popularRcAdapter

        settingObserve()
        if (popularRcAdapter.currentList.isEmpty()) {
            initPopularCategoryAdapter()
            popularViewModel.changeLoadingState(true)
            popularViewModel.getInitPopularData(requireContext())
        } else {
            popularViewModel.getAllItem()
        }
    }

    override fun initUiActionEvent() {

        binding.rcPopularList.checkLastItemVisible {
            if (popularRcAdapter.itemCount > 4) {
                if (it) {
                    if (!currentLast) {
                        currentLast = true
                        popularViewModel.changeLoadingState(true)
                        popularViewModel.getAddPopularData(1, requireContext())
                    }
                } else {
                    currentLast = false
                }
            }

        }

        //상단 드래그를 통한 새로고침
        binding.swipeRefresh.setOnRefreshListener {
            popularViewModel.getInitPopularData(requireContext())
        }

        popularRcAdapter.setOnRecommendClubClickListener(object :
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


        binding.rcPopularList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })

    }

    fun settingObserve() {
        popularViewModel.loadingStateLiveData.observe(viewLifecycleOwner) {
            binding.fantooLoadingView.visibility = if (it) View.VISIBLE else View.GONE
        }
        //popular trend item
        popularViewModel.trendItemListData.observe(viewLifecycleOwner) {
            popularRcAdapter.setTrendItems(transformTrendingData(it))
            popularRcAdapter.notifyItemChanged(0)
        }

        //popular item observer
        popularViewModel.popularItemLiveData.observe(viewLifecycleOwner) {
            popularRcAdapter.submitList(it)
            popularViewModel.changeLoadingState(false)
            binding.swipeRefresh.isRefreshing = false
        }

        //에러 데이터
        popularViewModel.errorDataLiveData.observe(this) {
            activity?.showErrorSnackBar(binding.root, it.toString())
        }

        //큐레이션 리스트
        popularViewModel.popularCurationLiveData.observe(viewLifecycleOwner) {
            if (it.isEmpty()) return@observe
            val tempList = ArrayList<CurationDataItem>()
            for ((index, a) in it.withIndex()) {
                tempList.add(a)
            }
            popularRcAdapter.setCurationItems(tempList)
        }

        //popular 추천 클럽 리스트
        popularViewModel.popularRecommendClubLiveData.observe(viewLifecycleOwner) {
            if (it.isEmpty()) return@observe
            it[0].title = getString(R.string.a_recomment_poplular_club)
            it[0].subText = getString(R.string.se_a_recommend_hottest_club)
            popularRcAdapter.setRecommendItem(it[0])

        }

        //좋아요 결과
        popularViewModel.likeDislikeResultLiveData.observe(this) {
            popularRcAdapter.currentList[it.third].boardPostItem =
                it.second[it.third].boardPostItem!!
            popularRcAdapter.notifyItemChanged(it.third, ConstVariable.PAYLOAD_LIKE_CLICK)
        }

        //아너 결과
        popularViewModel.honorResultLiveData.observe(this) {
            popularRcAdapter.currentList[it.second].boardPostItem =
                it.first[it.second].boardPostItem!!
            popularRcAdapter.notifyItemChanged(it.second, ConstVariable.PAYLOAD_HONOR_CLICK)
        }

        //에러 코드
        popularViewModel.errorMessageLiveData.observe(this) {
            activity?.showErrorSnackBar(binding.root, it.toString())
        }

        //저장하기
        popularViewModel.savePostResult.observe(viewLifecycleOwner) {
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
        popularViewModel.savePostSharePostResult.observe(viewLifecycleOwner) {
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
        popularViewModel.savePostJoinClubResult.observe(viewLifecycleOwner) {
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
        popularViewModel.savePostReportResult.observe(viewLifecycleOwner) {
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
        popularViewModel.pieceBlockResultLiveData.observe(this) {
            popularRcAdapter.currentList[it.second].boardPostItem =
                it.first[it.second].boardPostItem!!
            popularRcAdapter.notifyItemChanged(it.second)
        }

        //이 사용자 차단하기
        popularViewModel.accountBlockResultLiveData.observe(this) {
            popularRcAdapter.submitList(it)
        }

    }

    // popularCategory Adapter 초기화 및 셋팅
    private fun initPopularCategoryAdapter() {
        // 옵션 선택
        popularRcAdapter.setOnOptionsClickListener(object :
            BoardListeners.OnBoardPostClickListener {
            override fun onLikeClick(
                dbId: Int,
                postItem: PostListData,
                postType: String,
                changePos: Int,
                holder: RecyclerView.ViewHolder
            ) {
                when(postItem){
                    is BoardPostData -> {
                        popularViewModel.requestLikeUpdate(
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
                when(postItem){
                    is BoardPostData->{
                        popularViewModel.requestLikeUpdate(
                            postItem.postId.toString(),
                            ConstVariable.DISLIKE_CLICK,
                            changePos,
                            postType,
                            dbId,
                            postItem.dislikeYn == true
                        )
                    }
                    is ClubPostData->{
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
//                popularViewModel.requestHonorUpdate(
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
                //상단 오른쪽 클릭 처리
                activity?.showBottomSheetOfPost(
                    postAuthId == popularViewModel.uId,
                    popularViewModel.isUser(),
                    postType,
                    getBottomSheetClickListener(dbId, changePos, ConstVariable.DB_POPULARCATEGORY),
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
                                postType,
                                categoryId,
                                postId,
                            )
                        findNavController().navigate(action)
                    }
                    ConstVariable.TYPE_CLUB -> {
                        val action =
                            HomeTabFragmentDirections.actionHomeTabFragmentToClubPost(
                                postType,
                                categoryId,
                                clubId?:"-1",
                                postId,

                            )
                        findNavController().navigate(action)
                    }
                }
            }

            override fun onProfileClick(postItem: PostListData) {}

        })

        //trend 부분 선택
        popularRcAdapter.setOnTrendClickListener(object :
            CategoryPopularAdapter.OnTrendClickListener {
            override fun onGlobalClick(v: View) {
                activity?.let {
                    val bottomSheet = CustomBottomSheet()
                    bottomSheet.setTitleText(getString(R.string.a_language_filter))
                    bottomSheet.setBottomItems(popularViewModel.getFilterItem(resources))
                    bottomSheet.setOnCliCkListener(object : BottomSheetAdapter.OnItemClickListener {
                        override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                            when (name) {
                                resources.getString(R.string.en_global) -> {
                                    //api 통신 작성
                                    popularViewModel.setFilterSelectPos(pos)
//                                    popularViewModel.getInitPopularData()
                                }
                                resources.getString(R.string.n_setting_to_my_language) -> {
                                    //api 통신 작성
                                    popularViewModel.setFilterSelectPos(pos)
//                                    popularViewModel.getInitPopularData()

                                }
                                resources.getString(R.string.d_other_language_select)+" "+popularViewModel.selectOtherLang -> {
                                    //api 통신 작성
                                    LanguageBottomSheet().apply {
                                        itemClickListener=object :LanguageBottomSheet.ItemClickListener{
                                            override fun onItemClick(item: Country) {
                                                popularViewModel.selectOtherLang=item.nameEn
                                                dismiss()
                                            }
                                        }
                                    }.show(parentFragmentManager,"langSheet")
                                    popularViewModel.setFilterSelectPos(pos)
//                                    popularViewModel.getInitPopularData()

                                }
                            }
                            bottomSheet.dismiss()
                        }
                    })
                    bottomSheet.show(it.supportFragmentManager, "Tag")
                }
            }
        })

    }

    fun getBottomSheetClickListener(pk_id: Int, postPos: Int, type: String) =
        object : BottomSheetAdapter.OnItemClickListener {
            override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                when (name) {
//                    getString(R.string.j_to_save) -> {
//                        //api 통신 작성
//                        popularViewModel.requestSavePost(pk_id, type)
//                    }
//                    getString(R.string.g_to_share) -> {
//                        //api 통신 작성
//                        popularViewModel.requestSharePost(pk_id, type)
//                    }
//                    getString(R.string.g_to_join) -> {
//                        //api 통신 작성
//                        popularViewModel.requestJoinClub(pk_id, type)
//                    }
//                    getString(R.string.s_to_report) -> {
//                        //api 통신 작성
//                        popularViewModel.requestReportPost(pk_id, type)
//                    }
//                    getString(R.string.g_hide_post), getString(R.string.g_unhide_post) -> {
//                        //api 통신 작성
//                        when (type) {
//                            ConstVariable.DB_POPULARCATEGORY -> {
//                                popularViewModel.requestBlockPiecePost(pk_id, postPos)
//                            }
//                        }
//                    }
//                    getString(R.string.a_block_this_user), getString(R.string.a_unblock_this_user) -> {
//                        //api 통신 작성
//                        when (type) {
//                            ConstVariable.DB_POPULARCATEGORY -> {
//                                popularViewModel.requestBlockPostUser(pk_id)
//                            }
//                        }
//                    }
                }

            }
        }

    fun scrollTop() {
        binding.rcPopularList.anchorSmoothScrollToPosition(0, 5)
    }

    //trending 아이템 나누기
    private fun transformTrendingData(trendList: List<TrendTagItem>): ArrayList<ArrayList<TrendTagItem>> {
        //기기 전체 넓이
        val deviceWidth = SizeUtils.getDeviceSize(requireContext()).width
        //기기 양쪽 사이드 Margin
        val firstGap = SizeUtils.getDpValue(40f, requireContext())
        //각 아이템의 margin
        val sideGap = SizeUtils.getDpValue(12f, requireContext())
        //각 아이템의 padding
        val paddingHorizontal = SizeUtils.getDpValue(20f, requireContext())
        //전체 아이템 사이즈 ( 기기 가로사이즈를 넘어갈시 다음줄로 판단)
        var totalSize = 0f
        //최대 라인 값
        val maxLineCount = 3
        //라인 카운트 (최대 라인 (현재 3줄) 이 될시 아이템 insert )
        var lineCount = 0
        //전체 아이템 변수 (최대 라인(3줄)의 값이 배열로 입력됨)
        val totalTrendItem = arrayListOf<ArrayList<TrendTagItem>>()
        //각 한줄에 해당하는 아이템 변수
        var lineItem = arrayListOf<TrendTagItem>()
        //글자 font style 이 적용된 paint ( 글자 길이에 맞는 사이즈를 측정하기 위해 사용)
        val calculationPaint = Paint()
        calculationPaint.typeface = ResourcesCompat.getFont(requireContext(), R.font.noto_sans_kr)
        calculationPaint.textSize = SizeUtils.getSpValue(14f, requireContext())
        for ((index, a) in trendList.withIndex()) {
            //각 글자에 맞는 길이를 갖는 변수
            val textCalculation =
                calculationPaint.measureText("#${a.trendTagName}", 0, "#${a.trendTagName}".length)
            //각 줄의 첫 아이템에는 양쪽 margin 값을 추가
            if (lineItem.size == 0) {
                totalSize = firstGap
            }
            //각 아이템 margin + padding + textLength 를 합친 값
            totalSize += (textCalculation + paddingHorizontal)
            totalSize += sideGap
            //trend item 의 마지막 값일시 전체 아이템 변수에 insert
            if (index == trendList.size - 1) {
                totalTrendItem.add(lineItem)
            }
            // 각 줄의 길이의 합산이 기기의 width 값을 넘을시 다음 줄로 넘김
            if (totalSize > deviceWidth) {
                lineCount++
                //각 줄의 배열이 maxLine 를 넘길시 라인 count 값 초기화 및 전체 아이템에 배열 넣기
                if (lineCount == maxLineCount) {
                    lineCount = 0
                    totalTrendItem.add(lineItem)
                    lineItem = arrayListOf()
                }
                lineItem.add(a)
                totalSize = firstGap
                totalSize += (textCalculation + paddingHorizontal)
                totalSize += sideGap
            } else {
                lineItem.add(a)
            }
        }
        return totalTrendItem
    }

}