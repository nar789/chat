package com.rndeep.fns_fantoo.ui.login

import android.content.Context
import com.kakao.sdk.user.UserApiClient

class KakaoAuth(private var context: Context, private var listener: SimpleLoginListener) {

    fun login(){
        if(UserApiClient.instance.isKakaoTalkLoginAvailable(context)){
            UserApiClient.instance.loginWithKakaoTalk(context){
                token, error ->
                if(error != null){
                    listener.onSuccess(null, null, null, null, null)
                }else if(token != null){
                    getUserInfo()
                }

            }
        }else{
            UserApiClient.instance.loginWithKakaoAccount(context){
                token, error ->
                if(error != null){
                    listener.onSuccess(null, null, null, null, null)
                }else if(token != null){
                    getUserInfo()
                }
            }
        }
    }

    private fun getUserInfo(){
        UserApiClient.instance.me { user, error ->
            if(error != null){
                listener.onSuccess(null, null, null, null, null)
            }else if(user != null){
                listener.onSuccess(user.kakaoAccount?.email, SocialInfo.TYPE_KAKAO, "false", "", ""+user.id)
            }
        }
    }

    fun logout(){
        UserApiClient.instance.logout { error ->
            if(error != null){
                //Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
            }else{
                //Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
            }
        }
    }

    fun unlink(){
        UserApiClient.instance.unlink { error ->
            if(error != null){
                //Log.e(TAG, "연결 끊기 실패", error)
            }else{
                //Log.i(TAG, "연결 끊기 성공. SDK에서 토큰 삭제 됨")
            }
        }
    }

}