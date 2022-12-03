package com.rndeep.fns_fantoo.ui.home.tabpopular.trendpostlist

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.post.BoardPostData
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostData
import com.rndeep.fns_fantoo.data.remote.model.post.PostListData
import com.rndeep.fns_fantoo.databinding.FragmentTrendPostBinding
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrendPostFragment :
    BaseFragment<FragmentTrendPostBinding>(FragmentTrendPostBinding::inflate) {

    private val trendViewModel: TrendPostViewModel by viewModels()

    private val getArgs: TrendPostFragmentArgs by navArgs()

    private val trendPostAdapter = TrendPostAdapter()

    private lateinit var verticalItemDeco: RecyclerView.ItemDecoration

    private lateinit var trendName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalItemDeco = object : RecyclerView.ItemDecoration(){
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val itempostion = parent.getChildAdapterPosition(view)
                if (itempostion == RecyclerView.NO_POSITION) return
                val sideGap = SizeUtils.getDpValue(-5f,context).toInt()
                val firstGap = SizeUtils.getDpValue(-10f,context).toInt()
                val topOffset = if(isFirstItem(itempostion)) firstGap else sideGap
                outRect.set(0, topOffset, 0, sideGap)
            }

            private fun isFirstItem(index: Int) = index == 0
        }

        activity?.let {
            it.window?.statusBarColor = it.getColor(R.color.gray_25)
            it.setDarkStatusBarIcon()
        }
        trendName = getArgs.trendName
    }

    override fun initUi() {
        binding.rcTrendPostList.layoutManager = LinearLayoutManager(context)
        binding.rcTrendPostList.addSingleItemDecoRation(verticalItemDeco)
        binding.rcTrendPostList.adapter = trendPostAdapter
        binding.trendToolbar.title = trendName
        settingObserve()
        trendViewModel.getTrendPostItem(requireContext())
    }

    override fun initUiActionEvent() {
        binding.trendToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        trendPostAdapter.setOnPostOptionClickListener(object :
            BoardListeners.OnBoardPostClickListener {
            override fun onLikeClick(
                dbId: Int,
                postItem: PostListData,
                postType: String,
                changePos: Int,
                holder: RecyclerView.ViewHolder
            ) {
                when (postItem) {
                    is BoardPostData -> {
                        trendViewModel.requestLikeUpdate(
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
                        trendViewModel.requestLikeUpdate(
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
            }

            override fun onPostClick(
                categoryId: String,
                postId: Int,
                postType: String,
                clubId: String?
            ) {
                when(postType){
                    ConstVariable.TYPE_COMMUNITY->{
                        findNavController().navigate(
                            TrendPostFragmentDirections.actionTrendPostFragmentToCommunitypost(
                                postType, categoryId, postId
                            )
                        )
                    }
                    ConstVariable.TYPE_CLUB->{
                        findNavController().navigate(
                            TrendPostFragmentDirections.actionTrendPostFragmentToClubPost(
                                postType, categoryId,clubId?:"-1",postId
                            )
                        )
                    }
                }
            }

            override fun onProfileClick(title: String, boardCode: String) {}
        })

    }

    private fun settingObserve() {
        trendViewModel.trendPostLiveData.observe(viewLifecycleOwner) {
            trendPostAdapter.setTrendItem(it)
        }
    }

}