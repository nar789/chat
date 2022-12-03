package com.rndeep.fns_fantoo.ui.home.tabpopular.trendpostlist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.ui.common.post.BlankErrorVH
import com.rndeep.fns_fantoo.ui.common.post.PostClubVH
import com.rndeep.fns_fantoo.ui.common.post.PostCommunityVH
import com.rndeep.fns_fantoo.databinding.BlankLayoutForErrorBinding
import com.rndeep.fns_fantoo.databinding.CategoryHomePostBinding
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.ui.home.HomeCategoryAdVH
import com.rndeep.fns_fantoo.utils.ConstVariable

class TrendPostAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPECLUB = 0
    private val TYPECOMMUNITY = 1
    private val TYPEAD = 2

    private var trendPostItems = listOf<BoardPagePosts>()

    private var postOptionClickListener: BoardListeners.OnBoardPostClickListener? = null

    fun setOnPostOptionClickListener(listener: BoardListeners.OnBoardPostClickListener) {
        this.postOptionClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPECLUB -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_home_post, parent, false)
                PostClubVH(CategoryHomePostBinding.bind(view), postOptionClickListener,ConstVariable.POST_TYPE_FEED,ConstVariable.VIEW_CLUB_MAIN)
            }
            TYPECOMMUNITY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_home_post, parent, false)
                PostCommunityVH(CategoryHomePostBinding.bind(view), postOptionClickListener,ConstVariable.POST_TYPE_FEED,ConstVariable.VIEW_COMMUNITY_MAIN,)
            }
            TYPEAD -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_home_post, parent, false)
                return HomeCategoryAdVH(CategoryHomePostBinding.bind(view))
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.blank_layout_for_error, parent, false)
                BlankErrorVH(BlankLayoutForErrorBinding.bind(view))
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PostClubVH -> {
                holder.clubViewBind(
                    trendPostItems[holder.bindingAdapterPosition].clubPostData,
                    trendPostItems[holder.bindingAdapterPosition].boardPkId
                )
            }
            is PostCommunityVH -> {
                holder.communityViewBind(
                    trendPostItems[holder.bindingAdapterPosition].boardPostItem,
                    trendPostItems[holder.bindingAdapterPosition].boardPkId
                )
            }
//            is HomeCategoryAdVH -> {
//                holder.postSetting(
//                    trendPostItems[holder.bindingAdapterPosition].adInfo,
//                    ConstVariable.POST_TYPE_FEED
//                )
//            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (trendPostItems[position].type) {
            ConstVariable.TYPE_CLUB -> {
                TYPECLUB
            }
            ConstVariable.TYPE_COMMUNITY -> {
                TYPECOMMUNITY
            }
            ConstVariable.TYPE_AD -> {
                TYPEAD
            }
            else -> -1
        }
    }

    override fun getItemCount() = trendPostItems.size

    @SuppressLint("NotifyDataSetChanged")
    fun setTrendItem(items: List<BoardPagePosts>) {
        trendPostItems = items
        notifyDataSetChanged()
    }

}