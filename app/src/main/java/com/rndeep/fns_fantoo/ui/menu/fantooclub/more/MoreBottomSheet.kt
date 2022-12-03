package com.rndeep.fns_fantoo.ui.menu.fantooclub.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.MoreBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * More menu
 */
@AndroidEntryPoint
class MoreBottomSheet(
    private val moreMenuItems: List<MoreMenuItem>,
    private val onItemSelected: (MoreMenuItem) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: MoreBottomSheetBinding
    private lateinit var moreMenuListAdapter: MoreMenuListAdapter
    private lateinit var behavior: BottomSheetBehavior<View>
    private val viewModel: MoreBottomSheetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        moreMenuListAdapter = MoreMenuListAdapter { moreMenuItem ->
            Timber.d("selected item: $moreMenuItem")
            viewModel.updateMoreMenuItem(moreMenuItem)
            onItemSelected(moreMenuItem)
        }

        binding = MoreBottomSheetBinding.inflate(inflater, container, false).apply {
            menuList.run {
                adapter = moreMenuListAdapter
                setHasFixedSize(true)
                itemAnimator = null
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        behavior = BottomSheetBehavior.from(binding.bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        viewModel.initMoreMenuItems(moreMenuItems)

        viewModel.moreMenuItems.observe(viewLifecycleOwner) { list ->
            moreMenuListAdapter.submitList(list.toList())
            viewModel.updateCheckPeekHeight()
        }

        viewModel.checkPeekHeight.observe(viewLifecycleOwner) {
            behavior.peekHeight = binding.root.height
            Timber.d("peekHeight: ${behavior.peekHeight} ")
        }
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialog
    }

    companion object {
        val TAG = MoreBottomSheet::class.qualifiedName
    }
}