package com.rndeep.fns_fantoo.ui.login

import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentLoginPermissionBinding
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.ui.login.viewmodel.LoginPermissionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginPermissionFragment :
    BaseFragment<FragmentLoginPermissionBinding>(FragmentLoginPermissionBinding::inflate) {

    private val permissionViewModel: LoginPermissionViewModel by viewModels()

    override fun initUi() {
        (requireActivity() as LoginMainActivity).callbackPermissionResultListener = object :
            LoginMainActivity.PermissionsResultListener {
            override fun onRequestPermissionsResult(
                requestCode: Int,
                permissions: Array<out String>,
                grantResults: IntArray
            ) {
                moveToNextScreen()
            }
        }
    }

    override fun initUiActionEvent() {
        binding.btnNext.setOnClickListener {
            if (PermissionCheck.checkPermission(requireContext())) {
                moveToNextScreen()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(), PermissionCheck.PERMISSIONS_ARRAY,
                    PERMISSIONS_REQUEST_ALL
                )
            }
        }
    }

    private fun moveToNextScreen() {
        lifecycleScope.launch {
            permissionViewModel.setPermissinChecked(true)
            delay(100)
            navController.navigate(R.id.action_loginPermissionFragment_to_loginFragment)
        }
    }


    companion object {
        const val PERMISSIONS_REQUEST_ALL = 100
    }
}