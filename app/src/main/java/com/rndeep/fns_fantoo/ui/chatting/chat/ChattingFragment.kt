package com.rndeep.fns_fantoo.ui.chatting.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.rndeep.fns_fantoo.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChattingFragment : Fragment() {

    val viewModel: ChattingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {
                val uiState = viewModel.chatUiState.value
                MaterialTheme {
                    ChattingScreen(
                        uiState = uiState,
                        titleText = "Dasol",
                        onMessageSent = { viewModel.sendMessage(it) },
                        onTranslateClicked = { viewModel.setTranslateMode(!uiState.translateMode) },
                        onImageClicked = { imageUrl ->
                            findNavController().navigate(
                                ChattingFragmentDirections.actionChattingFragmentToImageViewerFragment(
                                    imageUrl
                                )
                            )
                        },
                        onImageSelectorClicked = { navigateToImagePicker() },
                        onClickAuthor = { navigateToProfileDetail(it) }
                    )
                }
            }
        }
    }

    private fun navigateToImagePicker() {
        findNavController().navigate(R.id.action_chattingFragment_to_imagePickerFragment)
    }

    private fun navigateToProfileDetail(userId: Long) {
        val direction =
            ChattingFragmentDirections.actionChattingFragmentToProfileDetailDialog(userId)
        findNavController().navigate(direction)
    }
}