package com.rndeep.fns_fantoo.ui.chatting.imageviewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

class ImageViewerFragment : Fragment() {

    val args by navArgs<ImageViewerFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    ImageViewerScreen(
                        imageUrl = args.imageUrl,
                        onClickCancel = { findNavController().popBackStack() }
                    )
                }
            }
        }
    }
}