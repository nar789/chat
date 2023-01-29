package com.rndeep.fns_fantoo.ui.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.telephony.TelephonyManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.rndeep.fns_fantoo.BuildConfig
import com.rndeep.fns_fantoo.MainActivity
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentLoginSplashBinding
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.ui.login.viewmodel.LoginSplashViewModel
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.LanguageUtils
import com.rndeep.fns_fantoo.utils.TimeUtils
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class LoginSplashFragment :
    BaseFragment<FragmentLoginSplashBinding>(FragmentLoginSplashBinding::inflate) {

    private val loginSplashViewModel: LoginSplashViewModel by viewModels()

    private val SPLASH_SHOW_TIME: Long = 2000
    private val MAX_RESPONSE_DELAY_TIME: Long = 10000
    private var isMoveToMainScreen: Boolean = false
    private var timeNetworkResponseDelay: Long = 0
    private val moveToNextScreenJob = Job()
    private lateinit var dialog: CommonDialog

    override fun initUi() {
        Timber.d("splash init")

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            Timber.d("firebase getToken :$token")
            loginSplashViewModel.setFCMToken(token)
        })

        loginSplashViewModel.accessTokenLiveData.observe(this) { accessTokenResponse ->
            accessTokenResponse?.let {
                when (accessTokenResponse.code) {
                    ConstVariable.RESULT_SUCCESS_CODE -> {
                        val accessTokenDto = accessTokenResponse.accessTokenDto
                        accessTokenDto?.let {
                            loginSplashViewModel.setAccessToken(accessTokenDto.accessToken)
                            isMoveToMainScreen = true
                        }
                    }
                    else -> {
                        accessTokenResponse.code?.let {
                            context?.showErrorSnackBar(binding.root, it)
                        }
                    }
                }
            }
        }

        loginSplashViewModel.configLiveData.observe(this) { configResponse ->
            configResponse?.let {
                Timber.d("configLiveData $it")
                if (it.apiUrl.isNotEmpty()) {
                    runBlocking {
                        //동작하지 않은 api주소가 내려오는 상태이므로 주석
                        //loginSplashViewModel.setApiUrl(it.apiUrl)
                    }
                }
                if (it.imageUrl.isNotEmpty()) {
                    runBlocking {
                        //loginSplashViewModel.setCloudFlareUrl(it.imageUrl)
                    }
                }
                val transUrl = it.transUrl
                val webUrl = it.webUrl
                val adUrl = it.adUrl
                val payUrl = it.payUrl
                val hasNoticeMessage = false//(it.enable == 1)
                val bUpdateEnable = false//(it.updateEnable == 1)
                val bForceUpdate = (it.forceUpdate == 1)
                if (hasNoticeMessage) {
                    lifecycleScope.launch {
                        showNoticePopup(if (loginSplashViewModel.getLanguageCode() == LanguageUtils.KOREAN) it.messageKr else it.messageEng)
                    }
                } else if (bUpdateEnable && isLowerAppVersion(
                        localAppVersionName = getVersionInfo(),
                        configAppVersionName = it.currentVersion
                    )
                ) {
                    showAppVersionDialog(bForceUpdate)
                } else {
                    checkingNextScreen()
                }
            }
        }

        loginSplashViewModel.configNetworkErrorLiveData.observe(this) { isConfigNetworkError ->
            if (isConfigNetworkError) {
                moveToNextScreenJob.cancel()
                showNetworkErrorDialog()
            }
        }

        lifecycleScope.launch {
            setSystemCountryCode()
            loginSplashViewModel.getConfig(ConstVariable.Config.SERVICE_TYPE_DEV)
            loginSplashViewModel.getSupportLanguageAll()
            loginSplashViewModel.getCountryAll()
        }
    }

    override fun initUiActionEvent() {
        Timber.d(
            "contry : ${requireContext().resources.configuration.locales.get(0).country}, lang : ${
                requireContext().resources.configuration.locales.get(
                    0
                ).language
            } "
        )
    }

    private fun checkingNextScreen() {
        lifecycleScope.launch {
            val isPermissionChecked = loginSplashViewModel.getIsPermissionChecked()
            Timber.d("isPermissionChecked = $isPermissionChecked")
            val moveToPermissionCheckScreen = isPermissionChecked != true
            if (moveToPermissionCheckScreen) {
                delay(SPLASH_SHOW_TIME)
                moveToNextScreen(R.id.action_loginSplashFragment_to_loginPermissionFragment, false)
            } else {
                var moveToLoginScreen = false
                val refreshPublishTime = loginSplashViewModel.getRefreshTokenPublishTime()
                Timber.d("refreshPublishTime time $refreshPublishTime")
                if (refreshPublishTime != null && refreshPublishTime > 0) {
                    try {
                        val refreshToken = loginSplashViewModel.getRefreshToken()
                        Timber.d("refreshToken : $refreshToken")
                        if (!refreshToken.isNullOrEmpty()) {
                            val refreshTokenPublishDate = Date(refreshPublishTime)
                            val cal = Calendar.getInstance()
                            cal.time = refreshTokenPublishDate
                            cal.add(
                                Calendar.DATE,
                                ConstVariable.Auth.REFRESH_TOKEN_EXPIRE_DAY
                            )//발급날짜에 60일 추가하여 만료날짜를 확인
                            val sdf = SimpleDateFormat()
                            sdf.timeZone =
                                android.icu.util.TimeZone.getTimeZone(ConstVariable.Time.TIME_ZONE_SEOUL)

                            val expireDateString = sdf.format(cal.time)//만료일자
                            val expireTime = sdf.parse(expireDateString).time//만료시간 long
                            //Timber.d("time expireDateString $expireDateString , expireDateLong = $expireTime")

                            val currentTime =
                                TimeUtils.getCurrentTime(ConstVariable.Time.TIME_ZONE_SEOUL)
                            //val remainDay = (expireTime - currentTime)/(1000*24*60*60)//리프레시 토큰 남은 일자
                            val remainMin = (expireTime - currentTime) / 60000  //리프레시 토큰 남은 시간(분)
                            Timber.d("time remainMin = $remainMin")
                            if (remainMin > 30) { //accesstoken 갱신, 남은시간 30분이하는 유효하지 않은것으로 체크
                                loginSplashViewModel.getAccessToken(
                                    hashMapOf(
                                        ConstVariable.Auth.KEY_CLIENT_ID to ConstVariable.Auth.VALUE_CLIENT_ID,
                                        ConstVariable.Auth.KEY_CLIENT_SECRET to ConstVariable.Auth.VALUE_CLIENT_SECRET,
                                        ConstVariable.Auth.KEY_GRANT_TYPE to ConstVariable.Auth.VALUE_GRANT_TYPE_REFRESH_TOKEN,
                                        ConstVariable.Auth.KEY_REFRESH_TOKEN to refreshToken
                                    )
                                )
                                CoroutineScope(Dispatchers.Main + moveToNextScreenJob).launch {
                                    while (true) {
                                        Timber.d("nextScreenJob running, time : $timeNetworkResponseDelay")
                                        if (loginSplashViewModel.isReceivedAccessTokenResponse.value == true) {
                                            if (timeNetworkResponseDelay >= SPLASH_SHOW_TIME) {//2초이후 response
                                                moveToNextScreen(
                                                    R.id.action_loginSplashFragment_to_loginFragment,
                                                    /*R.id.action_loginSplashFragment_to_loginWebViewFragment,*/
                                                    isMoveToMainScreen
                                                )
                                                break
                                            }
                                        } else {//액세스 토큰 get 응답이 없을때...
                                            if (timeNetworkResponseDelay >= MAX_RESPONSE_DELAY_TIME) {//10초(임시)이상
                                                showNetworkErrorDialog()
                                                break
                                            }
                                        }
                                        val nextCheckDelay: Long = 500
                                        delay(nextCheckDelay)
                                        timeNetworkResponseDelay += nextCheckDelay
                                    }
                                }
                            } else {
                                moveToLoginScreen = true
                            }
                        } else {
                            moveToLoginScreen = true
                        }
                    } catch (e: Exception) {
                        Timber.e("token refresh error, ${e.message}")
                        moveToLoginScreen = true
                    }
                } else {
                    moveToLoginScreen = true
                }
                if (moveToLoginScreen) {
                    delay(SPLASH_SHOW_TIME)
                    moveToNextScreen(R.id.action_loginSplashFragment_to_loginFragment /*R.id.action_loginSplashFragment_to_loginWebViewFragment*/, false)
                }
            }
        }
    }

    private fun moveToNextScreen(destinationNavId: Int, isMoveToMain: Boolean) {
        try {
            Timber.d("moveToNextScreen $destinationNavId, isMoveToMain =  $isMoveToMain")
            runBlocking { loginSplashViewModel.setIsLogined(isMoveToMain) }
            if (isMoveToMain) {
                requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                navController.navigate(destinationNavId)
            }
        } catch (e: Exception) {
            Timber.e("${e.printStackTrace()}")
        }
    }

    //튜토리얼

    //서버URL정보 갱신
    fun getServerURLInfo() {

    }

    //강제업데이트확인시 사용
    private fun getVersionInfo(): String {
        var version = ""
        //var versionCode: Long = -1
        try {
            //val packageInfo: PackageInfo =
            //    requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
            version = BuildConfig.VERSION_NAME//packageInfo.versionName
            /*versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                packageInfo.versionCode.toLong()
            }*/
            Timber.d("getVersionInfo version = $version")
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e("${e.printStackTrace()}")
        }
        return version//versionCode
    }

    private fun isLowerAppVersion(
        localAppVersionName: String,
        configAppVersionName: String
    ): Boolean {
        var ret = false
        val localAppVersions = localAppVersionName.split(".")
        val configAppVersions = configAppVersionName.split(".")
        if (configAppVersions[0].toInt() > localAppVersions[0].toInt()) {
            ret = true
        } else {
            if (configAppVersions[0].toInt() == localAppVersions[0].toInt()) {
                if (configAppVersions[1].toInt() > localAppVersions[1].toInt()) {
                    ret = true
                } else {
                    if (configAppVersions[1].toInt() == localAppVersions[1].toInt()) {
                        if (configAppVersions[2].toInt() > localAppVersions[2].toInt()) {
                            ret = true
                        }
                    }
                }
            }
        }
        Timber.d("isLowerAppVersion localAppVer $localAppVersionName , configAppVer $configAppVersionName , isLowerVer : $ret")
        return ret
    }

    private fun showAppVersionDialog(isForce: Boolean) {
        val dialogBuilder = CommonDialog.Builder()
        dialogBuilder.message(getString(R.string.a_update))
        dialogBuilder.setPositiveButtonText(getString(R.string.h_confirm))
        dialogBuilder.setPositiveButtonClickListener(object : CommonDialog.ClickListener {
            override fun onClick() {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(ConstVariable.Fantoo.STORE_URL)
                startActivity(intent)
                requireActivity().finish()
            }
        })
        dialogBuilder.setOnCancelListener(object : CommonDialog.CancelListener {
            override fun onCancel() {
                if (isForce) {
                    requireActivity().finish()
                } else {
                    checkingNextScreen()
                }
            }
        })
        dialog = dialogBuilder.build()
        if (isForce) {
            dialog.isCancelable = false
        }
        dialog.show(requireActivity().supportFragmentManager, "")
    }

    private fun showNoticePopup(message: String) {
        try {
            val dialogBuilder = CommonDialog.Builder()
            dialogBuilder.message(message)
            dialogBuilder.setPositiveButtonText(getString(R.string.h_confirm))
            dialogBuilder.setPositiveButtonClickListener(object : CommonDialog.ClickListener {
                override fun onClick() {
                    checkingNextScreen()
                }
            })
            dialog = dialogBuilder.build()
            dialog.show(requireActivity().supportFragmentManager, "")
        } catch (e: Exception) {
            Timber.e("${e.printStackTrace()}")
        }
    }

    private fun showNetworkErrorDialog() {
        try {
            val dialogBuilder = CommonDialog.Builder()
            dialogBuilder.message(getString(R.string.alert_network_error))
            dialogBuilder.setPositiveButtonText(getString(R.string.h_confirm))
            dialogBuilder.setPositiveButtonClickListener(object : CommonDialog.ClickListener {
                override fun onClick() {
                    requireActivity().finish()
                }
            })
            dialogBuilder.setOnCancelListener(object : CommonDialog.CancelListener {
                override fun onCancel() {
                    requireActivity().finish()
                }
            })
            dialogBuilder.build().show(requireActivity().supportFragmentManager, "")
        } catch (e: Exception) {
            Timber.e("${e.printStackTrace()}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (moveToNextScreenJob.isActive) {
            moveToNextScreenJob.cancel()
        }
    }

    private suspend fun setSystemCountryCode() {
        try {
            if (requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                val tm: TelephonyManager =
                    requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                tm.let {
                    val countryCode = tm.networkCountryIso
                    Timber.d("countryCode : $countryCode")
                    countryCode?.let {
                        loginSplashViewModel.setSystemCountryCode(countryCode)
                    }
                }
            }
        } catch (e: Exception) {
            Timber.w("${e.printStackTrace()}")
        }
    }
}