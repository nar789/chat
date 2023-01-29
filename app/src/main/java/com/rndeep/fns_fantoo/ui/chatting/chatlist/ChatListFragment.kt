package com.rndeep.fns_fantoo.ui.chatting.chatlist

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.ui.login.LoginMainActivity
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.setDarkStatusBarIcon
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChatListFragment : Fragment() {

    companion object {
        private const val PREF_ALARM = "prefAlarm"
        private const val KEY_ALARM_STATE = "chatAlarmState"
    }

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
        lifecycle.addObserver(viewModel)
        initObserver()
        viewModel.updateMuteChatIds(getMuteList())
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

    private fun navigateToChat(chatId: Int, roomName: String) {
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
        viewModel.updateMuteChatState.observe(viewLifecycleOwner) {
            changeMuteList(
                it.first,
                it.second
            )
        }
    }

    private fun requestLogin() {
        val intent = Intent(context, LoginMainActivity::class.java)
        intent.putExtra(
            ConstVariable.INTENT.EXTRA_NAV_START_DESTINATION_ID,
            R.id.loginFragment
        )
        requireContext().startActivity(intent)
        requireActivity().finish()
    }

    private fun changeMuteList(chatId: Int, isMute: Boolean) {
        val pref: SharedPreferences =
            requireContext().getSharedPreferences(PREF_ALARM, Context.MODE_PRIVATE) ?: return

        val editor = pref.edit()
        val gson = Gson()
        val chatList = getMuteList().toMutableList()
        if (isMute) {
            chatList.add(chatId)
        } else {
            chatList.remove(chatId)
        }
        val json = gson.toJson(chatList)

        editor.putString(KEY_ALARM_STATE, json)
        editor.apply()

        viewModel.updateMuteChatIds(chatList)
    }

    private fun getMuteList(): List<Int> {
        val pref: SharedPreferences =
            requireContext().getSharedPreferences(PREF_ALARM, Context.MODE_PRIVATE)
                ?: return emptyList()

        val json: String = pref.getString(KEY_ALARM_STATE, "") ?: ""
        return Gson().fromJson(json, object : TypeToken<List<Int>?>() {}.type)?: emptyList()
    }
}