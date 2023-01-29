package com.rndeep.fns_fantoo.ui.club.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rndeep.fns_fantoo.data.remote.model.ClubPagePostListType
import java.lang.ref.WeakReference

class ClubPageFragmentAdapter(fm :FragmentManager,lifecycle: Lifecycle) :FragmentStateAdapter(fm,lifecycle) {
    private var fragmentList =ArrayList<ClubPagePostListType>()
    override fun getItemCount()=fragmentList.size


    override fun createFragment(position: Int): Fragment {
        return fragmentList[position].fragment
    }
    fun getFragment(position: Int)=createFragment(position)

    fun setFragmentItem(items : ArrayList<ClubPagePostListType>){
        this.fragmentList=items
    }
}