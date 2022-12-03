package com.rndeep.fns_fantoo.ui.club.settings.management

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.dto.ClubCloseStateDto
import com.rndeep.fns_fantoo.data.remote.model.club.ClubLoginResult
import com.rndeep.fns_fantoo.data.remote.model.clubsetting.ClubJoinWaitMemberCountDto
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingManagementBinding
import com.rndeep.fns_fantoo.ui.club.settings.ClubSettingBaseFragment
import com.rndeep.fns_fantoo.ui.club.settings.management.viewmodel.ClubManagementViewModel
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubSetting.Companion.KEY_MEMBERS_ENTRY_ROUTE_TYPE
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubSetting.Companion.KEY_NOTI_DELEGATE_DIALOG
import com.rndeep.fns_fantoo.utils.ImageUtils
import com.rndeep.fns_fantoo.utils.setProfileAvatar
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class ClubSettingManagementFragment : ClubSettingBaseFragment<FragmentClubSettingManagementBinding>(
    FragmentClubSettingManagementBinding::inflate
) {

    private val clubManageViewModel: ClubManagementViewModel by viewModels()
    private lateinit var clubId: String
    private lateinit var uid: String

    @SuppressLint("SetTextI18n")
    override fun initUi() {
        val args: ClubSettingManagementFragmentArgs by navArgs()
        clubId = args.clubId
        uid = args.integUid.toString()

        if (!ConstVariable.Config.FEATURE_KDG) {
            binding.llSave.visibility = View.INVISIBLE
        }

        clubManageViewModel.networkErrorLiveData.observe(viewLifecycleOwner){
            showDialog(getString(R.string.alert_network_error))
        }

        clubManageViewModel.clubLoginResultLiveData.observe(viewLifecycleOwner) { loginResult ->
            when (loginResult.code) {
                ConstVariable.RESULT_SUCCESS_CODE -> {

                }
                else ->{
                    requireContext().showErrorSnackBar(binding.root, loginResult.code)
                }
            }
        }

        clubManageViewModel.clubManageInfoLiveData.observe(viewLifecycleOwner) { clubManageInfoResponse ->
            Timber.d("clubManageInfo $clubManageInfoResponse")
            when (clubManageInfoResponse.code) {
                ConstVariable.RESULT_SUCCESS_CODE -> {
                    val clubManageInfo = clubManageInfoResponse.dataObj
                    if (clubManageInfo != null) {
                        binding.tvClubName.text = clubManageInfo.clubName
                        try {
                            binding.tvClubOpenDate.text =
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(clubManageInfo.createDate)
                                    ?.let {
                                        SimpleDateFormat("yyyy.MM.dd").format(
                                            it
                                        )
                                    }
                        } catch (e: Exception) {
                            Timber.e("dateConvertException ${e.printStackTrace()}")
                        }
                        binding.tvClubPublic.text =
                            if (clubManageInfo.openYn) getString(R.string.g_open_public) else getString(
                                R.string.b_hidden
                            )
                        binding.ivPrivate.visibility =
                            if (clubManageInfo.openYn) View.GONE else View.VISIBLE
                        binding.tvMemberCount.text = clubManageInfo.memberCount.toString()
                        binding.tvPostCount.text = clubManageInfo.postCount.toString()
                        if (!clubManageInfo.profileImg.isNullOrEmpty()) {
                            setProfileAvatar(
                                binding.ivClubPic,
                                ImageUtils.getImageFullUriForCloudFlare(
                                    clubManageInfo.profileImg,
                                    true
                                )
                            )
                        }
                    }
                }
                else ->{
                    requireContext().showErrorSnackBar(binding.root, clubManageInfoResponse.code)
                }
            }
        }

        clubManageViewModel.joinWaitMemberCountLiveData.observe(viewLifecycleOwner){
            baseResponse ->
            when(baseResponse.code){
                ConstVariable.RESULT_SUCCESS_CODE ->{
                    if(baseResponse.dataObj is ClubJoinWaitMemberCountDto){
                        binding.tvJoinWaitCount.text = if (baseResponse.dataObj == null) (0.toString()+getString(R.string.m_people_count))
                        else (baseResponse.dataObj.joinCount.toString()+getString(R.string.m_people_count))
                    }
                }
                else ->{
                    requireContext().showErrorSnackBar( binding.root, baseResponse.code)
                }
            }
        }

        clubManageViewModel.getClubInfo(clubId)
        clubManageViewModel.loginClub(
            clubId, hashMapOf(
                "clubToken" to "",
                ConstVariable.KEY_UID to uid
            )
        )
        clubManageViewModel.getClubCloseStateInfo(clubId, uid)
        clubManageViewModel.getJoinWaitMemberCount(clubId, uid)
    }

    override fun initUiActionEvent() {
        binding.topbar.setOnBackButtonClickListener {
            navController.popBackStack()
        }
        val bundle = bundleOf(
            ConstVariable.ClubDef.KEY_CLUB_INFO_CLUBID to clubId,
            ConstVariable.KEY_UID to uid
        )
        //가입 승인 대기
        binding.rlJoinReview.setOnClickListener {
            navController.navigate(
                R.id.action_clubSettingManagementFragment_to_clubJoinWaitListFragment,
                bundle
            )
        }
        //멤버관리
        binding.rlMemberManagement.setOnClickListener {
            bundle.putSerializable(
                KEY_MEMBERS_ENTRY_ROUTE_TYPE,
                ConstVariable.ClubSetting.MembersEntryRouteType.MEMBER_MANAGEMENT
            )
            navController.navigate(
                R.id.action_clubSettingManagementFragment_to_clubMembersManagement,
                bundle
            )
        }
        //게시판 관리
        binding.rlClubBoardManagement.setOnClickListener {
            navController.navigate(
                R.id.action_clubSettingManagementFragment_to_clubBoardManagementFragment,
                bundle
            )
        }

        //폐쇄/위임임
        binding.rlClubCloseDelegate.setOnClickListener {
            var isClosingRequest = false
            clubManageViewModel.clubCloseStateInfoLiveData.value?.dataObj?.let {
                if (it is ClubCloseStateDto) {
                    isClosingRequest = it.closesStatus == 1
                }
            }
            var isDelegating = false
            clubManageViewModel.clubLoginResultLiveData.value?.data?.let {
                if (it is ClubLoginResult) {
                    isDelegating =
                        it.delegateStatus == 1 //위임 여부 (0:없음, 1:위임 요청)
                }
            }

            val memberCount = clubManageViewModel.clubManageInfoLiveData.value?.dataObj?.memberCount
            //폐쇄 신청 상태 ->
            if (isClosingRequest) {
                navController.navigate(
                    R.id.action_clubSettingManagementFragment_to_clubClosingFragment,
                    bundle
                )
                return@setOnClickListener
            }
            //위임 신청 상태 ->
            else if (isDelegating) {
                navController.navigate(
                    R.id.action_clubSettingManagementFragment_to_clubDelegatingFragment,
                    bundle
                )
                return@setOnClickListener
            } else if (memberCount != null) {
                if (memberCount > 1) {
                    // 회원수 > 1 ->
                    bundle.putSerializable(
                        KEY_MEMBERS_ENTRY_ROUTE_TYPE,
                        ConstVariable.ClubSetting.MembersEntryRouteType.CLUB_DELEGATE
                    )
                    bundle.putBoolean(KEY_NOTI_DELEGATE_DIALOG, true)
                    navController.navigate(
                        R.id.action_clubSettingManagementFragment_to_clubMembersManagement,
                        bundle
                    )
                } else {
                    // 회원수  1 ->
                    navController.navigate(
                        R.id.action_clubSettingManagementFragment_to_clubRequestCloseFragment,
                        bundle
                    )
                }
            } else {
                Timber.w(" $memberCount")
            }

        }
    }

}