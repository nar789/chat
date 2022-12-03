package com.rndeep.fns_fantoo.ui.club

import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.MyClubListItem
import com.rndeep.fns_fantoo.databinding.TabClubMyClubBinding
import com.rndeep.fns_fantoo.utils.HorizontalMarginItemDecoration
import com.rndeep.fns_fantoo.utils.addSingleItemDecoRation

class MyClubListVH(
    private val binding: TabClubMyClubBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val myClubAdapter = MainMyClubAdapter()
    private val clubListItemDeco = HorizontalMarginItemDecoration(
        20f, 6f, itemView.context
    )

    fun myClubBind(myClubItems: List<MyClubListItem>) {
        if (myClubItems.isEmpty()) {
            binding.tvNoMyClub.visibility = View.VISIBLE
            binding.rcMyClub.visibility = View.GONE
        } else {
            binding.tvNoMyClub.visibility = View.GONE
            binding.rcMyClub.visibility = View.VISIBLE
            myClubAdapter.setItem(myClubItems)
            binding.rcMyClub.layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            binding.rcMyClub.addSingleItemDecoRation(clubListItemDeco)
            val animator = binding.rcMyClub.itemAnimator
            if (animator is SimpleItemAnimator) {
                animator.supportsChangeAnimations = false
            }
            binding.rcMyClub.adapter = myClubAdapter

        }
        val showAllClickListener = View.OnClickListener {
            itemView.findNavController()
                .navigate(R.id.action_clubTabFragment_to_myClubListFragment2)
        }
        binding.tvMyClubShowAll.setOnClickListener(showAllClickListener)

    }
}