package com.rndeep.fns_fantoo.ui.common

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentProfileImageViewerBinding
import com.rndeep.fns_fantoo.utils.setProfileAvatar
import com.rndeep.fns_fantoo.utils.setStatusBar

class ProfileImageViewerFragment : Fragment() {

    private lateinit var binding: FragmentProfileImageViewerBinding
    private val args: ProfileImageViewerFragmentArgs by navArgs()

    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f
    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor

            // Don't let the object get too small or too large.
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 2.0f))

            binding.image.scaleX = scaleFactor
            binding.image.scaleY = scaleFactor

            return true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileImageViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBar(R.color.black, false)
        setProfileAvatar(binding.image, args.imageUrl)

        scaleGestureDetector = ScaleGestureDetector(requireContext(), scaleListener)

        binding.root.setOnTouchListener { _, motionEvent ->
            scaleGestureDetector.onTouchEvent(motionEvent)
        }

        binding.back.setOnClickListener {
            binding.root.findNavController().navigateUp()
        }
    }
}