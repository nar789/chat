package com.rndeep.fns_fantoo.ui.menu.fantooclub.more.report

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
 * ReportBottomSheet
 */
@AndroidEntryPoint
class ReportBottomSheet(
    private val reportMessageItems: List<ReportMessageItem>,
    private val onItemSelected: (ReportMessageItem) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: MoreBottomSheetBinding
    private lateinit var reportMessageListAdapter: ReportMessageListAdapter
    private lateinit var behavior: BottomSheetBehavior<View>
    private val viewModel: ReportBottomSheetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        reportMessageListAdapter = ReportMessageListAdapter { reportMessageItem ->
            Timber.d("selected item: $reportMessageItem")
            viewModel.updateReportMessageItems(reportMessageItem)
            onItemSelected(reportMessageItem)
        }

        binding = MoreBottomSheetBinding.inflate(inflater, container, false).apply {
            menuList.run {
                adapter = reportMessageListAdapter
                setHasFixedSize(true)
                itemAnimator = null
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        behavior = BottomSheetBehavior.from(binding.bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        viewModel.initReportMessageItems(reportMessageItems)

        viewModel.reportMessageItems.observe(viewLifecycleOwner) { list ->
            reportMessageListAdapter.submitList(list.toList())
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
        val TAG = ReportBottomSheet::class.qualifiedName
    }
}