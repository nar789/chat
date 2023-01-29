package com.rndeep.fns_fantoo.ui.menu.mywallet.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.userinfo.WalletHistoryType
import com.rndeep.fns_fantoo.databinding.BottomSheetWalletHistoryTypeBinding

class HistoryTypeBottomSheet(
    private val walletHistoryType: WalletHistoryType,
    private val onItemClicked: (WalletHistoryType) -> Unit
) : BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetWalletHistoryTypeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetWalletHistoryTypeBinding.inflate(inflater, container, false).apply {
            all.setOnClickListener {
                onClicked(WalletHistoryType.ALL)
            }
            paid.setOnClickListener {
                onClicked(WalletHistoryType.PAID)
            }
            used.setOnClickListener {
                onClicked(WalletHistoryType.USED)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSelectedHistoryType(walletHistoryType)
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialog
    }

    private fun onClicked(walletHistoryType: WalletHistoryType) {
        onItemClicked(walletHistoryType)
    }

    private fun setSelectedHistoryType(walletHistoryType: WalletHistoryType) {
        when(walletHistoryType) {
            WalletHistoryType.ALL -> {
                binding.allCheck.visibility = View.VISIBLE
                binding.all.setTextColor(requireContext().getColor(R.color.primary_default))
            }
            WalletHistoryType.PAID -> {
                binding.paidCheck.visibility = View.VISIBLE
                binding.paid.setTextColor(requireContext().getColor(R.color.primary_default))
            }
            WalletHistoryType.USED -> {
                binding.usedCheck.visibility = View.VISIBLE
                binding.used.setTextColor(requireContext().getColor(R.color.primary_default))
            }
        }
    }

    companion object {
        val TAG = HistoryTypeBottomSheet::class.qualifiedName
    }
}