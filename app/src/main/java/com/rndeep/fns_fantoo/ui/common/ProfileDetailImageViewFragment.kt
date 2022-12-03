package com.rndeep.fns_fantoo.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.CommonProfileDetailImageviewFragmentBinding
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_IMAGE_URL

class ProfileDetailImageViewFragment : Fragment() {

    private var _binding : CommonProfileDetailImageviewFragmentBinding? = null
    val binding get() = _binding!!

    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CommonProfileDetailImageviewFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        val imageUrl = arguments?.getString(KEY_IMAGE_URL)
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(binding.ivProfileImage.context)
                .load(imageUrl)
                .error(R.drawable.profile_character_l)
                .into(binding.ivProfileImage)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}