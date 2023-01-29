package com.rndeep.fns_fantoo.ui.club.detail

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.clubsetting.ClubDelegatingInfoDto
import com.rndeep.fns_fantoo.databinding.TabClubPageMasterDelefateDialogBinding
import com.rndeep.fns_fantoo.utils.TimeUtils

class ClubMasterDelegateDialog(
    private val delegateInfo :ClubDelegatingInfoDto,
    private val clubName : String,
    private val listener : DelegateClickListener
) : DialogFragment() {

    private var _binding : TabClubPageMasterDelefateDialogBinding? =null
    private val binding get() = _binding!!

    private var fstCheck = false
    private var secCheck = false

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.tab_club_page_master_delefate_dialog,container,false)
        _binding = TabClubPageMasterDelefateDialogBinding.bind(view)
        settingViewValue()
        acceptButtonState(fstCheck && secCheck)
        return binding.root
    }

    private fun settingViewValue(){
        val messageText =getString(R.string.se_k_delegate_club_to_you,clubName)
        val s =SpannableString(messageText).apply {
            setSpan(
                ForegroundColorSpan(requireContext().getColor(R.color.primary_500)),
                messageText.indexOf("["),
                messageText.indexOf("]"),
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.tvDelegateExplain.text=s

        if (delegateInfo.expectCompleteDate != null) {
            val delegateAcceptDate = TimeUtils.changeTimeStringFormat(
                delegateInfo.expectCompleteDate,
                "{yyyy}.{MM}.{dd}",
                "yyyy-MM-dd"
            )
            binding.tvFirstCheckMessage.text = requireContext().getString(
                R.string.se_k_delegating_complete_when_agree,
                delegateAcceptDate
            )
        }
        if(delegateInfo.expectCancelDate!=null) {
            val delegateCancelDate = TimeUtils.changeTimeStringFormat(
                delegateInfo.expectCancelDate,
                "{yyyy}.{MM}.{dd}",
                "yyyy-MM-dd"
            )
            binding.tvFirstCheckSubMessage.text = requireContext().getString(
                R.string.se_d_delegating_to_cancel_possible_by_manager,
                delegateCancelDate
            )
        }
        binding.firstCheck.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                binding.firstCheck.setBackgroundResource(R.drawable.checkbox_round_check)
            }else{
                binding.firstCheck.setBackgroundResource(R.drawable.checkbox_round_uncheck)
            }
            fstCheck=b
            acceptButtonState(fstCheck && secCheck)
        }
        binding.secondCheck.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                binding.secondCheck.setBackgroundResource(R.drawable.checkbox_round_check)
            }else{
                binding.secondCheck.setBackgroundResource(R.drawable.checkbox_round_uncheck)
            }
            secCheck=b
            acceptButtonState(fstCheck && secCheck)
        }
        binding.btnAcceptDelegate.setOnClickListener {
            val completeDate =TimeUtils.changeTimeStringFormat(delegateInfo.delegateCompleteDate,"{yyyy}.{MM}.{dd}",)
            listener.onAcceptDelegate(completeDate)
            dismiss()
        }
        binding.btnRejectDelegate.setOnClickListener {
            listener.onRejectDelegate()
            dismiss()
        }
        binding.ivCloseDialog.setOnClickListener {
            dismiss()
        }
    }

    private fun acceptButtonState(isAllCheck : Boolean){
        context?.let {
            if(isAllCheck){
                binding.btnAcceptDelegate.setBackgroundColor(it.getColor(R.color.state_enable_primary_default))
            }else{
                binding.btnAcceptDelegate.setBackgroundColor(it.getColor(R.color.state_disabled_gray_200))
            }
        }
        binding.btnAcceptDelegate.isClickable=isAllCheck
    }


    interface DelegateClickListener{
        fun onAcceptDelegate(completeDate :String)
        fun onRejectDelegate()
    }

}