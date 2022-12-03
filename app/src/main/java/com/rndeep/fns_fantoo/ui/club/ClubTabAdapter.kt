package com.rndeep.fns_fantoo.ui.club

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.*
import com.rndeep.fns_fantoo.data.remote.model.ClubChallengeItem
import com.rndeep.fns_fantoo.data.remote.model.MainClub
import com.rndeep.fns_fantoo.data.remote.model.MyClubListItem
import com.rndeep.fns_fantoo.databinding.*
import com.rndeep.fns_fantoo.ui.common.adapter.PostDiffUtil
import com.rndeep.fns_fantoo.ui.common.post.PostClubVH
import com.rndeep.fns_fantoo.ui.common.recommendclub.ClubRecommendVH
import com.rndeep.fns_fantoo.ui.common.recommendclub.RecommendClubListAdapter
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.HorizontalMarginItemDecoration
import com.rndeep.fns_fantoo.utils.addSingleItemDecoRation
import java.text.SimpleDateFormat
import java.util.*

class ClubTabAdapter() : ListAdapter<BoardPagePosts,RecyclerView.ViewHolder>(PostDiffUtil()) {
    private val TYPEMYCLUB = 0
    private val TYPEAD = 1
    private val TYPECLUBTOP10 = 2
    private val TYPE_HOT_RECOMMENDCLB = 3
    private val TYPE_NEW_RECOMMENDCLB = 4
    private val TYPE_CLUB_CHALLENGE = 5
    private val TYPE_CLUB = 6
    private val TYPE_NO_LIST = 7

    private var myClubItems = listOf<MyClubListItem>()
    private var hotRecommendClub = CommonRecommendSelect()
    private var newRecommendClub = CommonRecommendSelect()
    private var challengeItems = listOf<ClubChallengeItem>()

    //postOpton 클릭 리스너
    private var optionsClickListener: BoardListeners.OnBoardPostClickListener? = null

    fun setOnOptionsClickListener(listener: BoardListeners.OnBoardPostClickListener) {
        this.optionsClickListener = listener
    }

    //recommendRlubClickListener
    private var recommendClubClickListener: RecommendClubListAdapter.OnRecommendClubClickListener? =
        null

    fun setOnRecommendClubClickListener(listener: RecommendClubListAdapter.OnRecommendClubClickListener) {
        this.recommendClubClickListener = listener
    }

    class CategoryErrorVH(view: View) : RecyclerView.ViewHolder(view)

    inner class PopularCategoryClubSearchVH(private val binding: TabClubSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun clubBottomBind() {
            val data = Date()
            val cal = Calendar.getInstance()
            cal.time=data
            cal.add(Calendar.DAY_OF_MONTH,-1)
            val dateFormat = SimpleDateFormat("MM.dd", Locale.getDefault())
            binding.tvHotPostDate.text=dateFormat.format(cal.time)

        }
    }

    inner class NoListVH(private val binding: NoPostListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPEMYCLUB -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tab_club_my_club, parent, false)
                return MyClubListVH(TabClubMyClubBinding.bind(view))
            }
            TYPEAD -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.tab_club_ad, parent, false)
                return ClubCategoryAdVH(TabClubAdBinding.bind(view))
            }
            TYPECLUBTOP10 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tab_club_search, parent, false)
                return PopularCategoryClubSearchVH(TabClubSearchBinding.bind(view))
            }
            TYPE_HOT_RECOMMENDCLB -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.common_recommend_club, parent, false)
                return ClubRecommendVH(CommonRecommendClubBinding.bind(view))
            }
            TYPE_NEW_RECOMMENDCLB -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.common_recommend_club, parent, false)
                return ClubRecommendVH(CommonRecommendClubBinding.bind(view))
            }
            TYPE_CLUB_CHALLENGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tab_club_challenge, parent, false)
                return ClubChallengeVH(TabClubChallengeBinding.bind(view))
            }
            TYPE_CLUB -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_home_post, parent, false)
                return PostClubVH(
                    CategoryHomePostBinding.bind(view),
                    optionsClickListener,
                    ConstVariable.POST_TYPE_LIST,
                    ConstVariable.VIEW_CLUB_MAIN
                )
            }
            TYPE_NO_LIST -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.no_post_list_layout, parent, false)
                return NoListVH(NoPostListLayoutBinding.bind(view))
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.blank_layout_for_error, parent, false)
                return CategoryErrorVH(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.bindingAdapterPosition == RecyclerView.NO_POSITION) {
            return
        }
        when (holder) {
            is MyClubListVH -> {
                holder.myClubBind(myClubItems)
            }
            is ClubCategoryAdVH -> {
            }
            is PopularCategoryClubSearchVH -> {
                holder.clubBottomBind()
            }
            is PostClubVH -> {
                holder.clubViewBind(
                    getItem(holder.bindingAdapterPosition).clubPostData,
                    getItem(holder.bindingAdapterPosition).boardPkId
                )
            }
            is ClubRecommendVH -> {
                when (currentList[holder.bindingAdapterPosition].type) {
                    ConstVariable.TYPE_CLUB_HOT_RECOMMEND -> return holder.recommendBind(
                        hotRecommendClub,
                        recommendClubClickListener
                    )
                    ConstVariable.TYPE_CLUB_NEW_RECOMMEND -> return holder.recommendBind(
                        newRecommendClub,
                        recommendClubClickListener
                    )
                }
            }
            is ClubChallengeVH -> {
                holder.bind(challengeItems)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == RecyclerView.NO_POSITION) return super.getItemViewType(position)
        when (getItem(position).type) {
            ConstVariable.TYPE_MY_CLUB -> return TYPEMYCLUB
            ConstVariable.TYPE_AD -> return TYPEAD
            ConstVariable.TYPE_CLUB -> return TYPE_CLUB
            ConstVariable.TYPE_CLUB_TOP_10 -> return TYPECLUBTOP10
            ConstVariable.TYPE_CLUB_HOT_RECOMMEND -> return TYPE_HOT_RECOMMENDCLB
            ConstVariable.TYPE_CLUB_NEW_RECOMMEND -> return TYPE_NEW_RECOMMENDCLB
            ConstVariable.TYPE_CLUB_CHALLENGE -> return TYPE_CLUB_CHALLENGE
            ConstVariable.TYPE_NO_LIST -> return TYPE_NO_LIST
        }
        return super.getItemViewType(position)
    }

    fun setMyClubItem(items: List<MyClubListItem>) {
        this.myClubItems = items
    }

    fun setChallengeItem(items: List<ClubChallengeItem>) {
        this.challengeItems = items
    }

    fun setHotRecommendItem(item: CommonRecommendSelect) {
        this.hotRecommendClub = item
    }

    fun setNewRecommendItem(item: CommonRecommendSelect) {
        this.newRecommendClub = item
    }

}