package com.rndeep.fns_fantoo.ui.menu.fantooclub

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rndeep.fns_fantoo.ui.menu.fantooclub.category.CategoryFragment
import com.rndeep.fns_fantoo.ui.menu.fantooclub.contents.ContentsFragment

class FantooClubPagerAdapter(fragment: Fragment, private val clubId: String) : FragmentStateAdapter(fragment) {

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        HOME_PAGE_INDEX to { getFragment(HOME_PAGE_INDEX) },
        CATEGORY_PAGE_INDEX to { getFragment(CATEGORY_PAGE_INDEX) }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }

    private fun getFragment(page: Int): Fragment {
        val args = Bundle()
        args.putString(KEY_CLUB_ID, clubId)
        return when (page) {
            HOME_PAGE_INDEX -> {
                val fragment = ContentsFragment()
                fragment.arguments = args
                fragment
            }
            else -> {
                val fragment = CategoryFragment()
                fragment.arguments = args
                fragment
            }
        }
    }

    companion object {
        const val HOME_PAGE_INDEX = 0
        const val CATEGORY_PAGE_INDEX = 1
        const val KEY_CLUB_ID = "clubId"
    }
}