package com.rndeep.fns_fantoo.ui.chatting.chatlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.utils.setDarkStatusBarIcon
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatListFragment : Fragment() {

    private val viewModel by viewModels<ChatListViewModel>()

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
                ChatListScreen(viewModel) {
                    findNavController().navigate(
                        ChatListFragmentDirections.actionChatListFragmentToChattingFragment(
                            it
                        )
                    )
                }
            }
        }
    }
}