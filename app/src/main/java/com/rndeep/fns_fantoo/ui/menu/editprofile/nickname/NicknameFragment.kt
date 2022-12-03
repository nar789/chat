package com.rndeep.fns_fantoo.ui.menu.editprofile.nickname

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentNicknameBinding
import com.rndeep.fns_fantoo.ui.menu.DialogButton
import com.rndeep.fns_fantoo.ui.menu.DialogMessage
import com.rndeep.fns_fantoo.ui.menu.DialogTitle
import com.rndeep.fns_fantoo.ui.menu.DialogClickType
import com.rndeep.fns_fantoo.ui.menu.MenuDialogFragment
import com.rndeep.fns_fantoo.utils.dismissKeyboard
import com.rndeep.fns_fantoo.utils.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


/**
 * Nickname UI
 */
@AndroidEntryPoint
class NicknameFragment : Fragment() {

    lateinit var binding: FragmentNicknameBinding
    private val viewModel: NicknameViewModel by viewModels()

    private lateinit var dialogFragment: MenuDialogFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNicknameBinding.inflate(inflater, container, false).apply {

            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        launchAndRepeatWithViewLifecycle {
            viewModel.state.collect { state ->
                when (state) {
                    is NicknameUIState.Success -> {
                        Timber.d("state : ${state.codeState}")
                        when(state.codeState) {
                            NicknameState.COMPLETED -> view.findNavController().navigateUp()
                            else -> showMessage(state.codeState)
                        }
                    }
                    is NicknameUIState.Loading -> {
                        Timber.d("loading")
                    }
                    is NicknameUIState.Error -> {
                        Timber.d("error : ${state.exception}")
                    }
                    else -> {
                        Timber.d("else : $state")
                    }
                }
            }
        }

        viewModel.currentNickname.observe(viewLifecycleOwner) { nickname ->
            binding.userNameNicknameEdit.setText(nickname)
        }

        binding.userNameNicknameEdit.doOnTextChanged { text, _, _, _ ->
            val count = text?.length
            binding.editCounter.text = "$count/20"
        }

        binding.duplicateCheckBtn.setOnClickListener {
            dismissKeyboard(it)
            viewModel.checkDuplicateNickname(binding.userNameNicknameEdit.text.toString())
        }

        binding.nicknameSaveBtn.setOnClickListener {
            viewModel.updateNickname()
        }
    }

    private fun updateSaveBtn() {
        Timber.d("updateSaveBtn")
        viewModel.changedNickname = binding.userNameNicknameEdit.text.toString()
        binding.nicknameSaveBtn.isEnabled = true
        binding.nicknameSaveBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.state_active_primary_default))
    }

    private fun showMessage(state: NicknameState) {
        var message = DialogMessage(
            DialogTitle(getString(R.string.n_setting_nickname), getString(R.string.se_n_write_nickname), null),
            DialogButton(getString(R.string.h_confirm), null, false),
            isCompleted = false
        )
        dialogFragment = when (state) {
            NicknameState.EMPTY -> {
                MenuDialogFragment(message) { dismissDialog() }
            }
            NicknameState.THIRTY_DAYS -> {
                message = DialogMessage(
                    DialogTitle(getString(R.string.n_setting_nickname), getString(R.string.se_n_change_nickname_notice_2), null),
                    DialogButton(getString(R.string.h_confirm), null, false),
                    isCompleted = false
                )
                MenuDialogFragment(message) { dismissDialog() }
            }
            NicknameState.DUPLICATED -> {
                message = DialogMessage(
                    DialogTitle(getString(R.string.n_setting_nickname), getString(R.string.se_a_already_use_nickname), null),
                    DialogButton(getString(R.string.h_confirm), null, false),
                    isCompleted = false
                )
                MenuDialogFragment(message) { dismissDialog() }
            }
            else -> {
                message = DialogMessage(
                    DialogTitle(getString(R.string.n_setting_nickname), getString(R.string.se_s_use_possible_nickname_use_this), null),
                    DialogButton(getString(R.string.h_allow), getString(R.string.g_denial), true),
                    isCompleted = false
                )
                MenuDialogFragment(message) { clickType ->
                    when (clickType) {
                        DialogClickType.OK -> {
                            Timber.d("ok")
                            updateSaveBtn()
                        }
                        DialogClickType.CANCEL -> {
                            Timber.d("cancel")
                        }
                    }
                    dismissDialog()
                }
            }
        }
        Timber.d("state : $state , message : $message")
        openDialog()
    }

    private fun openDialog() {
        Timber.d("openDialog")
        dialogFragment.show(
            requireActivity().supportFragmentManager,
            MenuDialogFragment.DIALOG_MENU
        )
    }

    private fun dismissDialog() {
        Timber.d("dismissDialog")
        dialogFragment.dismiss()
    }

}