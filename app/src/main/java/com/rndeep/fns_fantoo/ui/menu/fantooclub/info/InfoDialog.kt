package com.rndeep.fns_fantoo.ui.menu.fantooclub.info

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.DialogInfoBinding

class InfoDialog(
    private val info: DialogInfo,
    private val onItemClicked: (InfoDialogClickType) -> Unit
) : AppCompatDialogFragment() {

    private lateinit var binding: DialogInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogInfoBinding.inflate(layoutInflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.followBtn.setOnClickListener {
            onItemClicked(InfoDialogClickType.FOLLOW)
            if (info.isLogin) {
                val isFollow = info.isFollow
                info.isFollow = !isFollow
                changeFollowBtn(info.isFollow)
            }
        }

        binding.shareBtn.setOnClickListener {
            onItemClicked(InfoDialogClickType.SHARE)
        }

        binding.closeBtn.setOnClickListener {
            onItemClicked(InfoDialogClickType.CLOSE)
        }

        binding.title.text = info.title
        binding.logo.setImageResource(info.logo)
        binding.desc.text = info.info
        binding.followerCount.text = info.followerCnt
        changeFollowBtn(info.isFollow)
    }

    private fun changeFollowBtn(isFollow: Boolean) {
        if (isFollow) {
            binding.followBtn.apply {
                backgroundTintList =
                    ColorStateList.valueOf(requireContext().getColor(R.color.bg_light_gray_50))
                strokeColor = ColorStateList.valueOf(requireContext().getColor(R.color.primary_500))
                setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_500))
                text = getString(R.string.p_following)
            }
        } else {
            binding.followBtn.apply {
                backgroundTintList =
                    ColorStateList.valueOf(requireContext().getColor(R.color.primary_default))
                strokeColor = ColorStateList.valueOf(requireContext().getColor(R.color.primary_100))
                setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_25))
                text = getString(R.string.p_follow)
            }
        }
    }

    companion object {
        const val DIALOG_INFO = "dialog_info"
    }
}