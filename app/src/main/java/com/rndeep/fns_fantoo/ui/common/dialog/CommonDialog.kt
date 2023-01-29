package com.rndeep.fns_fantoo.ui.common.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.*
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.CommonDialogBinding
import com.rndeep.fns_fantoo.utils.SizeUtils
import timber.log.Timber

open class CommonDialog() : DialogFragment(){

    protected lateinit var builder: Builder
    private var _binding:CommonDialogBinding? = null
    private val binding get() = _binding!!

    private constructor(builder: Builder) : this() {
        this.builder = builder
    }

    protected open fun initView(){
        binding.rlRoot.setOnClickListener {
            builder.cancelListener?.onCancel()
            if(builder.isCanceledOnTouchOutside){
                dismiss()
            }
        }
        if(!builder.title.isNullOrEmpty()) {
            binding.tvTitle.text = builder.title
            binding.tvTitle.visibility = View.VISIBLE
        }
        if(!builder.spannableTitle.isNullOrEmpty()) {
            binding.tvTitle.text = builder.spannableTitle
            binding.tvTitle.visibility = View.VISIBLE
        }
        if(!builder.titleOption.isNullOrEmpty()){
            binding.tvTitleOption.text = builder.titleOption
            binding.tvTitleOption.visibility = View.VISIBLE
            binding.tvTitleOption.setOnClickListener{onTitleOptionClick()}
        }
        if(!builder.message.isNullOrEmpty()) {
            binding.tvMessage.text = builder.message
            binding.tvMessage.visibility = View.VISIBLE
        }
        if(!builder.messageSpannable.isNullOrEmpty()){
            binding.tvMessage.setText(builder.messageSpannable, TextView.BufferType.SPANNABLE)
            binding.tvMessage.visibility = View.VISIBLE
        }
        if(!builder.tailMessage.isNullOrEmpty()){
            binding.tvTailMessage.text = builder.tailMessage
            binding.tvTailMessage.visibility = View.VISIBLE
        }

        if(builder.hasInputField){
            binding.rlInput.visibility = View.VISIBLE
            binding.etInput.addTextChangedListener(textWacher)
            if(builder.inputHint.isNotEmpty()){
                binding.etInput.hint = builder.inputHint
            }
            binding.flInputCancel.setOnClickListener {
                binding.etInput.setText("")
            }
        }

        if(!TextUtils.isEmpty(builder.negativeButtonString)){
            binding.llOneButton.visibility = View.GONE
            val isCustomSize = (builder.positiveButtonWidth != -1 || builder.negativeButtonWidth != -1)
            if(isCustomSize) {
                binding.llTwoButton.visibility = View.GONE
                binding.llCustomSizeTwoButton.visibility = View.VISIBLE
                if (builder.positiveButtonWidth != -1) {
                    context?.let {
                        binding.btnCSTwoButtonPositive.width = builder.positiveButtonWidth
                        binding.btnCSTwoButtonPositive.text = builder.positiveButtonString
                        binding.btnCSTwoButtonPositive.setOnClickListener { onPositiveButtonClick() }
                    }
                }
                if (builder.negativeButtonWidth != -1) {
                    context?.let {
                        binding.btnCSTwoButtonNegative.width = builder.negativeButtonWidth
                        binding.btnCSTwoButtonNegative.text = builder.negativeButtonString
                        binding.btnCSTwoButtonNegative.setOnClickListener { onNegativeButtonClick() }
                    }
                }
            }else {
                binding.llTwoButton.visibility = View.VISIBLE
                binding.btnTwoButtonPositive.text = builder.positiveButtonString
                binding.btnTwoButtonPositive.setOnClickListener { onPositiveButtonClick() }
                binding.btnTwoButtonNegative.text = builder.negativeButtonString
                binding.btnTwoButtonNegative.setOnClickListener { onNegativeButtonClick() }
            }
        }else{
            binding.btnOneButtonPositive.text = builder.positiveButtonString
            binding.btnOneButtonPositive.setOnClickListener{ onPositiveButtonClick() }
        }
        if(builder.hasInputField){
            binding.etInput.visibility = View.VISIBLE
        }

    }

    open fun onPositiveButtonClick(){
        if(builder.hasInputField){
            builder.inputFieldCallback?.onInputText(binding.etInput.text.toString())
        }
        builder.positiveButtonClickListener?.onClick()
        dialog?.dismiss()
    }

    private fun onNegativeButtonClick(){
        builder.negativeButtonClickListener?.onClick()
        dialog?.dismiss()
    }

    private fun onTitleOptionClick(){
        builder.titleOptionClickListener?.onClick()
        dialog?.dismiss()
    }

    private var textWacher = object :TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if(str?.length!! > 0){
                binding.flInputCancel.visibility = View.VISIBLE
            }else{
                binding.flInputCancel.visibility = View.INVISIBLE
            }
        }

        override fun afterTextChanged(p0: Editable?) {
        }

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

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            try{
                val width = WindowManager.LayoutParams.MATCH_PARENT
                val height = WindowManager.LayoutParams.MATCH_PARENT
                dialog.window!!.setLayout(width, height)
                val rootLayoutParams = binding.rlRoot.layoutParams

                var titleBarHeight = 0
                val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
                if (resourceId > 0) {
                    titleBarHeight = resources.getDimensionPixelSize(resourceId)
                }
                rootLayoutParams.height = context?.let { SizeUtils.getDeviceSize(it).height - titleBarHeight}!!
                //Timber.d("height = ${rootLayoutParams.height}, titlebar = $titleBarHeight")
                binding.rlRoot.layoutParams = rootLayoutParams
            }catch (e:Exception){
            }
        }
    }

    //override fun onResume() {
    //    super.onResume()
        //팝업 사이즈 여기서 호출해야함
        /*dialog?.window?.setLayout(
            resources.getDimensionPixelSize(R.dimen.common_dialog_width),
            ViewGroup.LayoutParams.WRAP_CONTENT)*/
    //}


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = CommonDialogBinding.inflate(layoutInflater, null, false)
            initView()
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)
            return builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    open class Builder{
        var isCanceledOnTouchOutside = true
        var positiveButtonClickListener: ClickListener? = null
        var negativeButtonClickListener: ClickListener? = null
        var titleOptionClickListener : ClickListener? = null
        var cancelListener : CancelListener? = null
        var inputFieldCallback: InputFieldCallBack? = null
        var title:String? = null
        var spannableTitle : SpannableString? =null
        var titleOption:String? = null
        var message:String? = null
        var tailMessage:String? = null
        var inputHint:String = ""
        var hasInputField:Boolean = false
        var messageSpannable: Spannable? = null

        var positiveButtonString:String = ""
        var negativeButtonString: String = ""

        /**
            @Important! assign pixel size
         */
        var positiveButtonWidth:Int = -1
        /**
            @Important! assign pixel size
         */
        var negativeButtonWidth:Int = -1

        fun title(title:String) = apply { this.title = title }
        fun spannableTitle(title:String) = apply { this.title = title }
        fun titleOption(titleOption:String) = apply { this.titleOption = titleOption }
        fun message(message: String) = apply { this.message = message }
        fun spannableMessage(message: String) = apply { this.message = message }
        fun tailMessage(message: String) = apply { this.tailMessage = message }
        fun setPositiveButtonText(text:String) = apply { this.positiveButtonString = text }
        fun setNegativeButtonText(text:String) = apply { this.negativeButtonString = text }
        fun setEnableInputField(enable:Boolean) = apply { this.hasInputField = enable}
        fun setInputFieldCallBack(inputFieldCallBack: InputFieldCallBack) = apply { this.inputFieldCallback = inputFieldCallBack }
        fun setInputFieldHintText(text:String) = apply {this.inputHint = text }
        fun setPositiveButtonClickListener(listener: ClickListener) = apply {this.positiveButtonClickListener = listener}
        fun setNegativeButtonClickListener(listener: ClickListener) = apply { this.negativeButtonClickListener = listener}
        fun setTitleOptionClickListener(listener: ClickListener) = apply { this.titleOptionClickListener = listener }
        fun setMessageSpannable(messageSpannable: Spannable) = apply { this.messageSpannable = messageSpannable }
        fun setCanceledOnTouchOutside(isCanceledOnTouchOutside: Boolean) = apply { this.isCanceledOnTouchOutside =  isCanceledOnTouchOutside}
        fun setOnCancelListener(cancelListener: CancelListener) = apply { this.cancelListener = cancelListener }
        open fun build() = CommonDialog(this)
    }

    interface InputFieldCallBack{
        fun onInputText(string:String)
    }

    interface ClickListener{
        fun onClick()
    }

    interface CancelListener{
        fun onCancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}