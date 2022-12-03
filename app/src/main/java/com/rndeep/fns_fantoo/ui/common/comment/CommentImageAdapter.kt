package com.rndeep.fns_fantoo.ui.common.comment

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.community.DetailAttachList
import com.rndeep.fns_fantoo.data.remote.model.post.CommunityOpenGraphItem
import com.rndeep.fns_fantoo.databinding.PostDetailCommentImageLayoutBinding
import com.rndeep.fns_fantoo.utils.setImageWithPlaceHolder

class CommentImageAdapter(
    private val commentImageData : List<DetailAttachList>?,
    private val openGraphItemData : List<CommunityOpenGraphItem>?
    ) : RecyclerView.Adapter<CommentImageAdapter.CommentImageVH>() {

    interface CommentImageClickListener{
        fun onImageClick(imageUrl : String)
    }

    private var commentImageClickListener : CommentImageClickListener? = null

    fun setOnCommentImageClickListener(listener: CommentImageClickListener){
        commentImageClickListener=listener
    }

    inner class CommentImageVH(
        private val binding: PostDetailCommentImageLayoutBinding,
        private val listener: CommentImageClickListener?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun imageBind(imageData : DetailAttachList){
            binding.ivCommentImage.visibility=View.VISIBLE
            binding.llCommentLinkContainer.visibility=View.GONE
            setImageWithPlaceHolder(binding.ivCommentImage,itemView.context.getString(R.string.imageUrlBase,imageData.id))
            itemView.setOnClickListener {
                listener?.onImageClick(itemView.context.getString(R.string.imageUrlBase,imageData.id))
            }
        }

        fun linkBind(linkData :CommunityOpenGraphItem){
            binding.ivCommentImage.visibility=View.GONE
            binding.llCommentLinkContainer.visibility=View.VISIBLE
            setImageWithPlaceHolder(binding.ivLinkImage,linkData.image)
            binding.ivLinkImage.clipToOutline=true

            binding.tvLinkTitle.text=linkData.title
            binding.tvLinkContent.text=linkData.url
            itemView.setOnClickListener {
                if(linkData.url.startsWith("http")){
                    val intent = Intent(Intent.ACTION_VIEW,Uri.parse(linkData.domain))
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentImageVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_detail_comment_image_layout,parent,false)
        return CommentImageVH(PostDetailCommentImageLayoutBinding.bind(view),commentImageClickListener)
    }

    override fun onBindViewHolder(holder: CommentImageVH, position: Int) {
        if(commentImageData?.isNotEmpty() == true){
            holder.imageBind(commentImageData[holder.bindingAdapterPosition])
        }else if(openGraphItemData?.isNotEmpty() == true){
            holder.linkBind(openGraphItemData[holder.bindingAdapterPosition])
        }
    }

    override fun getItemCount() : Int{
        return if (openGraphItemData.isNullOrEmpty()){
            (commentImageData?.size ?: 0)
        }else{
            openGraphItemData.size
        }
    }

}