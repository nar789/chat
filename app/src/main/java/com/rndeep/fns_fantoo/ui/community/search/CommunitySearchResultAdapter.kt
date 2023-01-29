package com.rndeep.fns_fantoo.ui.community.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.databinding.BlankLayoutForErrorBinding
import com.rndeep.fns_fantoo.databinding.CategoryHomePostBinding
import com.rndeep.fns_fantoo.ui.common.post.PostCommunityVH
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.utils.ConstVariable

class CommunitySearchResultAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val COMMUNITYTYPE = 1
    private var resultList = listOf<BoardPagePosts>()

    inner class ErrorView(private val binding: BlankLayoutForErrorBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var communityPostClickListener : BoardListeners.OnBoardPostClickListener? =null

    fun setOnBoardPostClickListener(listener : BoardListeners.OnBoardPostClickListener){
        this.communityPostClickListener=listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            COMMUNITYTYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_home_post, parent, false)
                PostCommunityVH(CategoryHomePostBinding.bind(view),communityPostClickListener,ConstVariable.POST_TYPE_LIST,ConstVariable.VIEW_COMMUNITY_SEARCH)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.blank_layout_for_error, parent, false)
                ErrorView(BlankLayoutForErrorBinding.bind(view))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.bindingAdapterPosition == RecyclerView.NO_POSITION) return
        when (holder) {
            is PostCommunityVH -> {
                holder.communityViewBind(
                    resultList[holder.bindingAdapterPosition].boardPostItem!!,
                    resultList[holder.bindingAdapterPosition].boardPkId
                )
            }
        }
    }

    override fun getItemCount() = resultList.size

    override fun getItemViewType(position: Int): Int {
        return when (resultList[position].type) {
            ConstVariable.TYPE_COMMUNITY -> COMMUNITYTYPE
            else -> super.getItemViewType(position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(list: List<BoardPagePosts>) {
        resultList = list
        notifyDataSetChanged()
    }

    fun getItems() = resultList


}