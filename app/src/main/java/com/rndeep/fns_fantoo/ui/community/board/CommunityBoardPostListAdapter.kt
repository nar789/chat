package com.rndeep.fns_fantoo.ui.community.board

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.data.remote.model.community.CategoryBoardCategoryList
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityNoticeItem
import com.rndeep.fns_fantoo.databinding.BlankLayoutForErrorBinding
import com.rndeep.fns_fantoo.databinding.CategoryHomePostBinding
import com.rndeep.fns_fantoo.databinding.NoPostListLayoutBinding
import com.rndeep.fns_fantoo.databinding.TabCommunityBoardNoticeBinding
import com.rndeep.fns_fantoo.ui.common.adapter.PostDiffUtil
import com.rndeep.fns_fantoo.ui.common.post.BlankErrorVH
import com.rndeep.fns_fantoo.ui.common.post.PostCommunityVH
import com.rndeep.fns_fantoo.utils.ConstVariable

class CommunityBoardPostListAdapter :
    ListAdapter<BoardPagePosts, RecyclerView.ViewHolder>(PostDiffUtil()) {

    private val TYPENOTICE = 0
    private val TYPECOMMUNITY = 2
    private val TYPENOLIST = 4

    private var selectPos: Int = 0

    private var globalYn = false

    //게시판의 종류 (HOT 이냐 아니냐)
    private var boardViewType = ConstVariable.VIEW_COMMUNITY_PAGE_COMMON

    //notice items
    private var noticeItems = listOf<CommunityNoticeItem>()

    //category items
    private var categoryItems = listOf<CategoryBoardCategoryList>()

    //공지 클릭 리스너
    private var onBoardNoticeClickListener: BoardListeners.OnBoardNoticeClickListener? = null

    fun setOnCommunityNoticeClickListener(listener: BoardListeners.OnBoardNoticeClickListener) {
        this.onBoardNoticeClickListener = listener
    }

    //카테고리 클릭 리스너
    private var onBoardCategoryClickListener: BoardListeners.OnBoardCategoryClickListener? = null

    fun setOnCommunityCategoryClickListener(listener: BoardListeners.OnBoardCategoryClickListener) {
        this.onBoardCategoryClickListener = listener
    }

    //언어 설정 클릭 리스너
    interface OnBoardFilterClickListener {
        fun onFilterClick(v: View, id: String)
    }

    private var onBoardFilterClickListener: OnBoardFilterClickListener? = null

    fun setOnCommunityFilterClickListener(listener: OnBoardFilterClickListener) {
        this.onBoardFilterClickListener = listener
    }


    private var onBoardPostClickListener: BoardListeners.OnBoardPostClickListener? = null

    fun setOnBoardPostClickListener(listener: BoardListeners.OnBoardPostClickListener) {
        this.onBoardPostClickListener = listener
    }

    inner class NoPostListVH(private val binding: NoPostListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPENOTICE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tab_community_board_notice, parent, false)
                CommunityBoardNoticeVH(
                    TabCommunityBoardNoticeBinding.bind(view),
                    onBoardNoticeClickListener,
                    onBoardCategoryClickListener,
                    onBoardFilterClickListener,
                )
            }
            TYPECOMMUNITY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_home_post, parent, false)
                PostCommunityVH(
                    CategoryHomePostBinding.bind(view),
                    onBoardPostClickListener,
                    ConstVariable.POST_TYPE_LIST,
                    boardViewType
                )
            }
            TYPENOLIST -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.no_post_list_layout, parent, false)
                NoPostListVH(NoPostListLayoutBinding.bind(view))
            }
            else -> {
                //어떠한 타입도 아니면 그냥 빈화면
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.blank_layout_for_error, parent, false)
                BlankErrorVH(BlankLayoutForErrorBinding.bind(view))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.bindingAdapterPosition == RecyclerView.NO_POSITION) {
            return
        }
        when (holder) {
            is CommunityBoardNoticeVH -> {
                holder.bind(
                    noticeItems,
                    categoryItems,
                    globalYn,
                    selectPos
                )
            }
            is PostCommunityVH -> {
                holder.communityViewBind(
                    getItem(position).boardPostItem!!,
                    getItem(position).boardPkId
                )
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        if (position == RecyclerView.NO_POSITION) return super.getItemViewType(position)
        return when (getItem(position).type) {
            ConstVariable.TYPE_COMMUNITY_NOTICE -> TYPENOTICE
            ConstVariable.TYPE_COMMUNITY -> TYPECOMMUNITY
            ConstVariable.TYPE_NO_LIST -> TYPENOLIST
            else -> -1
        }
    }

    //공지 아이템
    fun setNoticeItems(items: List<CommunityNoticeItem>) {
        this.noticeItems = items
    }

    //카테고리 아이템
    fun setCategoryItems(items: List<CategoryBoardCategoryList>) {
        this.categoryItems = items
    }

    fun selectCatePos(pos: Int) {
        selectPos = pos
    }

    fun setBoardViewType(viewType: String) {
        this.boardViewType = viewType
    }

    fun setGlobalYN(global: Boolean) {
        this.globalYn = global
    }

    public override fun getItem(position: Int): BoardPagePosts {
        return super.getItem(position)
    }


}