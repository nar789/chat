package com.rndeep.fns_fantoo.ui.common

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.viewpager2.widget.ViewPager2
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentImageListViewerBinding
import com.rndeep.fns_fantoo.ui.common.adapter.ImageListViewerAdapter
import com.rndeep.fns_fantoo.utils.setProfileAvatar
import com.rndeep.fns_fantoo.utils.setStatusBar

class ImageListViewerFragment : Fragment() {

    var _binding :FragmentImageListViewerBinding? = null
    val binding get() = _binding!!

    private val imageAdapter = ImageListViewerAdapter()

    private val getArg :ImageListViewerFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentImageListViewerBinding.inflate(inflater,container,false)

        binding.rcImage.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                binding.tvImageCount.text="${position+1}/${getArg.imageUrlList?.size?:0}"
            }
        })

        return _binding?.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBar(R.color.black, false)

        imageAdapter.setItem(getArg.imageUrlList?.toList()?: listOf())
        binding.rcImage.adapter=imageAdapter
        binding.rcImage.currentItem = getArg.imagePos
        binding.ivBack.setOnClickListener {
            binding.root.findNavController().popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

}