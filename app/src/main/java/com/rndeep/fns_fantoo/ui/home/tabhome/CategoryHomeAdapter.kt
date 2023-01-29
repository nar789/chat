package com.rndeep.fns_fantoo.ui.home.tabhome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.BannerItem
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.data.local.model.CommonRecommendSelect
import com.rndeep.fns_fantoo.data.local.model.MyProfile
import com.rndeep.fns_fantoo.databinding.*
import com.rndeep.fns_fantoo.ui.common.adapter.PostDiffUtil
import com.rndeep.fns_fantoo.ui.common.post.BlankErrorVH
import com.rndeep.fns_fantoo.ui.common.post.PostClubVH
import com.rndeep.fns_fantoo.ui.common.post.PostCommunityVH
import com.rndeep.fns_fantoo.ui.common.recommendclub.ClubRecommendVH
import com.rndeep.fns_fantoo.ui.common.recommendclub.RecommendClubListAdapter
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.ui.home.HomeCategoryAdVH
import com.rndeep.fns_fantoo.ui.home.HomeNotEditProfileVH
import com.rndeep.fns_fantoo.ui.home.HomeNotLoginVH
import com.rndeep.fns_fantoo.utils.ConstVariable


class CategoryHomeAdapter() : ListAdapter<BoardPagePosts, RecyclerView.ViewHolder>(PostDiffUtil()) {
    private val TYPEBANNER = 0
    private val TYPECLUB = 2
    private val TYPECOMMUNITY = 3
    private val TYPEAD = 4
    private val TYPENOTLOGIN = 5
    private val TYPENOTPROFILE = 6
    private val TYPERECOMMENDCLUB = 7

    //    private var homeCategoryItem = ArrayList<PostInfo>()
    private var homeBannerItem = listOf<BannerItem>()
    private var homeRecommendClub = CommonRecommendSelect()
    private var myProfileInfo : MyProfile? =null

    //post 옵션 선택 listener
    private var optionsClickListener: BoardListeners.OnBoardPostClickListener? = null

    fun setOnOptionsClickListener(optionsClickListener: BoardListeners.OnBoardPostClickListener) {
        this.optionsClickListener = optionsClickListener
    }

    //추천 클럽 선택
    private var recommendClubClickListener: RecommendClubListAdapter.OnRecommendClubClickListener? =
        null

    fun setOnRecommendClubClickListener(recommendClickListener: RecommendClubListAdapter.OnRecommendClubClickListener?) {
        this.recommendClubClickListener = recommendClickListener
    }

    //로그인 클릭
    interface OnLoginClickListener {
        fun onLoginCLick()
    }

    private var loginCLickListener: OnLoginClickListener? = null

    fun setOnLoginClickListener(listener: OnLoginClickListener) {
        this.loginCLickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPEBANNER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_home_banner, parent, false)
                HomeCategoryBannerVH(CategoryHomeBannerBinding.bind(view))
            }
            TYPECLUB -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_home_post, parent, false)
                PostClubVH(
                    CategoryHomePostBinding.bind(view),
                    optionsClickListener,
                    ConstVariable.POST_TYPE_FEED,
                    ConstVariable.VIEW_HOME_MAIN
                )
            }
            TYPECOMMUNITY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_home_post, parent, false)
                PostCommunityVH(
                    CategoryHomePostBinding.bind(view),
                    optionsClickListener,
                    ConstVariable.POST_TYPE_FEED,
                    ConstVariable.VIEW_HOME_MAIN
                )
            }
            TYPEAD -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_home_post, parent, false)
                HomeCategoryAdVH(CategoryHomePostBinding.bind(view))
            }
            TYPENOTLOGIN -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_home_not_login, parent, false)
                HomeNotLoginVH(CategoryHomeNotLoginBinding.bind(view),loginCLickListener)
            }
            TYPENOTPROFILE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_home_not_profile_edit, parent, false)
                HomeNotEditProfileVH(CategoryHomeNotProfileEditBinding.bind(view))
            }
            TYPERECOMMENDCLUB -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.common_recommend_club, parent, false)
                ClubRecommendVH(CommonRecommendClubBinding.bind(view),null)
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
        if (holder.bindingAdapterPosition == RecyclerView.NO_POSITION) {
            return
        }
        when (holder) {
            //배너
            is HomeCategoryBannerVH -> {
                holder.bannerBind(homeBannerItem)
            }
            //클럽 게시글
            is PostClubVH -> {
                val item = getItem(holder.bindingAdapterPosition).clubPostData!!
                holder.clubViewBind(
                    item,
                    getItem(holder.bindingAdapterPosition).boardPkId
                )
            }
            //커뮤니티 게시글
            is PostCommunityVH -> {
                val item = getItem(holder.bindingAdapterPosition).boardPostItem!!
                holder.communityViewBind(
                    item,
                    getItem(holder.bindingAdapterPosition).boardPkId
                )
            }
            //광고 게시글
//            is HomeCategoryAdVH -> {
//                holder.postSetting(homeCategoryItem[holder.bindingAdapterPosition].adInfo, ConstVariable.POST_TYPE_FEED)
//            }
            //로그인이 되어있지 않을 시
            is HomeNotLoginVH -> {
                holder.bind()
            }
            //프로필 작성이 되어 있지 않을 시
            is HomeNotEditProfileVH -> {
                holder.bind(myProfileInfo)
            }
            //클럽 추천 게시글
            is ClubRecommendVH -> {
                holder.recommendBind(homeRecommendClub,null,  recommendClubClickListener)
            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        if (position == RecyclerView.NO_POSITION) return super.getItemViewType(position)
        return when (getItem(position).type) {
            ConstVariable.TYPE_BANNER -> TYPEBANNER
            ConstVariable.TYPE_CLUB -> TYPECLUB
            ConstVariable.TYPE_COMMUNITY -> TYPECOMMUNITY
            ConstVariable.TYPE_AD -> TYPEAD
            ConstVariable.TYPE_IS_NOT_LOGIN -> TYPENOTLOGIN
            ConstVariable.TYPE_IS_NOT_PROFILE -> TYPENOTPROFILE
            ConstVariable.TYPE_HOME_RECOMMEND_CLUB -> TYPERECOMMENDCLUB
            else -> return -1
        }
    }

    //배너 아이템 셋팅
    fun setBannerItem(items: List<BannerItem>) {
        this.homeBannerItem = items
    }

    //추천 클럽 아이템 셋팅
    fun setRecommendItem(item: CommonRecommendSelect) {
        this.homeRecommendClub = item
    }

    fun setEditProfileItem(item : MyProfile){
        myProfileInfo=item
    }

}