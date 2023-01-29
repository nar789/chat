package com.rndeep.fns_fantoo.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeTabViewPagerAdapter(fm :FragmentManager,lifecycle : Lifecycle) :FragmentStateAdapter(fm,lifecycle) {
    private var fragmentList = listOf<Fragment>()

    override fun getItemCount()=fragmentList.size

    override fun createFragment(position: Int)=fragmentList[position]

    fun getFragment(position: Int) = fragmentList[position]

    fun setFragmentList(items : List<Fragment>){
        this.fragmentList=items
    }
}