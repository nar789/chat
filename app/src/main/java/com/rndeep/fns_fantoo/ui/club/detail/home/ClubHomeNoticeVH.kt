package com.rndeep.fns_fantoo.ui.club.detail.home

import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.club.ClubNoticeItem
import com.rndeep.fns_fantoo.databinding.TabClubHomeNoticeBinding
import com.rndeep.fns_fantoo.ui.club.detail.ClubPageFragmentDirections
import com.rndeep.fns_fantoo.ui.club.detail.ClubPageListAdapter
import com.rndeep.fns_fantoo.ui.community.BoardInfo
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.CustomDividerDecoration
import com.rndeep.fns_fantoo.utils.addSingleItemDecoRation

//상단 공지
class ClubHomeNoticeVH(
    private val binding: TabClubHomeNoticeBinding,
    private val noticeListener: BoardListeners.OnBoardNoticeClickListener?
) : RecyclerView.ViewHolder(binding.root) {

    private val clubNoticeAdapter = ClubNoticeAdapter()

    private val noticeDividerDecoration =
        CustomDividerDecoration(
            2f,
            20f,
            itemView.context.getColor(R.color.gray_400_opacity12),
            false
        )

    fun bind(noticeItem: List<ClubNoticeItem>) {
        if (noticeItem.isEmpty()) {
            binding.tvNoNotice.visibility = View.VISIBLE
            binding.rcCommunityNotice.visibility = View.GONE
            binding.tvNoticeMore.visibility = View.GONE
        } else {
            binding.tvNoNotice.visibility = View.GONE
            binding.rcCommunityNotice.visibility = View.VISIBLE
            binding.tvNoticeMore.visibility = View.VISIBLE
            binding.rcCommunityNotice.layoutManager = LinearLayoutManager(itemView.context)
            binding.rcCommunityNotice.addSingleItemDecoRation(noticeDividerDecoration)
            clubNoticeAdapter.setItems(noticeItem)
            clubNoticeAdapter.setOnNoticeListener(noticeListener)
            binding.rcCommunityNotice.adapter = clubNoticeAdapter
        }

        //공지 더보기 이동
        binding.tvNoticeMore.setOnClickListener {
            noticeListener?.onNoticeMore()
        }

    }
}