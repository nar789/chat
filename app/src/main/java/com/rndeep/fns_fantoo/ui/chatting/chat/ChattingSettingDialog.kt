package com.rndeep.fns_fantoo.ui.chatting.chat

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rndeep.fns_fantoo.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChattingSettingDialog @Inject constructor() : BottomSheetDialogFragment() {

    private val viewModel by viewModels<ChattingSettingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args by navArgs<ChattingSettingDialogArgs>()
        viewModel.init(args.chatId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setObserver()

        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    ChattingSettingScreen(
                        isAlarmOn = viewModel.isAlarmOn.value,
                        onClickAlarm = viewModel::setAlarm,
                        onClickLeave = viewModel::leaveChatting
                    )
                }
            }
        }
    }

    private fun setObserver() {
        viewModel.popBackStackEvent.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        viewModel.errorToastEvent.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.TransparentDialog)
    }
}