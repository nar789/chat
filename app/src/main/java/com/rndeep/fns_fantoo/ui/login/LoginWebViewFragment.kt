package com.rndeep.fns_fantoo.ui.login

import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.rndeep.fns_fantoo.databinding.FragmentLoginWebviewBinding
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import timber.log.Timber

class LoginWebViewFragment : BaseFragment<FragmentLoginWebviewBinding>(FragmentLoginWebviewBinding::inflate) {
    override fun initUi() {
        binding.wvLogin.settings.javaScriptEnabled = true
        binding.wvLogin.webViewClient = LoginWebViewClient()

        //test
        binding.wvLogin.loadUrl("http://naver.com")
    }

    override fun initUiActionEvent() {

    }

    private class LoginWebViewClient : WebViewClient(){

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            Timber.d("url = ${Uri.parse(request?.url?.toString())?.host}")
            return super.shouldOverrideUrlLoading(view, request)
        }
    }
}