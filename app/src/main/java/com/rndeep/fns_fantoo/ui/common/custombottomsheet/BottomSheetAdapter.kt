package com.rndeep.fns_fantoo.ui.common.custombottomsheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.BottomSheetLayoutBinding
import com.rndeep.fns_fantoo.utils.SizeUtils

class BottomSheetAdapter : RecyclerView.Adapter<BottomSheetAdapter.BottomSheetVH>() {
    private var bottomSheetItems = ArrayList<BottomSheetItem>()

    private var selectPos :Int?=null
    private var ratio = 0.29f
    interface OnItemClickListener{
        fun onItemClick(name:String, pos :Int,oldPos :Int?)
    }

    private var onItemClickListener : OnItemClickListener?=null

    fun setOnItemClickListener(onItemClickListener : OnItemClickListener?){
        this.onItemClickListener=onItemClickListener
    }

    fun setGuideLineRatio(ratio : Float){
        this.ratio=ratio
    }

    inner class BottomSheetVH(private val binding : BottomSheetLayoutBinding) : RecyclerView.ViewHolder(binding.root){

         fun bind(item : BottomSheetItem, clickListener: OnItemClickListener?, oldPos: Int?, guideRatio : Float){
             binding.tvSortText.text=item.itemName
             itemView.setOnClickListener {
                 clickListener?.let {
                     clickListener.onItemClick(item.itemName,bindingAdapterPosition,oldPos)
                 }
             }

             if(item.imageId==null){
                 binding.ivSheetIcon.visibility= View.GONE
                 (binding.tvSortText.layoutParams as ConstraintLayout.LayoutParams).marginStart=0
                 binding.guideline.setGuidelinePercent(guideRatio-0.05f)
             }else{
                 binding.ivSheetIcon.visibility= View.VISIBLE
                 (binding.tvSortText.layoutParams as ConstraintLayout.LayoutParams).marginStart=SizeUtils.getDpValue(20f,itemView.context).toInt()
                 binding.guideline.setGuidelinePercent(guideRatio)
                 Glide.with(itemView)
                     .load(item.imageId)
                     .into(binding.ivSheetIcon)
                 binding.ivSheetIcon.setColorFilter(itemView.context.getColor(R.color.gray_700))
             }

             if(item.subText==null){
                 binding.tvSubText.visibility=View.GONE
             }else{
                 binding.tvSubText.visibility=View.VISIBLE
                 binding.tvSubText.text=item.subText
             }

             when {
                 item.isChecked==null -> {
                     binding.ivItemCheck.visibility=View.GONE
                     if(item.subText==null){
                         binding.tvSortText.setTextAppearance(R.style.Body11622Regular)
                     }else{
                         binding.tvSortText.setTextAppearance(R.style.Body21420Regular)
                     }
                     binding.tvSortText.setTextColor(itemView.context.getColor(R.color.gray_800))
                 }
                 item.isChecked!! -> {
                     binding.ivItemCheck.visibility=View.VISIBLE
                     if(item.subText==null){
                        binding.tvSortText.setTextAppearance(R.style.Title51622Medium)
                     }else{
                         binding.tvSortText.setTextAppearance(R.style.Buttons1420Medium)
                     }
                     binding.tvSortText.setTextColor(itemView.context.getColor(R.color.primary_default))
                 }
                 else -> {
                     binding.ivItemCheck.visibility=View.GONE
                     if(item.subText==null){
                         binding.tvSortText.setTextAppearance(R.style.Body11622Regular)
                     }else{
                         binding.tvSortText.setTextAppearance(R.style.Body21420Regular)
                     }
                     binding.tvSortText.setTextColor(itemView.context.getColor(R.color.gray_800))
                 }
             }


         }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bottom_sheet_layout,parent,false)
        return BottomSheetVH(BottomSheetLayoutBinding.bind(view))
    }

    override fun onBindViewHolder(holder: BottomSheetVH, position: Int) {
        if(holder.bindingAdapterPosition==RecyclerView.NO_POSITION){
            return
        }
        holder.bind(bottomSheetItems[holder.bindingAdapterPosition],onItemClickListener,selectPos,ratio)
    }

    override fun getItemCount(): Int {
        return bottomSheetItems.size
    }

    fun setItems(items :ArrayList<BottomSheetItem>){
        this.bottomSheetItems=items
        notifyItemRangeChanged(0,bottomSheetItems.size)
    }

    fun getItem()=this.bottomSheetItems

}