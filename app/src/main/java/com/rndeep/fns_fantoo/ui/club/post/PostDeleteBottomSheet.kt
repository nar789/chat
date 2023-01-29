package com.rndeep.fns_fantoo.ui.club.post

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.PostDeleteBottomSheetBinding

class PostDeleteBottomSheet : BottomSheetDialogFragment() {

    private var _binding: PostDeleteBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var listener : DeleteSheetClickListener? =null

    private var isOpenDeleteReason =true

    private var reasonText =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PostDeleteBottomSheetBinding.inflate(inflater,container,false)

        binding.switchDeleteReason.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                binding.iBtnDelete.backgroundTintList= ColorStateList.valueOf(requireContext().getColor(R.color.state_enable_gray_200))
                binding.edtDeleteReason.hint = getString(R.string.post_delete_reason_hint)
            }else{
                binding.edtDeleteReason.setText("")
                binding.edtDeleteReason.hint = getString(R.string.post_delete_no_reason_message)
                binding.iBtnDelete.backgroundTintList= ColorStateList.valueOf(requireContext().getColor(R.color.state_active_primary_default))
            }
            binding.edtDeleteReason.isEnabled=b
            isOpenDeleteReason=b
        }

        binding.edtDeleteReason.doOnTextChanged { text, start, before, count ->
            if(binding.switchDeleteReason.isChecked){
                reasonText = if(text.isNullOrEmpty()){
                    binding.iBtnDelete.backgroundTintList= ColorStateList.valueOf(requireContext().getColor(R.color.state_enable_gray_200))
                    binding.iBtnDelete.isClickable=false
                    ""
                }else{
                    binding.iBtnDelete.backgroundTintList= ColorStateList.valueOf(requireContext().getColor(R.color.state_active_primary_default))
                    binding.iBtnDelete.isClickable=true
                    text.toString()
                }
            }
        }

        binding.iBtnDelete.setOnClickListener {
            listener?.onDeleteClick(
                reasonText,
                isOpenDeleteReason
            )
            dismiss()
        }

        binding.iBtnCancel.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    fun setOnDeleteClickListener(listener: DeleteSheetClickListener){
        this.listener=listener
    }

    interface DeleteSheetClickListener{
        fun onDeleteClick(deleteReason :String,isOpenReason :Boolean)
    }

}