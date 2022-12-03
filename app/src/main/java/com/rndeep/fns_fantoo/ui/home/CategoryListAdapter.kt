package com.rndeep.fns_fantoo.ui.home

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TabHomeCategorylistLayoutBinding
import com.rndeep.fns_fantoo.utils.SizeUtils

class CategoryListAdapter : RecyclerView.Adapter<CategoryListAdapter.CategoryListVH>() {
    private var listItems = listOf<String>()
    private var selectPos=0
    interface OnCategoryClickListener{
        fun OnCategoryClick(v: View,pos :Int,name:String)
    }

    var onCategoryClickListener : OnCategoryClickListener?=null

    fun setOnCategoryListener(onCategoryClickListener: OnCategoryClickListener){
        this.onCategoryClickListener=onCategoryClickListener
    }

    inner class CategoryListVH(private val binding: TabHomeCategorylistLayoutBinding) :RecyclerView.ViewHolder(binding.root){
        private val displayWidth = SizeUtils.getDeviceSize(itemView.context)

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(name:String, pos:Int, itemSize:Int){
            //최대 4개만 표현하고 넘을시 스크롤 되게 처리
//            val itemCnt=if(itemSize>4)4 else itemSize
            val settingParams = (binding.llCategoryBg.layoutParams as ConstraintLayout.LayoutParams)
            val calculationPaint = Paint()
            calculationPaint.typeface = ResourcesCompat.getFont(itemView.context, R.font.noto_sans_kr_medium)
            calculationPaint.textSize = SizeUtils.getSpValue(16f, itemView.context)
            val textCalculation =
                calculationPaint.measureText(name, 0, name.length)
            when(name){
                itemView.context.getString(R.string.en_home)->{
                    settingParams.width=textCalculation.toInt()+SizeUtils.getDpValue(70f,itemView.context).toInt()
                    binding.tvCategoryName.setPadding(
                        SizeUtils.getDpValue(28f,itemView.context).toInt(),
                        SizeUtils.getDpValue(18f,itemView.context).toInt(),
                        SizeUtils.getDpValue(42f,itemView.context).toInt(),
                        0
                    )
                    binding.tvCategoryName.width = SizeUtils.getDpValue(116f, itemView.context).toInt()
                }
                itemView.context.getString(R.string.en_popular)->{
                    settingParams.width=textCalculation.toInt()+SizeUtils.getDpValue(72f,itemView.context).toInt()
                    binding.tvCategoryName.setPadding(
                        SizeUtils.getDpValue(36f,itemView.context).toInt(),
                        SizeUtils.getDpValue(18f,itemView.context).toInt(),
                        SizeUtils.getDpValue(36f,itemView.context).toInt(),
                        0
                    )
                    binding.tvCategoryName.width = SizeUtils.getDpValue(130f, itemView.context).toInt()
                }
            }
//            (binding.llCategoryBg.layoutParams as ConstraintLayout.LayoutParams).width=displayWidth.width/itemCnt
            binding.tvCategoryName.text=name
            when(pos){
                selectPos->{
                    if(binding.tvCategoryName.text==itemView.context.getString(R.string.en_home)){
//                        binding.llCategoryBg.background=itemView.context.getDrawable(R.color.gray_400)
                        binding.tvCategoryName.background=itemView.context.getDrawable(R.drawable.top_tab1_t)
                        binding.tvCategoryName.setTextColor(itemView.context.getColor(R.color.primary_500))
                    }else if(binding.tvCategoryName.text==itemView.context.getString(R.string.en_popular)){
//                        binding.llCategoryBg.background=itemView.context.getDrawable(R.color.gray_400)
                        binding.tvCategoryName.background=itemView.context.getDrawable(R.drawable.top_tab2_t)
                        binding.tvCategoryName.setTextColor(itemView.context.getColor(R.color.primary_500))
                    }

                }
                else ->{
                    binding.llCategoryBg.background=itemView.context.getDrawable(R.color.transparent)
                    binding.tvCategoryName.background=itemView.context.getDrawable(R.color.transparent)
                    binding.tvCategoryName.setTextColor(itemView.context.getColor(R.color.state_enable_gray_25))
                }
            }

            itemView.setOnClickListener {
                onCategoryClickListener?.OnCategoryClick(itemView,pos,name)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tab_home_categorylist_layout,parent,false)
        return CategoryListVH(TabHomeCategorylistLayoutBinding.bind(view))
    }

    override fun onBindViewHolder(holder: CategoryListVH, position: Int) {
        if(holder.bindingAdapterPosition==RecyclerView.NO_POSITION){
            return
        }
        holder.bind(listItems[holder.bindingAdapterPosition],position,listItems.size)
    }

    override fun getItemCount()=listItems.size

    fun setListItem(items : List<String>){
        this.listItems=items
    }
    fun getSelectName()=listItems[selectPos]

    fun selectCategoryPos(selectPos :Int){
        val oldPos=this.selectPos
        this.selectPos=selectPos
        //데이터 변경 확인을 위한 코드
        if(oldPos-1>=0){
            notifyItemChanged(oldPos-1)
        }
        if(oldPos-1!=selectPos+1){
            notifyItemChanged(selectPos+1)
        }
        if(selectPos-1>=0){
            notifyItemChanged(selectPos-1)
        }
        if (selectPos-1!=oldPos+1){
            notifyItemChanged(oldPos+1)
        }
        notifyItemChanged(oldPos)
        notifyItemChanged(selectPos)
    }

}