package com.rndeep.fns_fantoo.ui.home.tabpopular

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.rndeep.fns_fantoo.databinding.CategoryPopularTrandingViewpagerBinding
import com.rndeep.fns_fantoo.data.local.model.TrendTagItem

class PopularCategoryTrendViewPagerVH(private val binding: CategoryPopularTrandingViewpagerBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val trendingAdapter = TrendingAdapter()

    fun trendBind(
        itemList: ArrayList<ArrayList<TrendTagItem>>,
        trendClickListener: CategoryPopularAdapter.OnTrendClickListener?
    ) {

        binding.trendingVP.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        trendingAdapter.setItems(itemList)
        binding.trendingVP.adapter = trendingAdapter

        binding.viewpageDotIndicator.attachTo(binding.trendingVP)

        binding.llGlobalContainer.setOnClickListener {
            trendClickListener?.onGlobalClick(itemView)
        }
    }
}