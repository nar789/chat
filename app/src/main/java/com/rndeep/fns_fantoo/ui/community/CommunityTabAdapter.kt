package com.rndeep.fns_fantoo.ui.community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.data.remote.model.community.CategoryBoardCategoryList
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityNoticeItem
import com.rndeep.fns_fantoo.databinding.*
import com.rndeep.fns_fantoo.ui.common.adapter.PostDiffUtil
import com.rndeep.fns_fantoo.ui.common.post.BlankErrorVH
import com.rndeep.fns_fantoo.ui.common.post.PostClubVH
import com.rndeep.fns_fantoo.ui.common.post.PostCommunityVH
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.CustomDividerDecoration
import com.rndeep.fns_fantoo.utils.addSingleItemDecoRation
import java.text.SimpleDateFormat
import java.util.*

class CommunityTabAdapter : ListAdapter<BoardPagePosts, RecyclerView.ViewHolder>(PostDiffUtil()) {
    private val TYPEBOARD = 0
    private val TYPENOTICE = 1
    private val TYPEREALTIME = 2
    private val TYPECOMMUNITY = 4
    private val TYPEAD = 5
    private val TYPEWEEKTOP = 6
    private val TYPEMORE = 7
    private val TYPENOLIST = 8

    private var boardItems = listOf<CategoryBoardCategoryList>()
    private var noticeItems = listOf<CommunityNoticeItem>()

    class CommunityTabNoticeVH(
        private val binding: TabCommunityNoticeBinding,
        private val listener: BoardListeners.OnBoardNoticeClickListener?
    ) :RecyclerView.ViewHolder(binding.root) {
        private val communityNoticeRcAdapter = CommunityNoticeRcAdapter()

        private val noticeDividerDecoration =
            CustomDividerDecoration(
                1f,
                0f,
                itemView.context.getColor(R.color.gray_400_opacity12),
                false
            )

        fun bind(
            noticeItem: List<CommunityNoticeItem>
        ) {
            if (noticeItem.isEmpty()) {
                binding.tvNoNotice.visibility = View.VISIBLE
                binding.rcCommunityNotice.visibility = View.GONE
            } else {
                binding.tvNoNotice.visibility = View.GONE
                binding.rcCommunityNotice.visibility = View.VISIBLE
                binding.rcCommunityNotice.layoutManager = LinearLayoutManager(itemView.context)
                binding.rcCommunityNotice.addSingleItemDecoRation(noticeDividerDecoration)
                communityNoticeRcAdapter.setItems(noticeItem)
                communityNoticeRcAdapter.setNoticeClickListener(listener)
                binding.rcCommunityNotice.adapter = communityNoticeRcAdapter
            }
            binding.tvNoticeMore.setOnClickListener {
                val boardAction = CommunityTabFragmentDirections.actionCommunityTabFragmentToNotice(
                    BoardInfo(
                        itemView.resources.getString(R.string.g_notice),
                        null,
                        ConstVariable.BOARD_NOTICE_TYPE
                    )
                )
                itemView.findNavController().navigate(boardAction)
            }
        }

    }

    class CommunityCategoryRealTimeVH(
        private val binding: TabCommunityRealtiemIssueBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun realTimeBind(type: String) {
            val text = when (type) {
                ConstVariable.TYPE_REALTIME -> {
                    settingTimeStr()
                    itemView.context.getString(R.string.s_realtime_top_issue)
                }
                ConstVariable.TYPE_WEEK_TOP -> {
                    settingDateRange()
                    itemView.context.getString(R.string.j_weekly_top)
                }
                else -> {
                    ""
                }
            }
            binding.tvRealTimeTitle.text = text
            binding.tvRealTimeTitle.visibility = View.VISIBLE
            binding.tvMoreIssue.visibility = View.GONE

        }

        private fun settingDateRange(){
            val format = SimpleDateFormat("MM-dd",Locale.getDefault())
            val cal = Calendar.getInstance()
            cal.time= Date()
            cal.add(Calendar.DAY_OF_MONTH,-1)
            val yesterdayDateStr = format.format(cal.time)
            cal.add(Calendar.DAY_OF_MONTH,-7)
            val beforeWeekDateStr = format.format(cal.time)
            binding.tvTimeRange.visibility=View.VISIBLE
            binding.tvTimeRange.text="${beforeWeekDateStr} ~ ${yesterdayDateStr}"
        }

        private fun settingTimeStr(){
            val format = SimpleDateFormat("hh:00",Locale.getDefault())
            val cal = Calendar.getInstance()
            cal.time= Date()
            val currentTimeHour = format.format(cal.time)
            binding.tvTimeRange.visibility=View.VISIBLE
            binding.tvTimeRange.text=currentTimeHour
        }

    }

    inner class CommunityNoListVH(private val binding: CommunityNoListLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    //post 옵션 선택 listener
    private var postOptionsClickListener: BoardListeners.OnBoardPostClickListener? = null

    fun setOnOptionsClickListener(listener: BoardListeners.OnBoardPostClickListener) {
        this.postOptionsClickListener = listener
    }

    private var boardNoticeClickListener: BoardListeners.OnBoardNoticeClickListener? = null

    fun setOnBoardNoticeClickListener(listener: BoardListeners.OnBoardNoticeClickListener) {
        this.boardNoticeClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPEBOARD -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tab_community_boardlist, parent, false)
                CommunityCategoryBoardVH(TabCommunityBoardlistBinding.bind(view))
            }
            TYPENOTICE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tab_community_notice, parent, false)
                CommunityTabNoticeVH(TabCommunityNoticeBinding.bind(view),boardNoticeClickListener)
            }
            TYPEREALTIME -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tab_community_realtiem_issue, parent, false)
                CommunityCategoryRealTimeVH(TabCommunityRealtiemIssueBinding.bind(view))
            }
            TYPECOMMUNITY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_home_post, parent, false)
                PostCommunityVH(
                    CategoryHomePostBinding.bind(view),
                    postOptionsClickListener,
                    ConstVariable.POST_TYPE_LIST,
                    ConstVariable.VIEW_COMMUNITY_MAIN
                )
            }
            TYPENOLIST -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.community_no_list_layout, parent, false)
                CommunityNoListVH(CommunityNoListLayoutBinding.bind(view))
            }
            TYPEAD -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_home_post, parent, false)
                CommunityPostAdVH(CategoryHomePostBinding.bind(view))
            }
            TYPEWEEKTOP -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tab_community_realtiem_issue, parent, false)
                CommunityCategoryRealTimeVH(TabCommunityRealtiemIssueBinding.bind(view))
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.blank_layout_for_error, parent, false)
                BlankErrorVH(BlankLayoutForErrorBinding.bind(view))
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val item = currentList[holder.bindingAdapterPosition]
            for (payload in payloads) {
                if (payload is String) {
                    when (payload) {
                        ConstVariable.PAYLOAD_LIKE_CLICK -> {
                            if (holder is PostCommunityVH) {
                                holder.changeLikeColor(
                                    item.boardPostItem?.likeYn == true,
                                    item.boardPostItem?.likeCnt?:0,
                                    item.boardPostItem?.dislikeCnt?:0
                                )
                                holder.changeDisLikeColor(item.boardPostItem?.dislikeYn == true)
                                item.boardPostItem?.let {
                                    holder.clickOfViewType(it,item.boardPkId)
                                }
                            }
                        }
                        ConstVariable.PAYLOAD_HONOR_CLICK -> {
                            if (holder is PostCommunityVH) {
                                holder.changeHonorColor(
                                    item.boardPostItem?.honorYn == true,
                                    item.boardPostItem?.honorCnt?.toString() ?: "0"
                                )
                                item.boardPostItem?.let {
                                    holder.clickOfViewType(it,item.boardPkId)
                                }
                            } else if (holder is PostClubVH) {
                                holder.changeHonorColor(
                                    item.boardPostItem?.honorYn == true,
                                    item.boardPostItem?.honorCnt?.toString() ?: "0"
                                )
                                item.clubPostData?.let {
                                    holder.clickOfViewType(it,item.boardPkId)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.bindingAdapterPosition == RecyclerView.NO_POSITION) return
        when (holder) {
            is CommunityCategoryBoardVH -> {
                holder.boardBind(boardItems)
            }
            is CommunityTabNoticeVH -> {
                holder.bind(noticeItems)
            }
            is CommunityCategoryRealTimeVH -> {
                holder.realTimeBind(currentList[holder.bindingAdapterPosition].type)
            }
            is PostCommunityVH -> {
                holder.communityViewBind(
                    currentList[holder.bindingAdapterPosition].boardPostItem!!,
                    currentList[holder.bindingAdapterPosition].boardPkId
                )
            }
            is CommunityPostAdVH -> {

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == RecyclerView.NO_POSITION) return super.getItemViewType(position)
        when (currentList[position].type) {
            ConstVariable.TYPE_BOARD -> return TYPEBOARD
            ConstVariable.TYPE_COMMUNITY_NOTICE -> return TYPENOTICE
            ConstVariable.TYPE_REALTIME -> return TYPEREALTIME
            ConstVariable.TYPE_WEEK_TOP -> return TYPEWEEKTOP
            ConstVariable.TYPE_COMMUNITY -> return TYPECOMMUNITY
            ConstVariable.TYPE_NO_LIST -> return TYPENOLIST
            ConstVariable.TYPE_AD -> return TYPEAD
            ConstVariable.TYPE_MORE -> return TYPEMORE
        }
        return -1
    }

    //게시판 아이템
    fun setBoardItem(item: List<CategoryBoardCategoryList>) {
        boardItems = item
    }

    //공지 아이템
    fun setNoticeItems(items: List<CommunityNoticeItem>) {
        noticeItems = items
    }
}