package com.rndeep.fns_fantoo.ui.club.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.ui.common.RecyclerViewItemClickListener
import com.rndeep.fns_fantoo.databinding.FragmentBottomMenuBinding
import com.rndeep.fns_fantoo.ui.club.settings.adapter.MenuBottomAdapter
import com.rndeep.fns_fantoo.ui.club.settings.data.MenuItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuBottomSheetDialogFragment()  : BottomSheetDialogFragment() {

    private var _binding:FragmentBottomMenuBinding? = null
    private val binding get() = _binding!!

    private var menuItemArrayList: ArrayList<MenuItem> = ArrayList()
    var currentMenuItem:MenuItem? = null
    private var menuItemClickListener: RecyclerViewItemClickListener? = null
    var title:String = ""

    constructor(menuItemArrayList: ArrayList<MenuItem>):this(){
        this.menuItemArrayList = menuItemArrayList
    }

    fun setOnMenuItemClickListener(itemClickListener: RecyclerViewItemClickListener) = apply{
        menuItemClickListener = itemClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomMenuBinding.inflate(inflater, container, false)

        val adapter = MenuBottomAdapter(menuItemArrayList)
        currentMenuItem?.let {
            adapter.setSelectMenuItem(it)
        }

        menuItemClickListener?.let {
            adapter.setOnMenuItemClickListener(object : RecyclerViewItemClickListener {
                override fun onItemClick(objects: Any) {
                    menuItemClickListener?.onItemClick(objects)
                    dismiss()
                }
            })
        }


        if(title.isNotEmpty()){
            binding.tvTitle.text = title
            binding.tvTitle.visibility = View.VISIBLE
        }
        binding.rcBottomSheet.layoutManager=
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        binding.rcBottomSheet.adapter = adapter
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}