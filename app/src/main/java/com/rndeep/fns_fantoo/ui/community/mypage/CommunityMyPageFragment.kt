package com.rndeep.fns_fantoo.ui.community.mypage

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.databinding.FragmentCommunityMyPageBinding
import com.rndeep.fns_fantoo.data.remote.model.post.PostListData
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.ui.community.BoardInfo
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.showCustomSnackBar
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CommunityMyPageFragment :
    BaseFragment<FragmentCommunityMyPageBinding>(FragmentCommunityMyPageBinding::inflate) {
    private val myPageViewModel: CommunityMyPageViewModel by viewModels()

    private lateinit var tabTextList: List<String>

    //페이지 어댑터들
    private val myWritePostAdapter = MyWritePostAdapter()
    private val myCommentAdapter = MyCommentAdapter()
    private val mySavePostAdapter = MySavePostAdapter()

    //viewpager 어댑터
    private val viewpagerAdapter = MyPageAdapter()

    private var refreshView : SwipeRefreshLayout? = null

    private var changeAlarm = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabTextList = listOf(
            getString(R.string.j_wrote_post),
            getString(R.string.j_wrote_rely),
            getString(R.string.j_save),
        )
    }

    override fun initUi() {
        initToolbar()
        initView()
        settingObserve()

        myPageViewModel.getComAlarmState()
        myPageViewModel.getMyPostData("init","10")
        myPageViewModel.getBookmarkPostData("init","10")
        myPageViewModel.getMyCommentData("init","10")
    }

    override fun initUiActionEvent() {
        clickFunc()

        viewpagerAdapter.setOnRefreshItem(object : MyPageAdapter.OnRefreshItem {
            override fun onRefresh(v: SwipeRefreshLayout,currentAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
                when(currentAdapter){
                    is MyWritePostAdapter ->{
                        myPageViewModel.getMyPostData("init","10")
                    }
                    is MyCommentAdapter ->{
                        myPageViewModel.getMyCommentData("init","10")
                    }
                    is MySavePostAdapter ->{
                        myPageViewModel.getBookmarkPostData("init","10")
                    }
                }
                refreshView=v
            }
        })

        viewpagerAdapter.setOnScrollBottomDetectListener(object :MyPageAdapter.OnScrollBottomDetectListener{
            override fun isScrollBottom(
                v: View,
                currentAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
            ) {
               when(currentAdapter){
                   is MyWritePostAdapter ->{
                       myPageViewModel.getMyPostData("add","10")
                   }
                   is MyCommentAdapter ->{
                       myPageViewModel.getMyCommentData("add","10")
                   }
                   is MySavePostAdapter ->{
                       myPageViewModel.getBookmarkPostData("add","10")
                   }
               }
            }
        })


    }

    private fun initToolbar() {
        binding.myCommunityToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.myCommunityToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_my_alarm -> {
                    changeAlarm=true
                    myPageViewModel.settingAlarm()
                    true
                }
                else -> false
            }
        }
    }

    private fun initView() {
        binding.vpCommunityMyPage.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewpagerAdapter.setAdapterList(
            arrayListOf(
                myWritePostAdapter,
                myCommentAdapter,
                mySavePostAdapter
            )
        )
        binding.vpCommunityMyPage.adapter = viewpagerAdapter
        //tablayout 과 viewpager 연결
        TabLayoutMediator(
            binding.myPageTabLayout,
            binding.vpCommunityMyPage
        ) { tab, position -> tab.text = tabTextList[position] }.attach()
    }

    private fun clickFunc() {
        myWriteAdapterClickListener()

        mySavePostAdapterClick()

        myCommentAdapterClick()
    }

    private fun myWriteAdapterClickListener(){
        myWritePostAdapter.setOnOptionsClickListener(object :
            BoardListeners.OnBoardPostClickListener {
            override fun onLikeClick(
                dbId: Int,
                postItem: PostListData,
                postType: String,
                changePos: Int,
                holder: RecyclerView.ViewHolder
            ) {}

            override fun onDisLikeClick(
                dbId: Int,
                postItem: PostListData,
                postType: String,
                changePos: Int,
                holder: RecyclerView.ViewHolder
            ) {}

            override fun onHonorClick(
                dbId: Int,
                postItem: PostListData,
                postType: String,
                changePos: Int,
                holder: RecyclerView.ViewHolder
            ) {}

            override fun onOptionClick(
                dbId: Int,
                postAuthId: String,
                postType: String,
                changePos: Int,
                postId: Int,
                isPieceBlockYn: Boolean?,
                isUserBlockYN: Boolean?,
                code: String
            ) {}

            override fun onPostClick(
                categoryId: String,
                postId: Int,
                postType: String,
                clubId: String?
            ) {
                findNavController().navigate(
                    CommunityMyPageFragmentDirections.actionCommunityMyPageFragmentToCommunitypost(
                        postType,categoryId,postId,-1
                    )
                )
            }

            override fun onProfileClick(title: String, boardCode: String) {
                findNavController().navigate(
                    CommunityMyPageFragmentDirections.actionCommunityMyPageFragmentToCommunityboard(
                        BoardInfo(title, boardCode, ConstVariable.BOARD_COMMON_TYPE)
                    )
                )
            }
        })
    }

    private fun mySavePostAdapterClick(){
        mySavePostAdapter.setOnOptionsClickListener(object :BoardListeners.OnBoardPostClickListener{
            override fun onLikeClick(
                dbId: Int,
                postItem: PostListData,
                postType: String,
                changePos: Int,
                holder: RecyclerView.ViewHolder
            ) {}

            override fun onDisLikeClick(
                dbId: Int,
                postItem: PostListData,
                postType: String,
                changePos: Int,
                holder: RecyclerView.ViewHolder
            ) {}

            override fun onHonorClick(
                dbId: Int,
                postItem: PostListData,
                postType: String,
                changePos: Int,
                holder: RecyclerView.ViewHolder
            ) {}

            override fun onOptionClick(
                dbId: Int,
                postAuthId: String,
                postType: String,
                changePos: Int,
                postId: Int,
                isPieceBlockYn: Boolean?,
                isUserBlockYN: Boolean?,
                code: String
            ) {}

            override fun onPostClick(
                categoryId: String,
                postId: Int,
                postType: String,
                clubId: String?
            ) {
                findNavController().navigate(
                    CommunityMyPageFragmentDirections.actionCommunityMyPageFragmentToCommunitypost(
                        postType,categoryId,postId,-1
                    )
                )
            }

            override fun onProfileClick(title: String, boardCode: String){
                findNavController().navigate(
                    CommunityMyPageFragmentDirections.actionCommunityMyPageFragmentToCommunityboard(
                        BoardInfo(title,boardCode,ConstVariable.BOARD_COMMON_TYPE)
                    )
                )
            }
        })
    }

    //작성글 어댑터 설정
    private fun myCommentAdapterClick() {

        myCommentAdapter.setOnMyCommentClickListener(object :MyCommentAdapter.MyCommentClickListener{
            override fun onMyCommentClick(postId: Int, categoryCode: String, replyId: Int) {
                findNavController().navigate(
                    CommunityMyPageFragmentDirections.actionCommunityMyPageFragmentToCommunitypost(
                        ConstVariable.TYPE_COMMUNITY,categoryCode,postId,replyId
                    )
                )
            }
        })

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun settingObserve() {
        //작성 글
        myPageViewModel.myCreatePostLiveData.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                myWritePostAdapter.submitList(
                    listOf(
                        BoardPagePosts(
                            type = ConstVariable.TYPE_NO_LIST,
                            boardPostShowPosition = ConstVariable.DB_COMMUNITY_BOOKMARK_POST
                        )
                    )
                ){
                    refreshFinish()
                }
                return@observe
            }
            myWritePostAdapter.submitList(it){
                refreshFinish()
            }
        }

        //북마크 글
        myPageViewModel.myBookmarkPostData.observe(viewLifecycleOwner){
            if (it.isEmpty()) {
                mySavePostAdapter.submitList(
                    listOf(
                        BoardPagePosts(
                            type = ConstVariable.TYPE_NO_LIST,
                            boardPostShowPosition = ConstVariable.DB_COMMUNITY_BOOKMARK_POST
                        )
                    )
                ){
                    refreshFinish()
                }
                return@observe
            }

            mySavePostAdapter.submitList(it){
                refreshFinish()
            }
        }

        //작성 댓글
        myPageViewModel.myCreateCommentLiveData.observe(viewLifecycleOwner) {
            myCommentAdapter.submitList(it){
                refreshFinish()
            }
        }

        myPageViewModel.alarmOnOffLiveData.observe(viewLifecycleOwner) {
            binding.myCommunityToolbar.menu.forEach { menuItem ->
                if (menuItem.itemId == R.id.menu_my_alarm) {
                    if (it) {
                        menuItem.icon = requireContext().getDrawable(R.drawable.icon_outline_alarm)
                        if(changeAlarm){
                            activity?.showCustomSnackBar(
                                binding.flMyView,
                                getString(R.string.se_k_setted_community_alarm_on)
                            )
                        }
                    } else {
                        menuItem.icon =
                            requireContext().getDrawable(R.drawable.icon_outline_alarm_off)
                        if(changeAlarm){
                            activity?.showCustomSnackBar(
                                binding.flMyView,
                                getString(R.string.se_k_setted_community_alarm_off)
                            )
                        }
                    }
                }
            }
            changeAlarm=false
        }

        myPageViewModel.errorCodeLiveData.observe(viewLifecycleOwner){
            activity?.showErrorSnackBar(binding.root,it.toString())
        }
    }

    private fun refreshFinish(){
        if(refreshView!=null){
            refreshView?.isRefreshing=false
            refreshView=null
        }
    }

}