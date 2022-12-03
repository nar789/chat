package com.rndeep.fns_fantoo.ui.home.tabpopular

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.CategoryPopularTrandingBinding
import com.rndeep.fns_fantoo.data.local.model.TrendTagItem

class TrendingAdapter() :RecyclerView.Adapter<PopularCategoryTrendVH>() {


    private var trendItemList = ArrayList<ArrayList<TrendTagItem>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularCategoryTrendVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_popular_tranding,parent,false)
        return PopularCategoryTrendVH(CategoryPopularTrandingBinding.bind(view))
    }

    override fun onBindViewHolder(holder: PopularCategoryTrendVH, position: Int) {
        holder.bannerBind(trendItemList[holder.bindingAdapterPosition])
    }

    override fun getItemCount()=trendItemList.size

    fun setItems(items : ArrayList<ArrayList<TrendTagItem>>){
        this.trendItemList=items
    }

}