package com.rndeep.fns_fantoo.ui.community.board.notice

import android.annotation.SuppressLint
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.community.CommunityNoticeItem
import com.rndeep.fns_fantoo.databinding.BoardNoticeLayoutBinding
import com.rndeep.fns_fantoo.utils.SizeUtils
import com.rndeep.fns_fantoo.utils.VerticalImageSpan
import com.rndeep.fns_fantoo.utils.setProfileAvatar

class BoardNoticeAdapter :RecyclerView.Adapter<BoardNoticeAdapter.BoardNoticeVH>() {

    inner class BoardNoticeVH(private val binding : BoardNoticeLayoutBinding) :RecyclerView.ViewHolder(binding.root){

        fun bind(item:CommunityNoticeItem,itemClickListener: NoticeItemClickListener?){
            //썸네일 이미지
            setProfileAvatar(binding.ivNoticeThumbnail,
                itemView.context.getString(R.string.imageUrlBase,item.userPhoto),
                R.drawable.profile_character2)

            binding.tvNoticeNickName.text=item.userNick
            binding.tvNoticeDate.text=item.createDate
            binding.ivNoticeOptions.visibility=View.GONE

            if(item.topYN){
                setImageSpanTitle(item.title)
            }else{
                binding.tvNoticeContent.text=item.title
            }

            binding.ivNoticeOptions.setOnClickListener {
                itemClickListener?.onOptionClick(item.postId)
            }

            itemView.setOnClickListener {
                itemClickListener?.onItemClick(item.postId)
            }

        }

        private fun setImageSpanTitle(contentText :String){
            val imageSize = SizeUtils.getSpValue(16f,itemView.context).toInt()
            var imageSpan: VerticalImageSpan? = null
            imageSpan = itemView.context.getDrawable(R.drawable.posting_fix)
                ?.apply {
                setBounds(0, 0, imageSize, imageSize)
            }?.let {
                VerticalImageSpan(it)
            }

            imageSpan ?: return
            val sbString = SpannableStringBuilder("   ${contentText}").apply {
                setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            binding.tvNoticeContent.text=sbString
        }

    }

    private var noticeItems = listOf<CommunityNoticeItem>()


    interface NoticeItemClickListener{
        fun onItemClick(postId :Int)
        fun onOptionClick(postId :Int)
    }

    private var noticeItemClickListener : NoticeItemClickListener?=null

    fun setOnNoticeItemClickListener(listener: NoticeItemClickListener){
        this.noticeItemClickListener=listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardNoticeVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.board_notice_layout,parent,false)
        return BoardNoticeVH(BoardNoticeLayoutBinding.bind(view))
    }

    override fun onBindViewHolder(holder: BoardNoticeVH, position: Int) {
        holder.bind(noticeItems[holder.bindingAdapterPosition],noticeItemClickListener)
    }

    override fun getItemCount()=noticeItems.size

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items : List<CommunityNoticeItem>){
        this.noticeItems=items
        notifyDataSetChanged()
    }

}