package com.rndeep.fns_fantoo.ui.login

import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.rndeep.fns_fantoo.MainActivity
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentLoginEmailBinding
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.ui.login.viewmodel.LoginViewModel
import com.rndeep.fns_fantoo.ui.regist.RegistActivity
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.ERROR_AE5007
import com.rndeep.fns_fantoo.utils.TimeUtils
import com.rndeep.fns_fantoo.utils.getErrorString
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.regex.Pattern

@AndroidEntryPoint
class LoginEmailFragment :
    BaseFragment<FragmentLoginEmailBinding>(FragmentLoginEmailBinding::inflate) {
    private val loginViewModel: LoginViewModel by viewModels()

    override fun initUi() {
        binding.btnLogin.isEnabled = false
        val focusedView = binding.root.findFocus()
        if (focusedView != null && (focusedView is TextInputEditText)) {
            lifecycleScope.launch {
                delay(300)
                binding.etEmail.requestFocus()
                showSoftInput(focusedView)
            }
        }

        loginViewModel.authCodeLiveData.observe(this) { authCodeResponse ->
            Timber.d("emailAuthCodeLiveData = $authCodeResponse")
            authCodeResponse?.let { authCode ->
                when (authCode.code) {
                    ConstVariable.RESULT_SUCCESS_CODE -> {
                        authCode.authCodeDto?.let {
                            val values = hashMapOf(
                                ConstVariable.Auth.KEY_CLIENT_ID to ConstVariable.Auth.VALUE_CLIENT_ID,
                                ConstVariable.Auth.KEY_CLIENT_SECRET to ConstVariable.Auth.VALUE_CLIENT_SECRET,
                                ConstVariable.Auth.KEY_GRANT_TYPE to ConstVariable.Auth.VALUE_GRANT_TYPE_AUTH,
                                ConstVariable.Auth.KEY_STATE to it.state,
                                ConstVariable.Auth.KEY_AUTHCODE to it.authCode
                            )
                            loginViewModel.getAccessToken(values)
                        }
                    }
                    ConstVariable.ERROR_AE5000 -> {
                        showDialog(
                            getString(R.string.r_login),
                            requireContext().getErrorString(authCode.code)
                        )
                    }
                    ConstVariable.ERROR_AE5002, ERROR_AE5007 -> {
                        showDialog(
                            getString(R.string.r_login),
                            getString(R.string.se_b_cannot_match_password_recheck)
                        )
                    }
                    ConstVariable.ERROR_AE5008 -> {
                        showDialog("", getString(R.string.se_t_check_rejoin_withdraw_account))
                    }
                    else -> {
                        authCode.code?.let {
                            requireContext().showErrorSnackBar(binding.root, it)
                        }
                    }
                }
            }
        }
        loginViewModel.accessTokenLiveData.observe(this) { accessTokenResponse ->
            lifecycleScope.launch {
                accessTokenResponse?.let {
                    if (accessTokenResponse.code == ConstVariable.RESULT_SUCCESS_CODE) {
                        val accessTokenDto = accessTokenResponse.accessTokenDto
                        if (accessTokenDto != null) {
                            Timber.d("--- auth Info --- \n accessToken : ${accessTokenDto.accessToken} \n Uid : ${accessTokenDto.integUid}")
                            loginViewModel.setAccessToken(accessTokenDto.accessToken)
                            loginViewModel.setRefreshAccessToken(accessTokenDto.refreshToken)
                            loginViewModel.setRefreshTokenPublishTime(
                                TimeUtils.getCurrentTime(
                                    ConstVariable.Time.TIME_ZONE_SEOUL
                                )
                            )
                            loginViewModel.setIsLogined(true)
                            loginViewModel.setLastLoginType(ConstVariable.LoginType.EMAIL)
                            loginViewModel.setUID(accessTokenDto.integUid)
                            delay(100)
                            startActivity(Intent(requireActivity(), MainActivity::class.java))
                            requireActivity().finish()
                        } else {
                            Timber.e("accessTokenDto is null")
                        }
                    } else {
                        //임시
                        accessTokenResponse.msg?.let { it1 -> showDialog(it1) }
                    }
                }
            }
        }

        loginViewModel.networkErrorLiveData.observe(this) {
            showDialog(getString(R.string.alert_network_error))
        }
    }

    override fun initUiActionEvent() {
        binding.btnLogin.setOnClickListener {
            val inputEmail: String = binding.etEmail.text.toString()
            if (TextUtils.isEmpty(inputEmail) || !Patterns.EMAIL_ADDRESS.matcher(inputEmail)
                    .matches()
            ) {
                showDialog(
                    getString(R.string.r_login),
                    getString(R.string.se_a_incorrect_email_format)
                )
                return@setOnClickListener
            }
            val inputPassword: String = binding.etPassword.text.toString()
            if (!Pattern.compile(ConstVariable.PASSWORD_REGEX).matcher(inputPassword).matches()) {
                showDialog(getString(R.string.r_login), getString(R.string.se_num_keep_format))
                return@setOnClickListener
            }
            val values = hashMapOf(
                ConstVariable.Auth.KEY_CLIENT_ID to ConstVariable.Auth.VALUE_CLIENT_ID,
                ConstVariable.KEY_LOGIN_ID to inputEmail,
                ConstVariable.KEY_LOGIN_PASSWORD to binding.etPassword.text.toString(),
                ConstVariable.KEY_LOGIN_TYPE to ConstVariable.LoginType.EMAIL,
                ConstVariable.Auth.KEY_RESPONSE_TYPE to ConstVariable.Auth.VALUE_RESPONSE_TYPE
            )
            loginViewModel.getAuthCode(values)
        }
        binding.ilTop.ivTopbarBack.setOnClickListener {
            navController.popBackStack()
        }
        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(requireActivity(), RegistActivity::class.java))
        }
        binding.tvFindPassword.setOnClickListener {
            navController.navigate(R.id.action_loginEmailFragment_to_loginFindPasswordFragment)
        }
        binding.etEmail.addTextChangedListener(textWatcher)
        binding.etPassword.addTextChangedListener(passwordTextWatcher)
        binding.etPassword.setOnFocusChangeListener { _, _ ->
            binding.etPassword.text?.let {
                /*if(hasFocus && it.isNotEmpty()){
                    binding.ivDelete.visibility = View.VISIBLE
                }else{
                    binding.ivDelete.visibility = View.INVISIBLE
                }*/
            }
        }
    }

    var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(p0: Editable?) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            setLoginButtonEnable()
        }
    }

    private var passwordTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            /*s?.let {
                binding.ivDelete.visibility = if(it.isNotEmpty())View.VISIBLE else View.INVISIBLE
            }*/
            setLoginButtonEnable()
        }
    }

    fun setLoginButtonEnable() {
        val idLength: Int = binding.etEmail.length()
        val pwLength: Int = binding.etPassword.length()
        binding.btnLogin.isEnabled = idLength > 0 && pwLength > 0
    }

}