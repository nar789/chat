package com.rndeep.fns_fantoo.ui.login

import android.app.Activity
import android.content.Context
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class TwitterAuth(private var context: Context, private var listener: SimpleLoginListener) {

    private var firebaseAuth: FirebaseAuth = Firebase.auth

    fun login(){
        val provider:OAuthProvider.Builder = OAuthProvider.newBuilder("twitter.com")
        //optional Target specific email with login hint.
        //provider.addCustomParameter("lang", "fr")

        val pendingResultTask : Task<AuthResult>? = firebaseAuth.pendingAuthResult
        if(pendingResultTask != null){
            pendingResultTask.addOnSuccessListener(
                OnSuccessListener {
                // User is signed in.
                    // authResult.getAdditionalUserInfo().getProfile().
                }
            )
                .addOnFailureListener {
                    //fail
                }
        }else{
            firebaseAuth.startActivityForSignInWithProvider(context as Activity, provider.build())
                .addOnSuccessListener { authResult ->
                    // User is signed in.
                    // authResult.getAdditionalUserInfo().getProfile().
                    Timber.d("twitter OnSuccessListener $authResult")
                    listener.onSuccess(
                        authResult.user?.email,
                        SocialInfo.TYPE_TWITTER,
                        "false",
                        "",
                        authResult.user?.uid
                    )
                }
                .addOnFailureListener {
                    Timber.d("twitter OnFailure : $it")
                    listener.onSuccess(null, null, null, null, null)
                }
        }
    }

    fun logout(){
        FirebaseAuth.getInstance().signOut()
    }

}