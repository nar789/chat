package com.rndeep.fns_fantoo.ui.club.settings.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rndeep.fns_fantoo.ui.club.settings.ClubSettingMyPostBoxFragment
import com.rndeep.fns_fantoo.ui.club.settings.tabs.CommentFragment
import com.rndeep.fns_fantoo.ui.club.settings.tabs.PostFragment
import com.rndeep.fns_fantoo.ui.club.settings.tabs.SaveFragment
import com.rndeep.fns_fantoo.data.remote.dto.ClubStoragePostListWithMeta
import com.rndeep.fns_fantoo.data.remote.dto.ClubStorageReplyListWithMeta

class MyPostBoxViewPagerAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle,
                                 val postItemclickListener:(ClubStoragePostListWithMeta)-> Unit,
                                 val commentItemclickListener:(ClubStorageReplyListWithMeta)-> Unit,
                                 val saveItemClickListener: (ClubStoragePostListWithMeta) -> Unit,
                                 val clubId:String,
                                 val uid:String,
                                 val memberId:String
    ):
    FragmentStateAdapter(fragmentManager, lifecycle){

    companion object {
        const val TAB_COUNT = 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            ClubSettingMyPostBoxFragment.TAB_POST -> {
                PostFragment(clubId, uid, memberId) { selectedItem: ClubStoragePostListWithMeta -> postItemclickListener(selectedItem)}
            }
            ClubSettingMyPostBoxFragment.TAB_COMMENT ->{
                CommentFragment(clubId, uid, memberId){selectedItem: ClubStorageReplyListWithMeta -> commentItemclickListener(selectedItem)}
            }
            ClubSettingMyPostBoxFragment.TAB_SAVE ->{
                SaveFragment(clubId, uid, memberId){selectedItem:ClubStoragePostListWithMeta -> saveItemClickListener(selectedItem)}
            }
            else ->{
                PostFragment(clubId, uid, memberId){selectedItem: ClubStoragePostListWithMeta -> postItemclickListener(selectedItem)}
            }
        }
    }

    override fun getItemCount(): Int {
        return TAB_COUNT
    }
}