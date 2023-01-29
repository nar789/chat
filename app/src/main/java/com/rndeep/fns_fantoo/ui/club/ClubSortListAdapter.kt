package com.rndeep.fns_fantoo.ui.club

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.ClubSubCategoryItem
import com.rndeep.fns_fantoo.databinding.TabClubSortLayoutBinding
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.SizeUtils
import kotlinx.coroutines.processNextEventInCurrentThread

class ClubSortListAdapter : ListAdapter<ClubSubCategoryItem,ClubSortListAdapter.ClubSortListVH>(diff) {
    private var clickPos = 0

    interface FreeBoardCategoryClickListener{
        fun onCategoryClick(url : String,pos :Int,categoryCode:String)
    }
    private var freeBoardCategoryClickListener : FreeBoardCategoryClickListener? =null

    fun setOnFreeBoardCategoryClickListener(listener: FreeBoardCategoryClickListener){
        this.freeBoardCategoryClickListener=listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubSortListVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tab_club_sort_layout, parent, false)
        return ClubSortListVH(TabClubSortLayoutBinding.bind(view),freeBoardCategoryClickListener)
    }

    override fun onBindViewHolder(holder: ClubSortListVH, position: Int) {
        if (holder.bindingAdapterPosition == RecyclerView.NO_POSITION) {
            return
        }
        holder.bind(currentList[holder.bindingAdapterPosition])
    }

    fun setClickPos(pos :Int){
        val oldPos =clickPos
        clickPos= pos
        notifyItemChanged(oldPos)
        notifyItemChanged(clickPos)
    }

    companion object{
        val diff =object : DiffUtil.ItemCallback<ClubSubCategoryItem>(){
            override fun areItemsTheSame(
                oldItem: ClubSubCategoryItem,
                newItem: ClubSubCategoryItem
            ): Boolean {
                return oldItem==newItem
            }

            override fun areContentsTheSame(
                oldItem: ClubSubCategoryItem,
                newItem: ClubSubCategoryItem
            ): Boolean {
                return (oldItem.categoryId==newItem.categoryId)
            }

        }
    }

    inner class ClubSortListVH(
        private val binding: TabClubSortLayoutBinding,
        private val listener: FreeBoardCategoryClickListener?
    ) : RecyclerView.ViewHolder(binding.root) {

        val sidePadding = SizeUtils.getDpValue(12f, itemView.context).toInt()
        val topBottomPadding = SizeUtils.getDpValue(7f, itemView.context).toInt()

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(item: ClubSubCategoryItem) {
            itemView.context.let {
                if (clickPos == bindingAdapterPosition) {
                    binding.tvSortText.background =
                        it.getDrawable(R.drawable.bg_border_radius_100_primary500)
                    binding.tvSortText.setTextColor(it.getColor(R.color.state_enable_gray_25))
                } else {
                    binding.tvSortText.background =
                        it.getDrawable(R.drawable.bg_border_radius_100_gray50)
                    binding.tvSortText.setTextColor(it.getColor(R.color.gray_850))
                }
            }
            binding.tvSortText.text = item.categoryName
            binding.tvSortText.setOnClickListener {
                if(clickPos==bindingAdapterPosition) return@setOnClickListener
                listener?.onCategoryClick(item.url,bindingAdapterPosition,item.categoryCode)
                val oldpos = clickPos
                clickPos = bindingAdapterPosition
                bindingAdapter?.notifyItemChanged(oldpos)
                bindingAdapter?.notifyItemChanged(clickPos)
            }
        }
    }

}