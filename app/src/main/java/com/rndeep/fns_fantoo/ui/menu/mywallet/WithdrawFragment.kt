package com.rndeep.fns_fantoo.ui.menu.mywallet

import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentWithdrawBinding
import timber.log.Timber

/**
 * Safe - Withdraw UI
 */
class WithdrawFragment : Fragment() {

    private lateinit var binding: FragmentWithdrawBinding
    private val keys : ArrayList<String> = ArrayList()
    private var myCoin = MutableLiveData<String>()
    private var coin = StringBuilder()
    private lateinit var clipboard: ClipboardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setKeys()
        clipboard = requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val keysAdapter = NumberKeyAdapter(keys) { item ->
            onClicked(item)
        }

        binding = FragmentWithdrawBinding.inflate(inflater, container, false).apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
            numberList.adapter = keysAdapter
            coinAddressField.setOnClickListener {
                if(clipboard.hasPrimaryClip()) {
                    val item = clipboard.primaryClip?.getItemAt(0)
                    if (item != null) {
                        coinAddress.text = item.text
                        Timber.d("coin address field clicked , text : ${item.text}")
                    }
                }
            }
            myCoin.observe(viewLifecycleOwner) {
                if(it.isNullOrEmpty()) {
                    withdrawCoin.text = getString(R.string.c_minimum_10_kdg)
                    withdrawCoin.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_400))
                } else {
                    withdrawCoin.text = it.toString()
                    withdrawCoin.setTextColor(ContextCompat.getColor(requireContext(), R.color.state_active_gray_900))
                }
            }
        }

        return binding.root
    }

    private fun onClicked(item: String) {
        when(item) {
            "-1" -> {
                val length = coin.length
                Timber.d("clicked item : $item, length : $length")
                if(coin.isNotEmpty()) {
                    coin.deleteCharAt(length-1)
                }
            }
            else -> coin.append(item)
        }
        myCoin.value = coin.toString()
        Timber.d("clicked item : $item, myCoin : ${myCoin.value}, coin : $coin")
    }

    private fun setKeys() {
        for(key in 1..12) {
            when(key) {
                10 -> keys.add("00")
                11 -> keys.add("0")
                12 -> keys.add("-1")
                else -> keys.add(key.toString())
            }
        }
    }

}