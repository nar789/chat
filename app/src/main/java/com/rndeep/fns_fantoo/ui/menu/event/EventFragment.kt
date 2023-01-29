package com.rndeep.fns_fantoo.ui.menu.event

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import androidx.navigation.findNavController
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentEventBinding
import com.rndeep.fns_fantoo.utils.setStatusBar

/**
 * Event UI
 */
class EventFragment : Fragment() {

    private lateinit var binding: FragmentEventBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventBinding.inflate(inflater, container, false).apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
        }
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setStatusBar(R.color.gray_25, true)

        val link = "https://www.fantoo.co.kr/event"
//        val link = "http://syncwebview.fantoo.co.kr/fanit"
//        val link = "https://www.fantoo.co.kr/service"
        binding.webView.settings.apply {
            useWideViewPort = true
            loadWithOverviewMode = true
            javaScriptEnabled = true
        }
//        binding.webView.addJavascriptInterface(WebAppInterface(),"Android")
        binding.webView.loadUrl(link)
    }

    inner class WebAppInterface {
        @JavascriptInterface
        fun getDeviceToken() : String {
            return "token"
        }
    }
}