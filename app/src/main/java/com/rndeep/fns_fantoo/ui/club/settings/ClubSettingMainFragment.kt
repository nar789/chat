package com.rndeep.fns_fantoo.ui.club.settings

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.ClubBasicInfo
import com.rndeep.fns_fantoo.data.remote.model.clubsetting.AlarmConfig
import com.rndeep.fns_fantoo.data.remote.model.clubsetting.AlarmConfigDto
import com.rndeep.fns_fantoo.data.remote.model.clubsetting.ClubAlarmStateDto
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingMainBinding
import com.rndeep.fns_fantoo.ui.club.settings.viewmodel.ClubSettingMainViewModel
import com.rndeep.fns_fantoo.utils.*
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.CLUB_MEMBERSHIP_LV_MANAGER
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.KEY_CLUB_INFO_CLUBID
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.KEY_CLUB_INFO_MEMBER
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.KEY_CLUB_INFO_MEMBERID
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_UID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ClubSettingMainFragment :
    ClubSettingBaseFragment<FragmentClubSettingMainBinding>(FragmentClubSettingMainBinding::inflate) {

    private val clubSettingMainViewModel: ClubSettingMainViewModel by viewModels()

    private lateinit var clubId: String
    private lateinit var uid: String
    private var memberId: String = ""
    private var myMemberId:String = ""

    override fun initUi() {
        val args: ClubSettingMainFragmentArgs by navArgs()
        clubId = args.clubId
        myMemberId = args.myMemberId
        setStatusBar(R.color.gray_25, true)

        clubSettingMainViewModel.networkErrorLiveData.observe(viewLifecycleOwner) {
            showDialog(getString(R.string.alert_network_error))
        }

        clubSettingMainViewModel.clubAlarmSetLiveData.observe(viewLifecycleOwner) { baseResponse ->
            when (baseResponse.code) {
                ConstVariable.RESULT_SUCCESS_CODE -> {
                    if (baseResponse.dataObj is ClubAlarmStateDto) {
                        setAlarm(baseResponse.dataObj.clubAgree?:false, true)
                    }
                }
                else -> {
                    requireContext().showErrorSnackBar(binding.root, baseResponse.code)
                }
            }
        }

        clubSettingMainViewModel.clubBasicInfoLiveData.observe(viewLifecycleOwner) {
            Timber.d("$it")
        }

        clubSettingMainViewModel.clubMemberInfoLiveData.observe(viewLifecycleOwner) { clubMemberInfoResponse ->
            if (clubMemberInfoResponse.code == ConstVariable.RESULT_SUCCESS_CODE) {
                val profileInfo = clubMemberInfoResponse.dataObj
                if (profileInfo != null) {
                    if (!profileInfo.profileImg.isNullOrEmpty()) {
                        setProfileAvatar(
                            binding.ivProfile,
                            ImageUtils.getImageFullUriForCloudFlare(profileInfo.profileImg, true)
                        )
                    }
                    memberId = profileInfo.memberId.toString()
                    binding.tvProfileName.text = profileInfo.nickname
                    val isManager = profileInfo.memberLevel == CLUB_MEMBERSHIP_LV_MANAGER
                    binding.tvProfileGrade.text =
                        if (isManager) getString(R.string.k_club_president) else getString(R.string.a_general_membership)
                    if (isManager) {
                        binding.rlClubResign.visibility = View.GONE
                        binding.rlClubSetting.visibility = View.VISIBLE
                        binding.rlClubManagement.visibility = View.VISIBLE
                    }
                }
            } else {
                requireContext().showErrorSnackBar(binding.root, clubMemberInfoResponse.code)
            }
        }

        clubSettingMainViewModel.clubMyStorageInfoLiveData.observe(viewLifecycleOwner) { clubStorageCountDto ->
            clubStorageCountDto?.let {
                binding.tvPostCount.text = it.postCount.toString()
                binding.tvCommentCount.text = it.replyCount.toString()
                binding.tvSaveCount.text = it.bookmarkCount.toString()
            }
        }

        clubSettingMainViewModel.clubAlarmGetStateLiveData.observe(viewLifecycleOwner) { baseResponse ->
            when (baseResponse.code) {
                ConstVariable.RESULT_SUCCESS_CODE -> {
                    if (baseResponse.dataObj is ClubAlarmStateDto) {
                        setAlarm(baseResponse.dataObj.clubAgree?:false, false)
                    }
                }
                else -> {
                    requireContext().showErrorSnackBar(binding.root, baseResponse.code)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            uid = clubSettingMainViewModel.getUID()
                ?: ""//"ft_u_f96a7595c13a11ecb6386f262a44c38b_2022_04_21_15_19_17_841"
            clubSettingMainViewModel.getClubBasicInfo(clubId, uid)
            clubSettingMainViewModel.getClubMemberInfo(clubId, uid)
            clubSettingMainViewModel.getClubStorageCount(clubId, uid)
            clubSettingMainViewModel.getAlarmState(clubId, uid)
        }
    }


    override fun initUiActionEvent() {
        binding.topbar.setOnBackButtonClickListener {
            navController.popBackStack()
        }
        binding.topbar.setTailImageClickListener {
            //알림아이콘
            clubSettingMainViewModel.setClubAlarm(
                hashMapOf("alimType" to "CLUB", KEY_UID to uid),
                clubId
            )
        }

        binding.llPost.setOnClickListener {
            if (memberId.isNotEmpty()) {
                val bundle: Bundle =
                    bundleOf(
                        ClubSettingMyPostBoxFragment.SELECT_TAB to ClubSettingMyPostBoxFragment.TAB_POST,
                        KEY_UID to uid,
                        KEY_CLUB_INFO_CLUBID to clubId, KEY_CLUB_INFO_MEMBERID to myMemberId
                    )
                navController.navigate(
                    R.id.action_clubSettingMainFragment_to_clubSettingMyPostBoxFragment,
                    bundle
                )
            }
        }
        binding.llComment.setOnClickListener {
            if (memberId.isNotEmpty()) {
                val bundle: Bundle =
                    bundleOf(
                        ClubSettingMyPostBoxFragment.SELECT_TAB to ClubSettingMyPostBoxFragment.TAB_COMMENT,
                        KEY_UID to uid,
                        KEY_CLUB_INFO_CLUBID to clubId, KEY_CLUB_INFO_MEMBERID to myMemberId
                    )
                navController.navigate(
                    R.id.action_clubSettingMainFragment_to_clubSettingMyPostBoxFragment,
                    bundle
                )
            }
        }
        binding.llSave.setOnClickListener {
            if (memberId.isNotEmpty()) {
                val bundle: Bundle =
                    bundleOf(
                        ClubSettingMyPostBoxFragment.SELECT_TAB to ClubSettingMyPostBoxFragment.TAB_SAVE,
                        KEY_UID to uid,
                        KEY_CLUB_INFO_CLUBID to clubId, KEY_CLUB_INFO_MEMBERID to myMemberId
                    )
                navController.navigate(
                    R.id.action_clubSettingMainFragment_to_clubSettingMyPostBoxFragment,
                    bundle
                )
            }
        }

        binding.rlProfileSet.setOnClickListener {
            val clubInfoPacerable =
                clubSettingMainViewModel.clubMemberInfoLiveData.value?.dataObj?.toClubMemberInfoPacerable()
            if (clubInfoPacerable != null) {
                val bundle = bundleOf(
                    KEY_CLUB_INFO_CLUBID to clubId,
                    KEY_UID to uid,
                    KEY_CLUB_INFO_MEMBER to clubInfoPacerable
                )
                Timber.d("gotoProfileSet bundle $bundle")
                navController.navigate(
                    R.id.action_clubSettingMainFragment_to_clubSettingMyProfileSetFragment,
                    bundle
                )
            } else {
                Timber.w("clubInfoPacerable is null")
            }
        }
        binding.rlMembersShow.setOnClickListener {
            clubSettingMainViewModel.clubBasicInfoLiveData.value?.dataObj?.let {
                if (it is ClubBasicInfo) {
                    val isSecretClub = (!it.memberListOpenYn)
                    if (isSecretClub) {
                        context?.showCustomSnackBar(
                            binding.root,
                            getString(R.string.se_m_member_hidden_club)
                        )
                    } else {
                        val bundle = bundleOf(KEY_CLUB_INFO_CLUBID to clubId, KEY_UID to uid)
                        bundle.putString("myMemberId", myMemberId)
                        navController.navigate(
                            R.id.action_clubSettingMainFragment_to_clubMembersFragment,
                            bundle
                        )
                    }
                }
            }
        }
        binding.rlClubResign.setOnClickListener {
            val bundle = bundleOf(KEY_CLUB_INFO_CLUBID to clubId, KEY_UID to uid)
            navController.navigate(
                R.id.action_clubSettingMainFragment_to_clubSettingResignFragment,
                bundle
            )
        }
        binding.rlClubSetting.setOnClickListener {
            val bundle = bundleOf(KEY_CLUB_INFO_CLUBID to clubId, KEY_UID to uid)
            Timber.d("gotoClubSet bundle $bundle")
            navController.navigate(
                R.id.action_clubSettingMainFragment_to_clubSettingInfoSetFragment,
                bundle
            )
        }
        binding.rlClubManagement.setOnClickListener {
            val bundle = bundleOf(KEY_CLUB_INFO_CLUBID to clubId, KEY_UID to uid)
            navController.navigate(
                R.id.action_clubSettingMainFragment_to_clubSettingManagementFragment,
                bundle
            )
        }
    }

    private fun setAlarm(isSet: Boolean, isNoti: Boolean) {
        if (isSet) {
            binding.topbar.setTailImageResource(R.drawable.icon_alarm)
            if (isNoti) showDialog(getString(R.string.se_k_set_club_alarm))
        } else {
            binding.topbar.setTailImageResource(R.drawable.icon_outline_alarm_off)
            if (isNoti) showDialog(getString(R.string.se_k_released_club_alarm))
        }
    }
}