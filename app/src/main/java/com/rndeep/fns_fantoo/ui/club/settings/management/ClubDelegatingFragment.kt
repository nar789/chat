package com.rndeep.fns_fantoo.ui.club.settings.management

import android.icu.text.SimpleDateFormat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingDelegatingBinding
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.ui.club.settings.ClubSettingBaseFragment
import com.rndeep.fns_fantoo.ui.club.settings.management.viewmodel.ClubDelegatingViewModel
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.setProfileAvatar
import com.rndeep.fns_fantoo.utils.showCustomSnackBar
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ClubDelegatingFragment :
    ClubSettingBaseFragment<FragmentClubSettingDelegatingBinding>(
        FragmentClubSettingDelegatingBinding::inflate
    ) {

    private val clubDelegatingViewModel: ClubDelegatingViewModel by viewModels()

    private lateinit var clubId: String
    private lateinit var uid: String
    private lateinit var targetMemberId:String
    override fun initUi() {
        val args: ClubDelegatingFragmentArgs by navArgs()
        clubId = args.clubId
        uid = args.integUid.toString()

        clubDelegatingViewModel.networkErrorLiveData.observe(viewLifecycleOwner){
            showDialog(getString(R.string.alert_network_error))
        }

        clubDelegatingViewModel.clubDelegateCancelResultLiveData.observe(viewLifecycleOwner) { result ->
            when (result.code) {
                ConstVariable.RESULT_SUCCESS_CODE -> {
                    lifecycleScope.launch {
                        context?.showCustomSnackBar(
                            binding.root,
                            getString(R.string.se_k_completed_to_cancel_delegating)
                        )
                        delay(500)
                        navController.popBackStack()

                    }
                }
                else ->{
                    requireContext().showErrorSnackBar(binding.root, result.code)
                }
            }
        }

        clubDelegatingViewModel.delegatingInfoLiveData.observe(viewLifecycleOwner){
            clubDelegatingInfoResponse->
            when(clubDelegatingInfoResponse.code){
                ConstVariable.RESULT_SUCCESS_CODE -> {
                    clubDelegatingInfoResponse.clubDelegatingInfoDto?.let {
                        targetMemberId = it.memberId.toString()
                        if(!it.profileImg.isNullOrEmpty()){
                            setProfileAvatar(binding.ivProfile, it.profileImg)
                        }
                        binding.tvNickName.text = it.nickname
                        binding.tvMemberGrade.text = if (it.memberLevel == ConstVariable.ClubDef.CLUB_MEMBERSHIP_LV_MANAGER) getString(
                            R.string.k_club_president
                        ) else getString(R.string.a_general_membership)

                        val delegateAgree = (it.delegateStatus == 2) //위임 여부(1:위임 요청, 2:멤버승인)
                        try {
                            val delegatingDate =
                                if (!it.delegateOkDate.isNullOrEmpty()) {
                                    SimpleDateFormat("yyyy.MM.dd HH:mm").format(
                                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(it.delegateOkDate)
                                    )
                                } else {
                                    ""
                                }
                            val subTitleString =
                                if (delegateAgree) delegatingDate else getString(R.string.m_confirmation_after_member_agree)
                            binding.tvTitle2.text =
                                getString(R.string.a_delegating_schedule) + " " + subTitleString

                        }catch (e:Exception){
                            Timber.e("${e.printStackTrace()}")
                        }
                    }
                }
                else ->{
                    clubDelegatingInfoResponse.code?.let {
                        requireContext().showErrorSnackBar(binding.root, it)
                    }
                }
            }
        }

        clubDelegatingViewModel.getDelegatingInfo(clubId, uid)

    }

    override fun initUiActionEvent() {
        binding.topbar.setOnBackButtonClickListener {
            navController.popBackStack()
        }

        binding.btnCancelDelegate.setOnClickListener {
            showDialog("", getString(R.string.se_a_canel_delegating_club), "",
                getString(R.string.a_cancel_delegating), getString(R.string.a_no),
                object : CommonDialog.ClickListener {
                    override fun onClick() {
                        clubDelegatingViewModel.cancelClubDelegating(
                            clubId, targetMemberId,
                            hashMapOf(
                                ConstVariable.KEY_UID to uid,
                                ConstVariable.ClubDef.KEY_CLUB_INFO_MEMBERID to targetMemberId,
                                "status" to 2
                            )
                        )//위임 설정 (1:멤버승인, 2:위임요청, 3:멤버거절, 4:클럽장취소)
                    }
                }, null
            )
        }
    }
}