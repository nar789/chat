package com.rndeep.fns_fantoo.ui.club.settings.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.dto.ClubInterestCategoryDto
import com.rndeep.fns_fantoo.databinding.TabClubSettingCategoryItemBinding
import timber.log.Timber

class ClubCategoryAdapter(val clickListener:(ClubInterestCategoryDto) -> Unit) : RecyclerView.Adapter<ClubCategoryAdapter.CategoryVH>(){
    private var selectedInterestCategoryId : Int = -1

    private val diffUtil = AsyncListDiffer(this, DiffUtilCallback())

    fun replaceTo(categoryList:List<ClubInterestCategoryDto>) = diffUtil.submitList(categoryList)

    fun getItem(position: Int): ClubInterestCategoryDto = diffUtil.currentList[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVH {
        return CategoryVH(TabClubSettingCategoryItemBinding.bind(
            LayoutInflater.from(parent.context).inflate(R.layout.tab_club_setting_category_item, parent, false)
        ))
    }

    override fun onBindViewHolder(holder: CategoryVH, position: Int) {
        try {
            holder.bind(getItem(position))
        }catch (e: IndexOutOfBoundsException){
            Timber.e("ClubCategoryAdapter onBindViewHolder IndexOutOfBoundsException.")
        }
    }

    override fun getItemCount(): Int = diffUtil.currentList.size

    fun setSelectedInterestCategoryId(interestCategoryId:Int){
        selectedInterestCategoryId = interestCategoryId
    }

    fun getSelectedInterestCategoryId():Int{
        return selectedInterestCategoryId
    }

    inner class CategoryVH(val binding:TabClubSettingCategoryItemBinding): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("NotifyDataSetChanged")
        fun bind(categoryInfo: ClubInterestCategoryDto){
            binding.tvCategoryName.text = categoryInfo.categoryName
            binding.root.setOnClickListener {
                selectedInterestCategoryId = categoryInfo.clubInterestCategoryId
                notifyDataSetChanged()
                clickListener(categoryInfo)
            }
            val isSelectedItem = (selectedInterestCategoryId == categoryInfo.clubInterestCategoryId)
            binding.tvCategoryName.isEnabled = isSelectedItem
            binding.root.isSelected = isSelectedItem
        }
    }
}

class DiffUtilCallback : DiffUtil.ItemCallback<ClubInterestCategoryDto>(){
    override fun areContentsTheSame(oldItem: ClubInterestCategoryDto, newItem: ClubInterestCategoryDto): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: ClubInterestCategoryDto, newItem: ClubInterestCategoryDto): Boolean {
        return oldItem.clubInterestCategoryId == newItem.clubInterestCategoryId
    }
}