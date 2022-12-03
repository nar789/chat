package com.rndeep.fns_fantoo.ui.community.board

import android.content.res.ColorStateList
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.community.CategoryBoardCategoryList
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityNoticeItem
import com.rndeep.fns_fantoo.databinding.TabCommunityBoardNoticeBinding
import com.rndeep.fns_fantoo.ui.community.CommunityNoticeRcAdapter
import com.rndeep.fns_fantoo.utils.CustomDividerDecoration
import com.rndeep.fns_fantoo.utils.HorizontalMarginItemDecoration
import com.rndeep.fns_fantoo.utils.addSingleItemDecoRation

//상단 공지
class CommunityBoardNoticeVH(
    private val binding: TabCommunityBoardNoticeBinding,
    private val noticeListener: BoardListeners.OnBoardNoticeClickListener?,
    private val categoryListener: BoardListeners.OnBoardCategoryClickListener?,
    private val filterListener: CommunityBoardPostListAdapter.OnBoardFilterClickListener?
) :
    RecyclerView.ViewHolder(binding.root) {
    private val categoryAdapter = BoardCategoryAdapter()
    private val categoryHorizontalItemDecoration =
        HorizontalMarginItemDecoration(20f, 1f, itemView.context)
    private val noticeDividerDecoration =
        CustomDividerDecoration(
            1f,
            20f,
            itemView.context.getColor(R.color.gray_400_opacity12),
            false
        )

    fun bind(
        noticeItem: List<CommunityNoticeItem>,
        categoryItem: List<CategoryBoardCategoryList>,
        globalYn: Boolean,
        selectCategoryPos: Int
    ) {
        noticeSetting(noticeItem)

        categorySetting(categoryItem, selectCategoryPos)

        changeFilterColor(globalYn)

        //필터 선택
        binding.ivBoardGlobalFilter.setOnClickListener {
            filterListener?.onFilterClick(itemView, "filter")
        }
    }

    fun noticeSetting(items: List<CommunityNoticeItem>) {
        if (items.isEmpty()) {
            binding.noticeGroup.visibility = View.GONE
            binding.tvNoNotice.visibility = View.VISIBLE
        } else {
            val communityNoticeRcAdapter = CommunityNoticeRcAdapter()
            binding.noticeGroup.visibility = View.VISIBLE
            binding.tvNoNotice.visibility = View.GONE
            binding.rcCommunityNotice.layoutManager = LinearLayoutManager(itemView.context)
            binding.rcCommunityNotice.addSingleItemDecoRation(noticeDividerDecoration)
            communityNoticeRcAdapter.setItems(items)
            communityNoticeRcAdapter.setNoticeClickListener(noticeListener)
            binding.rcCommunityNotice.adapter = communityNoticeRcAdapter

            //공지 더보기 선택
            binding.tvNoticeMore.setOnClickListener {
                noticeListener?.onNoticeMore()
            }
        }
    }

    fun categorySetting(items: List<CategoryBoardCategoryList>, selectPos: Int) {
        if (items.isEmpty()) {
            binding.categoryGroup.visibility = View.INVISIBLE
        } else {
            if(items[0].parentCode=="C_HOT"){
                binding.categoryGroup.visibility = View.INVISIBLE
                return
            }
            binding.categoryGroup.visibility = View.VISIBLE
            binding.rcCommunityBoardCategory.addSingleItemDecoRation(
                categoryHorizontalItemDecoration
            )
            binding.rcCommunityBoardCategory.layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

            //카테고리 선택
            categoryAdapter.setCategoryListener(object :
                BoardListeners.OnBoardCategoryClickListener {
                override fun onCategoryClick(v: View, id: String, position: Int) {
                    categoryListener?.onCategoryClick(v, id, position)
                }
            })
            categoryAdapter.selectPosition(selectPos)
            categoryAdapter.setItems(items)

            binding.rcCommunityBoardCategory.adapter = categoryAdapter
        }
    }

    fun changeFilterColor(globalYn: Boolean) {
        val filterColor = if (globalYn) {
            itemView.context.getColor(R.color.primary_default)
        } else {
            itemView.context.getColor(R.color.state_enable_gray_900)
        }
        binding.ivBoardGlobalFilter.imageTintList = ColorStateList.valueOf(filterColor)
    }

}