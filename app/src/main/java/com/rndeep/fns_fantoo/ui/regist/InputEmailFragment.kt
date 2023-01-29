package com.rndeep.fns_fantoo.ui.regist

import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.RegistInputEmailFragmentBinding
import com.rndeep.fns_fantoo.ui.regist.viewmodel.InputEmailViewModel
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.ERROR_FE1008
import com.rndeep.fns_fantoo.utils.ConstVariable.RESULT_SUCCESS_CODE
import com.rndeep.fns_fantoo.utils.TextUtils
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class InputEmailFragment :
    RegistFragment<RegistInputEmailFragmentBinding>(RegistInputEmailFragmentBinding::inflate) {

    private val inputEmailViewModel: InputEmailViewModel by viewModels()

    override fun initUi() {
        binding.btnNext.isEnabled = false

        binding.tvEmailInputTail.text = getString(R.string.se_a_login_already_registered)
        binding.tvEmailInputTail.makeLinks(Pair(getString(R.string.r_login), View.OnClickListener {
            requireActivity().finish()
        }))

        inputEmailViewModel.emailVerifyCodeLiveData.observe(viewLifecycleOwner) { result ->
            if (result) {
                navController.navigate(
                    R.id.inputVerifyCodeFragment, bundleOf(
                        ConstVariable.Regist.KEY_NAV_ARG_INPUT_EMAIL to binding.etEmail.text.toString()
                    )
                )
            } else {
                showDialog(getString(R.string.alert_network_error))
            }
        }

        inputEmailViewModel.checkingJoinedUserLiveData.observe(viewLifecycleOwner) { checkingJoinUserResponse ->
            checkingJoinUserResponse?.let { checkingJoinUser ->
                when (checkingJoinUser.code) {
                    RESULT_SUCCESS_CODE -> {
                        val checkingJoinUserDto = checkingJoinUser.checkingJoinUserDto
                        checkingJoinUserDto?.let {
                            if (!checkingJoinUserDto.isUser) {
                                binding.textInputLayout.isEndIconVisible = false
                                binding.etEmail.background.state = intArrayOf(R.attr.state_success)
                                lifecycleScope.launch {
                                    inputEmailViewModel.requestVerifyCodeToEmail(
                                        binding.etEmail.text.toString()
                                    )
                                }
                            } else {//가입된 기존회원 - isUser : true
                                showDialog(getString(R.string.se_a_already_use_email))
                            }
                        }
                    }
                    ERROR_FE1008 -> {//탈퇴 계정
                        showDialog(getString(R.string.se_t_check_rejoin_withdraw_account))
                    }
                    else -> {
                        requireContext().showErrorSnackBar(binding.root, checkingJoinUser.code)
                    }
                }
            }
        }

        inputEmailViewModel.networkErrorLiveData.observe(viewLifecycleOwner) {
            showDialog(getString(R.string.alert_network_error))
        }

        inputEmailViewModel.loadingStatusLiveData.observe(viewLifecycleOwner) {
            binding.loadingView.visibility = if (it) View.VISIBLE else View.INVISIBLE
            binding.loadingView.z = 10f//버튼뒤에 숨는 문제로 z order변경
        }

//        setGifImage(binding.loadingView.ivLoadingImage, R.drawable.character_loading)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            delay(300)
            binding.etEmail.requestFocus()
            showSoftInput(binding.etEmail)
        }
    }

    override fun initUiActionEvent() {
        binding.ilTopbar.ivTopbarBack.setOnClickListener {
            activity?.finish()
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            })
        binding.btnNext.setOnClickListener {
            hideSoftInput(binding.etEmail)
            val inputEmail = binding.etEmail.text.toString()
            if (!TextUtils.isValidEmailFormat(inputEmail)) {
                showInvalidEmailMessage(true)
                return@setOnClickListener
            }
            Timber.d("btnNext click")
            //이미 가입여부 체크
            inputEmailViewModel.checkingJoinedUser(
                ConstVariable.LoginType.EMAIL,
                inputEmail
            )
        }
        binding.etEmail.addTextChangedListener(emailTextWatcher)
    }

    private var emailTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(c: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding.btnNext.isEnabled = (c != null && c.isNotEmpty())
            binding.textInputLayout.isEndIconVisible = true
        }

        override fun afterTextChanged(p0: Editable?) {
            showInvalidEmailMessage(false)
        }
    }

    private fun showInvalidEmailMessage(isShow: Boolean) {
        binding.tvInvalidEmail.visibility = if (isShow) View.VISIBLE else View.GONE
        binding.llInputTail.visibility = if (isShow) View.INVISIBLE else View.VISIBLE
        //val state = if(isShow)intArrayOf(R.attr.state_warning) else intArrayOf(-R.attr.state_warning, android.R.attr.state_focused)
        //binding.etEmail.background.state = state
    }

    private fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(this.text)
        var startIndexOfLink = -1
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun updateDrawState(textPaint: TextPaint) {
                    context?.getColor(R.color.primary_500)?.let {
                        textPaint.color = it
                        textPaint.isUnderlineText = false
                    }
                }

                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
//      if(startIndexOfLink == -1) continue
            spannableString.setSpan(
                clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this.movementMethod =
            LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

}