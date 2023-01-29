package com.rndeep.fns_fantoo.ui.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.BottomSheetEditorBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class EditorBottomSheet(
    private val boardItems: List<BoardItem>,
    private val onItemSelected: (BoardItem) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetEditorBinding
    private lateinit var selectListAdapter: SelectListAdapter
    private lateinit var behavior: BottomSheetBehavior<View>
    private val viewModel: EditorBottomSheetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        selectListAdapter = SelectListAdapter { boardItem ->
            Timber.d("selected item: $boardItem")
            viewModel.updateBoardItem(boardItem)
            onItemSelected(boardItem)
        }
        binding = BottomSheetEditorBinding.inflate(inflater, container, false).apply {
            selectList.run {
                adapter = selectListAdapter
                setHasFixedSize(true)
                itemAnimator = null
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        behavior = from(binding.bottomSheet)
        behavior.state = STATE_EXPANDED

        viewModel.initBoardItem(boardItems)

        viewModel.bottomSheetBoardItems.observe(viewLifecycleOwner) { list ->
            selectListAdapter.submitList(list.map { it.copy() })
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
        val TAG = EditorBottomSheet::class.qualifiedName
    }
}