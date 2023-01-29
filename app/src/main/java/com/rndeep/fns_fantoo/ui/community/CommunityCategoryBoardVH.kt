package com.rndeep.fns_fantoo.ui.community

import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.databinding.TabCommunityBoardlistBinding
import com.rndeep.fns_fantoo.data.remote.model.community.CategoryBoardCategoryList
import com.rndeep.fns_fantoo.utils.HorizontalMarginItemDecoration
import com.rndeep.fns_fantoo.utils.addSingleItemDecoRation

class CommunityCategoryBoardVH(private val binding: TabCommunityBoardlistBinding) : RecyclerView.ViewHolder(binding.root){
    private val itemDeco =HorizontalMarginItemDecoration(8f,3f,itemView.context)
    private val boardAdapter= CommunityBoardRcAdapter()

    init {
        binding.rcCommunityBoardList.layoutManager= LinearLayoutManager(itemView.context,LinearLayoutManager.HORIZONTAL,false)
        binding.rcCommunityBoardList.adapter=boardAdapter
        binding.rcCommunityBoardList.addSingleItemDecoRation(itemDeco)
    }
    fun boardBind(items:List<CategoryBoardCategoryList>){
        boardAdapter.setItems(items)
        //전체 리스트 보기
        binding.ivCommunityTotalView.setOnClickListener {
            val action = CommunityTabFragmentDirections.actionCommunityTabFragmentToCommunityBoardShowAllFragment()
            itemView.findNavController().navigate(action)
        }
    }
}