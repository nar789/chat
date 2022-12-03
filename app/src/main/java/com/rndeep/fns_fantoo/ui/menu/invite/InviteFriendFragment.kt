package com.rndeep.fns_fantoo.ui.menu.invite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentInviteFriendBinding
import com.rndeep.fns_fantoo.ui.menu.DialogButton
import com.rndeep.fns_fantoo.ui.menu.DialogMessage
import com.rndeep.fns_fantoo.ui.menu.DialogTitle
import com.rndeep.fns_fantoo.ui.menu.MenuDialogFragment
import com.rndeep.fns_fantoo.ui.menu.MenuDialogFragment.Companion.DIALOG_MENU
import com.rndeep.fns_fantoo.utils.dismissKeyboard
import com.rndeep.fns_fantoo.utils.launchAndRepeatWithViewLifecycle
import com.rndeep.fns_fantoo.utils.setStatusBar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Invite Friend UI
 */
@AndroidEntryPoint
class InviteFriendFragment : Fragment() {

    private lateinit var binding: FragmentInviteFriendBinding
    private lateinit var dialogFragment: MenuDialogFragment
    private val args: InviteFriendFragmentArgs by navArgs()

    private val viewModel: InviteFriendViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentInviteFriendBinding.inflate(inflater, container, false).apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            viewModel.setMyCode(args.referralCode)
            myInviteCode.text = args.referralCode

            val text = HtmlCompat.fromHtml(
                getString(R.string.invite_friend_code_desc_2),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            recommendDesc.text = text

            codeCopyBtn.setOnClickListener {
                viewModel.setClipboard()
                dialogFragment = MenuDialogFragment(
                    DialogMessage(
                        DialogTitle(null, getString(R.string.se_c_share_friend_referral_code), null),
                        DialogButton(getString(R.string.h_confirm), null, false),
                        isCompleted = false
                    )
                ) { dismissDialog() }
                openDialog()
            }

            codeRegisterBtn.setOnClickListener {
                viewModel.checkInvalidCode(recommenderCodeInput.text.toString())
                dismissKeyboard(it)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBar(R.color.gray_25, true)

        launchAndRepeatWithViewLifecycle {
            viewModel.codeState.collect { state ->
                when(state) {
                    is RegisterCodeState.Success -> {
                        Timber.d("state : ${state.codeState}")
                        showMessage(state.codeState)
                    }
                    is RegisterCodeState.Loading -> {
                        Timber.d("loading")
                    }
                    is RegisterCodeState.Error -> {
                        Timber.d("error : ${state.exception}")
                        showErrorMessage(state.exception)
                    }
                    else -> {
                        Timber.d("none")
                    }
                }
            }
        }
    }

    private fun showErrorMessage(exception: Throwable) {
        val message = DialogMessage(
            DialogTitle(null, exception.message, null),
            DialogButton(getString(R.string.h_confirm), null, false),
            isCompleted = false
        )
        dialogFragment = MenuDialogFragment(message) { dismissDialog() }
        openDialog()
    }

    private fun showMessage(state: RecommendCodeState) {
        var message = DialogMessage(
            DialogTitle(null, getString(R.string.se_a_write_referral_code), null),
            DialogButton(getString(R.string.h_confirm), null, false),
            isCompleted = false
        )
        dialogFragment = when (state) {
            RecommendCodeState.EMPTY -> {
                MenuDialogFragment(message) { dismissDialog() }
            }
            RecommendCodeState.VALID -> {
                message = DialogMessage(
                    DialogTitle(null, getString(R.string.se_c_referral_code_succeess_registered), String.format(getString(R.string.se_p_fanit_paid_and_check), "500")),
                    DialogButton(getString(R.string.h_confirm), null, false),
                    isCompleted = false
                )
                MenuDialogFragment(message) { dismissDialog() }
            }
            RecommendCodeState.INVALID -> {
                message = DialogMessage(
                    DialogTitle(null, getString(R.string.se_a_invalid_referral_code), null),
                    DialogButton(getString(R.string.h_confirm), null, false),
                    isCompleted = false
                )
                MenuDialogFragment(message) { dismissDialog() }
            }
            RecommendCodeState.MYSELF -> {
                message = DialogMessage(
                    DialogTitle(null, getString(R.string.se_b_cannot_use_your_code), null),
                    DialogButton(getString(R.string.h_confirm), null, false),
                    isCompleted = false
                )
                MenuDialogFragment(message) { dismissDialog() }
            }
        }
        Timber.d("state : $state , message : ${message.title}")
        openDialog()
    }

    private fun openDialog() {
        dialogFragment.show(requireActivity().supportFragmentManager, DIALOG_MENU)
    }

    private fun dismissDialog() {
        Timber.d("Close dialog")
        dialogFragment.dismiss()
    }

}