package com.rndeep.fns_fantoo.ui.login

import android.app.Activity
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider

class AppleAuth(private var context: Context, private var listener: SimpleLoginListener) {

    fun login(){
        val provider:OAuthProvider.Builder = OAuthProvider.newBuilder("apple.com")

        val scopes:List<String> = arrayListOf("email", "name")
        provider.scopes = scopes
        FirebaseAuth.getInstance().startActivityForSignInWithProvider(context as Activity, provider.build())
            .addOnSuccessListener { authResult ->
                listener.onSuccess(
                    authResult.user?.email,
                    SocialInfo.TYPE_APPLE,
                    "false",
                    "",
                    authResult.user?.uid
                )
                logout()
            }
            .addOnFailureListener {
                listener.onSuccess(null, null, null, null, null)
                logout()
            }
    }

    fun logout(){
        FirebaseAuth.getInstance().signOut()
    }

}