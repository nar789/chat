package com.rndeep.fns_fantoo.ui.regist

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.RegistInputPasswordFragmentBinding
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.PASSWORD_REGEX
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.regex.Pattern

class InputPasswordFragment : RegistFragment<RegistInputPasswordFragmentBinding>(RegistInputPasswordFragmentBinding::inflate){
    companion object {
        private const val PASSWORD_MIN_LENGTH = 8
    }

    private var inputEmail = ""

    override fun initUi(){
        arguments?.let {
            inputEmail = it.getString(ConstVariable.Regist.KEY_NAV_ARG_INPUT_EMAIL).toString()
        }

        binding.btnNext.isEnabled = false
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            delay(300)
            binding.etPassword.requestFocus()
            showSoftInput(binding.etPassword)
        }
    }

    override fun initUiActionEvent() {
        binding.topbar.setOnClickListener {
            navController.apply {
                navigate(R.id.action_inputPasswordFragment_to_inputEmailFragment)
                backQueue.clear()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                navController.apply {
                    navigate(R.id.action_inputPasswordFragment_to_inputEmailFragment)
                    backQueue.clear()
                }
            }
        })
        binding.btnNext.setOnClickListener {
            navController.navigate(R.id.action_inputPasswordFragment_to_agreeConfirmFragment,
            bundleOf(ConstVariable.Regist.KEY_NAV_ARG_INPUT_EMAIL to inputEmail,
            ConstVariable.Regist.KEY_NAV_ARG_PASSWORD to binding.etRePassword.text.toString()))
        }
        binding.etPassword.addTextChangedListener(passwordWatcher1)
        binding.etRePassword.addTextChangedListener(passwordTextWatcher2)
    }


    private var passwordWatcher1: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun onTextChanged(c: CharSequence?, p1: Int, p2: Int, p3: Int) {
            context?.let { binding.tvPWFormatGuide.setTextColor(it.getColor(R.color.gray_500)) }
            checkingInputPassword(binding.etPassword)
        }
    }

    private val passwordTextWatcher2: TextWatcher = object :TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun afterTextChanged(s: Editable?) {
        }

        override fun onTextChanged(c: CharSequence?, start: Int, before: Int, count: Int) {
            checkingInputPassword(binding.etRePassword)
        }
    }

    fun checkingInputPassword(editText: TextInputEditText){
        try {
            var isValidPattern = false
            var nextButtonEnable = false
            binding.tvWrongPW.visibility = View.GONE
            val pw = binding.etPassword.text.toString()
            if (Pattern.compile(PASSWORD_REGEX).matcher(pw).matches()) {
                isValidPattern = true
                updateInputFieldState(binding.etPassword, true)
            } else {
                updateInputFieldState(binding.etPassword, false)
                if(binding.etPassword == editText){
                    context?.let { binding.tvPWFormatGuide.setTextColor(it.getColor(R.color.state_danger)) }
                }
            }
            val rePW = binding.etRePassword.text.toString()
            if (!TextUtils.isEmpty(rePW) && isValidPattern && rePW == pw) {
                nextButtonEnable = true
                updateInputFieldState(binding.etRePassword, true)
            } else {
                updateInputFieldState(binding.etRePassword, false)
                if (isValidPattern && !TextUtils.isEmpty(rePW) && rePW.length >= PASSWORD_MIN_LENGTH) {
                    binding.tvWrongPW.visibility = View.VISIBLE
                    //binding.etRePassword.background.state = intArrayOf(R.attr.state_warning)
                }
            }
            binding.btnNext.isEnabled = nextButtonEnable
        }catch (e :Exception){
            Timber.e("checkingInputPassword err msg : ${e.message}")
        }
    }

    private fun updateInputFieldState(view: View, isSuccess:Boolean){
        view.setBackgroundResource(if(isSuccess) R.drawable.input_field_success else R.drawable.input_field)
    }
}