package com.rndeep.fns_fantoo.ui.club.settings.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rndeep.fns_fantoo.ui.club.settings.tabs.CommentFragment
import com.rndeep.fns_fantoo.ui.club.settings.tabs.PostFragment
import com.rndeep.fns_fantoo.data.remote.dto.ClubStoragePostListWithMeta
import com.rndeep.fns_fantoo.data.remote.dto.ClubStorageReplyListWithMeta

class MemberDetailViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle,
                                   val postItemClickListener:(ClubStoragePostListWithMeta)-> Unit,
                                   val commentItemClickListener:(ClubStorageReplyListWithMeta) -> Unit,
                                   val clubId:String,
                                   val uid:String,
                                   val memberId:String):
    FragmentStateAdapter(fragmentManager, lifecycle){

    enum class Tab{
        Post,
        Comment
    }

    companion object {
        private const val TAB_POST = 0
        private const val TAB_COMMENT = 1
        private const val TAB_COUNT = 2
    }

    override fun getItemCount(): Int {
        return TAB_COUNT
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            TAB_POST ->{
                PostFragment(clubId, uid, memberId){selectedItem: ClubStoragePostListWithMeta -> postItemClickListener(selectedItem)}
            }
            TAB_COMMENT ->{
                CommentFragment(clubId, uid, memberId){selectedItem: ClubStorageReplyListWithMeta -> commentItemClickListener(selectedItem)}
            }
            else -> {
                PostFragment(clubId, uid, memberId){selectedItem: ClubStoragePostListWithMeta -> postItemClickListener(selectedItem)}
            }
        }
    }

}