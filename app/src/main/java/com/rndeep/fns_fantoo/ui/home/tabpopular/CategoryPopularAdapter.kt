package com.rndeep.fns_fantoo.ui.home.tabpopular

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.*
import com.rndeep.fns_fantoo.ui.common.post.PostClubVH
import com.rndeep.fns_fantoo.ui.common.post.PostCommunityVH
import com.rndeep.fns_fantoo.ui.common.recommendclub.ClubRecommendVH
import com.rndeep.fns_fantoo.databinding.*
import com.rndeep.fns_fantoo.ui.common.adapter.PostDiffUtil
import com.rndeep.fns_fantoo.ui.common.recommendclub.RecommendClubListAdapter
import com.rndeep.fns_fantoo.ui.home.HomeCategoryAdVH
import com.rndeep.fns_fantoo.ui.common.post.BlankErrorVH
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.utils.ConstVariable

//class CategoryPopularAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
class CategoryPopularAdapter :
    ListAdapter<BoardPagePosts, RecyclerView.ViewHolder>(PostDiffUtil()) {
    private val TYPETREND = 0
    private val TYPECLUB = 2
    private val TYPECOMMUNITY = 3
    private val TYPEAD = 4
    private val TYPERECOMMENDCLUB = 5
    private val TYPECURATION = 6
    var currentType = ConstVariable.POST_TYPE_FEED

    private var trendItemList = ArrayList<ArrayList<TrendTagItem>>()
    private var curationItemList = ArrayList<CurationDataItem>()
    private var popularRecommendClub = CommonRecommendSelect()

    //post 옵션 선택 listener
    private var optionsClickListener: BoardListeners.OnBoardPostClickListener? = null

    fun setOnOptionsClickListener(optionsClickListener: BoardListeners.OnBoardPostClickListener) {
        this.optionsClickListener = optionsClickListener
    }

    //트랜드 선택
    interface OnTrendClickListener {
        fun onGlobalClick(v: View)
    }

    private var onTrendClickListener: OnTrendClickListener? = null

    fun setOnTrendClickListener(listener: OnTrendClickListener) {
        this.onTrendClickListener = listener
    }

    private var recommendCLubClickListener: RecommendClubListAdapter.OnRecommendClubClickListener? =
        null

    fun setOnRecommendClubClickListener(listener: RecommendClubListAdapter.OnRecommendClubClickListener) {
        this.recommendCLubClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPETREND -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_popular_tranding_viewpager, parent, false)
                PopularCategoryTrendViewPagerVH(CategoryPopularTrandingViewpagerBinding.bind(view))
            }
            TYPECLUB -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_home_post, parent, false)
                PostClubVH(
                    CategoryHomePostBinding.bind(view),
                    optionsClickListener,
                    currentType,
                    ConstVariable.VIEW_HOME_MAIN
                )
            }
            TYPECOMMUNITY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_home_post, parent, false)
                PostCommunityVH(
                    CategoryHomePostBinding.bind(view),
                    optionsClickListener,
                    currentType,
                    ConstVariable.VIEW_HOME_MAIN
                )
            }
            TYPEAD -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_home_post, parent, false)
                HomeCategoryAdVH(CategoryHomePostBinding.bind(view))
            }
            TYPERECOMMENDCLUB -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.common_recommend_club, parent, false)
                ClubRecommendVH(CommonRecommendClubBinding.bind(view))
            }
            TYPECURATION -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_popular_curation, parent, false)
                PopularCategoryCurationVH(CategoryPopularCurationBinding.bind(view))
            }
            else -> {
                //어떠한 타입도 아니면 그냥 빈화면
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
                                    holder.clickOfViewType(it, item.boardPkId)
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
                                    holder.clickOfViewType(it, item.boardPkId)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.bindingAdapterPosition == RecyclerView.NO_POSITION) {
            return
        }
        when (holder) {
            is PopularCategoryTrendViewPagerVH -> {
                holder.trendBind(trendItemList, onTrendClickListener)
            }
            is PostClubVH -> {
                holder.clubViewBind(
                    getItem(holder.bindingAdapterPosition).clubPostData,
                    getItem(holder.bindingAdapterPosition).boardPkId
                )
            }
            is PostCommunityVH -> {
                holder.communityViewBind(
                    getItem(holder.bindingAdapterPosition).boardPostItem,
                    getItem(holder.bindingAdapterPosition).boardPkId
                )
            }
//            is HomeCategoryAdVH -> {
//                holder.postSetting(
//                    homeCategoryItem[holder.bindingAdapterPosition].adInfo,
//                    currentType
//                )
//            }
            is ClubRecommendVH -> {
                holder.recommendBind(popularRecommendClub,  recommendCLubClickListener)
            }
            is PopularCategoryCurationVH -> {
                holder.curationBind(curationItemList)
            }
        }

    }

    public override fun getItem(position: Int): BoardPagePosts {
        return super.getItem(position)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == RecyclerView.NO_POSITION) return super.getItemViewType(position)
        return when (getItem(position).type) {
            ConstVariable.TYPE_BANNER -> TYPETREND
            ConstVariable.TYPE_CLUB -> TYPECLUB
            ConstVariable.TYPE_COMMUNITY -> TYPECOMMUNITY
            ConstVariable.TYPE_AD -> TYPEAD
            ConstVariable.TYPE_HOME_RECOMMEND_CLUB -> TYPERECOMMENDCLUB
            ConstVariable.TYPE_POPULAR_CURATION -> TYPECURATION
            else -> return -1
        }
//        return super.getItemViewType(position)
    }

    //큐레이션 아이템
    fun setCurationItems(items: ArrayList<CurationDataItem>) {
        this.curationItemList = items
    }

    //트랜드 아이템
    fun setTrendItems(items: ArrayList<ArrayList<TrendTagItem>>) {
        this.trendItemList = items
    }

    //추천 클럽 아이템
    fun setRecommendItem(item: CommonRecommendSelect) {
        this.popularRecommendClub = item
    }

}