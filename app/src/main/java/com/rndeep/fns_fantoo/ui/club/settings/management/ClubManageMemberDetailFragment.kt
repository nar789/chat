package com.rndeep.fns_fantoo.ui.club.settings.management

import android.annotation.SuppressLint
import android.text.Spannable
import android.view.View
import androidx.appcompat.widget.TooltipCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.ui.common.RecyclerViewItemClickListener
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingManageMemberDetailBinding
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.ui.club.settings.ClubSettingBaseFragment
import com.rndeep.fns_fantoo.ui.club.settings.MenuBottomSheetDialogFragment
import com.rndeep.fns_fantoo.ui.club.settings.data.MenuItem
import com.rndeep.fns_fantoo.ui.club.settings.management.adapter.ManageMemberDetailViewPagerAdapter
import com.rndeep.fns_fantoo.data.remote.dto.ClubStoragePostListWithMeta
import com.rndeep.fns_fantoo.data.remote.dto.ClubStorageReplyListWithMeta
import com.rndeep.fns_fantoo.ui.club.settings.management.viewmodel.ClubManageMemberDetailViewModel
import com.rndeep.fns_fantoo.utils.*
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.CLUB_DELEGATE_TAGET_MEMBER_AT_LEASET_VISIT_DATE
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.CLUB_MEMBERSHIP_LV_MANAGER
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.KEY_CLUB_INFO_CLUBID
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.KEY_CLUB_INFO_MEMBERID
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_IMAGE_URL
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_UID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.threeten.bp.*
import timber.log.Timber
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ClubManageMemberDetailFragment :
    ClubSettingBaseFragment<FragmentClubSettingManageMemberDetailBinding>(
        FragmentClubSettingManageMemberDetailBinding::inflate
    ) {

    private val clubManageMemberDetailViewModel: ClubManageMemberDetailViewModel by viewModels()
    private lateinit var clubId: String
    private lateinit var uid: String
    private lateinit var memberId: String
    private lateinit var entryRouteType: ConstVariable.ClubSetting.MembersEntryRouteType

    @SuppressLint("SetTextI18n")
    override fun initUi() {
        val args: ClubManageMemberDetailFragmentArgs by navArgs()

        clubId = args.clubId
        uid = args.integUid.toString()
        memberId = args.memberId
        entryRouteType = args.membersScreenEnterType

        val tabTitles = arrayOf(
            getString(R.string.j_wrote_post),
            getString(R.string.j_wrote_rely),
            getString(R.string.j_save)
        )

        if (!ConstVariable.Config.FEATURE_KDG) {
            binding.tvGivingTitle.visibility = View.INVISIBLE
            binding.tvGivingAmount.visibility = View.INVISIBLE
        }

        binding.vp.adapter =
            ManageMemberDetailViewPagerAdapter(
                requireActivity().supportFragmentManager, lifecycle,
                postItemClickListener = { item: ClubStoragePostListWithMeta ->
                    onPostItemSelected(
                        item
                    )
                },
                commentItemClickListener = { item: ClubStorageReplyListWithMeta ->
                    onCommentItemSelected(
                        item
                    )
                },
                saveItemClickListener = { item: ClubStoragePostListWithMeta ->
                    onSaveItemSelected(
                        item
                    )
                },
                clubId,
                uid,
                memberId
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

        clubManageMemberDetailViewModel.networkErrorLiveData.observe(viewLifecycleOwner){
            showDialog(getString(R.string.alert_network_error))
        }

        clubManageMemberDetailViewModel.clubMemberBanResultLiveData.observe(viewLifecycleOwner) {
            baseResponse ->
            baseResponse?.let {
                when(it.code){
                    ConstVariable.RESULT_SUCCESS_CODE ->{
                        lifecycleScope.launch {
                            delay(500)
                            navController.popBackStack()
                        }
                    }
                    else ->{
                        requireContext().showErrorSnackBar(binding.root, it.code)
                    }
                }
            }
        }

        clubManageMemberDetailViewModel.clubDelegateResultLiveData.observe(viewLifecycleOwner) { result ->
            when (result.code) {
                ConstVariable.RESULT_SUCCESS_CODE -> {
                    lifecycleScope.launch {
                        context?.showCustomSnackBar(
                            binding.root,
                            getString(R.string.se_k_completed_to_delegating)
                        )
                        val bundle = bundleOf(
                            KEY_UID to uid,
                            KEY_CLUB_INFO_CLUBID to clubId,
                            KEY_CLUB_INFO_MEMBERID to memberId
                        )
                        navController.navigate(
                            R.id.action_clubManageMemberDetailFragment_to_clubDelegatingFragment,
                            bundle
                        )
                    }
                }
                else ->{
                    requireContext().showErrorSnackBar(binding.root, result.code)
                }
            }
        }

        clubManageMemberDetailViewModel.clubMemberInfoLiveData.observe(viewLifecycleOwner) { clubMemberInfoDto ->
            clubMemberInfoDto?.let {
                it.profileImg?.let {
                    profileImgUrl ->
                    setProfileAvatar(binding.ivProfile, ImageUtils.getImageFullUriForCloudFlare(profileImgUrl, true), R.drawable.profile_character_l)
                }
                binding.toolbar.title = it.nickname
                binding.tvMemberGrade.text =
                    if (it.memberLevel == CLUB_MEMBERSHIP_LV_MANAGER) getString(R.string.k_club_president) else getString(
                        R.string.a_general_membership
                    )
                try {
                    binding.tvJoinDate.text =
                        TimeUtils.changeTimeStringFormat(it.createDate!!, "yyyy. MM. dd")
                    binding.tvLastVisitDate.text =
                        TimeUtils.changeTimeStringFormat(it.visitDate!!, "yyyy. MM. dd")
                }catch (e:Exception){
                    Timber.e("${e.printStackTrace()}")
                }
                //binding.tvGivingAmount.text = "365 KDG"
                if(it.memberLevel != CLUB_MEMBERSHIP_LV_MANAGER){
                    binding.ivMore.visibility = View.VISIBLE
                }
            }
        }

        clubManageMemberDetailViewModel.getClubMemberInfo(clubId, uid, memberId)
    }

    override fun initUiActionEvent() {
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
        binding.ivMore.setOnClickListener {
            if(clubManageMemberDetailViewModel.clubMemberInfoLiveData.value?.memberLevel == CLUB_MEMBERSHIP_LV_MANAGER){
                return@setOnClickListener
            }
            showBottomSheet()
        }

        binding.ivProfile.setOnClickListener {
            clubManageMemberDetailViewModel.clubMemberInfoLiveData.value?.profileImg?.let {
                navController.navigate(
                    R.id.action_clubManageMemberDetailFragment_to_profileDetailImageViewFragment,
                    bundleOf(
                        KEY_IMAGE_URL to
                                ImageUtils.getImageFullUriForCloudFlare(it, false)
                    )
                )
            }
        }
    }

    private fun onPostItemSelected(clubStoragePostListWithMeta: ClubStoragePostListWithMeta) {
        val postData = clubStoragePostListWithMeta.postDto
        postData?.let {
            navController.navigate(
                ClubManageMemberDetailFragmentDirections.actionClubManageMemberDetailFragmentToClubPost(
                    clubId = postData.clubId,
                    categoryCode = postData.categoryCode,
                    postId = postData.postId,
                    previewType = ConstVariable.TYPE_CLUB
                )
            )
        }
    }

    private fun onCommentItemSelected(clubStorageReplyListWithMeta: ClubStorageReplyListWithMeta) {
        val commentData = clubStorageReplyListWithMeta.clubStorageReplyDto
        commentData?.let {
            navController.navigate(
                ClubManageMemberDetailFragmentDirections.actionClubManageMemberDetailFragmentToComment(
                    replyId = commentData.replyId,
                    postId = commentData.postId,
                )
            )
        }
    }

    private fun onSaveItemSelected(clubStoragePostListWithMeta: ClubStoragePostListWithMeta) {
        val postData = clubStoragePostListWithMeta.postDto
        postData?.let {
            navController.navigate(
                ClubManageMemberDetailFragmentDirections.actionClubManageMemberDetailFragmentToClubPost(
                    clubId = postData.clubId,
                    categoryCode = postData.categoryCode,
                    postId = postData.postId,
                    previewType = ConstVariable.TYPE_CLUB
                )
            )
        }
    }


    private fun getMenuItemList(): ArrayList<MenuItem> {
        val menuItemArrayList: ArrayList<MenuItem> = ArrayList()
        return when (entryRouteType) {
            ConstVariable.ClubSetting.MembersEntryRouteType.MEMBER_MANAGEMENT -> {
                val menuItem = MenuItem(0, getString(R.string.g_force_leave), "")
                menuItemArrayList.add(menuItem)
                menuItemArrayList
            }
            ConstVariable.ClubSetting.MembersEntryRouteType.CLUB_DELEGATE -> {
                val menuItem = MenuItem(0, getString(R.string.a_to_delegate), "")
                menuItemArrayList.add(menuItem)
                menuItemArrayList.add(MenuItem(1, getString(R.string.g_force_leave), ""))
                menuItemArrayList
            }
        }
    }

    private fun showBottomSheet() {
        val menuBottomSheetDialogFragment = MenuBottomSheetDialogFragment(getMenuItemList())
        menuBottomSheetDialogFragment.setOnMenuItemClickListener(object :
            RecyclerViewItemClickListener {
            override fun onItemClick(objects: Any) {
                val selectedMenuItem = objects as MenuItem
                Timber.d("bottomsheet select item = ${selectedMenuItem.title1}")
                // 위임하기
                if (selectedMenuItem.title1 == getString(R.string.a_to_delegate)) {
                    var isValidTarget = false
                    val memberInfo = clubManageMemberDetailViewModel.clubMemberInfoLiveData.value
                    if (memberInfo != null && !memberInfo.visitDate.isNullOrEmpty()) {
                        //memberInfo.visitDate
                        isValidTarget = calculateDateDurationFromToday(
                            memberInfo.visitDate
                        ) <= CLUB_DELEGATE_TAGET_MEMBER_AT_LEASET_VISIT_DATE
                    }

                    if (isValidTarget) {//14일내 클럽방문자
                        val messageSpannable: Spannable? =
                            context?.getColor(R.color.primary_500)?.let {
                                TextUtils.getParticalColorSpannable(
                                    binding.toolbar.title.toString(),
                                    getString(R.string.se_n_really_delegating_to_member),
                                    it, true
                                )
                            }
                        Timber.d("clubMessage = $messageSpannable")
                        messageSpannable?.let {
                            showSpannablePopup(it,
                                getString(R.string.h_confirm),
                                getString(R.string.c_cancel),
                                object : CommonDialog.ClickListener {
                                    override fun onClick() {
                                        clubManageMemberDetailViewModel.requestClubDelegate(
                                            clubId, memberId, hashMapOf(
                                                KEY_UID to uid,
                                                KEY_CLUB_INFO_MEMBERID to memberId,
                                                "status" to 2 //위임 설정 (1:멤버승인, 2:위임요청, 3:멤버거절, 4:클럽장취소)?
                                            )
                                        )
                                    }
                                })
                        }
                        /* 컬러 적용하지 않고 %s를 쓸 경우
                    showDialog("", String.format(getString(R.string.do_you_want_delegate_club), binding.toolbar.title),
                    "", getString(R.string.confirm), getString(R.string.cancel),
                        object :CommonDialog.ClickListener{
                            override fun onClick() {

                            }
                        }, null
                    )*/
                    } else {
                        val messageSpannable: Spannable? =
                            context?.getColor(R.color.primary_500)?.let {
                                TextUtils.getParticalColorSpannable(
                                    binding.toolbar.title.toString(),
                                    getString(R.string.se_n_delegating_impossible_to_member),
                                    it, true
                                )
                            }
                        messageSpannable?.let {
                            showSpannablePopup(
                                messageSpannable,
                                getString(R.string.h_confirm),
                                "",
                                null
                            )
                        }
                        //showDialog(String.format(getString(R.string.invalid_target_delegate_club), binding.toolbar.title))
                    }
                } else if (selectedMenuItem.title1 == getString(R.string.g_force_leave)) {
                    showBanPopup()
                }
            }
        })
        menuBottomSheetDialogFragment.show(requireActivity().supportFragmentManager, "")
    }

    private fun showSpannablePopup(
        spannable: Spannable,
        positiveButtonString: String, negativeButtonString: String,
        positiveClickListener: CommonDialog.ClickListener?
    ) {
        val dialogBuilder = CommonDialog.Builder()
        dialogBuilder.setMessageSpannable(spannable)
        if (positiveButtonString.isNotEmpty()) {
            dialogBuilder.positiveButtonString = positiveButtonString
        }
        if (negativeButtonString.isNotEmpty()) {
            dialogBuilder.negativeButtonString = negativeButtonString
        }
        if (positiveClickListener != null) {
            dialogBuilder.positiveButtonClickListener = positiveClickListener
        }
        requireActivity().let {
            dialogBuilder.build().show(it.supportFragmentManager, "")
        }
    }

    private fun showBanPopup() {
        val dialogBuilder = CommonDialog.Builder()
        dialogBuilder.title = getString(R.string.g_force_leave)
        dialogBuilder.message = getString(R.string.se_g_cannot_to_cancel_force_leave)
        dialogBuilder.positiveButtonString = getString(R.string.g_confirm_force_leave)
        dialogBuilder.negativeButtonString = getString(R.string.c_cancel)
        context?.let {
            dialogBuilder.positiveButtonWidth =
                it.resources.getDimensionPixelSize(R.dimen.club_setting_ban_positive_button_width)
            dialogBuilder.negativeButtonWidth =
                it.resources.getDimensionPixelSize(R.dimen.club_setting_ban_negative_button_width)
        }
        dialogBuilder.positiveButtonClickListener = object : CommonDialog.ClickListener {
            override fun onClick() {
                clubManageMemberDetailViewModel.requestMemberBan(
                    clubId, memberId, hashMapOf(
                        KEY_UID to uid,
                        "joinYn" to true
                    )
                )
            }
        }
        requireActivity().let {
            dialogBuilder.build().show(it.supportFragmentManager, "")
        }
    }

    private fun calculateDateDurationFromToday(inputDate: String): Long {
        //2022-09-27T02:03:21.399Z"
        try {
            val inputDateTime = DateTimeUtils.toDate(Instant.parse(inputDate)).time
            val today =
                LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            //Timber.d("$inputDate today = ${today} , time = ${(today - inputDateTime)/(24*60*60*1000)}")
            return (today - inputDateTime) / (24 * 60 * 60 * 1000)
        } catch (e: Exception) {
            Timber.e("${e.printStackTrace()}")
        }
        return 0
    }
}