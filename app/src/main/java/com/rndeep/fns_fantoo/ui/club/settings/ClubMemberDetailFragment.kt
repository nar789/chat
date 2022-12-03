package com.rndeep.fns_fantoo.ui.club.settings

import android.annotation.SuppressLint
import android.view.View
import androidx.appcompat.widget.TooltipCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.ui.common.RecyclerViewItemClickListener
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingMemberDetailBinding
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.ui.club.settings.adapter.MemberDetailViewPagerAdapter
import com.rndeep.fns_fantoo.ui.club.settings.data.MenuItem
import com.rndeep.fns_fantoo.data.remote.dto.ClubStoragePostListWithMeta
import com.rndeep.fns_fantoo.data.remote.dto.ClubStorageReplyListWithMeta
import com.rndeep.fns_fantoo.ui.club.settings.viewmodel.ClubMemberDetailViewModel
import com.rndeep.fns_fantoo.utils.*
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.KEY_CLUB_INFO_MEMBERID
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_IMAGE_URL
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_UID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ClubMemberDetailFragment :
    ClubSettingBaseFragment<FragmentClubSettingMemberDetailBinding>(
        FragmentClubSettingMemberDetailBinding::inflate
    ) {

    private val clubMemberDetailViewModel: ClubMemberDetailViewModel by viewModels()
    private lateinit var clubId: String
    private lateinit var uid: String
    private lateinit var memberId: String
    private lateinit var myMemberId:String

    companion object {
        const val KEY_MEMBER_ID = "memberId"
    }

    @SuppressLint("SetTextI18n")
    override fun initUi() {
        val args: ClubMemberDetailFragmentArgs by navArgs()
        clubId = args.clubId
        uid = args.integUid.toString()
        memberId = args.memberId.toString()
        myMemberId = args.myMemberId
        Timber.d("member =  $memberId")

        if(memberId == myMemberId){
            binding.ivMore.visibility = View.INVISIBLE
        }

        clubMemberDetailViewModel.networkErrorLiveData.observe(viewLifecycleOwner){
            showDialog(getString(R.string.alert_network_error))
        }

        clubMemberDetailViewModel.loadingStatusLiveData.observe(viewLifecycleOwner) { loadingState ->
            setLoadingView(loadingState)
        }

        clubMemberDetailViewModel.memberInfoLiveData.observe(viewLifecycleOwner) { clubMemberInfoResponse ->
            clubMemberInfoResponse?.let {
                when (clubMemberInfoResponse.code) {
                    ConstVariable.RESULT_SUCCESS_CODE -> {
                        val memberInfo = clubMemberInfoResponse.dataObj
                        memberInfo?.let {
                            if (!memberInfo.profileImg.isNullOrEmpty()) {
                                setProfileAvatar(
                                    binding.ivProfile,
                                    ImageUtils.getImageFullUriForCloudFlare(
                                        memberInfo.profileImg,
                                        true
                                    ),
                                    R.drawable.profile_character_l
                                )
                            }
                            binding.toolbar.title = memberInfo.nickname
                            binding.tvMemberGrade.text =
                                if (memberInfo.memberLevel == ConstVariable.ClubDef.CLUB_MEMBERSHIP_LV_MANAGER) getString(
                                    R.string.k_club_president
                                ) else getString(R.string.a_general_membership)
                        }
                    }
                    else ->{
                        requireContext().showErrorSnackBar(binding.root, clubMemberInfoResponse.code)
                    }
                }
            }
        }
        clubMemberDetailViewModel.memberBlockInfoResultLiveData.observe(viewLifecycleOwner) {

        }

        clubMemberDetailViewModel.postBlockSetLiveData.observe(viewLifecycleOwner){
            clubPostBlockResultResponse ->
            when(clubPostBlockResultResponse.code){
                ConstVariable.RESULT_SUCCESS_CODE ->{
                    clubPostBlockResultResponse.dataObj?.let {
                        if(!it.blockYn){
                            context?.showCustomSnackBar(
                                binding.root,
                                getString(R.string.se_s_block_released_select_post)
                            )
                            clubPostBlockResultResponse.navDirections?.let {
                                navController.navigate(clubPostBlockResultResponse.navDirections)
                            }
                        }else{
                            Timber.w("block not release")
                        }
                    }
                }
                else ->{
                    requireContext().showErrorSnackBar(binding.root, clubPostBlockResultResponse.code)
                }
            }
        }
        
        clubMemberDetailViewModel.commentBlockSetLiveData.observe(viewLifecycleOwner){
            clubCommentBlockResultResponse ->
            when(clubCommentBlockResultResponse.code){
                ConstVariable.RESULT_SUCCESS_CODE ->{
                    clubCommentBlockResultResponse.dataObj?.let { 
                        if(!it.blockYn){
                            context?.showCustomSnackBar(
                                binding.root,
                                getString(R.string.se_s_block_released_select_comment)
                            )
                            clubCommentBlockResultResponse.navDirections?.let {
                                navController.navigate(clubCommentBlockResultResponse.navDirections)
                            }
                        }else{
                            Timber.w("block not release")
                        }
                    }
                }
                else ->{
                    requireContext().showErrorSnackBar(binding.root, clubCommentBlockResultResponse.code)
                }
            }
        }
        clubMemberDetailViewModel.memberBlockSetResultLiveData.observe(viewLifecycleOwner) { memberSetBlockResponse ->
            memberSetBlockResponse?.let {
                when (memberSetBlockResponse.code) {
                    ConstVariable.RESULT_SUCCESS_CODE -> {
                        val blockState = memberSetBlockResponse.dataObj
                        blockState?.let {
                            if (blockState.blockYn) {
                                context?.showCustomSnackBar(
                                    binding.root,
                                    getString(R.string.se_s_blocked_select_user)
                                )
                            } else {
                                context?.showCustomSnackBar(
                                    binding.root,
                                    getString(R.string.se_s_block_released_select_user)
                                )
                            }
                            memberSetBlockResponse.navDirections?.let {
                                navController.navigate(it)
                            }
                        }
                    }
                    else ->{
                        requireContext().showErrorSnackBar(binding.root, memberSetBlockResponse.code)
                    }
                }
            }
        }


        val tabTitles = arrayOf(
            getString(R.string.j_wrote_post),
            getString(R.string.j_wrote_rely)
        )

        binding.vp.adapter = MemberDetailViewPagerAdapter(
            requireActivity().supportFragmentManager, lifecycle,
            postItemClickListener = { postSelectedItem: ClubStoragePostListWithMeta? ->
                onPostItemSelected(
                    postSelectedItem
                )
            },
            commentItemClickListener = { commentSelectedItem: ClubStorageReplyListWithMeta ->
                onCommentItemSelected(
                    commentSelectedItem
                )
            },
            clubId = clubId,
            uid = uid,
            memberId = memberId
        )
        TabLayoutMediator(binding.tabLayout, binding.vp, false, false) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        //롱클릭 툴팁 disable
        for (i in 0 until binding.tabLayout.tabCount) {
            binding.tabLayout.getTabAt(i)?.view?.let { tabView ->
                TooltipCompat.setTooltipText(tabView, null)
            }
        }

        if (!ConstVariable.Config.FEATURE_KDG) {
            binding.tvGivingTitle.visibility = View.INVISIBLE
            binding.tvGivingAmount.visibility = View.INVISIBLE
        }
        clubMemberDetailViewModel.getMemberInfo(clubId = clubId, uid = uid, memberId = memberId)
        clubMemberDetailViewModel.getMemberBlockInfo(clubId, uid, memberId)
    }


    override fun initUiActionEvent() {
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
        binding.ivMore.setOnClickListener {
            //Timber.d("mem : $memberId, my: $myMemberId")
            val isBlocked =
                if (clubMemberDetailViewModel.memberBlockInfoLiveData.value == null) false
                else clubMemberDetailViewModel.memberBlockInfoLiveData.value!!
            showBottomSheet(!isBlocked)
        }

        binding.ivProfile.setOnClickListener {
            clubMemberDetailViewModel.memberInfoLiveData.value?.dataObj?.let { clubMemberInfoDto ->
                clubMemberInfoDto.profileImg?.let {
                    navController.navigate(
                        R.id.action_clubMemberDetailFragment3_to_profileDetailImageViewFragment2,
                        bundleOf(
                            KEY_IMAGE_URL to ImageUtils.getImageFullUriForCloudFlare(
                                clubMemberInfoDto.profileImg,
                                false
                            )
                        )
                    )
                }
            }

        }

        /*
        Collapsed 상태 event
        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                //  Collapsed
                //binding.topbar.setTitle(binding.tvNickName.text.toString())
            } else {
                //Expanded
                //binding.topbar.setTitle("")
            }
        })*/
    }

    private fun showBottomSheet(isBlock: Boolean) {
        val menuItemArrayList: ArrayList<MenuItem> = ArrayList()
        val itemString =
            if (isBlock) getString(R.string.g_block_account) else getString(R.string.c_release_account_block)
        val menuItem = MenuItem(0, itemString, "")
        menuItemArrayList.add(menuItem)
        val menuBottomSheetDialogFragment = MenuBottomSheetDialogFragment(menuItemArrayList)
        menuBottomSheetDialogFragment.setOnMenuItemClickListener(object :
            RecyclerViewItemClickListener {
            override fun onItemClick(objects: Any) {
                if (isBlock) {
                    showDialog(
                        getString(R.string.g_block_account),
                        getString(R.string.se_s_do_you_want_block_select_user),
                        "",
                        getString(R.string.h_confirm),
                        getString(R.string.c_cancel),
                        object : CommonDialog.ClickListener {
                            override fun onClick() {
                                clubMemberDetailViewModel.setMemberBlock(clubId, memberId, uid, null)
                            }
                        },
                        null
                    )
                } else {
                    showDialog(
                        getString(R.string.c_release_account_block),
                        getString(R.string.se_s_do_you_want_release_block_select_user),
                        "",
                        getString(R.string.h_confirm),
                        getString(R.string.c_cancel),
                        object : CommonDialog.ClickListener {
                            override fun onClick() {
                                clubMemberDetailViewModel.setMemberBlock(clubId, memberId, uid, null)
                            }
                        },
                        null
                    )
                }
            }
        })
        menuBottomSheetDialogFragment.show(requireActivity().supportFragmentManager, "")
    }

    private fun onPostItemSelected(clubStoragePostListWithMeta: ClubStoragePostListWithMeta?) {
        lifecycleScope.launch {
            val postData = clubStoragePostListWithMeta?.postDto
            postData?.let {
                val navDirections = ClubMemberDetailFragmentDirections.actionClubMemberDetailFragment3ToClubPost(
                    clubId = postData.clubId,
                    categoryCode = postData.categoryCode,
                    postId = postData.postId,
                    previewType = ConstVariable.TYPE_CLUB,
                    replyId = -1
                )
                when(ConstVariable.BlockType.compare(postData.blockType)) {//(0:없음, 1:멤버, 2:게시글)
                    ConstVariable.BlockType.MEMBER ->{
                        showDialog(
                            getString(R.string.c_release_account_block),
                            getString(R.string.se_s_do_you_want_release_block_select_user),
                            "",
                            getString(R.string.h_confirm),
                            getString(R.string.c_cancel),
                            object : CommonDialog.ClickListener {
                                override fun onClick() {
                                    clubMemberDetailViewModel.setMemberBlock(clubId, memberId, uid, navDirections)
                                }
                            },
                            null
                        )
                    }
                    ConstVariable.BlockType.POST ->{
                        showDialog(
                            getString(R.string.a_see_post),
                            getString(R.string.se_s_want_to_unblock_select_post),
                            "",
                            getString(R.string.h_confirm),
                            getString(R.string.c_cancel),
                            object : CommonDialog.ClickListener {
                                override fun onClick() {
                                    clubMemberDetailViewModel.setPostBlock(
                                        clubId = clubId,
                                        categroyCode = postData.categoryCode,
                                    postId = postData.postId.toString(),
                                    hashMapOf(KEY_UID to uid,
                                        KEY_CLUB_INFO_MEMBERID to postData.memberId,
                                        "categoryCode" to postData.categoryCode,
                                        "postId" to postData.postId,
                                        "parentReplyId" to 0,
                                        "replyId" to 0
                                        ),
                                        navDirections)
                                }
                            },
                            null
                        )
                    }
                    ConstVariable.BlockType.COMMENT, ConstVariable.BlockType.NONE ->{
                        navController.navigate(
                            navDirections
                        )
                    }
                }
            }
        }
        //navController.navigate(ClubMemberDetailFragmentDirections.actionClubMemberDetailFragmentToPost())//SendPostItem
    }

    private fun onCommentItemSelected(clubStorageReplyListWithMeta: ClubStorageReplyListWithMeta) {
        lifecycleScope.launch {
            val commentData = clubStorageReplyListWithMeta.clubStorageReplyDto
            commentData?.let {
                val navDirections = ClubMemberDetailFragmentDirections.actionClubMemberDetailFragment3ToClubPost(
                    clubId = commentData.clubId,
                    categoryCode = commentData.categoryCode,
                    postId = commentData.postId,
                    previewType = ConstVariable.TYPE_CLUB,
                    replyId = commentData.replyId
                )
                when(ConstVariable.BlockType.compare(commentData.blockType)){
                    ConstVariable.BlockType.MEMBER ->{
                        showDialog(
                            getString(R.string.c_release_account_block),
                            getString(R.string.se_s_do_you_want_release_block_select_user),
                            "",
                            getString(R.string.h_confirm),
                            getString(R.string.c_cancel),
                            object : CommonDialog.ClickListener {
                                override fun onClick() {
                                    clubMemberDetailViewModel.setMemberBlock(clubId, memberId, uid, navDirections)
                                }
                            },
                            null
                        )
                    }
                    ConstVariable.BlockType.COMMENT ->{
                        showDialog(
                            getString(R.string.a_see_comment),
                            getString(R.string.se_s_want_to_unblock_select_comment),
                            "",
                            getString(R.string.h_confirm),
                            getString(R.string.c_cancel),
                            object : CommonDialog.ClickListener {
                                override fun onClick() {
                                    if(commentData.parentReplyId == commentData.replyId){
                                        clubMemberDetailViewModel.setCommentBlock(
                                            clubId = clubId,
                                            categroyCode = commentData.categoryCode,
                                            postId = commentData.postId.toString(),
                                            replyId = commentData.replyId.toString(),
                                            hashMapOf(
                                                KEY_UID to uid,
                                                KEY_CLUB_INFO_MEMBERID to memberId,
                                                "categoryCode" to commentData.categoryCode,
                                                "parentReplyId" to commentData.parentReplyId,
                                                "postId" to commentData.postId,
                                                "replyId" to commentData.replyId
                                            ),
                                            navDirections
                                        )
                                    }else{
                                        clubMemberDetailViewModel.setSubCommentBlock(
                                            clubId = clubId,
                                            categroyCode = commentData.categoryCode,
                                            postId = commentData.postId.toString(),
                                            replyId = commentData.replyId.toString(),
                                            parentReplyId = commentData.parentReplyId.toString(),
                                            hashMapOf(
                                                KEY_UID to uid,
                                                KEY_CLUB_INFO_MEMBERID to memberId,
                                                "categoryCode" to commentData.categoryCode,
                                                "parentReplyId" to commentData.parentReplyId,
                                                "postId" to commentData.postId,
                                                "replyId" to commentData.replyId
                                            ),
                                            navDirections
                                        )
                                    }
                                }
                            },
                            null
                        )
                    }
                    ConstVariable.BlockType.POST, ConstVariable.BlockType.NONE->{
                        navController.navigate(
                            navDirections
                        )
                    }
                }
            }
        }
    }

    private fun setLoadingView(isShow: Boolean) {
        binding.loadingView.visibility = if (isShow) View.VISIBLE else View.GONE
    }

}