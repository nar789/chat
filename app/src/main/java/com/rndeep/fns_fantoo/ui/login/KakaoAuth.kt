package com.rndeep.fns_fantoo.ui.login

import android.content.Context
import com.kakao.sdk.user.UserApiClient
import timber.log.Timber

class KakaoAuth(private var context: Context, private var listener: SimpleLoginListener) {

    fun login() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    listener.onSuccess(null, null, null, null, null)
                } else if (token != null) {
                    getUserInfo()
                }

            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
                if (error != null) {
                    listener.onSuccess(null, null, null, null, null)
                } else if (token != null) {
                    getUserInfo()
                }
            }
        }
    }

    private fun getUserInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                listener.onSuccess(null, null, null, null, null)
            } else if (user != null) {
                listener.onSuccess(
                    user.kakaoAccount?.email,
                    SocialInfo.TYPE_KAKAO,
                    "false",
                    "",
                    "" + user.id
                )
            }
        }
    }

    fun logout() {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Timber.e("Logout failed. Token was deleted from SDK, $error")
            } else {
                Timber.i("Logout success.")
            }
        }
    }

    fun unlink() {
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Timber.e("Unlink was failed, $error")
            } else {
                Timber.i("Unlink success")
            }
        }
    }

}