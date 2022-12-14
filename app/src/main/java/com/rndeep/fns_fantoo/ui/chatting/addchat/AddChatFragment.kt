package com.rndeep.fns_fantoo.ui.chatting.addchat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.utils.setDarkStatusBarIcon

class AddChatFragment : Fragment() {

    private val viewModel by viewModels<AddChatViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.let {
            activity?.window?.statusBarColor = it.getColor(R.color.gray_25)
            it.setDarkStatusBarIcon()
        }
        return ComposeView(requireContext()).apply {
            setContent {
                AddChatScreen(::onBack)
            }
        }
    }

    private fun onBack() {
        findNavController().popBackStack()
    }
}