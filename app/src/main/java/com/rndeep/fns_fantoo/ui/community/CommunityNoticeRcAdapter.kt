package com.rndeep.fns_fantoo.ui.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityNoticeItem
import com.rndeep.fns_fantoo.databinding.TabCommunityNoticeLayoutBinding
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners

class CommunityNoticeRcAdapter() :RecyclerView.Adapter<CommunityNoticeRcAdapter.CommunityNoticeVH>() {
    private var noticeItems = listOf<CommunityNoticeItem>()

    private var listener : BoardListeners.OnBoardNoticeClickListener? =null

    inner class CommunityNoticeVH(private val binding :TabCommunityNoticeLayoutBinding,
    private val listener: BoardListeners.OnBoardNoticeClickListener?):RecyclerView.ViewHolder(binding.root){

        fun bind(item:CommunityNoticeItem){
            binding.tvNoticeText.text=item.title
            itemView.setOnClickListener {
                listener?.onNoticeClick(itemView,item.postId,null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityNoticeVH {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.tab_community_notice_layout,parent,false)
        return CommunityNoticeVH(TabCommunityNoticeLayoutBinding.bind(view),listener)
    }

    override fun onBindViewHolder(holder: CommunityNoticeVH, position: Int) {
        if(holder.bindingAdapterPosition==RecyclerView.NO_POSITION){
            return
        }
        holder.bind(noticeItems[holder.bindingAdapterPosition])
    }

    override fun getItemCount()=noticeItems.size

    fun setItems(items :List<CommunityNoticeItem>){
        this.noticeItems=items
    }

    fun setNoticeClickListener(listener: BoardListeners.OnBoardNoticeClickListener?){
        this.listener=listener
    }

}