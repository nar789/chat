package com.rndeep.fns_fantoo.ui.login

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentLoginFindPasswordBinding
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.ui.login.viewmodel.LoginViewModel
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.TextUtils
import com.rndeep.fns_fantoo.utils.showCustomSnackBar
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule

@AndroidEntryPoint
class LoginFindPasswordFragment :
    BaseFragment<FragmentLoginFindPasswordBinding>(FragmentLoginFindPasswordBinding::inflate) {
    private val loginViewModel: LoginViewModel by viewModels()
    override fun initUi() {
        //임시비밀번호 요청 결과
        loginViewModel.requestTempPasswordLiveData.observe(this) { requestTempPasswordResponse ->
            requestTempPasswordResponse?.let {
                when (requestTempPasswordResponse.code) {
                    ConstVariable.RESULT_SUCCESS_CODE -> {
                        binding.textInputLayout.isEndIconVisible = false
                        requireActivity().showCustomSnackBar(
                            binding.root,
                            getString(R.string.se_a_sent_cert_number)
                        )
                        Timer("", false).schedule(1000) {
                            lifecycleScope.launch {
                                navController.popBackStack()
                            }
                        }
                    }
                    else -> {
                        requireContext().showErrorSnackBar(binding.root, it.code)
                    }
                }
            }
        }

        loginViewModel.networkErrorLiveData.observe(this) {
            showDialog(getString(R.string.alert_network_error))
        }
        lifecycleScope.launchWhenCreated {
            delay(300)
            binding.etEmail.requestFocus()
            showSoftInput(binding.etEmail)
        }
    }

    override fun initUiActionEvent() {
        binding.ilTop.ivTopbarBack.setOnClickListener {
            navController.popBackStack()
        }

        binding.etEmail.addTextChangedListener(emailTextWatcher)
        binding.btnRequestCode.isEnabled = false
        binding.btnRequestCode.setOnClickListener {
            val inputEmail = binding.etEmail.text.toString()
            if (!TextUtils.isValidEmailFormat(inputEmail)) {
                setEmailFormatWarningTextVisible(true)
                return@setOnClickListener
            }
            //임시비밀번호 요청
            loginViewModel.requestTempPassword(inputEmail)
        }
    }

    private var emailTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(c: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding.btnRequestCode.isEnabled = (c != null && c.isNotEmpty())
        }

        override fun afterTextChanged(p0: Editable?) {
            setEmailFormatWarningTextVisible(false)
        }
    }

    private fun setEmailFormatWarningTextVisible(visible: Boolean) {
        binding.tvWrongFormat.visibility = if (visible) View.VISIBLE else View.GONE
        binding.llInputTail.visibility = if (visible) View.GONE else View.VISIBLE
    }
}