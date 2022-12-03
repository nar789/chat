package com.rndeep.fns_fantoo.ui.menu.settings.changepassword

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentChangePasswordBinding
import com.rndeep.fns_fantoo.ui.login.LoginMainActivity
import com.rndeep.fns_fantoo.ui.menu.DialogButton
import com.rndeep.fns_fantoo.ui.menu.DialogMessage
import com.rndeep.fns_fantoo.ui.menu.DialogTitle
import com.rndeep.fns_fantoo.ui.menu.DialogClickType
import com.rndeep.fns_fantoo.ui.menu.MenuDialogFragment
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.dismissKeyboard
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Account Info - Change Password UI
 */
@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {

    lateinit var binding: FragmentChangePasswordBinding
    private val viewModel : ChangePasswordViewModel by viewModels()
    private lateinit var dialogFragment: MenuDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChangePasswordBinding.inflate(inflater, container, false).apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            newPasswordEditField.editText?.doOnTextChanged { text, _, _, _ ->
                text?.let {
                    if (viewModel.checkPasswordRegex(it.toString())){
                        binding.newPasswordEditField.error = null
                        binding.newPasswordEditField.setBoxStrokeColorStateList(requireContext().getColorStateList(R.color.box_stroke_color_done))
                        binding.confirmPasswordEditField.isEnabled = true
                        viewModel.setNewPassword(it.toString())
                    } else {
                        binding.newPasswordEditField.error = getString(R.string.se_num_keep_format)
                    }
                }
            }

            confirmPasswordEditField.editText?.doOnTextChanged { text, _, _, _ ->
                text?.let {
                    if (viewModel.checkConfirmPassword(it.toString())){
                        binding.confirmPasswordEditField.error = null
                        binding.confirmPasswordEditField.setBoxStrokeColorStateList(requireContext().getColorStateList(R.color.box_stroke_color_done))
                        dismissKeyboard(confirmPasswordEditField)
                        enableChangePasswordBtn()
                    } else {
                        binding.confirmPasswordEditField.error = getString(R.string.se_a_mismatch_password)
                    }
                }
            }

            changePasswordBtn.setOnClickListener {
                openDialog()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.currentPasswordEdit.setOnEditorActionListener { textView, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    Timber.d("action done text : ${textView.text}")
                    viewModel.checkCurrentPassword(textView.text.toString())
                    true
                }
                else -> false
            }
        }

        viewModel.isMatchPassword.observe(viewLifecycleOwner) { isMatch ->
            if (isMatch) {
                binding.currentPasswordEditField.error = null
                binding.currentPasswordEditField.setBoxStrokeColorStateList(
                    requireContext().getColorStateList(
                        R.color.box_stroke_color_done
                    )
                )
                binding.currentPassword.hint = null
                binding.newPasswordEditField.isEnabled = true
                binding.currentPasswordEdit.nextFocusDownId = binding.newPasswordEditField.id
                binding.newPasswordEdit.requestFocus()
            } else {
//                viewModel.errorMessage?.let { message ->
//                    binding.currentPasswordEditField.error = message
//                } ?: run {
//                    binding.currentPasswordEditField.error =
//                        getString(R.string.current_password_dismatch_msg)
//                }
                binding.currentPasswordEditField.error =
                    getString(R.string.se_h_not_matched_current_password)
            }
        }

        viewModel.changePasswordSuccess.observe(viewLifecycleOwner) { success ->
            if(success) {
                val intent = Intent(requireContext(), LoginMainActivity::class.java)
                intent.putExtra(ConstVariable.INTENT.EXTRA_NAV_START_DESTINATION_ID, R.id.loginFragment)
                startActivity(intent)
                requireActivity().finish()
            } else {
                Timber.d("change password fail!")
            }
        }
    }

    private fun enableChangePasswordBtn() {
        binding.changePasswordBtn.isFocusable = true
        binding.changePasswordBtn.isEnabled = true
        binding.changePasswordBtn.backgroundTintList = ColorStateList.valueOf(requireContext().getColor(R.color.primary_default))
    }

    private fun openDialog() {
        Timber.d("openDialog")
        val message = DialogMessage(
            DialogTitle(getString(R.string.b_change_password), getString(R.string.se_b_alert_change_password), null),
            DialogButton(getString(R.string.a_yes), getString(R.string.a_no), true),
            isCompleted = false
        )
        dialogFragment = MenuDialogFragment(message) { clickType ->
            when (clickType) {
                DialogClickType.OK -> {
                    Timber.d("ok")
                    viewModel.startChangePassword()
                }
                DialogClickType.CANCEL -> {
                    Timber.d("cancel")
                }
            }
            dismissDialog()
        }
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