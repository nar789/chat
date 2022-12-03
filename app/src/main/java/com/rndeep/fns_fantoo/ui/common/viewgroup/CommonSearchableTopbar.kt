package com.rndeep.fns_fantoo.ui.common.viewgroup

import android.content.Context
import android.content.res.TypedArray
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TopbarSearchableLayoutBinding
import timber.log.Timber
import java.util.concurrent.CopyOnWriteArrayList

class CommonSearchableTopbar : ConstraintLayout{
    private lateinit var binding:TopbarSearchableLayoutBinding
    private var typedArray: TypedArray? = null
    private var searchModeChangedListenerList: CopyOnWriteArrayList<SearchModeChangedListener> = CopyOnWriteArrayList()
    private var searchClickListenerList:CopyOnWriteArrayList<SearchClickListener> = CopyOnWriteArrayList()

    interface SearchClickListener{
        fun onSearchClick(searchString:String)
    }

    interface SearchModeChangedListener{
        fun onModeChanged(isSearchMode:Boolean, view:EditText)
    }

    constructor(context: Context):this(context, null)

    constructor(context: Context, attribute: AttributeSet?):this(context, attribute, 0)

    constructor(context: Context, attribute: AttributeSet?, style:Int):super(context, attribute, style){
        typedArray = context.obtainStyledAttributes(attribute, R.styleable.CommonSearchableTopbar)
        init()
    }

    fun init(){
        binding = TopbarSearchableLayoutBinding.bind(LayoutInflater.from(context).inflate(R.layout.topbar_searchable_layout, this))
        typedArray?.let {
            binding.tvTopbarTitle.text = it.getString(R.styleable.CommonSearchableTopbar_titleText)
            binding.etSearch.hint = it.getString(R.styleable.CommonSearchableTopbar_editTextHint)
        }
        binding.ivSearch.setOnClickListener {
            showInputMode(true)
        }
        binding.tvSearchCancel.setOnClickListener {
            showInputMode(false)
        }
        binding.etSearch.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    binding.ivDeleteSearch.visibility = if(it.isNotEmpty())View.VISIBLE else View.GONE
                }
            }
        })
        binding.ivDeleteSearch.visibility = View.GONE
        binding.ivDeleteSearch.setOnClickListener {
            binding.etSearch.setText("")
        }
        binding.ivSeachInEdit.setOnClickListener {
            for(searchClickListener in searchClickListenerList) {
                searchClickListener.onSearchClick(binding.etSearch.text.toString())
            }
        }
    }

    fun setTitle(title:String){
        binding.tvTopbarTitle.text = title
    }

    fun getEditTextString():String{
        return binding.etSearch.text.toString()
    }

    fun setOnBackButtonClickListener(clickListener: OnClickListener){
        binding.ivTopbarBack.setOnClickListener { clickListener.onClick(it) }
    }

    fun setSearchModeChangeListener(searchModeChangedListener: SearchModeChangedListener){
        searchModeChangedListenerList.add(searchModeChangedListener)
    }

    fun removeSearchModeChangeListener(searchModeChangedListener: SearchModeChangedListener){
        try {
            if (searchModeChangedListenerList.contains(searchModeChangedListener)) {
                searchModeChangedListenerList.remove(searchModeChangedListener)
            }
        }catch (e:Exception){
            Timber.e("removeSearchModeChangeListener e: ${e.message}")
        }
    }

    fun setOnSearchClickListener(searchClickListener: SearchClickListener){
        searchClickListenerList.add(searchClickListener)
    }

    fun removeSearchClickListener(searchClickListener: SearchClickListener){
        try {
            if (searchClickListenerList.contains(searchClickListener)) {
                searchClickListenerList.remove(searchClickListener)
            }
        }catch (e: Exception){
            Timber.e("removeSearchClickListener e: ${e.message}")
        }
    }

    private fun showInputMode(isInputMode: Boolean){
        binding.ivTopbarBack.visibility = if(isInputMode)View.GONE else View.VISIBLE
        binding.clTitle.visibility = if(isInputMode)View.GONE else View.VISIBLE
        binding.clInput.visibility = if(isInputMode)View.VISIBLE else View.GONE
        binding.etSearch.setText("")
        binding.etSearch.requestFocus()
        if(isInputMode) {
            showSoftInput(binding.etSearch)
        }else{
            hideSoftInput(binding.etSearch)
        }
        for(searchModeChangedListener in searchModeChangedListenerList) {
            searchModeChangedListener?.onModeChanged(isInputMode, binding.etSearch)
        }
    }

    fun isSearchMode():Boolean{
        return binding.clInput.visibility == View.VISIBLE
    }

    private fun showSoftInput(view:View){
        try{
            view.context?.let {
                val imm: InputMethodManager =
                    (it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                imm.showSoftInput(view, 0)
            }
        }catch (e: Exception){
            Timber.d("showSoftInput err: "+e.message)
        }
    }

    private fun hideSoftInput(view:View){
        try{
            view.context?.let {
                val imm: InputMethodManager =
                    (it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }catch (e: Exception){
            Timber.d("hideSoftInput err: "+e.message)
        }
    }

    override fun onDetachedFromWindow() {
        searchModeChangedListenerList.clear()
        searchClickListenerList.clear()
        super.onDetachedFromWindow()
    }
}