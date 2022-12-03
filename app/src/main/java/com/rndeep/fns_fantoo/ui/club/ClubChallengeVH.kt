package com.rndeep.fns_fantoo.ui.club

import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TabClubChallengeBinding
import com.rndeep.fns_fantoo.ui.club.ChallengeListAdapter
import com.rndeep.fns_fantoo.data.remote.model.ClubChallengeItem
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.CustomDividerDecoration
import com.rndeep.fns_fantoo.utils.addSingleItemDecoRation

class ClubChallengeVH(private val binding: TabClubChallengeBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val challengeListAdapter = ChallengeListAdapter()
    private val customDivider = CustomDividerDecoration(
        0.5f, 0f, itemView.context.getColor(R.color.gray_400_opacity12), false
    )

    fun bind(challengeItems: List<ClubChallengeItem>) {
        binding.rcChallengeList.layoutManager = LinearLayoutManager(itemView.context)
        challengeListAdapter.setChallengeItem(challengeItems)
        binding.rcChallengeList.addSingleItemDecoRation(customDivider)
        binding.rcChallengeList.adapter = challengeListAdapter

        binding.tvChallengeShowAll.setOnClickListener {
            itemView.findNavController().navigate(R.id.action_clubTabFragment_to_clubChallengeFragment)
        }

    }

}