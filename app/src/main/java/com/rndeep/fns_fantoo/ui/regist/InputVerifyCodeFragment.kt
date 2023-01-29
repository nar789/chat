package com.rndeep.fns_fantoo.ui.regist

import android.graphics.Paint
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.RegistInputVerifyCodeFragmentBinding
import com.rndeep.fns_fantoo.ui.regist.viewmodel.InputVerifyCodeViewModel
import com.rndeep.fns_fantoo.utils.ConstVariable.Regist.Companion.KEY_NAV_ARG_INPUT_EMAIL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class InputVerifyCodeFragment :
    RegistFragment<RegistInputVerifyCodeFragmentBinding>(RegistInputVerifyCodeFragmentBinding::inflate) {

    companion object {
        private const val LIMIT_TIME: Long = 600000//milliseconds
        private const val VERIFY_CODE_LENGTH = 6
    }

    private val inputVerifyCodeViewModel: InputVerifyCodeViewModel by viewModels()

    private var countDownTimer: CountDownTimer? = null
    private var inputEmail = ""

    override fun initUi() {
        arguments?.let {
            inputEmail = it.getString(KEY_NAV_ARG_INPUT_EMAIL).toString()
        }
        //binding.btnVerify.isEnabled = false
        binding.btnNext.isEnabled = false
        initCountDownTimer()
        binding.tvTailDesc.text =
            String.format(getString(R.string.se_a_cert_number_will_sent), inputEmail)
        binding.tvResend.paintFlags = binding.tvResend.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        inputVerifyCodeViewModel.checkingVerifyCodeLiveData.observe(viewLifecycleOwner) { verifyCodeCheckResponse ->
            verifyCodeCheckResponse?.let {
                if (verifyCodeCheckResponse.isCheck) {
                    binding.etVerifyCode.background.state = intArrayOf(R.attr.state_success)
                    binding.textInputLayout.isEndIconVisible = false
                    // 패스워드 설정화면으로 이동
                    cancelTimeCounter()
                    navController.navigate(
                        R.id.action_inputVerifyCodeFragment_to_inputPasswordFragment, bundleOf(
                            KEY_NAV_ARG_INPUT_EMAIL to inputEmail
                        )
                    )
                } else {
                    //when(verifyCodeCheckResponse.code){
                    //인증실패?
                    showDialog(getString(R.string.se_a_not_match_cert_number))
                    // }
                }
            }
        }
        inputVerifyCodeViewModel.networkErrorLiveData.observe(viewLifecycleOwner) {
            showDialog(getString(R.string.alert_network_error))
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            delay(300)
            binding.etVerifyCode.requestFocus()
            showSoftInput(binding.etVerifyCode)
        }
    }

    override fun initUiActionEvent() {
        binding.ilTopbar.ivTopbarBack.setOnClickListener {
            cancelTimeCounter()
            navController.popBackStack()
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    cancelTimeCounter()
                    navController.popBackStack()
                }
            })

        //인증코드 재전송
        binding.tvResend.setOnClickListener {
            lifecycleScope.launch {
                inputVerifyCodeViewModel.requestVerifyCodeToEmail(inputEmail)

                binding.llTimeover.visibility = View.GONE
                binding.tvResend.visibility = View.INVISIBLE
                initCountDownTimer()
            }
        }
        binding.btnNext.setOnClickListener {
            //인증코드 체크
            val verifyCode = binding.etVerifyCode.text.toString()
            lifecycleScope.launch {
                inputVerifyCodeViewModel.checkVerifyCode(
                    verifyCode,
                    inputEmail
                )
            }


        }

        binding.etVerifyCode.addTextChangedListener(inputCodeTextWatcher)
    }

    private fun initCountDownTimer() {
        cancelTimeCounter()
        binding.tvRemainTitle.setTextColor(
            ContextCompat.getColor(
                requireContext().applicationContext,
                R.color.primary_500
            )
        )
        binding.tvRemainTime.setTextColor(
            ContextCompat.getColor(
                requireContext().applicationContext,
                R.color.primary_500
            )
        )
        countDownTimer = object : CountDownTimer(LIMIT_TIME, 1000) {
            override fun onTick(millis: Long) {
                binding.tvRemainTime.text = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
                )
            }

            override fun onFinish() {
                binding.llTimeover.visibility = View.VISIBLE
                binding.tvRemainTitle.setTextColor(
                    ContextCompat.getColor(
                        context!!.applicationContext,
                        R.color.gray_400
                    )
                )
                binding.tvRemainTime.setTextColor(
                    ContextCompat.getColor(
                        context!!.applicationContext,
                        R.color.gray_400
                    )
                )
                binding.tvResend.visibility = View.VISIBLE
            }
        }
        countDownTimer?.start()
    }

    private fun cancelTimeCounter() {
        if (countDownTimer != null) {
            countDownTimer?.cancel()
            countDownTimer = null
        }
    }

    /*fun setDangerView(isDanger:Boolean){

    }*/

    private var inputCodeTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(c: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding.textInputLayout.isEndIconVisible = true
            binding.btnNext.isEnabled = (c != null && c.length >= VERIFY_CODE_LENGTH)
        }

        override fun afterTextChanged(p0: Editable?) {
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        cancelTimeCounter()
    }

}