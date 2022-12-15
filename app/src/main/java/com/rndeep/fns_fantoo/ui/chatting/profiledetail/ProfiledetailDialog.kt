package com.rndeep.fns_fantoo.ui.chatting.profiledetail

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.ui.chatting.chat.ChattingViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileDetailDialog @Inject constructor() : DialogFragment() {

    // TODO : temp viewModel remove this
    val viewModel: ChattingViewModel by viewModels({ findNavController().getBackStackEntry(R.id.chattingFragment) })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    val uiState = viewModel.chatUiState.value

                    ProfileDetailScreen(
                        profileImage = "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMjA2MjFfMjYz%2FMDAxNjU1NzgxMTkyMTU5.YO7UnyTXMzeXg02Jz1tPCDba5Nsr7m-vuOMGwT1WXfEg.GfjVMhmbCK2UuWqIcvtpCPfvhX39IvwQ7smctj0-3I8g.JPEG.gydls004%2FInternet%25A3%25DF20220621%25A3%25DF121040%25A3%25DF8.jpeg&type=sc960_832",
                        userBlocked = uiState.userBlocked,
                        onClickCancel = { dismiss() },
                        onClickBlock = { viewModel.setUserBlock(it) }
                    )
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return ComponentDialog(requireContext(), R.style.TransparentDialog)
    }
}