package com.rndeep.fns_fantoo.ui.menu.fantooclub.detail

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.rndeep.fns_fantoo.databinding.FragmentHanryuNewsDetailBinding


/**
 * Hanryu New Detail page
 */
class HanryuNewsDetailFragment : Fragment() {

    private lateinit var binding: FragmentHanryuNewsDetailBinding
    private val args: HanryuNewsDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHanryuNewsDetailBinding.inflate(inflater, container, false).apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
        }
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.webView.settings.apply {
            useWideViewPort = true
            loadWithOverviewMode = true
            javaScriptEnabled = true
        }
        val link = args.newsLink
        binding.webView.loadUrl(link)
    }

}