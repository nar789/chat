package com.rndeep.fns_fantoo.ui.menu.mywallet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentSafeBinding
import com.rndeep.fns_fantoo.utils.setStatusBar
import timber.log.Timber


/**
 * My Wallet - Safe UI
 */
class SafeFragment : Fragment() {

    private lateinit var binding: FragmentSafeBinding
    private val args: SafeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSafeBinding.inflate(inflater, container, false).apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            withdrawBtn.setOnClickListener {
                findNavController().navigate(R.id.action_safeFragment_to_withdrawFragment)
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
            "KDG" -> R.drawable.kingdom_coin_gold
            "FANIT" -> R.drawable.fanit_round
            else -> R.drawable.honor
        }
        binding.assetImg.setImageDrawable(
            AppCompatResources.getDrawable(requireContext(), icon)
        )
        binding.assetName.text = type
    }
}