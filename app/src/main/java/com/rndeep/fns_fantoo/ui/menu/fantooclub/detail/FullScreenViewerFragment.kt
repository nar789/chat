package com.rndeep.fns_fantoo.ui.menu.fantooclub.detail

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.util.Util
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentFullScreenViewerBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Youtube Full Screen Viewer UI
 */
@AndroidEntryPoint
class FullScreenViewerFragment : Fragment() {

    private lateinit var binding: FragmentFullScreenViewerBinding

    private val videoInfoViewModel: VideoInfoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFullScreenViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        hideSystemUi()

        videoInfoViewModel.initializePlayer(requireContext(), binding.playerView)

        binding.playerView.findViewById<ImageButton>(R.id.exo_fullscreen).setImageResource(R.drawable.icon_outline_reduce)

        binding.playerView.setFullscreenButtonClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || videoInfoViewModel.getExoPlayer() == null) {
            videoInfoViewModel.initializePlayer(requireContext(), binding.playerView)
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT > 23) {
            videoInfoViewModel.releasePlayer()
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (Util.SDK_INT > 23) {
            videoInfoViewModel.releasePlayer()
        }
        showSystemUi()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        val window = requireActivity().window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    @SuppressLint("InlinedApi")
    private fun showSystemUi() {
        val window = requireActivity().window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).show(WindowInsetsCompat.Type.systemBars())
    }
}