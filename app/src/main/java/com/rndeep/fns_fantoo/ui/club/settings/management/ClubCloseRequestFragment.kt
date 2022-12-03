package com.rndeep.fns_fantoo.ui.club.settings.management

import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingClubCloseRequestBinding
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.ui.club.settings.ClubSettingBaseFragment
import com.rndeep.fns_fantoo.ui.club.settings.management.viewmodel.ClubCloseViewModel
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.CLUB_CLOSE_REASON_MAX_LENGTH
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.CLUB_CLOSE_REASON_MIN_LENGTH
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_UID
import com.rndeep.fns_fantoo.utils.TextUtils
import com.rndeep.fns_fantoo.utils.showCustomSnackBar
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ClubCloseRequestFragment :
    ClubSettingBaseFragment<FragmentClubSettingClubCloseRequestBinding>(FragmentClubSettingClubCloseRequestBinding::inflate){

    private val clubCloseViewModel : ClubCloseViewModel by viewModels()
    private lateinit var clubId: String
    private lateinit var uid: String

    override fun initUi() {
        val args : ClubCloseRequestFragmentArgs by navArgs()
        clubId = args.clubId
        uid = args.integUid.toString()

        clubCloseViewModel.networkErrorLiveData.observe(viewLifecycleOwner){
            showDialog(getString(R.string.alert_network_error))
        }

        clubCloseViewModel.clubCloseRequestResultLiveData.observe(viewLifecycleOwner){
            result ->
            result?.let {
                when(it.code){
                    ConstVariable.RESULT_SUCCESS_CODE ->{
                        lifecycleScope.launch{
                            context?.showCustomSnackBar(binding.root, getString(R.string.a_done))
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
    }

    override fun initUiActionEvent() {
        binding.topbar.setOnBackButtonClickListener{
            navController.popBackStack()
        }

        binding.btnClubCloseRequest.setOnClickListener{
            showDialog(getString(R.string.k_club_closure), getString(R.string.se_k_want_to_close_club), "",
                getString(R.string.a_yes), getString(R.string.a_no),
                object : CommonDialog.ClickListener{
                    override fun onClick() {
                        clubCloseViewModel.requestClubClose(clubId,
                        hashMapOf(KEY_UID to uid))
                    }
                }, null)
        }
    }
}