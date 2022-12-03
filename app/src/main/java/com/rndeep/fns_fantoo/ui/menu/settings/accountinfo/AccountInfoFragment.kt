package com.rndeep.fns_fantoo.ui.menu.settings.accountinfo

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentAccountInfoBinding
import com.rndeep.fns_fantoo.ui.login.LoginMainActivity
import com.rndeep.fns_fantoo.ui.menu.*
import com.rndeep.fns_fantoo.ui.menu.DialogClickType
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.SizeUtils
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

/**
 * Account Info UI
 */
@AndroidEntryPoint
class AccountInfoFragment : Fragment() {

    private lateinit var binding: FragmentAccountInfoBinding
    private val viewModel: AccountInfoViewModel by viewModels()
    private lateinit var dialogFragment: MenuDialogFragment
    private lateinit var accountInfoAdapter: MenuListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        accountInfoAdapter = MenuListAdapter { item ->
            Timber.d("clicked item : $item")
            val menuItem = item as MenuItem.Item
            navigateByItem(menuItem.name)
        }

        binding = FragmentAccountInfoBinding.inflate(layoutInflater, container, false).apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
            accountInfoMenuList.run {
                adapter = accountInfoAdapter
                setHasFixedSize(true)
                itemAnimator = null
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.accountInfoMenu.observe(viewLifecycleOwner) { list ->
            accountInfoAdapter.submitList(list)
        }

        viewModel.unAuthorized.observe(viewLifecycleOwner) {
            if (it) {
                val intent = Intent(requireContext(), LoginMainActivity::class.java)
                intent.putExtra(ConstVariable.INTENT.EXTRA_NAV_START_DESTINATION_ID, R.id.loginFragment)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    private fun navigateByItem(item: MenuItemType) {
        when (item) {
            MenuItemType.CHANGE_PASSWORD -> {
                findNavController().navigate(R.id.action_accountInfoFragment_to_changePasswordFragment)
            }
            MenuItemType.LOGOUT -> {
                openDialog()
            }
            MenuItemType.DELETE_ACCOUNT -> {
                findNavController().navigate(R.id.action_accountInfoFragment_to_deleteAccountFragment)
            }
            MenuItemType.QR_AUTH -> {
                showQrAuthDialog()
            }
            else -> {
                Timber.d("else $item")
            }
        }
    }

    private fun openDialog() {
        val message = DialogMessage(
            DialogTitle(getString(R.string.r_logout), getString(R.string.se_r_question_login), null),
            DialogButton(getString(R.string.a_yes), getString(R.string.a_no), true),
            isCompleted = false
        )
        dialogFragment = MenuDialogFragment(message) { clickType ->
            when (clickType) {
                DialogClickType.OK -> {
                    Timber.d("ok")
                    viewModel.startLogout()
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
        Timber.d("Close dialog")
        dialogFragment.dismiss()
    }

    private fun showQrAuthDialog() {
        val dialogBuilder = AlertDialog.Builder(context)

        dialogBuilder.setTitle("My Account Qr Code")
        val qrImageView = ImageView(context)
        val hints = Hashtable<EncodeHintType, String>()
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"

        val qrWrite = MultiFormatWriter()
        try {
            val bitMatrix = qrWrite.encode(
                viewModel.getIntegUid(),
                BarcodeFormat.QR_CODE,
                SizeUtils.getDpValue(200f, context).toInt(),
                SizeUtils.getDpValue(200f, context).toInt(),
                hints
            )
            val barcodeEncoder = BarcodeEncoder()
            val qrBitmap = barcodeEncoder.createBitmap(bitMatrix)
            qrImageView.setImageBitmap(qrBitmap)
        } catch (e: Exception) {

        }

        dialogBuilder.setView(qrImageView)
        dialogBuilder.setPositiveButton(
            "Quit"
        ) { dialog, _ -> dialog?.dismiss() }
        dialogBuilder.show()
    }
}