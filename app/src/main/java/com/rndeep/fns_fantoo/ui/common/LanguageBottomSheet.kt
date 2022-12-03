package com.rndeep.fns_fantoo.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.toCountryDTO
import com.rndeep.fns_fantoo.data.remote.model.Country
import com.rndeep.fns_fantoo.data.remote.model.toCountryEntity
import com.rndeep.fns_fantoo.databinding.FragmentBottomLanguageSheetBinding
import com.rndeep.fns_fantoo.ui.common.adapter.LanguageBottomSheetAdapter
import com.rndeep.fns_fantoo.ui.common.viewmodel.LanguageBottomSheetViewModel
import com.rndeep.fns_fantoo.utils.LanguageUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.stream.Collectors

@AndroidEntryPoint
class LanguageBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomLanguageSheetBinding? = null
    private val binding get() = _binding!!
    private val languagesViewModel by viewModels<LanguageBottomSheetViewModel>()

    var title: String = ""

    var selectLanguageCode: String = ""
    private var currentAppLangCode: String = ""

    interface ItemClickListener {
        fun onItemClick(item: Country)
    }

    var itemClickListener: ItemClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomLanguageSheetBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            val appLangCode = languagesViewModel.getLanguageCode()
            appLangCode?.let { langCode ->
                currentAppLangCode = langCode
                languagesViewModel.countryAllList.observe(
                    viewLifecycleOwner
                ) { countryList ->
                    if (countryList != null) {
                        lifecycleScope.launch {
                            languagesViewModel.addAllCountry(
                                countryList.stream().map(Country::toCountryEntity).collect(
                                    Collectors.toList()
                                )
                            )
                        }
                        initAdapter(countryList.sortedBy { if (currentAppLangCode == LanguageUtils.KOREAN) it.nameKr else it.nameEn })
                    }
                }
                languagesViewModel.countryAllEntityList.observe(viewLifecycleOwner) { countryEntityList ->
                    //Timber.d("getCountryAllDB $countryEntityList")
                    if (countryEntityList != null && countryEntityList.isNotEmpty()) {
                        val countryDTOList = countryEntityList.stream()
                            .map(com.rndeep.fns_fantoo.data.local.model.Country::toCountryDTO)
                            .collect(
                                Collectors.toList()
                            )
                        initAdapter(countryDTOList.sortedBy { if (currentAppLangCode == LanguageUtils.KOREAN) it.nameKr else it.nameEn })
                    } else {
                        lifecycleScope.launch {
                            languagesViewModel.getCountryAll()
                        }
                    }
                }
                languagesViewModel.getCountryAllFromDB()
            }
        }
        binding.rcBottomSheet.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        if (title.isNotEmpty()) {
            binding.tvTitle.text = title
            binding.tvTitle.visibility = View.VISIBLE
        }
        return binding.root
    }

    private fun initAdapter(countryList: List<Country>) {
        val adapater = LanguageBottomSheetAdapter()
        itemClickListener?.let {
            adapater.itemClickListener = it
        }
        adapater.setItemsList(countryList)
        if (selectLanguageCode.isNotEmpty()) {
            for (country in countryList) {
                if (country.iso2 == selectLanguageCode) {
                    adapater.setSelectedLanguageItem(country)
                    break
                }
            }
        }
        if (currentAppLangCode.isNotEmpty()) {
            adapater.setCurrentAppLangCode(currentAppLangCode)
        }
        binding.rcBottomSheet.adapter = adapater
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}