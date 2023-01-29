package com.rndeep.fns_fantoo.ui.menu.editprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.GenderType
import com.rndeep.fns_fantoo.databinding.BottomSheetGenderBinding

class GenderBottomSheet(
    private val gender: GenderType,
    private val onItemClicked: (GenderType) -> Unit
) : BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetGenderBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetGenderBinding.inflate(inflater, container, false).apply {
            male.setOnClickListener {
                onClicked(GenderType.MALE)
            }
            female.setOnClickListener {
                onClicked(GenderType.FEMALE)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setGenderIcon(gender)
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialog
    }

    private fun onClicked(genderType: GenderType) {
        onItemClicked(genderType)
    }

    private fun setGenderIcon(genderType: GenderType) {
        when(genderType) {
            GenderType.MALE -> {
                binding.maleCheck.visibility = View.VISIBLE
                binding.male.setTextColor(requireContext().getColor(R.color.primary_default))
            }
            GenderType.FEMALE -> {
                binding.femaleCheck.visibility = View.VISIBLE
                binding.female.setTextColor(requireContext().getColor(R.color.primary_default))
            }
            else -> {
                binding.maleCheck.visibility = View.GONE
                binding.femaleCheck.visibility = View.GONE
            }
        }
    }

    companion object {
        val TAG = GenderBottomSheet::class.qualifiedName
    }
}