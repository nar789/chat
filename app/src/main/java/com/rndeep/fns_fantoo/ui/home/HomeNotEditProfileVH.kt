package com.rndeep.fns_fantoo.ui.home

import android.view.View
import androidx.core.text.HtmlCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.GenderType
import com.rndeep.fns_fantoo.data.local.model.MyProfile
import com.rndeep.fns_fantoo.databinding.CategoryHomeNotProfileEditBinding
import com.rndeep.fns_fantoo.ui.menu.MenuFragment

class HomeNotEditProfileVH(private val binding: CategoryHomeNotProfileEditBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(profileInfo : MyProfile?){
        if(profileInfo!=null){
            binding.tvProfileSubTitle.text=HtmlCompat.fromHtml(
                itemView.context.getString(R.string.complete_profile_explain_text),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            checkProfileCompleteCount(profileInfo)
        }
        binding.rlEditProfileCard.setOnClickListener {
            itemView.findNavController().navigate(R.id.action_homeTabFragment_to_editProfileFragment3)
        }
    }


    private fun checkProfileCompleteCount(profile: MyProfile) {
        var count = MenuFragment.MINIMUM_PROFILE_COUNT
        if (!profile.imageUrl.isNullOrEmpty()) count++
        if (profile.gender != GenderType.UNKNOWN) count++
        if (!profile.concern.isNullOrEmpty()) count++
        if (!profile.birthday.isNullOrEmpty()) count++
        binding.segmentProgressProfileEdit.setDivisions(MenuFragment.COMPLETE_PROFILE_COUNT)
        binding.segmentProgressProfileEdit.setEnabledDivisions(count)
        setCompleteProfileNumber(count)
    }

    private fun setCompleteProfileNumber(num: Int) {
        val number = HtmlCompat.fromHtml(
            String.format(itemView.context.getString(R.string.complete_profile_number_text), num),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        binding.tvCompleteCount.text = number

    }

}