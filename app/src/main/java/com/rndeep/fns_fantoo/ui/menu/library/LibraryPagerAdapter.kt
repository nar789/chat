package com.rndeep.fns_fantoo.ui.menu.library

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class LibraryPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        COMMUNITY_PAGE_INDEX to { CommunityLibraryFragment() },
        CLUB_PAGE_INDEX to { ClubLibraryFragment() }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }

    companion object {
        const val COMMUNITY_PAGE_INDEX = 0
        const val CLUB_PAGE_INDEX = 1
    }
}