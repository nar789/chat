package com.rndeep.fns_fantoo.ui.login

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.linecorp.linesdk.Scope
import com.linecorp.linesdk.api.LineApiClientBuilder
import com.linecorp.linesdk.auth.LineAuthenticationParams
import com.linecorp.linesdk.auth.LineLoginApi
import com.rndeep.fns_fantoo.R
import java.lang.Exception

class LineAuth(
    private val context: Context,
    private val listener: SimpleLoginListener,
    private val resultLauncher: ActivityResultLauncher<Intent>
) {

    fun login() {
        try {
            val loginIntent: Intent = LineLoginApi.getLoginIntent(
                context,
                context.getString(R.string.lineLoginKey),
                LineAuthenticationParams.Builder().scopes(listOf(Scope.PROFILE)).build()
            )
            resultLauncher.launch(loginIntent)
        } catch (e: Exception) {
            listener.onSuccess(null, null, null, null, null)
        }
    }

    fun logout() {
        context.let {
            val lineApiClient = LineApiClientBuilder(
                it.applicationContext,
                context.getString(R.string.lineLoginKey)
            ).build()
            lineApiClient.logout()
        }
    }
}