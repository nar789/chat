package com.rndeep.fns_fantoo.ui.chatting.chatlist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.ui.login.LoginMainActivity
import com.rndeep.fns_fantoo.utils.ConstVariable
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
        initObserver()
        return ComposeView(requireContext()).apply {
            setContent {
                ChatListScreen(
                    viewModel,
                    { id, name -> navigateToChat(id, name) },
                    ::navigateToAddChat
                )
            }
        }
    }

    private fun navigateToChat(chatId: Long, roomName: String) {
        findNavController().navigate(
            ChatListFragmentDirections.actionChatListFragmentToChattingFragment(chatId, roomName)
        )
    }

    private fun navigateToAddChat() {
        findNavController().navigate(
            ChatListFragmentDirections.actionChatListFragmentToAddChatFragment()
        )
    }

    private fun initObserver() {
        viewModel.navigateToLogin.observe(viewLifecycleOwner) { requestLogin() }
    }

    private fun requestLogin() {
        val intent = Intent(context, LoginMainActivity::class.java)
        intent.putExtra(
            ConstVariable.INTENT.EXTRA_NAV_START_DESTINATION_ID,
            R.id.loginFragment
        )
        context?.startActivity(intent)
        activity?.finish()
    }
}