package com.rndeep.fns_fantoo.ui.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TabCommunityBoardlistLayoutBinding
import com.rndeep.fns_fantoo.data.remote.model.community.CategoryBoardCategoryList
import com.rndeep.fns_fantoo.utils.ConstVariable

class CommunityBoardRcAdapter() :RecyclerView.Adapter<CommunityBoardRcAdapter.CommunityBoardVH>() {

    private var boardListItems = listOf<CategoryBoardCategoryList>()
    inner class CommunityBoardVH(private val binding:TabCommunityBoardlistLayoutBinding) :RecyclerView.ViewHolder(binding.root){

        fun bind(item: CategoryBoardCategoryList){
            val local = itemView.context.resources.configuration.locales[0]
            // local.language => 한국(ko) 미국(en) 일본(ja) 중국(zh) 대만(zh)....
            // local.country => 한국(KR) 미국(US) 일본(JP) 중국(CH) 대만(TW)....
            // 확실한 구분을 위해서는 country 가 낫지만 언어 설정이니까..
            when(local.language){
                "ko"->{
                    binding.tvBoardListTitle.text=item.codeNameKo
                }
                else->{
                    binding.tvBoardListTitle.text=item.codeNameEn
                }
            }
            //특정 게시판 이동
            binding.tvBoardListTitle.setOnClickListener {
                val boardAction = CommunityTabFragmentDirections.actionCommunityTabFragmentToCommunityboard(
                    BoardInfo(binding.tvBoardListTitle.text.toString(),item.code,ConstVariable.BOARD_COMMON_TYPE)
                )
                itemView.findNavController().navigate(boardAction)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityBoardVH {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.tab_community_boardlist_layout,parent,false)
        return CommunityBoardVH(TabCommunityBoardlistLayoutBinding.bind(view))
    }

    override fun onBindViewHolder(holder: CommunityBoardVH, position: Int) {
        if(holder.bindingAdapterPosition==RecyclerView.NO_POSITION){
            return
        }
        holder.bind(boardListItems[holder.bindingAdapterPosition])
    }

    override fun getItemCount() = boardListItems.size

    fun setItems(items :List<CategoryBoardCategoryList>){
        boardListItems=items
        notifyItemRangeChanged(0,boardListItems.size)
    }


}