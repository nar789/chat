package com.rndeep.fns_fantoo.ui.club.detail.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.club.ClubNoticeItem
import com.rndeep.fns_fantoo.databinding.TabCommunityNoticeLayoutBinding
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners

class ClubNoticeAdapter : RecyclerView.Adapter<ClubNoticeAdapter.ClubNoticeVH>() {
    private var noticeItems = listOf<ClubNoticeItem>()

    private var listener : BoardListeners.OnBoardNoticeClickListener? =null

    fun setOnNoticeListener(listener: BoardListeners.OnBoardNoticeClickListener?){
        this.listener=listener
    }

    inner class ClubNoticeVH(private val binding : TabCommunityNoticeLayoutBinding,
                                  private val listener: BoardListeners.OnBoardNoticeClickListener?):
        RecyclerView.ViewHolder(binding.root){

        fun bind(item: ClubNoticeItem){
            binding.tvNoticeText.text=item.subject
            itemView.setOnClickListener {
                listener?.onNoticeClick(itemView,item.postId,item.clubId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubNoticeVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tab_community_notice_layout,parent,false)
        return ClubNoticeVH(TabCommunityNoticeLayoutBinding.bind(view),listener)
    }

    override fun onBindViewHolder(holder: ClubNoticeVH, position: Int) {
        if(holder.bindingAdapterPosition== RecyclerView.NO_POSITION){
            return
        }
        holder.bind(noticeItems[holder.bindingAdapterPosition])
    }

    override fun getItemCount()=noticeItems.size

    fun setItems(items :List<ClubNoticeItem>){
        this.noticeItems=items
    }

    fun setNoticeClickListener(listener: BoardListeners.OnBoardNoticeClickListener?){
        this.listener=listener
    }

}