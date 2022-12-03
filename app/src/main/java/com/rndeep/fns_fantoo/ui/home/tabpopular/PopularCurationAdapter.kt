package com.rndeep.fns_fantoo.ui.home.tabpopular

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.CategoryPopularCurationLayoutBinding
import com.rndeep.fns_fantoo.data.local.model.CurationDataItem

class PopularCurationAdapter :RecyclerView.Adapter<PopularCurationAdapter.PopularCurationVH>() {

    private var curationItems =ArrayList<CurationDataItem>()

    inner class PopularCurationVH(private val binding : CategoryPopularCurationLayoutBinding) :RecyclerView.ViewHolder(binding.root){

        fun bind(item : CurationDataItem){
            binding.tvCurationText.text=item.curationText
            binding.ivCurationImage.clipToOutline=true
            binding.tvCurationText.setTextColor(itemView.context.getColor(R.color.gray_25))
            Glide.with(itemView)
                .load(item.curationImage)
                .into(binding.ivCurationImage)
//            Glide.with(itemView)
//                .asBitmap()
//                .load(item.curationImage)
//                .into(object :BitmapImageViewTarget(binding.ivCurationImage){
//                    override fun onResourceReady(
//                        resource: Bitmap,
//                        transition: Transition<in Bitmap>?
//                    ) {
//                        super.onResourceReady(resource, transition)
//                        Palette.from(resource).generate { palette ->
//                            palette?.let { it ->
//                                //원하는 swatch
//                                val dominantSwatch: Palette.Swatch? = it.dominantSwatch
//                                //swatch에서 rbg 값 뽑기
//                                val dominantRgb = dominantSwatch?.rgb
//                                dominantRgb?.let { rgb ->
//                                    //값이 작을수록 어두움
//                                    if(ColorUtils.calculateLuminance(rgb)<0.5){
//                                        binding.tvCurationText.setTextColor(itemView.context.getColor(R.color.gray_25))
//                                    }else{
//                                        binding.tvCurationText.setTextColor(itemView.context.getColor(R.color.gray_900))
//                                    }
//                                }
//                            }
//                        }
//                    }
//                })

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularCurationVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_popular_curation_layout,parent,false)
        return PopularCurationVH(CategoryPopularCurationLayoutBinding.bind(view))
    }

    override fun onBindViewHolder(holder: PopularCurationVH, position: Int) {
        holder.bind(curationItems[holder.bindingAdapterPosition])

    }

    override fun getItemCount()=curationItems.size

    fun setCurationItem(newItems :ArrayList<CurationDataItem>){
        this.curationItems=newItems
    }

}