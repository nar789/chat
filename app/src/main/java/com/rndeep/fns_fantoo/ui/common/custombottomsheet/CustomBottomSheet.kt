package com.rndeep.fns_fantoo.ui.common.custombottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentBottomSheetBinding
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetAdapter
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetItem

class CustomBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetBinding? = null;
    private val binding get() = _binding!!

    private val bottomSheetAdapter = BottomSheetAdapter()
    private var itemClickListener: BottomSheetAdapter.OnItemClickListener? = null

    //Sheet 타이틀
    private var sheetTitle = ""
    private var sheetSubTitle = ""
    private var subTextColor :Int? = null
    private var guidelineRatio = 0.29f



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        bottomSheetAdapter.setOnItemClickListener(itemClickListener)
        bottomSheetAdapter.setGuideLineRatio(guidelineRatio)
        binding.rcBottomSheet.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rcBottomSheet.adapter = bottomSheetAdapter
        setTitleView()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        WindowInsetsControllerCompat(dialog?.window!!,binding.root).isAppearanceLightNavigationBars=true
    }

    fun getTextList() = bottomSheetAdapter.getItem()

    private fun setTitleView() {
        if (sheetTitle == "") {
            binding.tvSheetTitle.visibility = View.GONE
        } else {
            binding.tvSheetTitle.visibility = View.VISIBLE
            binding.tvSheetTitle.text = sheetTitle
        }
        if (sheetSubTitle == "") {
            binding.tvSubTitle.visibility = View.GONE
        } else {
            binding.tvSubTitle.visibility = View.VISIBLE
            binding.tvSubTitle.text = sheetSubTitle
        }
        if(subTextColor !=null){
            binding.tvSubTitle.setTextColor(subTextColor!!)
        }
    }

    fun setTitleText(title: String) {
        sheetTitle = title
    }

    fun setSubTitleText(subText: String) {
        sheetSubTitle = subText
    }

    fun setSubTitleTextColor(@ColorInt colorInt :Int){
        subTextColor = colorInt
    }

    fun setOnCliCkListener(itemClickListener: BottomSheetAdapter.OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    fun setBottomItems(items: ArrayList<BottomSheetItem>) {
        var textMaxLength = 0
        for (a in items) {
            if (textMaxLength < a.itemName.length) textMaxLength = a.itemName.length
        }
        setTextRatio(textMaxLength)
        bottomSheetAdapter.setItems(items)
    }


    fun setTextRatio(textlength: Int) {
        when {
            textlength < 4 -> {
                guidelineRatio = 0.29f
            }
            textlength >= 4 -> {
                guidelineRatio = 0.29f + (textlength - 3) * 0.02f
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}