package com.rndeep.fns_fantoo.ui.club.settings

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.BaseResponse
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingResignBinding
import com.rndeep.fns_fantoo.ui.club.settings.viewmodel.ClubResignViewModel
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ClubSettingResignFragment :
    ClubSettingBaseFragment<FragmentClubSettingResignBinding>(FragmentClubSettingResignBinding::inflate){

    private val clubResignViewModel : ClubResignViewModel by viewModels()
    private lateinit var clubId:String
    private lateinit var uid: String

    override fun initUi() {
        val args:ClubSettingResignFragmentArgs by navArgs()
        clubId = args.clubId
        uid = args.integUid.toString()

        clubResignViewModel.networkErrorLiveData.observe(viewLifecycleOwner){
            showDialog(getString(R.string.alert_network_error))
        }

        clubResignViewModel.loadingStatusLiveData.observe(viewLifecycleOwner) { loadingState ->
            setLoadingView(loadingState)
        }

        clubResignViewModel.clubLeaveLiveData.observe(viewLifecycleOwner){
            resignResult:BaseResponse ->
            when(resignResult.code){
                ConstVariable.RESULT_SUCCESS_CODE ->{
                    navController.navigate(ClubSettingResignFragmentDirections.actionClubSettingResignFragmentToClubPageDetail(clubId = clubId))
                }
                else ->{
                    requireContext().showErrorSnackBar(binding.root, resignResult.code)
                }
            }
        }
    }

    override fun initUiActionEvent() {
        binding.topbar.setOnBackButtonClickListener{
            navController.popBackStack()
        }

        binding.llResignAgree.setOnClickListener {
            binding.cbResignAgree.isChecked = !binding.cbResignAgree.isChecked
        }
        binding.cbResignAgree.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.btnClubResign.isEnabled = isChecked
        }

        binding.btnClubResign.setOnClickListener{
            showDialog(getString(R.string.k_club_leave), getString(R.string.se_t_sure_want_withdraw), "",
            getString(R.string.t_cancel_leave), getString(R.string.t_do_leave), null, object : CommonDialog.ClickListener{
                    override fun onClick() {
                        clubResignViewModel.leaveClub(clubId, uid)
                    }
                })
        }
    }

    private fun setLoadingView(isShow: Boolean) {
        binding.loadingView.visibility = if (isShow) View.VISIBLE else View.GONE
    }

}