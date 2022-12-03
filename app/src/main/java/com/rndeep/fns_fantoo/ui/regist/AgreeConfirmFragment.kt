package com.rndeep.fns_fantoo.ui.regist

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CompoundButton
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rndeep.fns_fantoo.MainActivity
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.Country
import com.rndeep.fns_fantoo.ui.common.LanguageBottomSheet
import com.rndeep.fns_fantoo.databinding.RegistAgreeConfirmFragmentBinding
import com.rndeep.fns_fantoo.ui.common.viewgroup.CommonEditTextLayout
import com.rndeep.fns_fantoo.ui.login.LoginMainActivity
import com.rndeep.fns_fantoo.ui.regist.viewmodel.AgreeConfirmViewModel
import com.rndeep.fns_fantoo.utils.*
import com.rndeep.fns_fantoo.utils.ConstVariable.ERROR_FE1007
import com.rndeep.fns_fantoo.utils.ConstVariable.INTENT.Companion.EXTRA_LOGIN_TYPE
import com.rndeep.fns_fantoo.utils.ConstVariable.INTENT.Companion.EXTRA_MYPROFILE_NICK_NAME
import com.rndeep.fns_fantoo.utils.ConstVariable.INTENT.Companion.EXTRA_REGISTER_USER
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_LOGIN_ID
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_LOGIN_PASSWORD
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_LOGIN_TYPE
import com.rndeep.fns_fantoo.utils.ConstVariable.ProfileDef.Companion.NICKNAME_MAX_LENGTH
import com.rndeep.fns_fantoo.utils.ConstVariable.ProfileDef.Companion.NICKNAME_MIN_LENGTH
import com.rndeep.fns_fantoo.utils.ConstVariable.RESULT_SUCCESS_CODE
import com.rndeep.fns_fantoo.utils.ConstVariable.Regist.Companion.KEY_JOIN_AD_AGREE
import com.rndeep.fns_fantoo.utils.ConstVariable.Regist.Companion.KEY_JOIN_AGE_AGREE
import com.rndeep.fns_fantoo.utils.ConstVariable.Regist.Companion.KEY_JOIN_COUNTRY
import com.rndeep.fns_fantoo.utils.ConstVariable.Regist.Companion.KEY_JOIN_REFERRAL_CODE
import com.rndeep.fns_fantoo.utils.ConstVariable.Regist.Companion.KEY_JOIN_SERVICE_AGREE
import com.rndeep.fns_fantoo.utils.ConstVariable.Regist.Companion.KEY_JOIN_TEENAGER_AGREE
import com.rndeep.fns_fantoo.utils.ConstVariable.Regist.Companion.KEY_JOIN_USERINFO_AGREE
import com.rndeep.fns_fantoo.utils.ConstVariable.Regist.Companion.KEY_NAV_ARG_INPUT_EMAIL
import com.rndeep.fns_fantoo.utils.ConstVariable.Regist.Companion.KEY_NAV_ARG_PASSWORD
import com.rndeep.fns_fantoo.utils.ConstVariable.Regist.Companion.KEY_USER_NICK
import com.rndeep.fns_fantoo.utils.ConstVariable.VALUE_NO
import com.rndeep.fns_fantoo.utils.ConstVariable.VALUE_YES
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class AgreeConfirmFragment :
    RegistFragment<RegistAgreeConfirmFragmentBinding>(RegistAgreeConfirmFragmentBinding::inflate) {

    companion object {
        private const val NICK_LIMIT_COUNT = 20
    }

    private val agreeConfirmViewModel: AgreeConfirmViewModel by viewModels()

    //email가입  data
    private var inputEmail = ""
    private var password = ""

    private var nickName = ""

    //SNS가입 data
    private var isFromLoginActivity = false
    private var snsLoginId: String? = ""
    private var snsLoginType: String? = ""

    override fun initUi() {
        arguments?.let {
            val callerActivity = it.getString(ConstVariable.INTENT.EXTRA_FROM_ACTIVITY)
            snsLoginId = it.getString(ConstVariable.INTENT.EXTRA_LOGIN_ID)
            snsLoginType = it.getString(EXTRA_LOGIN_TYPE)
            if (!callerActivity.isNullOrEmpty() && callerActivity == LoginMainActivity::class.java.simpleName) {
                isFromLoginActivity = true
            }
            Timber.d(
                "byLoginActivity = $isFromLoginActivity " +
                        ", sns-id = $snsLoginId " +
                        " , sns-type = $snsLoginType"
            )
            if (!isFromLoginActivity) {
                inputEmail = it.getString(KEY_NAV_ARG_INPUT_EMAIL).toString()
                password = it.getString(KEY_NAV_ARG_PASSWORD).toString()
            }
        }

        Timber.d("AgreeConfirmFrag")
        binding.cEtLayout.setButtonEnable(false)
        binding.btnComplete.isEnabled = false
        activity?.let { it ->
            agreeConfirmViewModel.selectedCountry.observe(it) {
                lifecycleScope.launch {
                    agreeConfirmViewModel.getLanguageCode()?.let { langCode ->
                        val countryName =
                            if (langCode == LanguageUtils.KOREAN) it.nameKr else it.nameEn
                        binding.tvSelectedCountry.text = countryName
                        updateCompleteButtonEnable()
                    }
                }
            }
        }

        agreeConfirmViewModel.checkingNickNameLiveData.observe(viewLifecycleOwner) { userNickCheckResponse ->
            userNickCheckResponse?.let { userNickCheck ->
                Timber.d("nickCheck result code ${userNickCheck.code}")
                when (userNickCheck.code) {
                    RESULT_SUCCESS_CODE -> {
                        val userNickCheckDto = userNickCheck.userNickCheckDto
                        userNickCheckDto?.let {
                            updateNickCheckResult(it.isCheck)
                        }
                    }
                    ERROR_FE1007 -> {
                        updateNickCheckResult(true)
                    }
                    else -> {
                        requireContext().showErrorSnackBar(binding.root, userNickCheck.code)
                    }
                }
            }
        }

        if (isFromLoginActivity) {
            agreeConfirmViewModel.joinSnsLiveData.observe(viewLifecycleOwner) { registerResponse ->
                registerResponse?.let { it ->
                    when (it.code) {
                        RESULT_SUCCESS_CODE -> {//성공 SNS가입
                            activity?.let {
                                val intent = Intent()
                                intent.putExtra(
                                    ConstVariable.INTENT.EXTRA_MYPROFILE_COUNTRY_ISO2,
                                    (agreeConfirmViewModel.selectedCountry.value as Country).iso2
                                )
                                intent.putExtra(EXTRA_MYPROFILE_NICK_NAME, nickName)
                                it.setResult(AppCompatActivity.RESULT_OK, intent)
                                it.finish()
                            }
                        }
                        else -> {
                            requireContext().showErrorSnackBar(binding.root, registerResponse.code)
                        }
                    }
                }
            }
        } else {
            agreeConfirmViewModel.joinEmailLiveData.observe(viewLifecycleOwner) { registResponse ->
                registResponse?.let { regist ->
                    when (regist.code) {
                        RESULT_SUCCESS_CODE -> {
                            //성공 Email가입
                            //Timber.d("getEmailAuthCode id = ${inputEmail} , pw = ${password}")
                            val values = hashMapOf(
                                ConstVariable.Auth.KEY_CLIENT_ID to ConstVariable.Auth.VALUE_CLIENT_ID,
                                KEY_LOGIN_ID to inputEmail,
                                KEY_LOGIN_PASSWORD to password,
                                KEY_LOGIN_TYPE to ConstVariable.LoginType.EMAIL,
                                ConstVariable.Auth.KEY_RESPONSE_TYPE to ConstVariable.Auth.VALUE_RESPONSE_TYPE
                            )
                            agreeConfirmViewModel.getAuthCode(values)
                        }
                        else -> {
                            requireContext().showErrorSnackBar(binding.root, regist.code)
                        }
                    }
                }
            }
        }
        //통합인증 ob1
        agreeConfirmViewModel.authCodeLiveData.observe(viewLifecycleOwner) { authCodeResponse ->
            authCodeResponse?.let {
                when (authCodeResponse.code) {
                    RESULT_SUCCESS_CODE -> {
                        val authCodeDto = authCodeResponse.authCodeDto
                        if (authCodeDto != null) {
                            val values = hashMapOf(
                                ConstVariable.Auth.KEY_CLIENT_ID to ConstVariable.Auth.VALUE_CLIENT_ID,
                                ConstVariable.Auth.KEY_CLIENT_SECRET to ConstVariable.Auth.VALUE_CLIENT_SECRET,
                                ConstVariable.Auth.KEY_GRANT_TYPE to ConstVariable.Auth.VALUE_GRANT_TYPE_AUTH,
                                ConstVariable.Auth.KEY_STATE to authCodeDto.state,
                                ConstVariable.Auth.KEY_AUTHCODE to authCodeDto.authCode
                            )
                            agreeConfirmViewModel.getAccessToken(values)
                        } else {
                            Timber.e("authCodeDto is null")
                        }
                    }
                }
            }
        }

        agreeConfirmViewModel.accessTokenLiveData.observe(viewLifecycleOwner) { accessTokenResponse ->
            accessTokenResponse?.let {
                when (accessTokenResponse.code) {
                    RESULT_SUCCESS_CODE -> {
                        val accessTokenDto = accessTokenResponse.accessTokenDto
                        accessTokenDto?.let {
                            lifecycleScope.launch {
                                agreeConfirmViewModel.setAccessToken(it.accessToken)
                                agreeConfirmViewModel.setUID(it.integUid)
                                agreeConfirmViewModel.setIsLogined(true)
                                agreeConfirmViewModel.setLastLoginType(ConstVariable.LoginType.EMAIL)
                                agreeConfirmViewModel.setRefreshAccessToken(it.refreshToken)
                                agreeConfirmViewModel.setRefreshTokenPublishTime(
                                    TimeUtils.getCurrentTime(
                                        ConstVariable.Time.TIME_ZONE_SEOUL
                                    )
                                )
                                delay(100)
                                requireActivity().let {
                                    val intent = Intent(it, MainActivity::class.java)
                                    intent.putExtra(EXTRA_REGISTER_USER, true)
                                    startActivity(intent)
                                    it.finish()
                                }
                            }
                        }
                    }
                    else -> {
                        accessTokenResponse.msg?.let { it1 -> showDialog(it1) }
                    }
                }
            }
        }

        val eventString = getString(R.string.a_agree_recieve_event_optional) + " "
        val optionString ="("+getString(R.string.s_select)+")"
        context?.let {
            TextUtils.applyParticalColor(
                eventString,
                optionString,
                it.getColor(R.color.gray_300),
                binding.tvEventTitle,
                false
            )
        }


        val nicknameString = getString(R.string.n_setting_nickname)
        val starMarkStr = getString(R.string.star_mark)
        context?.let {
            TextUtils.applyParticalColor(
                nicknameString,
                starMarkStr,
                it.getColor(R.color.primary_500),
                binding.tvNickNameSettingTitle,
                false
            )
        }

        val countryTitleString = getString(R.string.g_select_country)
        context?.let {
            TextUtils.applyParticalColor(
                countryTitleString,
                starMarkStr,
                it.getColor(R.color.primary_500),
                binding.tvSelectCountry,
                false
            )
        }

        updateNickTextCount()
    }

    @SuppressLint("SetTextI18n")
    private fun updateNickTextCount() {
        val nickLength = binding.cEtLayout.getText().length
        if (nickLength == 0) {
            binding.tvNickLimit.text = "$nickLength/$NICK_LIMIT_COUNT"
        } else {
            context?.let {
                TextUtils.applyParticalColor(
                    binding.tvNickLimit.text.toString().length.toString(),
                    getString(R.string.slash) +
                            ConstVariable.ClubDef.CLUB_INTRODUCE_LENGTH_LIMIT,
                    it.getColor(R.color.primary_default), binding.tvNickLimit, true
                )
            }
        }
    }

    override fun initUiActionEvent() {
        binding.ilTopbar.ivTopbarBack.setOnClickListener {
            actionBack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    actionBack()
                }
            })

        binding.tvSelectedCountry.setOnClickListener {
            //viewModel.goCountrySelect()
            val languageBottomSheet = LanguageBottomSheet()
            languageBottomSheet.title = getString(R.string.g_select_country)
            if (agreeConfirmViewModel.selectedCountry.value != null) {
                val country: Country = agreeConfirmViewModel.selectedCountry.value as Country
                languageBottomSheet.selectLanguageCode = country.countryCode
            }
            languageBottomSheet.itemClickListener =
                (object : LanguageBottomSheet.ItemClickListener {
                    override fun onItemClick(item: Country) {
                        agreeConfirmViewModel.setSelectCountry(item)
                        languageBottomSheet.dismiss()
                    }
                })
            languageBottomSheet.show(requireActivity().supportFragmentManager, "")
        }

        binding.tvUseServiceMore.setOnClickListener {
            moveToPolicy(ConstVariable.Regist.PolicyType.TYPE_USE_SERVICE)
        }
        binding.tvPrivateMore.setOnClickListener {
            moveToPolicy(ConstVariable.Regist.PolicyType.TYPE_PRIVATE_TREATMENT)
        }
        binding.tvYouthMore.setOnClickListener {
            moveToPolicy(ConstVariable.Regist.PolicyType.TYPE_YOUTH_CARE)
        }
        /*binding.tvEventMore.setOnClickListener {
            moveToPolicy(Consts.Companion.Regist.PolicyType.TYPE_EVENT_RECEIVE)
        }
*/
        binding.llTopAgree.setOnClickListener {
            binding.cbAgreeAll.isChecked = !binding.cbAgreeAll.isChecked
            val b = binding.cbAgreeAll.isChecked
            binding.cbAge.isChecked = b
            binding.cbUseService.isChecked = b
            binding.cbPrivate.isChecked = b
            binding.cbYouth.isChecked = b
            binding.cbEvent.isChecked = b
            updateCompleteButtonEnable()
        }

        binding.rlAge.setOnClickListener {
            binding.cbAge.isChecked = !binding.cbAge.isChecked
            updateCompleteButtonEnable()
        }

        binding.rlUseService.setOnClickListener {
            binding.cbUseService.isChecked = !binding.cbUseService.isChecked
            updateCompleteButtonEnable()
        }

        binding.rlPrivate.setOnClickListener {
            binding.cbPrivate.isChecked = !binding.cbPrivate.isChecked
            updateCompleteButtonEnable()
        }

        binding.rlYouth.setOnClickListener {
            binding.cbYouth.isChecked = !binding.cbYouth.isChecked
            updateCompleteButtonEnable()
        }

        binding.rlEvent.setOnClickListener {
            binding.cbEvent.isChecked = !binding.cbEvent.isChecked
            updateCompleteButtonEnable()
        }
        //binding.cbAge.setOnCheckedChangeListener(PolicyAgreeCheckedListener())
        //binding.cbUseService.setOnCheckedChangeListener(PolicyAgreeCheckedListener())
        //binding.cbPrivate.setOnCheckedChangeListener(PolicyAgreeCheckedListener())
        //binding.cbYouth.setOnCheckedChangeListener(PolicyAgreeCheckedListener())
        binding.cbEvent.setOnCheckedChangeListener(PolicyAgreeCheckedListener())

        //중복확인
        binding.cEtLayout.setButtonClickListener {
            val inputText = binding.cEtLayout.getText()
            if (inputText.isEmpty()) {
                return@setButtonClickListener
            }
            //사용불가 문자포함
            //showDialog(getString(R.string.se_s_including_cannot_use_char))

            if (inputText[0] == ' ') {
                setWarningMessageShow(true, getString(R.string.se_c_cannot_use_blank_first_character), false)
                return@setButtonClickListener
            }

            //길이 제한
            val inputTextLength = inputText.length
            if (inputTextLength < NICKNAME_MIN_LENGTH || inputTextLength > NICKNAME_MAX_LENGTH) {
                setWarningMessageShow(
                    true, String.format(
                        getString(R.string.se_j_write_min_max_limit_length),
                        NICKNAME_MIN_LENGTH.toString(), NICKNAME_MAX_LENGTH.toString()
                    ), false
                )
                return@setButtonClickListener
            }

            lifecycleScope.launch {
                agreeConfirmViewModel.checkUserNickname(
                    binding.cEtLayout.getText()
                )
            }
        }

        binding.btnComplete.setOnClickListener {
            val adAgree = if (binding.cbEvent.isChecked) VALUE_YES else VALUE_NO
            val ageAgree = if (binding.cbAge.isChecked) VALUE_YES else VALUE_NO
            val serviceAgree =
                if (binding.cbUseService.isChecked) VALUE_YES else VALUE_NO
            val teenagerAgree = if (binding.cbYouth.isChecked) VALUE_YES else VALUE_NO
            val userInfoAgree = if (binding.cbPrivate.isChecked) VALUE_YES else VALUE_NO
            val countryIsoTwo = (agreeConfirmViewModel.selectedCountry.value as Country).iso2
            val referralCode = binding.etRefferal.text.toString()
            nickName = binding.cEtLayout.getText()
            if (isFromLoginActivity) {// sns회원가입
                try {
                    val loginId = snsLoginId!!
                    val loginType = snsLoginType!!
                    lifecycleScope.launch {
                        Timber.d("joinBySns id = $loginId , loginType = $loginType")
                        val value = hashMapOf(
                            KEY_JOIN_AD_AGREE to adAgree,
                            KEY_JOIN_AGE_AGREE to ageAgree,
                            KEY_JOIN_COUNTRY to countryIsoTwo,
                            KEY_LOGIN_ID to loginId,
                            KEY_LOGIN_TYPE to loginType,
                            KEY_JOIN_SERVICE_AGREE to serviceAgree,
                            KEY_JOIN_TEENAGER_AGREE to teenagerAgree,
                            KEY_JOIN_USERINFO_AGREE to userInfoAgree,
                            KEY_USER_NICK to nickName
                        )
                        if (referralCode.isNotEmpty()) {
                            value[KEY_JOIN_REFERRAL_CODE] = referralCode
                        }
                        agreeConfirmViewModel.joinBySns(
                            value
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                //이메일 회원가입
                try {
                    lifecycleScope.launch {
                        val data: HashMap<String, String> = HashMap()
                        data[KEY_JOIN_AD_AGREE] = adAgree
                        data[KEY_JOIN_AGE_AGREE] = ageAgree
                        data[KEY_JOIN_COUNTRY] = countryIsoTwo
                        data[ConstVariable.LoginType.EMAIL] = inputEmail
                        data[KEY_LOGIN_PASSWORD] = password
                        data[KEY_JOIN_SERVICE_AGREE] = serviceAgree
                        data[KEY_JOIN_TEENAGER_AGREE] = teenagerAgree
                        data[KEY_JOIN_USERINFO_AGREE] = userInfoAgree
                        data[KEY_USER_NICK] = nickName
                        if (referralCode.isNotEmpty()) {
                            data[KEY_JOIN_REFERRAL_CODE] = referralCode
                        }
                        //Timber.d("joinByEmail id : "+inputEmail+" , value =  $data")//+" , pw = "+password)
                        agreeConfirmViewModel.joinByEmail(
                            data
                        )
                    }
                } catch (e: Exception) {
                    Timber.e("flow joinByEmail err:  ${e.message}")
                }
            }
        }

        binding.cEtLayout.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(c: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setWarningMessageShow(false, "", false)
                updateNickTextCount()

                //닉네임 중복체크후 수정하면 다시 체크...
                agreeConfirmViewModel.setNickNameSetted(false)
                updateCompleteButtonEnable()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun actionBack() {
        if (isFromLoginActivity) {
            activity?.finish()
        } else {
            navController.apply {
                navigate(R.id.action_agreeConfirmFragment_to_inputEmailFragment)
                backQueue.clear()
            }
        }
    }

    private fun updateNickCheckResult(isCheck: Boolean) {//isCheck == false :사용가능
        if (!isCheck) {
            binding.cEtLayout.setInputState(CommonEditTextLayout.EditorState.SUCCESS)
            agreeConfirmViewModel.setNickNameSetted(true)
            //사용가능
            setWarningMessageShow(
                true, getString(
                    R.string.se_s_can_use_nickname)
                , true
            )
        } else {
            agreeConfirmViewModel.setNickNameSetted(false)
            setWarningMessageShow(
                true, getString(
                    R.string.se_a_already_use_nickname)
                , false
            )
        }
        val nickGuideLocation = IntArray(2)
        binding.tvWarning.getLocationOnScreen(nickGuideLocation)
        val rect = Rect()
        binding.tvWarning.getWindowVisibleDisplayFrame(rect) //키보드를 제외한 화면사이즈
        val screenVisibilityHeight = rect.bottom - rect.top
        val adjustNickGuideHeight = binding.tvWarning.height / 2
        Timber.d(
            "nickGuideLocationY = ${nickGuideLocation[1]} ,screenVisibilityHeight = $screenVisibilityHeight , " +
                    "nickGuideHeight = ${binding.tvWarning.height}"
        )
        if (screenVisibilityHeight + adjustNickGuideHeight < nickGuideLocation[1]) {//중복확인 결과 메시지가 보이도록 올림
            binding.sv.smoothScrollTo(
                0,
                binding.sv.scrollY + (nickGuideLocation[1] - screenVisibilityHeight + adjustNickGuideHeight)
            )
        }
        updateCompleteButtonEnable()
    }

    private fun moveToPolicy(policyType: ConstVariable.Regist.PolicyType) {
        val bundle = Bundle()
        bundle.putSerializable(ConstVariable.Regist.KEY_POLICY_ENTRY_ROUTE_TYPE, policyType)
        navController.navigate(R.id.action_agreeConfirmFragment_to_policyFragment, bundle)
    }

    inner class PolicyAgreeCheckedListener : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(button: CompoundButton?, checked: Boolean) {
            updateCompleteButtonEnable()
            if (button == binding.cbEvent) {
                val todayStr = TimeUtils.yearMonthDayDotString(Calendar.getInstance().timeInMillis)
                val message: String = if (checked) {
                    getString(R.string.se_p_agree_market_info)
                } else {
                    getString(R.string.se_p_disagree_marketing_info)
                }
                showDialog(getString(R.string.g_advertisement_alarm), message, todayStr)
            }
        }
    }

    private fun updateCompleteButtonEnable() {
        var completeButtonEnable = false
        if (binding.cbAge.isChecked
            && binding.cbUseService.isChecked
            && binding.cbPrivate.isChecked
            && binding.cbYouth.isChecked
            && agreeConfirmViewModel.selectedCountry.value != null
            && (agreeConfirmViewModel.nickNameSetted.value == true)
        ) {
            completeButtonEnable = true
        }
        binding.btnComplete.isEnabled = completeButtonEnable
    }

    private fun setWarningMessageShow(isShow: Boolean, message: String, stateEnable: Boolean) {
        if (isShow) {
            binding.tvWarning.visibility = View.VISIBLE
            binding.tvWarning.text = message
            binding.tvWarning.isEnabled = stateEnable
            //binding.cEtLayout.setOutLineState(CommonEditTextLayout.EditorState.WARNING)
        } else {
            binding.tvWarning.visibility = View.GONE
            //binding.cEtLayout.setOutLineState(CommonEditTextLayout.EditorState.FOCUSED)
        }
    }
}