package com.rndeep.fns_fantoo.ui.home.tabpopular

import android.os.Parcelable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.rndeep.fns_fantoo.databinding.CategoryPopularTrandingBinding
import com.rndeep.fns_fantoo.data.local.model.TrendTagItem
import com.rndeep.fns_fantoo.utils.HorizontalMarginItemDecoration

class PopularCategoryTrendVH (private val binding: CategoryPopularTrandingBinding) : RecyclerView.ViewHolder(binding.root){


    private var recyclerviewState : Parcelable? =null


    val trendAdapter = PopularTrendRcAdapter()

    private val itemDeco = HorizontalMarginItemDecoration(0f,0f,itemView.context)

    fun bannerBind(tendItem:List<TrendTagItem>){

        binding.rcPopularTending.adapter=trendAdapter
        trendAdapter.setItems(tendItem)
        binding.rcPopularTending.layoutManager=
            FlexboxLayoutManager(itemView.context,FlexDirection.ROW,FlexWrap.WRAP).apply {
                justifyContent=JustifyContent.FLEX_START
            }

        recyclerviewState?.let {
            (binding.rcPopularTending.layoutManager as LinearLayoutManager).onRestoreInstanceState(it)
        }

        settingRCSetting()
    }

    private fun settingRCSetting(){

        //간격 주는 itemDecoration
        binding.rcPopularTending.removeItemDecoration(itemDeco)
        binding.rcPopularTending.addItemDecoration(itemDeco)

    }


}