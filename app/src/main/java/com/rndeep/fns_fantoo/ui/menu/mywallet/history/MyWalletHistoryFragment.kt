package com.rndeep.fns_fantoo.ui.menu.mywallet.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.userinfo.WalletHistoryType
import com.rndeep.fns_fantoo.databinding.FragmentMyWalletHistoryBinding
import com.rndeep.fns_fantoo.ui.menu.*
import com.rndeep.fns_fantoo.utils.TimeUtils
import com.rndeep.fns_fantoo.utils.setStatusBar
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import java.text.DecimalFormat


/**
 * My Wallet History UI
 */
@AndroidEntryPoint
class MyWalletHistoryFragment : Fragment() {

    private lateinit var binding: FragmentMyWalletHistoryBinding
    private lateinit var myWalletHistoryAdapter: MyWalletHistoryAdapter
    private lateinit var historyTypeBottomSheet: HistoryTypeBottomSheet
    private lateinit var dialogFragment: MenuDialogFragment
    private lateinit var currentYearMonth: LocalDateTime

    private val viewModel: MyWalletHistoryViewModel by viewModels()
    private val args: MyWalletHistoryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyWalletHistoryBinding.inflate(inflater, container, false).apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            myWalletHistoryAdapter = MyWalletHistoryAdapter()
            historyList.run {
                adapter = myWalletHistoryAdapter
                setHasFixedSize(true)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBar(R.color.gray_25, true)

        val type = args.walletType
        Timber.d("type: $type")
        val icon = when (type) {
            WalletType.KDG.type -> R.drawable.kingdom_coin_gold
            WalletType.HONOR.type -> R.drawable.honor
            else -> R.drawable.fanit_round
        }
        binding.assetImg.setImageDrawable(
            AppCompatResources.getDrawable(requireContext(), icon)
        )
        binding.assetTitle.text = type

        currentYearMonth = TimeUtils.getCurrentLocalDateTime()
        binding.date.text = TimeUtils.getYearMonthFromLocalDateTime(currentYearMonth)

        binding.filter.setOnClickListener {
            showHistoryTypeBottomSheet(viewModel.currentHistoryType)
        }

        binding.previous.setOnClickListener {
            currentYearMonth = currentYearMonth.minusMonths(1)
            if(checkValidMonth()){
                getWalletHistory(getType(viewModel.currentHistoryType))
            } else {
                currentYearMonth = currentYearMonth.plusMonths(1)
                showErrorMessage()
            }
        }

        binding.next.setOnClickListener {
            currentYearMonth = currentYearMonth.plusMonths(1)
            if(TimeUtils.getCurrentLocalDateTime().isAfter(currentYearMonth)){
                getWalletHistory(getType(viewModel.currentHistoryType))
            } else {
                currentYearMonth = currentYearMonth.minusMonths(1)
            }
        }

        getWalletHistory(WalletListType.All)

        viewModel.myWallet.observe(viewLifecycleOwner) { myWallet ->
            val decimalFormat = DecimalFormat("#,###")
            myWallet?.let {
                binding.myAsset.text = decimalFormat.format(myWallet.fanit)
            }
        }

        viewModel.myWalletHistory.observe(viewLifecycleOwner) { myWalletHistory ->
            myWalletHistory?.let { walletHistory ->
                Timber.d("walletHistory: $walletHistory")
                myWalletHistoryAdapter.submitList(walletHistory.walletList)

                val visibility = if(myWalletHistory.listSize == 0) View.VISIBLE else View.GONE
                binding.emptyImg.visibility = visibility
                binding.emptyMsg.visibility = visibility
            }
        }
    }

    private fun getWalletHistory(walletListType: WalletListType) {
        val walletType = args.walletType.lowercase()
        Timber.d("getWalletHistory - walletListType: ${walletListType.type}, currentYearMonth: $currentYearMonth, walletType: $walletType")
        viewModel.fetchUserWalletHistory(walletType, walletListType.type, TimeUtils.getYearMonthFromLocalDateTime(currentYearMonth), nextId = 0, size = 10)
        binding.date.text = TimeUtils.getYearMonthFromLocalDateTime(currentYearMonth)
    }

    private fun checkValidMonth(): Boolean {
        val validMonth = TimeUtils.getCurrentLocalDateTime().minusMonths(3)
        Timber.d("validMonth: $validMonth, currentYearMonth: $currentYearMonth")
        return currentYearMonth.isAfter(validMonth)
    }

    private fun showErrorMessage() {
        val dialogMessage = DialogMessage(
            DialogTitle(null, getString(R.string.alert_cannot_load_ago_three_month), null),
            DialogButton(getString(R.string.h_confirm), null, false),
            isCompleted = false
        )
        dialogFragment = MenuDialogFragment(dialogMessage) {
            Timber.d("click")
            dialogFragment.dismiss()
        }
        dialogFragment.show(
            requireActivity().supportFragmentManager,
            MenuDialogFragment.DIALOG_MENU
        )
    }

    private fun showHistoryTypeBottomSheet(walletHistoryType: WalletHistoryType) {
        historyTypeBottomSheet = HistoryTypeBottomSheet(walletHistoryType) { historyType ->
            Timber.d("clicked walletHistoryType : $historyType")
            if(viewModel.currentHistoryType == historyType) {
                return@HistoryTypeBottomSheet
            } else {
                viewModel.currentHistoryType = historyType
                binding.filter.text = getString(viewModel.currentHistoryType.stringRes)
                getWalletHistory(getType(historyType))
            }
            historyTypeBottomSheet.dismiss()
        }
        historyTypeBottomSheet.show(requireActivity().supportFragmentManager, HistoryTypeBottomSheet.TAG)
    }

    private fun getType(walletHistoryType: WalletHistoryType) = when (walletHistoryType) {
        WalletHistoryType.USED -> WalletListType.USED
        WalletHistoryType.PAID -> WalletListType.PAID
        WalletHistoryType.ALL -> WalletListType.All
    }
}