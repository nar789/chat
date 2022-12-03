package com.rndeep.fns_fantoo.ui.club.settings.management.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rndeep.fns_fantoo.ui.common.viewgroup.CommonSearchableTopbar
import com.rndeep.fns_fantoo.ui.club.settings.management.ClubAllMembersTabFragment
import com.rndeep.fns_fantoo.ui.club.settings.management.ClubBanMembersTabFragment
import com.rndeep.fns_fantoo.ui.club.settings.management.ClubMembersManagement
import com.rndeep.fns_fantoo.utils.ConstVariable
import java.util.concurrent.CopyOnWriteArrayList

class MembersViewPagerAdapter(
    private val fragmentManager: FragmentManager,
    private val lifecycle: Lifecycle,
    private val parentLifecycleListener: CopyOnWriteArrayList<ClubMembersManagement.ParentLifecycleListener>,
    private val topbar: CommonSearchableTopbar,
    private val entryRouteType: ConstVariable.ClubSetting.MembersEntryRouteType,
    private val clubId: String,
    private val uid: String,
    private val searchModeChangeListener: (Boolean) -> Unit
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    companion object {
        const val TAB_ALL_MEMBER = 0
        const val TAB_BAN_MEMBER = 1
        const val TAB_COUNT = 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ClubAllMembersTabFragment(
                parentLifecycleListener = parentLifecycleListener,
                searchableTopbar = topbar,
                entryRouteType = entryRouteType,
                clubId = clubId,
                uid = uid
            )
            1 -> ClubBanMembersTabFragment(
                searchableTopbar = topbar,
                clubId = clubId,
                uid = uid,
                onSearchModeChanged = searchModeChangeListener
            )
            else -> ClubAllMembersTabFragment(
                parentLifecycleListener = parentLifecycleListener,
                searchableTopbar = topbar,
                entryRouteType = entryRouteType,
                clubId = clubId,
                uid = uid
            )
        }
    }

    override fun getItemCount(): Int {
        return TAB_COUNT
    }

    fun getFragment(position: Int): Fragment? {
        return fragmentManager.findFragmentByTag("f" + position)
    }
}