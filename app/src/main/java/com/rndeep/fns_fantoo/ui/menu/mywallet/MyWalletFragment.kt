package com.rndeep.fns_fantoo.ui.menu.mywallet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentMyWalletBinding
import com.rndeep.fns_fantoo.utils.setStatusBar
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

/**
 * My Wallet UI
 */
@AndroidEntryPoint
class MyWalletFragment : Fragment() {

    private lateinit var binding: FragmentMyWalletBinding
    private val viewModel: MyWalletViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMyWalletBinding.inflate(inflater, container, false).apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
            kdgHistoryBtn.setOnClickListener {
                navigateToHistory(MyWalletType.KDG)
            }
            fanitHistoryBtn.setOnClickListener {
                navigateToHistory(MyWalletType.FANIT)
            }
            honorHistoryBtn.setOnClickListener {
                navigateToHistory(MyWalletType.HONOR)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBar(R.color.bg_light_gray_50, true)

        viewModel.myWallet.observe(viewLifecycleOwner) { wallets ->
            wallets?.let {
                val decimalFormat = DecimalFormat("#,###")
                binding.myKdg.text = decimalFormat.format(it.kdg)
                binding.myFanit.text = decimalFormat.format(it.fanit)
                binding.myHonor.text = decimalFormat.format(it.honor)
            }
        }
    }

    private fun navigateToHistory(type: MyWalletType) {
        val direction =
//            MyWalletFragmentDirections.actionMyWalletFragmentToSafeFragment(type.toString())
            MyWalletFragmentDirections.actionMyWalletFragmentToMyWalletHistoryFragment(type.toString())
        findNavController().navigate(direction)
    }

}