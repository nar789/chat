package com.rndeep.fns_fantoo.ui.chatting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentChattingBinding

class ChattingFragment : Fragment() {

    private var _binding : FragmentChattingBinding? =null
    val binding : FragmentChattingBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChattingBinding.inflate(inflater,container,false)

        return binding.root
    }

}