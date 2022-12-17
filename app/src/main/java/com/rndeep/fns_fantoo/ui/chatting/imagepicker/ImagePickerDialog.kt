package com.rndeep.fns_fantoo.ui.chatting.imagepicker

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rndeep.fns_fantoo.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ImagePickerDialog @Inject constructor() : BottomSheetDialogFragment() {

    val viewModel: ImagePickerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    ImagePickerScreen(
                        onClickCancel = { dismissAllowingStateLoss() },
                        onClickDone = { viewModel.sendImages(); dismissAllowingStateLoss() },
                        onClickImage = { viewModel.selectImage(it) },
                        images = viewModel.imageList.collectAsLazyPagingItems(),
                        checkedImages = viewModel.checkedImages
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadImageList(requireActivity().contentResolver)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), R.style.TransparentDialog)

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { parent ->
                val behaviour = BottomSheetBehavior.from(parent)
                setupFullHeight(parent)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                behaviour.isDraggable = false
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }
}