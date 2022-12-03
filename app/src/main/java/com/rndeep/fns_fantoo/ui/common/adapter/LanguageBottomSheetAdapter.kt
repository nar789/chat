package com.rndeep.fns_fantoo.ui.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.Country
import com.rndeep.fns_fantoo.ui.common.LanguageBottomSheet
import com.rndeep.fns_fantoo.databinding.BottomLanguageSheetItemBinding
import com.rndeep.fns_fantoo.utils.LanguageUtils

class LanguageBottomSheetAdapter : RecyclerView.Adapter<LanguageBottomSheetAdapter.BottomSheetVH>() {
    private var bottomSheetItems = mutableListOf<Country>()

    var itemClickListener : LanguageBottomSheet.ItemClickListener? = null
    private var selectedItem: Country? = null
    private var currentAppLangCode:String = ""

    inner class BottomSheetVH(private val binding : BottomLanguageSheetItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : Country){
            binding.tvLang.isEnabled = false
            binding.tvLang.text = if(currentAppLangCode == LanguageUtils.KOREAN)item.nameKr else item.nameEn
            if(selectedItem != null && item == selectedItem){
                binding.tvLang.isEnabled = true
                binding.ivCheck.visibility = View.VISIBLE
            }else{
                binding.ivCheck.visibility = View.GONE
            }
            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bottom_language_sheet_item, parent,false)
        return BottomSheetVH(BottomLanguageSheetItemBinding.bind(view))
    }

    override fun onBindViewHolder(holder: BottomSheetVH, position: Int) {
        if(holder.bindingAdapterPosition==RecyclerView.NO_POSITION){
            return
        }
        holder.bind(bottomSheetItems[holder.bindingAdapterPosition])
    }

    override fun getItemCount(): Int {
        return bottomSheetItems.size
    }

    fun setItemsList(items:List<Country>){
        bottomSheetItems.clear()
        bottomSheetItems.addAll(items)
    }

    fun setSelectedLanguageItem(country: Country){
        selectedItem = country
    }

    fun setCurrentAppLangCode(langCode:String){
        currentAppLangCode = langCode
    }
}