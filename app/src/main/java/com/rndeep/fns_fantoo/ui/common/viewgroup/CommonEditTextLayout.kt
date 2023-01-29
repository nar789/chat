package com.rndeep.fns_fantoo.ui.common.viewgroup

import android.content.Context
import android.content.res.TypedArray
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.CommonEdittextBinding
import timber.log.Timber
import java.lang.IndexOutOfBoundsException

class CommonEditTextLayout : ConstraintLayout{

    private lateinit var binding:CommonEdittextBinding
    private var typedArray:TypedArray? = null
    private var maxLength:Int = -1
    private var inputState = EditorState.NONE

    constructor(context: Context):this(context, null)

    constructor(context: Context, attribute:AttributeSet?):this(context, attribute, 0)

    constructor(context: Context, attribute:AttributeSet?, style:Int):super(context, attribute, style){
        typedArray = context.obtainStyledAttributes(attribute, R.styleable.CommonEditTextLayout)
        init()
    }

    fun init(){
        binding = CommonEdittextBinding.bind(LayoutInflater.from(context).inflate(R.layout.common_edittext, this))
        typedArray?.let {
            binding.btnDone.text = it.getString(R.styleable.CommonEditTextLayout_buttonText)
            binding.et.hint = it.getString(R.styleable.CommonEditTextLayout_hintText)
            maxLength = it.getInteger(R.styleable.CommonEditTextLayout_maxLength, -1)
        }

        binding.et.addTextChangedListener(editTextWatcher())
    }

    fun getText():String{
        return binding.et.text.toString()
    }

    fun setText(string:String){
        binding.et.setText(string)
        try {
            binding.et.setSelection(string.length)
        }catch (ioe:IndexOutOfBoundsException){
        }
    }

    fun getEditText():EditText{
        return binding.et
    }

    fun setMaxLength(maxLength:Int){
        this.maxLength = maxLength
    }

    fun addTextChangedListener(textWatcher: TextWatcher){
        textWatcher.let {
            binding.et.addTextChangedListener(it)
        }
    }

    fun setButtonClickListener(clickListener: OnClickListener){
        clickListener.let {
            binding.btnDone.setOnClickListener(it)
        }
    }

    fun setButtonEnable(enable:Boolean){
        binding.btnDone.isEnabled = enable
    }

    fun setInputState(editorState : EditorState){
        inputState = editorState
        when(editorState){
            EditorState.SUCCESS ->{
                binding.inputLayout.setBackgroundResource(R.drawable.input_field_success)
                binding.inputLayout.isEndIconVisible = false
            }
            EditorState.WARNING ->{
            }
            EditorState.NONE ->{
                binding.inputLayout.setBackgroundResource(R.drawable.input_field)
                binding.inputLayout.isEndIconVisible = true
            }
        }
    }

    private fun editTextWatcher() = object :TextWatcher{
        var beforeText:String = ""
        override fun beforeTextChanged(c: CharSequence?, p1: Int, p2: Int, p3: Int) {
            c?.let {
                beforeText = it.toString()
            }
        }
        override fun afterTextChanged(editable: Editable?) {
            if(inputState != EditorState.NONE) {
                setInputState(EditorState.NONE)
            }
            editable?.let {
                if(maxLength != -1){
                    if(it.length > maxLength){
                        binding.et.setText(beforeText)
                        binding.et.text?.let {
                                editable ->
                            binding.et.setSelection(editable.length)
                        }
                    }
                }
            }
        }

        override fun onTextChanged(c: CharSequence?, p1: Int, p2: Int, p3: Int) {
            c?.let {
                binding.btnDone.isEnabled = it.isNotEmpty()
            }
        }
    }

    enum class EditorState{
        NONE, SUCCESS, WARNING
    }
}