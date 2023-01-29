package com.rndeep.fns_fantoo.ui.club.settings

import androidx.appcompat.widget.TooltipCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.CommentItem
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingMyPostBoxBinding
import com.rndeep.fns_fantoo.ui.club.settings.adapter.MyPostBoxViewPagerAdapter
import com.rndeep.fns_fantoo.data.local.model.*
import com.rndeep.fns_fantoo.data.remote.dto.ClubStoragePostListWithMeta
import com.rndeep.fns_fantoo.data.remote.dto.ClubStorageReplyListWithMeta
import com.rndeep.fns_fantoo.ui.club.settings.viewmodel.ClubMyPostBoxViewModel
import com.rndeep.fns_fantoo.ui.club.settings.viewmodel.ClubMyProfileViewModel
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ClubSettingMyPostBoxFragment :
    ClubSettingBaseFragment<FragmentClubSettingMyPostBoxBinding>(FragmentClubSettingMyPostBoxBinding::inflate) {
    private val clubMyPostBoxViewModel: ClubMyPostBoxViewModel by viewModels()
    private lateinit var clubId:String
    private lateinit var uid:String
    private lateinit var memberId:String
    override fun initUi() {
        val args : ClubSettingMyPostBoxFragmentArgs by navArgs()
        clubId = args.clubId
        uid = args.integUid.toString()
        memberId = args.memberId.toString()
        val tabTitles = arrayOf(
            getString(R.string.j_wrote_post),
            getString(R.string.j_wrote_rely),
            getString(R.string.j_save),
        )

        binding.vp.adapter = MyPostBoxViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle,
            postItemclickListener = {postItemSelect:ClubStoragePostListWithMeta -> onPostItemSelected(postItemSelect)},
            commentItemclickListener = {commentItem: ClubStorageReplyListWithMeta ->  onCommentItemSelected(commentItem)},
            saveItemClickListener = {saveItemSelect: ClubStoragePostListWithMeta -> onSaveItemSelected(saveItemSelect)},
            clubId,
            uid,
            memberId
        )

        TabLayoutMediator(binding.tabLayout, binding.vp, false, false){ tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        //롱클릭 툴팁 disable
        for(i in 0 until binding.tabLayout.tabCount){
            binding.tabLayout.getTabAt(i)?.view?.let{
                    tabView -> TooltipCompat.setTooltipText(tabView, null)
            }
        }

        val selectTabPos = arguments?.getInt("selectTab")
        //Timber.d("ClubSettingMyPostBoxFragment tab = $selectTabPos")
        binding.tabLayout.getTabAt(selectTabPos!!)?.select()
    }

    override fun initUiActionEvent() {
        binding.topbar.setOnBackButtonClickListener {
            navController.popBackStack()
        }

    }

    private fun onPostItemSelected(clubStoragePostListWithMeta: ClubStoragePostListWithMeta){
        val postData = clubStoragePostListWithMeta.postDto
        postData?.let {
            navController.navigate(ClubSettingMyPostBoxFragmentDirections.actionClubSettingMyPostBoxFragmentToClubPost(
                previewType = ConstVariable.TYPE_CLUB,
                categoryCode = postData.categoryCode,
                clubId = postData.clubId,
                postId = postData.postId,
                replyId = -1
            ))
        }
    }

    private fun onCommentItemSelected(clubStorageReplyListWithMeta: ClubStorageReplyListWithMeta){
        val commentData = clubStorageReplyListWithMeta.clubStorageReplyDto
        commentData?.let {
            navController.navigate(ClubSettingMyPostBoxFragmentDirections.actionClubSettingMyPostBoxFragmentToClubPost(
                previewType = ConstVariable.TYPE_CLUB,
                clubId = commentData.clubId,
                categoryCode = commentData.categoryCode,
                replyId = commentData.replyId,
                postId = commentData.postId,
            ))
        }
    }

    private fun onSaveItemSelected(clubStoragePostListWithMeta: ClubStoragePostListWithMeta){
        val postData = clubStoragePostListWithMeta.postDto
        postData?.let {
            navController.navigate(ClubSettingMyPostBoxFragmentDirections.actionClubSettingMyPostBoxFragmentToClubPost(
                previewType = ConstVariable.TYPE_CLUB,
                categoryCode = postData.categoryCode,
                clubId = postData.clubId,
                postId = postData.postId,
                replyId = -1
            ))
        }
    }

    companion object{
        const val SELECT_TAB = "selectTab"

        const val TAB_POST = 0
        const val TAB_COMMENT = 1
        const val TAB_SAVE = 2
    }

}