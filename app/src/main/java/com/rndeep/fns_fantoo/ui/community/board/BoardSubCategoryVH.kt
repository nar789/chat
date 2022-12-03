package com.rndeep.fns_fantoo.ui.community.board

import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.community.CategoryBoardCategoryList
import com.rndeep.fns_fantoo.databinding.TabCommunityBoardCategoryLayoutBinding

class BoardSubCategoryVH(private val binding: TabCommunityBoardCategoryLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: CategoryBoardCategoryList,
        selectPos: Int,
        listener: BoardListeners.OnBoardCategoryClickListener?
    ) {
        binding.llCategoryContainer.clipToOutline = true

        val local = itemView.context.resources.configuration.locales[0]
        when (local.language) {
            "ko" -> {
                binding.tvCategoryName.text = item.codeNameKo
            }
            else -> {
                binding.tvCategoryName.text = item.codeNameEn
            }
        }

        if (selectPos == bindingAdapterPosition) {
            binding.tvCategoryName.setBackgroundColor(itemView.context.getColor(R.color.state_enable_primary_100))
            binding.tvCategoryName.setTextColor(itemView.context.getColor(R.color.primary_600))
        } else {
            binding.tvCategoryName.setBackgroundColor(itemView.context.getColor(R.color.gray_25))
            binding.tvCategoryName.setTextColor(itemView.context.getColor(R.color.state_active_gray_700))
        }

        itemView.setOnClickListener {
            listener?.onCategoryClick(itemView, item.code, bindingAdapterPosition)
        }
    }


}