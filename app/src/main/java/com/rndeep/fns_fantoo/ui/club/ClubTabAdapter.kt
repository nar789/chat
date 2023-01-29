package com.rndeep.fns_fantoo.ui.club

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.*
import com.rndeep.fns_fantoo.data.remote.model.ClubChallengeItem
import com.rndeep.fns_fantoo.data.remote.model.ClubRecommendCategory
import com.rndeep.fns_fantoo.data.remote.model.MyClubListItem
import com.rndeep.fns_fantoo.databinding.*
import com.rndeep.fns_fantoo.ui.common.adapter.PostDiffUtil
import com.rndeep.fns_fantoo.ui.common.post.PostClubVH
import com.rndeep.fns_fantoo.ui.common.recommendclub.ClubRecommendVH
import com.rndeep.fns_fantoo.ui.common.recommendclub.RecommendClubListAdapter
import com.rndeep.fns_fantoo.ui.common.recommendclub.RecommendClubSortAdapter
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.TimeUtils
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class ClubTabAdapter() : ListAdapter<BoardPagePosts,RecyclerView.ViewHolder>(PostDiffUtil()) {
    private val TYPE_MYCLUB = 0
    private val TYPE_AD = 1
    private val TYPE_CLUBTOP10 = 2
    private val TYPE_HOT_RECOMMENDCLB = 3
    private val TYPE_NEW_RECOMMENDCLB = 4
    private val TYPE_CLUB_CHALLENGE = 5
    private val TYPE_CLUB = 6
    private val TYPE_NO_LIST = 7

    private var myClubItems = listOf<MyClubListItem>()
    private var hotRecommendClub = CommonRecommendSelect()
    private var newRecommendClub = CommonRecommendSelect()
    private var hotClubCategory : List<ClubRecommendCategory> = listOf()
    private var challengeItems = listOf<ClubChallengeItem>()
    private var postTime : String? =null


    //postOpton 클릭 리스너
    private var optionsClickListener: BoardListeners.OnBoardPostClickListener? = null

    fun setOnOptionsClickListener(listener: BoardListeners.OnBoardPostClickListener) {
        this.optionsClickListener = listener
    }

    private var categoryClickListener : RecommendClubSortAdapter.RecommendCategoryClickListener? =null

    fun setOnCategoryClickListener(listener: RecommendClubSortAdapter.RecommendCategoryClickListener){
        this.categoryClickListener=listener
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
        fun clubBottomBind(postTime :String?) {
            val formattingString = if(postTime==null){
                val data = Date()
                val cal = Calendar.getInstance()
                cal.time=data
                cal.add(Calendar.DAY_OF_MONTH,-1)
                val dateFormat=SimpleDateFormat("MM.dd", Locale.getDefault())
                dateFormat.format(cal.time)
            }else{
                TimeUtils.changeTimeStringFormat(postTime,"MM.dd")
            }
            binding.tvHotPostDate.text=formattingString

        }
    }

    inner class NoListVH(private val binding: NoPostListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_MYCLUB -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tab_club_my_club, parent, false)
                return MyClubListVH(TabClubMyClubBinding.bind(view))
            }
            TYPE_AD -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.tab_club_ad, parent, false)
                return ClubCategoryAdVH(TabClubAdBinding.bind(view))
            }
            TYPE_CLUBTOP10 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tab_club_search, parent, false)
                return PopularCategoryClubSearchVH(TabClubSearchBinding.bind(view))
            }
            TYPE_HOT_RECOMMENDCLB -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.common_recommend_club, parent, false)
                return ClubRecommendVH(CommonRecommendClubBinding.bind(view),categoryClickListener)
            }
            TYPE_NEW_RECOMMENDCLB -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.common_recommend_club, parent, false)
                return ClubRecommendVH(CommonRecommendClubBinding.bind(view),null)
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
                holder.clubBottomBind(postTime)
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
                        hotClubCategory,
                        recommendClubClickListener
                    )
                    ConstVariable.TYPE_CLUB_NEW_RECOMMEND -> return holder.recommendBind(
                        newRecommendClub,
                        null,
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
            ConstVariable.TYPE_MY_CLUB -> return TYPE_MYCLUB
            ConstVariable.TYPE_AD -> return TYPE_AD
            ConstVariable.TYPE_CLUB -> return TYPE_CLUB
            ConstVariable.TYPE_CLUB_TOP_10 -> return TYPE_CLUBTOP10
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
        currentList.forEachIndexed { index, boardPagePosts ->
            if(boardPagePosts.type == ConstVariable.TYPE_CLUB_HOT_RECOMMEND){
                notifyItemChanged(index)
                return
            }
        }
    }

    fun setNewRecommendItem(item: CommonRecommendSelect) {
        this.newRecommendClub = item
    }

    fun setHotClubCategory(item : List<ClubRecommendCategory>){
        this.hotClubCategory=item
    }

    fun setPostTime(time : String){
        postTime=time
    }
}