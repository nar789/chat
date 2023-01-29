package com.rndeep.fns_fantoo.ui.home.tabhome

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TabHomeBannerImgLayoutBinding
import com.rndeep.fns_fantoo.data.local.model.BannerItem
import com.rndeep.fns_fantoo.utils.SizeUtils

class HomeBannerRcAdapter() : RecyclerView.Adapter<HomeBannerRcAdapter.BannerViewPagerVH>() {

    private val itemLists=ArrayList<BannerItem>()

    inner class BannerViewPagerVH(private val binding:TabHomeBannerImgLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageWidth = SizeUtils.getDeviceSize(itemView.context).width-SizeUtils.getDpValue(20f,itemView.context).toInt()
        fun bind(item : BannerItem){
            Glide.with(itemView)
                .load(item.imageUrl)
                .override(
                    imageWidth,
                    imageWidth*919/2064)
                .fitCenter()
                .into(itemView.findViewById(R.id.ivBannerImage))
            binding.ivBannerImage.scaleType=ImageView.ScaleType.FIT_XY
            binding.ivBannerImage.clipToOutline=true
            itemView.setOnClickListener {
                //배너 선택시 해당 moveurl 로 이동
                val intent =Intent(Intent.ACTION_VIEW, Uri.parse(item.moveLink))
                itemView.context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewPagerVH {

        val view =LayoutInflater.from(parent.context).inflate(R.layout.tab_home_banner_img_layout,parent,false)

        return BannerViewPagerVH(TabHomeBannerImgLayoutBinding.bind(view))
    }

    override fun onBindViewHolder(holder: BannerViewPagerVH, position: Int) {
        if(holder.bindingAdapterPosition==RecyclerView.NO_POSITION){
            return
        }
        holder.bind(itemLists[holder.bindingAdapterPosition])
    }

    override fun getItemCount()= itemLists.size

    fun setItems(items:List<BannerItem>){
        itemLists.addAll(items)
    }

    fun addAllItem(){
        val preSize = itemLists.size
        itemLists.addAll(itemLists)
        notifyItemRangeInserted(preSize,itemCount)
    }
}