package com.rndeep.fns_fantoo.ui.login

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.linecorp.linesdk.LineApiResponseCode
import com.linecorp.linesdk.auth.LineLoginApi
import com.linecorp.linesdk.auth.LineLoginResult
import com.rndeep.fns_fantoo.MainActivity
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.Language
import com.rndeep.fns_fantoo.databinding.FragmentLoginBinding
import com.rndeep.fns_fantoo.ui.common.AppLanguageBottomSheet
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.ui.login.viewmodel.LoginViewModel
import com.rndeep.fns_fantoo.ui.regist.RegistActivity
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.TimeUtils
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var googleAuth: GoogleAuth

    private lateinit var facebookAuth: FacebookAuth

    private lateinit var kakaoAuth: KakaoAuth

    private lateinit var twitterAuth: TwitterAuth

    private lateinit var lineAuth: LineAuth

    private lateinit var appleAuth: AppleAuth

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun initUi() {
        loginViewModel.checkingJoinedUserLiveData.observe(viewLifecycleOwner) { checkingJoinUserResponse ->
            Timber.d("checkingJoinedUserLiveData = $checkingJoinUserResponse")
            checkingJoinUserResponse?.let {
                when (checkingJoinUserResponse.code) {
                    ConstVariable.RESULT_SUCCESS_CODE -> {
                        val checkingJoinUserDto = checkingJoinUserResponse.checkingJoinUserDto
                        checkingJoinUserDto?.let {
                            loginViewModel.isAleardyRegistUser = (checkingJoinUserDto.isUser)
                            if (checkingJoinUserDto.isUser) {//가입된 기존회원 - isUser : true
                                val values = hashMapOf(
                                    ConstVariable.Auth.KEY_CLIENT_ID to ConstVariable.Auth.VALUE_CLIENT_ID,
                                    ConstVariable.KEY_LOGIN_ID to loginViewModel.socialId!!,
                                    ConstVariable.KEY_LOGIN_TYPE to loginViewModel.socialType!!,
                                    ConstVariable.Auth.KEY_RESPONSE_TYPE to ConstVariable.Auth.VALUE_RESPONSE_TYPE
                                )
                                loginViewModel.getAuthCode(values)
                            } else {
                                setVisibleLoading(false)
                                val intent = Intent(requireActivity(), RegistActivity::class.java)
                                intent.putExtra(
                                    ConstVariable.INTENT.EXTRA_FROM_ACTIVITY,
                                    LoginMainActivity::class.java.simpleName
                                )
                                intent.putExtra(
                                    ConstVariable.INTENT.EXTRA_LOGIN_ID,
                                    loginViewModel.socialId
                                )
                                intent.putExtra(
                                    ConstVariable.INTENT.EXTRA_LOGIN_TYPE,
                                    loginViewModel.socialType
                                )
                                activityResultLauncher.launch(intent)
                            }
                        }
                    }
                    ConstVariable.ERROR_FE1008 -> {
                        showDialog("", getString(R.string.se_t_check_rejoin_withdraw_account))
                    }
                    else -> {
                        context?.showErrorSnackBar(binding.root, checkingJoinUserResponse.code)
                    }
                }
            }
        }
        @Suppress("NAME_SHADOWING")
        loginViewModel.authCodeLiveData.observe(this) { authCodeResponse ->
            Timber.d("snsAuthCodeLiveData = $authCodeResponse")
            authCodeResponse?.let {
                when (authCodeResponse.code) {
                    ConstVariable.RESULT_SUCCESS_CODE -> {
                        authCodeResponse.authCodeDto?.let {
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
                    else -> {
                        val errorData = authCodeResponse.errorData
                        errorData?.let {
                            authCodeResponse.msg?.let { message ->
                                showDialog(
                                    getString(R.string.r_login),
                                    message
                                )
                            }
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
                        accessTokenDto?.let {
                            Timber.d("login token = ${accessTokenDto.accessToken}")
                            loginViewModel.setIsLogined(true)
                            loginViewModel.setLastLoginType(loginViewModel.socialType!!)
                            loginViewModel.setUID(accessTokenDto.integUid)
                            loginViewModel.setAccessToken(accessTokenDto.accessToken)
                            loginViewModel.setRefreshAccessToken(accessTokenDto.refreshToken)
                            loginViewModel.setRefreshTokenPublishTime(
                                TimeUtils.getCurrentTime(
                                    ConstVariable.Time.TIME_ZONE_SEOUL
                                )
                            )
                            Timber.d("uid = ${accessTokenDto.integUid}")
                            delay(100)
                            val intent = Intent(requireActivity(), MainActivity::class.java)
                            if (!loginViewModel.isAleardyRegistUser) {
                                intent.putExtra(ConstVariable.INTENT.EXTRA_REGISTER_USER, true)
                            }
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    } else {
                        accessTokenResponse.code?.let {
                            context?.showErrorSnackBar(binding.root, it)
                        }
                    }
                }
            }
        }

        loginViewModel.networkErrorLiveData.observe(this) {
            showDialog(getString(R.string.alert_network_error))
        }

        loginViewModel.loadingStatusLiveData.observe(this) {
            setVisibleLoading(it)
        }

        initSocialsAuth()
        updateRecentLogin()
        initSocialLoginButtons()

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val resultIntent = result.data
                    if (loginViewModel.socialId != null && loginViewModel.socialType != null
                        && resultIntent != null
                    ) {
                        loginViewModel.isAleardyRegistUser = false
                        loginViewModel.myProfileNickName =
                            resultIntent.getStringExtra(ConstVariable.INTENT.EXTRA_MYPROFILE_NICK_NAME)
                                .toString()
                        loginViewModel.myProfileCountryIso2 = resultIntent.getStringExtra(
                            ConstVariable.INTENT.EXTRA_MYPROFILE_COUNTRY_ISO2
                        ).toString()
                        Timber.d("register result nick : ${loginViewModel.myProfileNickName} , country : ${loginViewModel.myProfileCountryIso2}")
                        val values = hashMapOf(
                            ConstVariable.Auth.KEY_CLIENT_ID to ConstVariable.Auth.VALUE_CLIENT_ID,
                            ConstVariable.KEY_LOGIN_ID to loginViewModel.socialId!!,
                            ConstVariable.KEY_LOGIN_TYPE to loginViewModel.socialType!!,
                            ConstVariable.Auth.KEY_RESPONSE_TYPE to ConstVariable.Auth.VALUE_RESPONSE_TYPE
                        )
                        loginViewModel.getAuthCode(values)
                    } else {
                        Timber.e("social info not exist.")
                    }
                }
            }


    }

    override fun initUiActionEvent() {
        binding.llLoginEmail.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_loginEmailFragment)
        }
        binding.tvLoginGuest.setOnClickListener {
            lifecycleScope.launch {
                loginViewModel.setIsLogined(false)
                delay(100)
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }
        }
        binding.ivTranslate.setOnClickListener {
            val languageBottomSheet = AppLanguageBottomSheet()
            languageBottomSheet.title = getString(R.string.b_select_trans_language)
            lifecycleScope.launch {
                val languageCode = loginViewModel.getLanguageCode()
                Timber.d("getLanguageCode = $languageCode")
                if (!languageCode.isNullOrEmpty()) {
                    languageBottomSheet.selectLanguageCode = languageCode
                }
            }
            languageBottomSheet.itemClickListener =
                (object : AppLanguageBottomSheet.ItemClickListener {
                    override fun onItemClick(item: Language) {
                        lifecycleScope.launch {
                            loginViewModel.setLanguageCode(item.langCode)
                            Timber.d("configSet ${item.langCode}")
                            languageBottomSheet.dismiss()
                            //언어설정 저장처리가 취소되지 않도록 delay추가
                            lifecycleScope.launch {
                                delay(200)
                                findNavController().navigate(
                                    R.id.loginFragment,
                                    arguments,
                                    NavOptions.Builder().setPopUpTo(R.id.loginFragment, true)
                                        .build()
                                )
                            }
                            //액티비티 재시작방식
                            /*Timer("SafetyLangSet", false).schedule(200) {
                                val requireIntent = requireActivity().intent
                                requireIntent.putExtra(
                                    EXTRA_NAV_START_DESTINATION_ID,
                                    R.id.loginFragment
                                )
                                requireActivity().finish()
                                requireActivity().overridePendingTransition(0, 0)
                                requireActivity().startActivity(requireIntent)
                                requireActivity().overridePendingTransition(0, 0)
                            }*/
                        }
                    }
                })
            languageBottomSheet.show(requireActivity().supportFragmentManager, "")
        }
    }

    private fun initSocialsAuth() {
        val simpleLoginListener: SimpleLoginListener = object : SimpleLoginListener {
            override fun onSuccess(
                email: String?,
                socialSite: String?,
                bool: String?,
                sub: String?,
                snsId: String?
            ) {
                simpleLoginImp(email, socialSite, bool, sub, snsId)
            }
        }
        googleAuth =
            GoogleAuth(requireActivity(), simpleLoginListener, googleAuthActivityResultLauncher)
        facebookAuth = FacebookAuth(this, simpleLoginListener)
        kakaoAuth = KakaoAuth(requireActivity(), simpleLoginListener)
        twitterAuth = TwitterAuth(requireActivity(), simpleLoginListener)
        lineAuth = LineAuth(requireActivity(), simpleLoginListener, lineAuthActivityResultLauncher)
        appleAuth = AppleAuth(requireActivity(), simpleLoginListener)
    }

    private fun initSocialLoginButtons() {
        try {
            binding.tvMainSocial.text =
                getString(R.string.google) + getString(R.string.r_continue_at)
            binding.llMainLogin.setOnClickListener {
                loginViewModel.setLoadingStatus(true)
                googleAuth.login()
            }
            binding.btnFacebook.setOnClickListener {
                loginViewModel.setLoadingStatus(true)
                facebookAuth.login()
            }
            binding.btnLine.setOnClickListener {
                loginViewModel.setLoadingStatus(true)
                lineAuth.login()
            }
            binding.btnKakao.setOnClickListener {
                loginViewModel.setLoadingStatus(true)
                kakaoAuth.login()
            }
            binding.btnApple.setOnClickListener {
                loginViewModel.setLoadingStatus(true)
                appleAuth.login()
            }
            binding.btnTwitter.setOnClickListener {
                loginViewModel.setLoadingStatus(true)
                twitterAuth.login()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateRecentLogin() {
        lifecycleScope.launch {
            var recentLoginType = loginViewModel.getLastLoginType()
            Timber.d("updateRecentLogin recentLoginType = $recentLoginType")
            if (!recentLoginType.isNullOrEmpty()) {
                try {
                    recentLoginType = recentLoginType.substring(0, 1)
                        .uppercase(Locale.getDefault()) + recentLoginType.substring(1)
                } catch (e: Exception) {
                }
                binding.tvRecentlyLogin.text = recentLoginType
                binding.llRecentLogin.visibility = View.VISIBLE
            }
        }
    }

    fun simpleLoginImp(
        email: String?,
        socialType: String?,
        bool: String?,
        sub: String?,
        snsId: String?
    ) {
        Timber.d("simpleLoginImp email = $email , socialType = $socialType , bool = $bool, sub = $sub , snsId = $snsId")
        loginViewModel.setLoadingStatus(false)
        if (email == null && socialType == null && bool == null) {
            //로그인 실패?(취소포함)
            return
        }

        loginViewModel.socialType = socialType?.lowercase()
        loginViewModel.socialId = snsId //+ "12345732"

        lifecycleScope.launch {
            loginViewModel.checkingJoinedUser(socialType!!.lowercase(), loginViewModel.socialId!!)
        }
    }

    private fun setVisibleLoading(loading: Boolean) {
        binding.loadingView.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private val googleAuthActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            Timber.d("google login resultCode  ${activityResult.resultCode}")
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val data = activityResult.data
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                    if (account.idToken != null) {
                        val email: String? = account.email
                        simpleLoginImp(email, SocialInfo.TYPE_GOOGLE, "false", "", account.id)
                    } else {
                        simpleLoginImp(null, null, null, null, null)
                    }
                    googleAuth.logout()
                } catch (e: Exception) {
                    Timber.e("${e.printStackTrace()}")
                }
            }
        }

    private val lineAuthActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            Timber.d("line login result $activityResult")
            val result: LineLoginResult = LineLoginApi.getLoginResultFromIntent(activityResult.data)
            when (result.responseCode) {
                LineApiResponseCode.SUCCESS -> {
                    //val accessToken = result.lineCredential?.accessToken?.tokenString
                    val userId = result.lineProfile?.userId
                    val userEmail = result.lineIdToken?.email
                    simpleLoginImp(userEmail, SocialInfo.TYPE_LINE, "false", "", userId)
                }
                LineApiResponseCode.CANCEL -> {
                    simpleLoginImp(null, null, null, null, null)
                }
                else -> {
                    simpleLoginImp(null, null, null, null, null)
                }
            }
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    lineAuth.logout()
                }
            }
        }
}