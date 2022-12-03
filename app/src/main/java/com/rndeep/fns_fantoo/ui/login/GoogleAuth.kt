package com.rndeep.fns_fantoo.ui.login

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.rndeep.fns_fantoo.R
import timber.log.Timber

class GoogleAuth
    (
    var context: Context,
    var listener: SimpleLoginListener,
    val resultLauncher: ActivityResultLauncher<Intent>
) {
    companion object {
        val TAG: String = GoogleAuth::class.java.simpleName
    }

    private var googleSignInClient: GoogleSignInClient

    init {
        val googleSingnInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestIdToken(context.getString(R.string.googleClientKey))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, googleSingnInOptions)
    }

    fun login() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    fun logout() {
        googleSignInClient.signOut()
    }

    fun getGoogleSignInClientInstance(): GoogleSignInClient {
        return googleSignInClient
    }

}