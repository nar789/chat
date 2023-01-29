package com.rndeep.fns_fantoo.ui.regist

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import com.rndeep.fns_fantoo.databinding.CongrationDialogBinding
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.utils.SizeUtils

class CongrationDialog() : CommonDialog() {

    private var _binding: CongrationDialogBinding? = null
    private val binding get() = _binding!!

    private constructor(builder: Builder) : this() {
        this.builder = builder
    }

    override fun initView() {
        binding.tvTitle.text = builder.title
        binding.tvTitleOption.text = builder.titleOption
        binding.tvTitleOption.setOnClickListener { dismiss() }
        binding.tvMessage.text = builder.message
        binding.tvTailMessage.text = builder.tailMessage
        binding.btnOneButtonPositive.text = builder.positiveButtonString
        binding.btnOneButtonPositive.setOnClickListener { onPositiveButtonClick() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        dialog?.window?.statusBarColor = Color.TRANSPARENT
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            _binding = CongrationDialogBinding.inflate(layoutInflater, null, false)
            initView()
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = WindowManager.LayoutParams.MATCH_PARENT
            val height = WindowManager.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            val rootLayoutParams = binding.rlRoot.layoutParams

            var titleBarHeight = 0
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                titleBarHeight = resources.getDimensionPixelSize(resourceId)
            }
            rootLayoutParams.height =
                context?.let { SizeUtils.getDeviceSize(it).height - titleBarHeight }!!
            //Timber.d("height = ${rootLayoutParams.height}, titlebar = $titleBarHeight")
            binding.rlRoot.layoutParams = rootLayoutParams
        }
    }

    class Builder : CommonDialog.Builder() {

        override fun build() = CongrationDialog(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}