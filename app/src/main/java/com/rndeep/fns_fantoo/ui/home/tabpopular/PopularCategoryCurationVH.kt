package com.rndeep.fns_fantoo.ui.home.tabpopular

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.databinding.CategoryPopularCurationBinding
import com.rndeep.fns_fantoo.data.local.model.CurationDataItem
import com.rndeep.fns_fantoo.utils.HorizontalMarginItemDecoration
import com.rndeep.fns_fantoo.utils.addSingleItemDecoRation

class PopularCategoryCurationVH(private val binding:CategoryPopularCurationBinding) :RecyclerView.ViewHolder(binding.root) {
    private val popularCurationAdapter = PopularCurationAdapter()
    private val curationDeco = HorizontalMarginItemDecoration(20f,5f,itemView.context)
    fun curationBind(curationItems :ArrayList<CurationDataItem>){
        binding.rcCurationList.layoutManager=LinearLayoutManager(itemView.context,LinearLayoutManager.HORIZONTAL,false)
        binding.rcCurationList.adapter=popularCurationAdapter
        binding.rcCurationList.addSingleItemDecoRation(curationDeco)
        popularCurationAdapter.setCurationItem(curationItems)

    }
}