package com.rndeep.fns_fantoo.ui.club.detail.archive

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.data.remote.model.ClubSubCategoryItem
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostData
import com.rndeep.fns_fantoo.data.remote.model.post.PostListData
import com.rndeep.fns_fantoo.databinding.TabClubPageArchiveListBinding
import com.rndeep.fns_fantoo.ui.club.detail.ClubPageArchiveListAdapter
import com.rndeep.fns_fantoo.ui.club.detail.ClubPageFragmentDirections
import com.rndeep.fns_fantoo.ui.club.detail.ClubPageListAdapter
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClubPageArchiveFragment : Fragment() {

    private var _binding: TabClubPageArchiveListBinding? = null
    private val binding get() = _binding!!

    private val archiveViewModel: ClubPageArchiveViewModel by viewModels()

    //backpress
    private lateinit var backPressCallBack: OnBackPressedCallback

    //아카이브 메인 어댑터
    private val clubPageArchiveListAdapter = ClubPageArchiveListAdapter()
    private lateinit var archiveBoardItemDeco: VerticalMarginItemDecoration

    //디테일 상세
    private val clubPageListAdapter = ClubPageListAdapter()
    private lateinit var detailPostItemDeco: RecyclerView.ItemDecoration

    //디테일 이미지 타입
    private val archiveImageAdapter = ArchiveImageAdapter()
    private lateinit var archiveImageItemDeco : RecyclerView.ItemDecoration

    //아카이브 상세 이동 여부
    private var isDetailArchive = false
    private var detailType = "List"

    private val DECOTYPEARCHIVE = 0
    private val DECOTYPEDETAIL = 1
    private val DECOTYPEIMAGE = 2

    private var clubID = ""
    private var categoryCode = ""

    private var detailCategoryCode :String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        archiveBoardItemDeco = VerticalMarginItemDecoration(14f, 1f, requireContext())
        detailPostItemDeco = object :RecyclerView.ItemDecoration(){
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position =parent.getChildAdapterPosition(view)
                if(position==0 || position==-1) return
                val marginSize =SizeUtils.getDpValue(8f,requireContext()).toInt()
                if(isLastItem(position, parent)){
                    outRect.set(0,0,0,0)
                }else{
                    outRect.set(0,0,0,marginSize)
                }
            }
            private fun isLastItem(index: Int, recyclerView: RecyclerView) =
                index == recyclerView.adapter!!.itemCount - 1
        }
        archiveImageItemDeco= object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position =parent.getChildAdapterPosition(view)
                if(position==0 || position==-1) return

                val marginSize =SizeUtils.getDpValue(1.5f,requireContext()).toInt()
                if (isRight(position)){
                    outRect.set(marginSize,marginSize,0,marginSize)
                }else if(isLeft(position)){
                    outRect.set(0,marginSize,marginSize,marginSize)
                }else{
                    outRect.set(marginSize,marginSize,marginSize,marginSize)
                }
            }
            fun isLeft(position : Int):Boolean = position%3==1
            fun isRight(position : Int):Boolean = position%3==0
        }
        clubID= arguments?.getString(ConstVariable.ClubDetail.KEY_DETAIL_CLUB_ID).toString()
        categoryCode= arguments?.getString(ConstVariable.ClubDetail.KEY_CATEGORY_CODE).toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TabClubPageArchiveListBinding.inflate(inflater, container, false)
        initView()
        settingObserve()
        return binding.root
    }

    private fun initView() {
        //상세화면 확인
        if (detailType == "common" && archiveViewModel.currentPostUrl!=null) {
            settingItemDecoration(DECOTYPEDETAIL)
            binding.rcClubPagePostList.adapter = clubPageListAdapter
            archiveViewModel.getArchiveCommonList(archiveViewModel.currentPostUrl!!)
        } else if (detailType== "image" && archiveViewModel.currentPostUrl!=null){
            settingItemDecoration(DECOTYPEIMAGE)
            binding.rcClubPagePostList.adapter = archiveImageAdapter
            archiveViewModel.getArchiveImageList(archiveViewModel.currentPostUrl!!)
        }else {
            settingItemDecoration(DECOTYPEARCHIVE)
            binding.rcClubPagePostList.adapter = clubPageArchiveListAdapter
        }

        clubPageArchiveListAdapter.setOnArchiveClick(object :
            ClubPageArchiveListAdapter.OnArchiveClickListener {
            override fun onArchiveClick(
                v: View,
                postUrl: String,
                boardType: Int,
                categoryName: String
            ) {
                //adapter을 바꿔 끼는 방식으로 할 예정
                changeArchiveView(boardType,postUrl,categoryName)
            }
        })
        archiveViewModel.getArchiveBoardList(clubID,categoryCode)
    }

    private fun settingObserve() {
        //클럽 페이지 아카이브 설정
        archiveViewModel.clubArchivePostLiveData.observe(viewLifecycleOwner) {
            setArchiveItems(it)
        }
        //이미지 타입 아카이브 진입시 받는 데이터
        archiveViewModel.archiveImageLiveData.observe(viewLifecycleOwner) {
            if(it.isEmpty()){
                showNoListView(true)
            }else{
                showNoListView(false)
                settingImageArchive(it)
            }
        }
        //일반 타임 아카이브 진입시 받는 데이터
        archiveViewModel.archiveCommonLiveData.observe(viewLifecycleOwner) {
            if(it.isEmpty()){
                showNoListView(true)
            }else{
                showNoListView(false)
                settingCommonArchive(it)
            }
        }

        archiveViewModel.archiveErrorData.observe(viewLifecycleOwner){
            if(it == "FE3001"){
                showClubOpenView(false)
            }else{
                activity?.showErrorSnackBar(binding.root,it)
            }
        }
    }

    private fun changeArchiveView(boardType :Int,postUrl :String,categoryName : String){
        archiveViewModel.initPostItemForInfo()
        if (boardType == 2) {
            archiveViewModel.getArchiveImageList(postUrl)
            archiveImageAdapter.setArchiveCategoryName(categoryName)
            detailType = "image"
        } else {
            archiveViewModel.getArchiveCommonList(postUrl)
            clubPageListAdapter.setSelectArchiveName(categoryName)
            detailType = "common"
        }
        archiveViewModel.currentPostUrl=postUrl
        isDetailArchive = true
    }

    private fun setArchiveItems(items: List<ClubSubCategoryItem>) {
        showNoListView(items.isEmpty())
        if (items.isNotEmpty()) {
            clubPageArchiveListAdapter.setArchiveList(items)
            for(a in items){
                if(a.categoryCode==detailCategoryCode){
                    changeArchiveView(a.boardType,a.url,a.categoryName)
                    detailCategoryCode=null
                    break
                }
            }
        }
    }

    private fun showNoListView(isShow :Boolean){
        if(isShow){
            binding.rcClubPagePostList.visibility = View.GONE
            binding.llNoArchiveList.visibility = View.VISIBLE
            binding.llNotLoginView.visibility = View.GONE
        }else{
            binding.rcClubPagePostList.visibility = View.VISIBLE
            binding.llNoArchiveList.visibility = View.GONE
            binding.llNotLoginView.visibility = View.GONE
        }
    }

    private fun showClubOpenView(isOpen :Boolean){
        if(isOpen){
            binding.rcClubPagePostList.visibility = View.VISIBLE
            binding.llNoArchiveList.visibility = View.GONE
            binding.llNotLoginView.visibility = View.GONE
        }else{
            binding.rcClubPagePostList.visibility = View.GONE
            binding.llNoArchiveList.visibility = View.GONE
            binding.llNotLoginView.visibility = View.VISIBLE
        }
    }

    private fun settingImageArchive(imageArchiveItems: List<ClubPostData>) {
        archiveImageAdapter.setUrlItem(imageArchiveItems)
        archiveImageAdapter.setOnArchiveNameClickListener(object :ArchiveImageAdapter.ArchiveTopTypeClickListener{
            override fun onListClick() {
                activity?.onBackPressed()
            }
        })
        settingItemDecoration(DECOTYPEIMAGE)
        binding.rcClubPagePostList.adapter = archiveImageAdapter
    }

    private fun settingCommonArchive(commonArchiveItems: List<BoardPagePosts>) {
        clubPageListAdapter.submitList(commonArchiveItems)
        clubPageListAdapter.setListOrFeed(ConstVariable.POST_TYPE_FEED)
        clubPageListAdapter.setViewType(ConstVariable.VIEW_CLUB_ARCHIVE_POST)
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
                //상단 오른쪽 클릭 처리
                activity?.onBackPressed()
            }

            override fun onPostClick(categoryId: String, postId: Int, postType: String, clubId: String?) {
                val action =
                    ClubPageFragmentDirections.actionClubDetailPageFragmentToClubPost(postType,categoryId,clubId?:"-1",postId, -1)
                findNavController().navigate(action)
            }

            override fun onProfileClick(postItem: PostListData) {}
        })
        clubPageListAdapter.setOnArchiveNameClickListener(object :ArchiveImageAdapter.ArchiveTopTypeClickListener{
            override fun onListClick() {
                activity?.onBackPressed()
            }
        })
        settingItemDecoration(DECOTYPEDETAIL)
        binding.rcClubPagePostList.adapter = clubPageListAdapter
    }

    override fun onResume() {
        super.onResume()
        //뒤로가기 설정
        backPressCallBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isDetailArchive) {
                    settingItemDecoration(DECOTYPEARCHIVE)
                    showNoListView(false)
                    binding.rcClubPagePostList.adapter = clubPageArchiveListAdapter
                    detailType = "List"
                    archiveViewModel.currentPostUrl=null
                    isDetailArchive = !isDetailArchive
                } else {
                    CommonDialog.Builder().apply {
                        message=getString(R.string.se_k_leave_from_club)
                        negativeButtonString=getString(R.string.a_no)
                        positiveButtonString=getString(R.string.a_yes)
                        positiveButtonClickListener=object : CommonDialog.ClickListener{
                            override fun onClick() {
                                findNavController().popBackStack()
                            }
                        }
                    }.build().show(parentFragmentManager,"quitDialog")
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressCallBack)
    }

    private fun settingItemDecoration(type: Int) {
        when (type) {
            DECOTYPEARCHIVE -> {
                context?.let {
                    binding.rcClubPagePostList.setBackgroundColor(it.getColor(R.color.bg_light_gray_50))
                    binding.rcClubPagePostList.setPadding(
                        SizeUtils.getDpValue(14f,it).toInt(),0,
                        SizeUtils.getDpValue(14f,it).toInt(),0)
                }
                binding.rcClubPagePostList.layoutManager = LinearLayoutManager(context)
                binding.rcClubPagePostList.removeItemDecoration(detailPostItemDeco)
                binding.rcClubPagePostList.removeItemDecoration(archiveImageItemDeco)
                binding.rcClubPagePostList.addSingleItemDecoRation(archiveBoardItemDeco)
            }
            DECOTYPEDETAIL -> {
                context?.let {
                    binding.rcClubPagePostList.setBackgroundColor(it.getColor(R.color.bg_light_gray_50))
                }
                binding.rcClubPagePostList.setPadding(0,0,0,0)
                binding.rcClubPagePostList.layoutManager = LinearLayoutManager(context)
                binding.rcClubPagePostList.removeItemDecoration(archiveBoardItemDeco)
                binding.rcClubPagePostList.removeItemDecoration(archiveImageItemDeco)
                binding.rcClubPagePostList.addSingleItemDecoRation(detailPostItemDeco)
            }
            DECOTYPEIMAGE -> {
                context?.let {
                    binding.rcClubPagePostList.setBackgroundColor(it.getColor(R.color.gray_25))
                }
                binding.rcClubPagePostList.setPadding(0,0,0,0)
                binding.rcClubPagePostList.layoutManager = GridLayoutManager(context, 3).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when (position) {
                                0 -> 3
                                else -> 1
                            }
                        }
                    }
                }
                binding.rcClubPagePostList.removeItemDecoration(archiveBoardItemDeco)
                binding.rcClubPagePostList.removeItemDecoration(detailPostItemDeco)
                binding.rcClubPagePostList.addSingleItemDecoRation(archiveImageItemDeco)
            }
        }
    }

    fun setDetailCategoryCode(code: String){
        this.detailCategoryCode =code
    }

    override fun onPause() {
        super.onPause()
        backPressCallBack.remove()
        archiveViewModel.initPostItemForInfo()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}