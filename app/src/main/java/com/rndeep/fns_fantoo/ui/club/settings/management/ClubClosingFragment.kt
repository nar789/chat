package com.rndeep.fns_fantoo.ui.club.settings.management

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingClubClosingBinding
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.ui.club.settings.ClubSettingBaseFragment
import com.rndeep.fns_fantoo.ui.club.settings.management.viewmodel.ClubClosingViewModel
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_UID
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClubClosingFragment :
    ClubSettingBaseFragment<FragmentClubSettingClubClosingBinding>(
        FragmentClubSettingClubClosingBinding::inflate
    ) {
    private val clubClosingViewModel: ClubClosingViewModel by viewModels()
    private lateinit var clubId:String
    private lateinit var uid:String
    override fun initUi() {
        val args : ClubClosingFragmentArgs by navArgs()
        clubId = args.clubId
        uid = args.integUid.toString()
        val date = "2022. 08. 31 09:53"
        binding.tvDesc.text = getString(R.string.p_date_will_closed_date)+" "+ date

        clubClosingViewModel.networkErrorLiveData.observe(viewLifecycleOwner){
            showDialog(getString(R.string.alert_network_error))
        }

        clubClosingViewModel.closeCancelResultLiveData.observe(viewLifecycleOwner) { baseResponse ->
            when (baseResponse.code) {
                ConstVariable.RESULT_SUCCESS_CODE -> {
                    showDialog(
                        getString(R.string.k_done_close_club),
                        getString(R.string.se_k_cancel_close_club),
                        "",
                        getString(R.string.h_confirm),
                        "",
                        object : CommonDialog.ClickListener{
                            override fun onClick() {
                                navController.popBackStack()
                            }
                        }, null
                    )
                }
                else ->{
                    requireContext().showErrorSnackBar(binding.root, baseResponse.code)
                }
            }
        }
    }

    override fun initUiActionEvent() {
        binding.topbar.setOnBackButtonClickListener {
            navController.popBackStack()
        }

        binding.btnClubClosingCancel.setOnClickListener {
            showDialog(getString(R.string.k_cancel_close_club),
                getString(R.string.se_k_do_you_cancel_close_club),
                "",
                getString(R.string.a_yes),
                getString(R.string.a_no),
                object : CommonDialog.ClickListener {
                    override fun onClick() {
                        clubClosingViewModel.cancelClubClose(clubId, uid, hashMapOf(
                            KEY_UID to uid
                        ))
                    }
                },
                null
            )
        }


    }

}