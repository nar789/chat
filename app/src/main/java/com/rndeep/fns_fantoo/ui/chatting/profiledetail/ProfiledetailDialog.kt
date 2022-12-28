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
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.ui.chatting.chat.ChattingViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileDetailDialog @Inject constructor(
    val userId: String
) : DialogFragment() {

    val viewModel: ChattingViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.initProfileDetail(userId)
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    ProfileDetailScreen(
                        uiState = viewModel.profileUiState.value,
                        onClickCancel = { dismiss() },
                        onClickBlock = { viewModel.setUserBlock(it) },
                        onClickFollow = { viewModel.followUser(it) }
                    )
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return ComponentDialog(requireContext(), R.style.TransparentDialog)
    }
}