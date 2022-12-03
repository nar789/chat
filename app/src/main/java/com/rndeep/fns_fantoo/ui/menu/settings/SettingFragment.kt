package com.rndeep.fns_fantoo.ui.menu.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.Language
import com.rndeep.fns_fantoo.databinding.FragmentSettingBinding
import com.rndeep.fns_fantoo.ui.common.AppLanguageBottomSheet
import com.rndeep.fns_fantoo.ui.common.LocaleSetFragment
import com.rndeep.fns_fantoo.ui.menu.*
import com.rndeep.fns_fantoo.ui.menu.DialogClickType
import com.rndeep.fns_fantoo.utils.TimeUtils
import com.rndeep.fns_fantoo.utils.setStatusBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Setting
 */
@AndroidEntryPoint
class SettingFragment : LocaleSetFragment() {

    private lateinit var binding: FragmentSettingBinding
    private val viewModel: SettingViewModel by viewModels()
    private lateinit var dialogFragment: MenuDialogFragment
    private lateinit var settingAdapter: MenuListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        settingAdapter = MenuListAdapter { item ->
            Timber.d("clicked item : $item")
            when (item) {
                is MenuItem.Item -> navigateByItem(item.name)
                is MenuItem.AccountItem -> navigateByItem(item.name)
                is MenuItem.VersionItem -> navigateByItem(item.name)
                is MenuItem.SwitchItem -> navigateBySwitchItem(item)
                else -> Timber.d("else $item")
            }
        }

        binding = FragmentSettingBinding.inflate(inflater, container, false).apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            settingMenuList.run {
                adapter = settingAdapter
                setHasFixedSize(true)
                itemAnimator = null
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBar(R.color.gray_25, true)

        viewModel.settingsMenu.observe(viewLifecycleOwner) { list ->
            Timber.d("onViewCreated : $list")
            settingAdapter.submitList(list.toList())
        }
    }

    private fun navigateByItem(item: MenuItemType) {
        when (item) {
            MenuItemType.ACCOUNT_INFO -> {
                findNavController().navigate(R.id.action_settingFragment_to_accountInfoFragment)
            }
            MenuItemType.SELECT_TRANSLATE_LANGUAGE -> {
                selectTranslateLanguage()
            }
            MenuItemType.PUSH_NOTIFICATION_SETTING -> {
                findNavController().navigate(R.id.action_settingFragment_to_pushNotificationSettingFragment)
            }
            MenuItemType.MARKETING_AGREE -> {
                Timber.d("item : $item")
            }
            MenuItemType.CONTACT_EMAIL -> {
                composeEmail()
            }
            MenuItemType.YOUTH_PROTECTION_POLICY -> {
                val link = "https://www.fantoo.co.kr/youthpolicy"
                loadWebView(getString(item.stringRes), link)
            }
            MenuItemType.PRIVACY_POLICY -> {
                val link = "https://www.fantoo.co.kr/privacy"
                loadWebView(getString(item.stringRes), link)
            }
            MenuItemType.TERMS_OF_SERVICE -> {
                val link = "https://www.fantoo.co.kr/service"
                loadWebView(getString(item.stringRes), link)
            }
            else -> {
                Timber.d("else $item")
            }
        }
    }

    private fun navigateBySwitchItem(item: MenuItem.SwitchItem) {
        val extraMsg = TimeUtils.yearMonthDayDotString(System.currentTimeMillis())
        val subTitle =
            if (item.isChecked) getString(R.string.se_p_agree_market_info) else getString(R.string.se_p_disagree_marketing_info)
        val message = DialogMessage(
            DialogTitle(getString(R.string.g_advertisement_alarm), subTitle, extraMsg),
            DialogButton(getString(R.string.h_confirm), null, false),
            isCompleted = false
        )
        dialogFragment = MenuDialogFragment(message) { clickType ->
            when (clickType) {
                DialogClickType.OK -> {
                    Timber.d("ok")
                    viewModel.startSubscribeAds(item.isChecked)
                }
                DialogClickType.CANCEL -> {
                    Timber.d("cancel")
                }
            }
            dismissDialog()
        }

        dialogFragment.show(
            requireActivity().supportFragmentManager,
            MenuDialogFragment.DIALOG_MENU
        )
    }

    private fun dismissDialog() {
        Timber.d("Close dialog")
        dialogFragment.dismiss()
    }

    private fun selectTranslateLanguage() {
        val languageBottomSheet = AppLanguageBottomSheet()
        languageBottomSheet.title = getString(R.string.b_select_trans_language)
        lifecycleScope.launch {
            val languageCode = viewModel.getLanguageCode()
            Timber.d("getLanguageCode = $languageCode")
            languageCode?.let { code ->
                languageBottomSheet.selectLanguageCode = code
            }
        }
        languageBottomSheet.itemClickListener = (object : AppLanguageBottomSheet.ItemClickListener {
            override fun onItemClick(item: Language) {
                viewModel.setLanguageCode(item.langCode)
                languageBottomSheet.dismiss()
                lifecycleScope.launch {
                    delay(200)
                    findNavController().navigate(R.id.settingFragment, arguments, NavOptions.Builder().setPopUpTo(R.id.settingFragment, true).build())
                }
            }
        })
        languageBottomSheet.show(requireActivity().supportFragmentManager, "")
    }

    private fun composeEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // only email apps should handle this
        }
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            Timber.d("email app not found")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView(title: String, link: String) {
        val webView = WebView(requireContext()).apply { loadUrl(link) }
        webView.settings.apply {
            useWideViewPort = true
            loadWithOverviewMode = true
            javaScriptEnabled = true
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(webView)
            .create()
            .show()
    }

}