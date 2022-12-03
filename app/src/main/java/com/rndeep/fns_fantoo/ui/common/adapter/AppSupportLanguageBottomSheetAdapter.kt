package com.rndeep.fns_fantoo.ui.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.Language
import com.rndeep.fns_fantoo.databinding.BottomLanguageSheetItemBinding
import com.rndeep.fns_fantoo.ui.common.AppLanguageBottomSheet
import com.rndeep.fns_fantoo.utils.LanguageUtils

class AppSupportLanguageBottomSheetAdapter : RecyclerView.Adapter<AppSupportLanguageBottomSheetAdapter.BottomSheetVH>() {
    private var bottomSheetItems = mutableListOf<Language>()

    var itemClickListener : AppLanguageBottomSheet.ItemClickListener? = null
    private var selectedItem: Language? = null
    private var currentAppLangCode :String = ""

    inner class BottomSheetVH(private val binding : BottomLanguageSheetItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : Language){
            binding.tvLang.isEnabled = false
            binding.tvLang.text = if(currentAppLangCode == LanguageUtils.KOREAN) item.nameKr else item.name
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
        if(holder.bindingAdapterPosition== RecyclerView.NO_POSITION){
            return
        }
        holder.bind(bottomSheetItems[holder.bindingAdapterPosition])
    }

    override fun getItemCount(): Int {
        return bottomSheetItems.size
    }

    fun setItemsList(items:List<Language>){
        bottomSheetItems.clear()
        bottomSheetItems.addAll(items)
    }

    fun setSelectedLanguageItem(languageItem: Language){
        selectedItem = languageItem
    }

    fun setCurrentAppLangCode(langCode:String){
        currentAppLangCode = langCode
    }
}