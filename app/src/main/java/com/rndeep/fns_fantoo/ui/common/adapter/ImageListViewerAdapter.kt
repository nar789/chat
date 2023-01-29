package com.rndeep.fns_fantoo.ui.common.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.ImageViewerLayoutBinding
import com.rndeep.fns_fantoo.utils.setImageWithPlaceHolder
import timber.log.Timber

class ImageListViewerAdapter : RecyclerView.Adapter<ImageListViewerAdapter.ImageListViewerVH>() {

    inner class ImageListViewerVH(private val binding : ImageViewerLayoutBinding) : RecyclerView.ViewHolder(binding.root){

        @SuppressLint("ClickableViewAccessibility")
        fun bind(attachId : String){
            setImageWithPlaceHolder(binding.photo,itemView.context.getString(R.string.imageUrlBase,attachId))

        }
    }

    private var attachList : List<String> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListViewerVH {
        val view2 = LayoutInflater.from(parent.context).inflate(R.layout.image_viewer_layout,parent,false)

        return ImageListViewerVH(ImageViewerLayoutBinding.bind(view2))
    }

    override fun onBindViewHolder(holder: ImageListViewerVH, position: Int) {
        holder.bind(attachList[holder.bindingAdapterPosition])
    }

    override fun getItemCount()=attachList.size

    fun setItem(attachItem : List<String>){
        this.attachList= attachItem
        notifyDataSetChanged()
    }
}