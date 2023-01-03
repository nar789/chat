package com.rndeep.fns_fantoo.ui.chatting.addchat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.utils.setDarkStatusBarIcon
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
        initObservers()
        return ComposeView(requireContext()).apply {
            setContent {
                AddChatScreen(viewModel, ::onBack)
            }
        }
    }

    private fun onBack() {
        findNavController().popBackStack()
    }

    private fun initObservers() {
        viewModel.navigateToChat.observe(viewLifecycleOwner) { chatId ->
            if (chatId == -1) return@observe
            findNavController().navigate(
                AddChatFragmentDirections.actionAddChatFragmentToChattingFragment(
                    chatId
                )
            )
        }
        viewModel.showErrorToast.observe(viewLifecycleOwner) { exit ->
            Toast.makeText(requireContext(), R.string.add_chat_error_toast, Toast.LENGTH_SHORT).show()
            if (exit) {
                findNavController().popBackStack()
            }
        }
    }
}