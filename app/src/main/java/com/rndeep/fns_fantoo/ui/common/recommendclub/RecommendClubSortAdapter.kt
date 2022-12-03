package com.rndeep.fns_fantoo.ui.common.recommendclub

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TabClubSortLayoutBinding
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.SizeUtils

class RecommendClubSortAdapter: RecyclerView.Adapter<RecommendClubSortAdapter.RecommendClubSortListVH>() {
    private val clubSortList: ArrayList<String> =
        arrayListOf("Popular", "Hot", "New", "한류 영상", "한류 뷰티", "한류 푸드", "AD", "Show me")
    private var clickPos = 0

    inner class RecommendClubSortListVH(private val binding: TabClubSortLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val sidePadding = SizeUtils.getDpValue(12f, itemView.context).toInt()
        val topBottomPadding = SizeUtils.getDpValue(7f, itemView.context).toInt()

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(item: String) {
            if (item == "") return
            itemView.context.let {
                if (clickPos == bindingAdapterPosition) {
                    binding.tvSortText.setPadding(
                        sidePadding,
                        topBottomPadding,
                        sidePadding,
                        topBottomPadding
                    )
                    binding.tvSortText.background =
                        it.getDrawable(R.drawable.bg_border_radius_100_btnactivie)
                    binding.tvSortText.setTextColor(it.getColor(R.color.state_enable_gray_25))
                } else {
                    binding.tvSortText.setPadding(
                        sidePadding,
                        topBottomPadding,
                        sidePadding,
                        topBottomPadding
                    )
                    binding.tvSortText.background =
                        it.getDrawable(R.drawable.bg_border_radius_100_darkblue25)
                    binding.tvSortText.setTextColor(it.getColor(R.color.state_active_gray_900))

                }
            }
            binding.tvSortText.text = item
            binding.tvSortText.setOnClickListener {
                val oldpos = clickPos
                clickPos = bindingAdapterPosition
                bindingAdapter?.notifyItemChanged(oldpos)
                bindingAdapter?.notifyItemChanged(clickPos)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendClubSortListVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tab_club_sort_layout, parent, false)
        return RecommendClubSortListVH(TabClubSortLayoutBinding.bind(view))
    }

    override fun onBindViewHolder(holder: RecommendClubSortListVH, position: Int) {
        if (holder.bindingAdapterPosition == RecyclerView.NO_POSITION) {
            return
        }
        holder.bind(clubSortList[holder.bindingAdapterPosition])
    }

    override fun getItemCount() = clubSortList.size


}
