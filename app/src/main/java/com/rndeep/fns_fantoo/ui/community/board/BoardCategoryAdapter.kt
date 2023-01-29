package com.rndeep.fns_fantoo.ui.community.board

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TabCommunityBoardCategoryLayoutBinding
import com.rndeep.fns_fantoo.data.remote.model.community.CategoryBoardCategoryList

class BoardCategoryAdapter() : RecyclerView.Adapter<BoardSubCategoryVH>() {
    private var categoryItems = listOf<CategoryBoardCategoryList>()

    //선택 position
    private var selectPos = 0

    //카테고리 선택
    private var categoryListener: BoardListeners.OnBoardCategoryClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardSubCategoryVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tab_community_board_category_layout, parent, false)
        return BoardSubCategoryVH(TabCommunityBoardCategoryLayoutBinding.bind(view))
    }

    override fun onBindViewHolder(holder: BoardSubCategoryVH, position: Int) {
        holder.bind(categoryItems[holder.bindingAdapterPosition], selectPos, categoryListener)
    }

    override fun getItemCount() = categoryItems.size

    fun setItems(items: List<CategoryBoardCategoryList>) {
        this.categoryItems = items
    }

    fun selectPosition(position: Int) {
        if(selectPos==position) return

        val oldPos = selectPos
        selectPos = position
        notifyItemChanged(oldPos)
        notifyItemChanged(selectPos)
    }

    fun setCategoryListener(listener: BoardListeners.OnBoardCategoryClickListener?) {
        this.categoryListener = listener
    }

}