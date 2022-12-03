package com.rndeep.fns_fantoo.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import timber.log.Timber
import kotlin.collections.ArrayList

class FacebookAuth(private var fragment: Fragment, private var listener: SimpleLoginListener) {
    private lateinit var callbackManager: CallbackManager

    private val read = arrayOf("public_profile", "email")
    private val readPermissions: List<String> = ArrayList<String>(listOf(*read))

    init {
        initFacebookSDK()
    }

    private fun initFacebookSDK() {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    Timber.d("FacebookCallback onSuccess")
                    val request: GraphRequest = GraphRequest.newMeRequest(
                        result.accessToken
                    ) { obj, response ->
                        val email: String?
                        if (response != null) {
                            if (response.jsonObject!!.has("email")) {
                                email = response.jsonObject!!.getString("email")
                                listener.onSuccess(
                                    email,
                                    SocialInfo.TYPE_FACEBOOK,
                                    "false",
                                    "",
                                    response.jsonObject!!.getString("id")
                                )
                                //FirebaseAuth.getInstance().signOut()
                                LoginManager.getInstance().logOut()
                            }
                        } else {
                            listener.onSuccess(null, null, null, null, null)
                        }
                    }

                    val parameters: Bundle = Bundle()
                    parameters.putString(
                        "fields",
                        "id,name,email"
                    ) // facebook으로부터 id, name, email을 얻어온다.
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {
                    Timber.d("FacebookCallback onCancel")
                    listener.onSuccess(null, null, null, null, null)
                }

                override fun onError(error: FacebookException) {
                    Timber.d("FacebookCallback onError")
                    listener.onSuccess(null, null, null, null, null)
                }

            })
    }

    fun login() {
        LoginManager.getInstance().logOut()
        LoginManager.getInstance()
            .logInWithReadPermissions(fragment as LoginFragment, callbackManager, readPermissions)
    }

    fun logout() {
        LoginManager.getInstance().logOut()
    }

    fun getCallbackManager(): CallbackManager {
        return callbackManager
    }
}