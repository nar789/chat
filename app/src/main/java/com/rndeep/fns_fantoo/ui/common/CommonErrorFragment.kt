package com.rndeep.fns_fantoo.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rndeep.fns_fantoo.databinding.FragmentCommonErrorBinding

class CommonErrorFragment : Fragment()  {//공통UI15, 차후 필요시 사용

    private lateinit var binding : FragmentCommonErrorBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommonErrorBinding.inflate(inflater, container, false)
        binding.toolbar.setOnClickListener { findNavController().popBackStack() }
        binding.btnErrorPrevious.setOnClickListener { findNavController().popBackStack() }
        return binding.root
    }
}