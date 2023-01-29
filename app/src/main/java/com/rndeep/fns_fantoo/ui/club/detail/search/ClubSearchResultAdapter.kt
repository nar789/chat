package com.rndeep.fns_fantoo.ui.club.detail.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostData
import com.rndeep.fns_fantoo.databinding.BlankLayoutForErrorBinding
import com.rndeep.fns_fantoo.databinding.CategoryHomePostBinding
import com.rndeep.fns_fantoo.ui.common.post.PostClubVH
import com.rndeep.fns_fantoo.ui.common.post.PostCommunityVH
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.utils.ConstVariable

class ClubSearchResultAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var resultList = listOf<ClubPostData>()

    private var communityPostClickListener : BoardListeners.OnBoardPostClickListener? =null

    fun setOnBoardPostClickListener(listener : BoardListeners.OnBoardPostClickListener){
        this.communityPostClickListener=listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_home_post, parent, false)
        return PostClubVH(CategoryHomePostBinding.bind(view),communityPostClickListener,ConstVariable.POST_TYPE_LIST,ConstVariable.VIEW_CLUB_SEARCH)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.bindingAdapterPosition == RecyclerView.NO_POSITION) return
        when (holder) {
            is PostClubVH -> {
                holder.clubViewBind(resultList[holder.bindingAdapterPosition],-1)
            }
        }
    }

    override fun getItemCount() = resultList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(list: List<ClubPostData>) {
        resultList = list
        notifyDataSetChanged()
    }

    fun getItems() = resultList


}