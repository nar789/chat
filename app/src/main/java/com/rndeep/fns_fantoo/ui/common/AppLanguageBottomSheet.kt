package com.rndeep.fns_fantoo.ui.common

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.toLanguageDTO
import com.rndeep.fns_fantoo.data.remote.model.Language
import com.rndeep.fns_fantoo.data.remote.model.toLanguageEntity
import com.rndeep.fns_fantoo.databinding.FragmentBottomAppLanguageSheetBinding
import com.rndeep.fns_fantoo.ui.common.adapter.AppSupportLanguageBottomSheetAdapter
import com.rndeep.fns_fantoo.ui.common.viewmodel.LanguageBottomSheetViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.stream.Collectors

@AndroidEntryPoint
class AppLanguageBottomSheet : BottomSheetDialogFragment() {

    private var _binding : FragmentBottomAppLanguageSheetBinding? = null
    private val binding get() = _binding!!
    private val languagesViewModel by viewModels<LanguageBottomSheetViewModel>()

    var title : String = ""

    var selectLanguageCode : String = ""
    private var currentAppLangCode : String = ""

    interface ItemClickListener{
        fun onItemClick(item: Language)
    }

    var itemClickListener: ItemClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    //full screen option
    //override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //val bottomSheetDialog = BottomSheetDialog(require
        /*bottomSheetDialog?.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                //behaviour?.skipCollapsed = true
                behaviour?.isHideable  = true
                behaviour?.peekHeight = Resources.getSystem().displayMetrics.heightPixels
            }
        }*/
        //return bottomSheetDialog
   // }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomAppLanguageSheetBinding.inflate(inflater, container, false)
        lifecycleScope.launch{
            val appLangCode = languagesViewModel.getLanguageCode()
            appLangCode?.let { langCode ->
                currentAppLangCode = langCode
                languagesViewModel.languageList.observe(viewLifecycleOwner
                ) { list ->

                    if (list != null) {
                        lifecycleScope.launch{
                            languagesViewModel.addAllLanguageDB(list.stream().map(Language::toLanguageEntity).collect(Collectors.toList()))
                        }
                        initLanguageAdapter(list)
                    }
                }

                languagesViewModel.languageEntityList.observe(viewLifecycleOwner){
                    list ->
                    Timber.d("languageList getCountryAllDB $list")
                    if(list != null && list.isNotEmpty()){
                        initLanguageAdapter(list.stream().map(com.rndeep.fns_fantoo.data.local.model.Language::toLanguageDTO).collect(Collectors.toList()))
                    }else{
                        lifecycleScope.launch {
                            languagesViewModel.getSupportLanguageAll()
                        }
                    }
                }
                languagesViewModel.getSupportLanguageAllFromDB()
            }
        }
        binding.rcBottomSheet.layoutManager=
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        if(title.isNotEmpty()){
            binding.tvTitle.text = title
            binding.tvTitle.visibility = View.VISIBLE
        }
        return binding.root
    }


    private fun initLanguageAdapter(languageList: List<Language>){
        val adapater = AppSupportLanguageBottomSheetAdapter()
        itemClickListener?.let {
            adapater.itemClickListener = it
        }
        adapater.setItemsList(languageList)
        if (selectLanguageCode.isNotEmpty()) {
            for (languageItem in languageList) {
                if (languageItem.langCode == selectLanguageCode) {
                    adapater.setSelectedLanguageItem(languageItem)
                    break
                }
            }
        }
        if(currentAppLangCode.isNotEmpty()){
            adapater.setCurrentAppLangCode(currentAppLangCode)
        }
        binding.rcBottomSheet.adapter=adapater
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}