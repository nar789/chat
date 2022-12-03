package com.rndeep.fns_fantoo.ui.community.mypage

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.ui.common.comment.CommentImageAdapter
import com.rndeep.fns_fantoo.data.remote.model.community.DetailAttachList
import com.rndeep.fns_fantoo.data.remote.model.community.MyCommentItem
import com.rndeep.fns_fantoo.databinding.TabCommunityMyCommentLayoutBinding
import com.rndeep.fns_fantoo.utils.TimeUtils
import com.rndeep.fns_fantoo.utils.setProfileAvatar

class MyCommentAdapter : ListAdapter<MyCommentItem,RecyclerView.ViewHolder>(diff) {

    inner class MyCommentVH(
        private val binding: TabCommunityMyCommentLayoutBinding,
        private val listener: MyCommentClickListener?,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(commentItem: MyCommentItem) {
            binding.ivCommentOption.visibility=View.INVISIBLE
            //프로필 이미지
            setProfileAvatar(binding.ivProfileThumbnail,itemView.context.getString(R.string.imageUrlBase,commentItem.image))
            //닉네임
            binding.tvNickName.text = commentItem.userNick
            //게시판 이름
            binding.tvBoardTitle.text = commentItem.code
            //작성 날짜
            binding.tvCreateDate.text = TimeUtils.diffTimeWithCurrentTime(commentItem.createDate)
            //댓글 내용
            binding.tvContent.text = commentItem.content
            //게시글 제목
            binding.tvPostTitle.text= commentItem.postTitle

            if (commentItem.attachList == null && commentItem.openGraphList == null) binding.rcCommentImage.visibility = View.GONE
            commentItem.attachList?.let {
                if (it.isEmpty() && commentItem.openGraphList.isNullOrEmpty()) {
                    binding.rcCommentImage.visibility = View.GONE
                    return@let
                } else {
                    binding.rcCommentImage.visibility = View.VISIBLE
                    binding.rcCommentImage.layoutManager = LinearLayoutManager(itemView.context)
                    val items = arrayListOf<DetailAttachList>()
                    if(it.isNotEmpty()) items.add(DetailAttachList("image", it[0].id))
                    else if(commentItem.openGraphList?.isNotEmpty()==true) items.add(DetailAttachList("link", commentItem.openGraphList[0].image))
                    binding.rcCommentImage.adapter = CommentImageAdapter(items,commentItem.openGraphList)
                }
            }

            itemView.setOnClickListener {
                listener?.onMyCommentClick(commentItem.comPostId,commentItem.code,if(commentItem.parentReplyId!=0) commentItem.parentReplyId else commentItem.replyId.toInt())
            }

        }
    }

    interface MyCommentClickListener{
        fun onMyCommentClick(postId:Int,categoryCode :String,replyId :Int)
    }

    private var myCommentClickListener :MyCommentClickListener? =null

    fun setOnMyCommentClickListener(listener: MyCommentClickListener){
        this.myCommentClickListener=listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tab_community_my_comment_layout, parent, false)
        return MyCommentVH(TabCommunityMyCommentLayoutBinding.bind(view),myCommentClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyCommentVH) {
            holder.bind(currentList[holder.bindingAdapterPosition])
        }
    }

    companion object{
        val diff =object  :DiffUtil.ItemCallback<MyCommentItem>(){
            override fun areItemsTheSame(oldItem: MyCommentItem, newItem: MyCommentItem): Boolean {
                return oldItem==newItem
            }

            override fun areContentsTheSame(
                oldItem: MyCommentItem,
                newItem: MyCommentItem
            ): Boolean {
                return oldItem.replyId == newItem.replyId ||
                        oldItem.pieceBlockYn == newItem.pieceBlockYn ||
                        oldItem.userBlockYn == newItem.userBlockYn
            }
        }
    }

}