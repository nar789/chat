package com.rndeep.fns_fantoo.ui.community.boardlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TabCommunityBoardShowAllLayoutBinding
import com.rndeep.fns_fantoo.ui.community.BoardInfo
import com.rndeep.fns_fantoo.data.remote.model.community.CategoryBoardCategoryList
import com.rndeep.fns_fantoo.utils.ConstVariable

class CommunityBoardShowAllAdapter :
    RecyclerView.Adapter<CommunityBoardShowAllAdapter.BoardShowAllVH>() {
    private var boardList= listOf<CategoryBoardCategoryList>()

    interface OnFavoriteClickListener{
        fun onFavoriteIconClick(v:View ,boardId: String,isBookmark :Boolean)
        fun onBoardItemClick(v:View,position: Int,item: BoardInfo)
    }

    private var onFavoriteClickListener: OnFavoriteClickListener?=null

    fun setOnFavoriteClickListener(listener: OnFavoriteClickListener){
        this.onFavoriteClickListener=listener
    }

    inner class BoardShowAllVH(private val binding : TabCommunityBoardShowAllLayoutBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item : CategoryBoardCategoryList, listener: OnFavoriteClickListener?){
            val local = itemView.context.resources.configuration.locales[0]
            binding.ivFavorite.setOnClickListener {
                listener?.onFavoriteIconClick(itemView, item.code ,item.favorite==true)
            }
            binding.ivFavorite.setColorFilter( if(item.favorite==true){
                itemView.context.getColor(R.color.state_active_secondary_default)
            }else{
                itemView.context.getColor(R.color.state_disabled_gray_200)
            })

            itemView.setOnClickListener {
                when(local.language){
                    "ko"->{
                        listener?.onBoardItemClick(itemView,bindingAdapterPosition, BoardInfo(item.codeNameKo,item.code,ConstVariable.BOARD_COMMON_TYPE))
                    }
                    else ->{
                        listener?.onBoardItemClick(itemView,bindingAdapterPosition, BoardInfo(item.codeNameEn,item.code,ConstVariable.BOARD_COMMON_TYPE))
                    }
                }

            }
            when(local.language){
                "ko"->{
                    binding.tvBoardListTitle.text=item.codeNameKo
                }
                else ->{
                    binding.tvBoardListTitle.text=item.codeNameEn
                }
            }
            if(item.code.length>2){
                binding.tvBoardCode.text = item.code.substring(0,2)
            }else{
                binding.tvBoardCode.text = item.code
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardShowAllVH {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.tab_community_board_show_all_layout,parent,false)
        return BoardShowAllVH(TabCommunityBoardShowAllLayoutBinding.bind(view))
    }

    override fun onBindViewHolder(holder: BoardShowAllVH, position: Int) {
        if(holder.bindingAdapterPosition==RecyclerView.NO_POSITION){
            return
        }
        holder.bind(boardList[holder.bindingAdapterPosition],onFavoriteClickListener)
    }

    override fun getItemCount() :Int {
        return boardList.size

    }
    fun setItems(list : List<CategoryBoardCategoryList>){
        this.boardList=list
        notifyDataSetChanged()
    }

}