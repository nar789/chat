package com.rndeep.fns_fantoo.ui.club.detail.freeboard

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
import com.rndeep.fns_fantoo.databinding.TabClubPageFreeboardListBinding
import com.rndeep.fns_fantoo.ui.club.detail.ClubPageListAdapter
import com.rndeep.fns_fantoo.ui.club.ClubSortListAdapter
import com.rndeep.fns_fantoo.data.remote.model.post.PostListData
import com.rndeep.fns_fantoo.ui.club.detail.ClubPageFragmentDirections
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetAdapter
import com.rndeep.fns_fantoo.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ClubPageFreeBoardFragment : Fragment() {

    private var _binding: TabClubPageFreeboardListBinding? = null
    private val binding get() = _binding!!

    private val freeBoardViewModel: ClubPageFreeBoardViewModel by viewModels()

    //게시글 정보
    private val clubPageListAdapter = ClubPageListAdapter()
    private lateinit var verticalItemDeco: VerticalMarginItemDecoration

    //카테고리 리스트 어댑터
    private val sortAdapter = ClubSortListAdapter()
    private lateinit var itemDeco: HorizontalMarginItemDecoration

    //클럽 가입 여부
    private var isClubOpen = true

    private var clubID = ""
    private var categoryCode = ""

    private var isLoading = true

    private var detailCategoryCode :String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clubID= arguments?.getString(ConstVariable.ClubDetail.KEY_DETAIL_CLUB_ID).toString()
        categoryCode= arguments?.getString(ConstVariable.ClubDetail.KEY_CATEGORY_CODE).toString()

        itemDeco = HorizontalMarginItemDecoration(20f, 5f, requireContext())
        verticalItemDeco = VerticalMarginItemDecoration(0f, 4f, requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = TabClubPageFreeboardListBinding.inflate(inflater, container, false)

        initView()
        settingObserve()
        if(isClubOpen){
            freeBoardViewModel.getFreeBoardCategoryList(clubID,categoryCode,detailCategoryCode)
        }
        return binding.root
    }

    fun initView() {

        //게시글의 보여주는 타입
        clubPageListAdapter.setListOrFeed(ConstVariable.POST_TYPE_LIST)
        clubPageListAdapter.setViewType(ConstVariable.VIEW_CLUB_FREE_BOARD)

        binding.rcClubPagePostList.layoutManager = LinearLayoutManager(context)
        binding.rcClubPagePostList.addSingleItemDecoRation(verticalItemDeco)
        binding.rcClubPagePostList.adapter = clubPageListAdapter

        binding.rcClubPagePostList.checkLastItemVisible {
            if(it && !isLoading && isClubOpen && clubPageListAdapter.itemCount>8){
                freeBoardViewModel.addClubFreeBoardPostItem()
                isLoading=true
            }
        }

        //게시글 선택 어댑터
        clubPageListAdapter.setOnOptionsClickListener(object : BoardListeners.OnBoardPostClickListener {
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
                    postAuthId==freeBoardViewModel.uId,
                    freeBoardViewModel.isUser(),
                    postType,
                    getBottomSheetClickListener(dbId, ConstVariable.DB_CLUB_DETAIL_FREEBOARD),
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
                    ClubPageFragmentDirections.actionClubDetailPageFragmentToClubPost(postType,categoryId,clubId?:"-1",postId,-1
                    )
                )
            }

            override fun onProfileClick(postItem: PostListData) {}
        })

    }

    fun settingObserve() {
        freeBoardViewModel.clubFreeBoardErrorCodeLiveData.observe(viewLifecycleOwner){
            if(it == "FE3001"){
                setClubJoinView(false)
            }else{
                activity?.showErrorSnackBar(binding.root,it)
            }
        }

        //카테고리 정보
        freeBoardViewModel.freeBoardCategoryLiveData.observe(viewLifecycleOwner){
            val state =binding.rcFilterCategory.layoutManager?.onSaveInstanceState()
            sortAdapter.submitList(it){
                binding.rcFilterCategory.layoutManager?.onRestoreInstanceState(state)
                if(detailCategoryCode!=null){
                    for( a in it.indices){
                        if(detailCategoryCode == it[a].categoryCode){
                            sortAdapter.setClickPos(a)
                            detailCategoryCode=null
                            break
                        }
                    }
                }
            }
            settingCategory()
        }

        freeBoardViewModel.detailClubFreeBoardPostItems.observe(viewLifecycleOwner) {
            if(it.isEmpty() ){
                binding.llNotPostData.visibility=View.VISIBLE
            }else{
                binding.llNotPostData.visibility=View.GONE
            }
            binding.llNotLoginView.visibility=View.GONE
            val state =binding.rcClubPagePostList.layoutManager?.onSaveInstanceState()
            clubPageListAdapter.submitList(it){
                binding.rcClubPagePostList.layoutManager?.onRestoreInstanceState(state)
                isLoading=false
            }
        }
    }

    //카테고리 셋팅
    private fun settingCategory() {
        binding.rcFilterCategory.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rcFilterCategory.addSingleItemDecoRation(itemDeco)
        binding.rcFilterCategory.adapter = sortAdapter

        sortAdapter.setOnFreeBoardCategoryClickListener(object :ClubSortListAdapter.FreeBoardCategoryClickListener{
            override fun onCategoryClick(url: String, pos: Int, categoryCode: String) {
                freeBoardViewModel.initNextId()
                freeBoardViewModel.getClubDetailFreeBoardPostItem(url)
                detailCategoryCode=categoryCode
            }
        })
    }

    fun getBottomSheetClickListener(pk_id: Int, type: String) =
        object : BottomSheetAdapter.OnItemClickListener {
            override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
//                when (name) {
//                    getString(R.string.j_to_save) -> {
//                        //api 통신 작성
//                        freeBoardViewModel.requestSavePost(pk_id, type)
//                    }
//                    getString(R.string.g_to_share) -> {
//                        //api 통신 작성
//                        freeBoardViewModel.requestSharePost(pk_id, type)
//                    }
//                    getString(R.string.g_to_join) -> {
//                        //api 통신 작성
//                        freeBoardViewModel.requestJoinClub(pk_id, type)
//                    }
//                    getString(R.string.s_to_report) -> {
//                        //api 통신 작성
//                        freeBoardViewModel.requestReportPost(pk_id, type)
//                    }
//                    getString(R.string.g_hide_post), getString(R.string.g_unhide_post) -> {
//                    }
//                    getString(R.string.a_block_this_user), getString(R.string.a_unblock_this_user) -> {
//                        //api 통신 작성
//                    }
//                }
            }
        }

    fun setClubJoinView(isJoin: Boolean){
        if (isJoin) {
            binding.nestWrap.visibility = View.VISIBLE
            binding.rcClubPagePostList.visibility = View.VISIBLE
            binding.llNotPostData.visibility=View.GONE
            binding.llNotLoginView.visibility = View.GONE
        } else {
            binding.nestWrap.visibility = View.GONE
            binding.rcClubPagePostList.visibility = View.GONE
            binding.llNotPostData.visibility=View.GONE
            binding.llNotLoginView.visibility = View.VISIBLE

        }
    }

    fun setDetailCategoryCode(code :String){
        detailCategoryCode=code
    }

    override fun onPause() {
        super.onPause()
        freeBoardViewModel.initNextId()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}