package com.rndeep.fns_fantoo.ui.menu.settings.deleteaccount

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentDeleteAccountBinding
import com.rndeep.fns_fantoo.ui.login.LoginMainActivity
import com.rndeep.fns_fantoo.ui.menu.*
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


/**
 * Delete Account UI
 */
@AndroidEntryPoint
class DeleteAccountFragment : Fragment() {

    lateinit var binding: FragmentDeleteAccountBinding
    private lateinit var dialogFragment: MenuDialogFragment
    private val viewModel : DeleteAccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDeleteAccountBinding.inflate(inflater, container, false).apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.confirmImg.setOnClickListener {
            val checked = viewModel.isChecked.value
            checked?.let {
                viewModel.setChecked(!it)
            }
        }

        binding.deleteAccountBtn.setOnClickListener {
            openDialog()
        }

        viewModel.userInfo.observe(viewLifecycleOwner) { info ->
            info?.let {
                binding.accountTypeImg.setImageResource(getAccountIcon(info.loginType))
                binding.account.text = info.loginId
            }
        }

        viewModel.myWallet.observe(viewLifecycleOwner) { wallet ->
            wallet?.let {
                binding.myFanitInfo.text = getString(R.string.b_have_fan_it) +" "+wallet.fanit.toString()
                binding.myKdgInfo.text = getString(R.string.b_have_kdg)+" "+ wallet.kdg.toString()
                binding.myHonorInfo.text = getString(R.string.b_have_honor)+" "+ wallet.honor.toString()
            }
        }

        viewModel.isChecked.observe(viewLifecycleOwner) {
            setCheckedAgree(it)
        }

        viewModel.unAuthorized.observe(viewLifecycleOwner) {
            if(it) {
                val intent = Intent(requireContext(), LoginMainActivity::class.java)
                intent.putExtra(ConstVariable.INTENT.EXTRA_NAV_START_DESTINATION_ID, R.id.loginFragment)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    private fun setCheckedAgree(agree: Boolean) {
        if(agree) {
            binding.confirmImg.setImageResource(R.drawable.checkbox_round_check)
            binding.deleteAccountBtn.isEnabled = true
            binding.deleteAccountBtn.setBackgroundColor(requireContext().getColor(R.color.state_active_primary_default))
        } else {
            binding.confirmImg.setImageResource(R.drawable.checkbox_round_uncheck)
            binding.deleteAccountBtn.isEnabled = false
            binding.deleteAccountBtn.setBackgroundColor(requireContext().getColor(R.color.state_disabled_gray_200))
        }
    }

    private fun getAccountIcon(loginType: String) = when (loginType) {
        "apple" -> R.drawable.btn_logo_apple
        "google" -> R.drawable.btn_logo_google
        "facebook" -> R.drawable.btn_logo_facebook
        "line" -> R.drawable.btn_logo_line
        "kakao" -> R.drawable.btn_logo_kakaotalk
        else -> R.drawable.btn_logo_email
    }

    private fun openDialog() {
        val message = DialogMessage(
            DialogTitle(getString(R.string.s_leave), getString(R.string.se_t_sure_want_withdraw), null),
            DialogButton(getString(R.string.t_do_leave), getString(R.string.t_cancel_leave), true),
            isCompleted = false
        )
        dialogFragment = MenuDialogFragment(message) { clickType ->
            when (clickType) {
                DialogClickType.OK -> {
                    Timber.d("ok")
                    viewModel.startDeleteAccount()
                    findNavController().navigateUp()
                }
                DialogClickType.CANCEL -> {
                    Timber.d("cancel")
                }
            }
            dismissDialog()
        }
        dialogFragment.show(requireActivity().supportFragmentManager,
            MenuDialogFragment.DIALOG_MENU
        )
    }

    private fun dismissDialog() {
        Timber.d("Close dialog")
        dialogFragment.dismiss()
    }

}