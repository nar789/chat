package com.rndeep.fns_fantoo.ui.club.settings.management

import androidx.appcompat.widget.TooltipCompat
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingMembersManagementBinding
import com.rndeep.fns_fantoo.ui.club.settings.ClubSettingBaseFragment
import com.rndeep.fns_fantoo.ui.club.settings.management.adapter.MembersViewPagerAdapter
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubSetting.Companion.KEY_MEMBERS_ENTRY_ROUTE_TYPE
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubSetting.Companion.KEY_NOTI_DELEGATE_DIALOG
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.concurrent.CopyOnWriteArrayList

@AndroidEntryPoint
class ClubMembersManagement : ClubSettingBaseFragment<FragmentClubSettingMembersManagementBinding>(
    FragmentClubSettingMembersManagementBinding::inflate
) {

    //private val clubSettingViewModel:ClubSettingViewModel by activityViewModels()
    private lateinit var clubId: String
    private lateinit var uid: String
    private lateinit var entryRouteType: ConstVariable.ClubSetting.MembersEntryRouteType

    interface ParentLifecycleListener{
        fun onResumeParentFragment()
        fun onPauseParentFragment()
    }

    private val parentLifecycleListener:CopyOnWriteArrayList<ParentLifecycleListener> = CopyOnWriteArrayList<ParentLifecycleListener>()

    override fun initUi() {
        val args: ClubMembersManagementArgs by navArgs()
        clubId = args.clubId
        uid = args.integUid.toString()
        entryRouteType = args.membersScreenEnterType

        val tabTitles = arrayOf(
            getString(R.string.j_all_members),
            getString(R.string.g_member_forced_to_leave)
        )

        when (entryRouteType) {
            ConstVariable.ClubSetting.MembersEntryRouteType.CLUB_DELEGATE -> {
                binding.topbar.setTitle(getString(R.string.k_close_delete_club))
                val isShowDelegateDialog = arguments?.getBoolean(KEY_NOTI_DELEGATE_DIALOG)
                if (isShowDelegateDialog == true) {
                    showDialog(
                        getString(R.string.k_club_closure_and_delegating_guide),
                        "",
                        getString(R.string.se_h_club_member_two_more_can_delegating)
                    )
                    arguments?.remove(KEY_NOTI_DELEGATE_DIALOG)
                }
            }
            ConstVariable.ClubSetting.MembersEntryRouteType.MEMBER_MANAGEMENT -> {

            }
        }

        binding.vp.adapter = MembersViewPagerAdapter(
            requireActivity().supportFragmentManager,
            lifecycle,
            parentLifecycleListener,
            binding.topbar,
            entryRouteType,
            clubId,
            uid
        ){ isSearchMode:Boolean -> topbarSearchModeChanged(isSearchMode)}

        TabLayoutMediator(binding.tabLayout, binding.vp, false, false) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                //선택한 fragment접근이 필요할때
                /*if (tab != null) {
                    val membersAdapter = (binding.vp.adapter as MembersViewPagerAdapter)

                    membersAdapter.getFragment(0)?.let {
                        (it as ClubAllMembersTabFragment)
                    }
                }*/
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        //롱클릭 툴팁 disable
        for (i in 0 until binding.tabLayout.tabCount) {
            binding.tabLayout.getTabAt(i)?.view?.let { tabView ->
                TooltipCompat.setTooltipText(tabView, null)
            }
        }
    }

    override fun initUiActionEvent() {
        binding.topbar.setOnBackButtonClickListener {
            navController.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        parentLifecycleListener.map {
            it?.onResumeParentFragment()
        }
    }

    override fun onPause() {
        super.onPause()
        parentLifecycleListener.map {
            it?.onPauseParentFragment()
        }
    }

    /**
        강제탈퇴멤버에서 서치선택시 이벤트 콜백
    */
    private fun topbarSearchModeChanged(isSearchMode:Boolean){
        if(isSearchMode){
            binding.tabLayout.getTabAt(0)?.select()
        }
    }

    override fun onDetach() {
        parentLifecycleListener.clear()
        super.onDetach()
    }
}