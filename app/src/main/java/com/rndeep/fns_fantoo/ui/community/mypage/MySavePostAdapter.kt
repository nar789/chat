package com.rndeep.fns_fantoo.ui.community.mypage

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.ui.common.post.BlankErrorVH
import com.rndeep.fns_fantoo.ui.common.post.PostCommunityVH
import com.rndeep.fns_fantoo.databinding.BlankLayoutForErrorBinding
import com.rndeep.fns_fantoo.databinding.CategoryHomePostBinding
import com.rndeep.fns_fantoo.databinding.CommunityNoListLayoutBinding
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.utils.ConstVariable

class MySavePostAdapter : ListAdapter<BoardPagePosts,RecyclerView.ViewHolder>(diff) {

    private val TYPECOMMUNITY = 3
    private val TYPENOLIST = 2

    private var optionsClickListener: BoardListeners.OnBoardPostClickListener? = null

    fun setOnOptionsClickListener(listener: BoardListeners.OnBoardPostClickListener) {
        this.optionsClickListener = listener
    }

    inner class CommunityNoListVH(binding: CommunityNoListLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPECOMMUNITY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_home_post, parent, false)
                PostCommunityVH(CategoryHomePostBinding.bind(view), optionsClickListener,ConstVariable.POST_TYPE_LIST,ConstVariable.VIEW_COMMUNITY_BOOKMARK,)
            }
            TYPENOLIST -> {
                val view =LayoutInflater.from(parent.context)
                    .inflate(R.layout.community_no_list_layout,parent,false)
                CommunityNoListVH(CommunityNoListLayoutBinding.bind(view))
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
            is PostCommunityVH -> {
                holder.communityViewBind(
                    currentList[holder.bindingAdapterPosition].boardPostItem,
                    currentList[holder.bindingAdapterPosition].boardPkId
                )
            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        if (position == RecyclerView.NO_POSITION) return super.getItemViewType(position)
        return when (currentList[position].type) {
            ConstVariable.TYPE_COMMUNITY -> TYPECOMMUNITY
            ConstVariable.TYPE_NO_LIST -> TYPENOLIST
            else -> return -1
        }
//        return super.getItemViewType(position)
    }

    companion object{
        val diff = object : DiffUtil.ItemCallback<BoardPagePosts>(){
            override fun areItemsTheSame(
                oldItem: BoardPagePosts,
                newItem: BoardPagePosts
            ): Boolean {
                return oldItem==newItem
            }

            override fun areContentsTheSame(
                oldItem: BoardPagePosts,
                newItem: BoardPagePosts
            ): Boolean {
                return oldItem.boardPostItem?.postId == newItem.boardPostItem?.postId
            }
        }
    }

}