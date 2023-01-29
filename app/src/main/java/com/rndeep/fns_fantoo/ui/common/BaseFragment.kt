package com.rndeep.fns_fantoo.ui.common

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewbinding.ViewBinding
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.ui.common.viewmodel.BaseViewModel
import com.rndeep.fns_fantoo.utils.LanguageUtils
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

//https://stackoverflow.com/questions/64819181/how-to-make-basefragment-with-view-binding
//fragment 메모리 이슈해결 + boilerplate code문제 해결

abstract class BaseFragment<viewBinding:ViewBinding>(private val inflate:Inflate<viewBinding>) : LocaleSetFragment() {

    private lateinit var dialog: CommonDialog

    lateinit var navController: NavController

    private var _binding: viewBinding? = null
    val binding get() = _binding!!

    abstract fun initUi()
    abstract fun initUiActionEvent()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)

        initUi()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            navController = Navigation.findNavController(view)
        }catch (e: Exception){
            //Timber.d("onViewCreated err : "+e.message)
        }
        initUiActionEvent()
    }

    protected fun showDialog(message:String){
        showDialog("", message, "")
    }

    protected fun showDialog(title:String, message:String){
        showDialog(title, message, "")
    }

    protected fun showDialog(title:String, message:String, tailMessage:String){
        showDialog(title, message, tailMessage, getString(R.string.h_confirm), "")
    }

    protected fun showDialog(title:String, message:String, tailMessage:String, positiveButtonText:String, negativeButtonText:String){
        showDialog(title, message, tailMessage, positiveButtonText, negativeButtonText,null, null)
    }

    protected fun showDialog(title:String, message:String, tailMessage:String, positiveButtonText:String, negativeButtonText:String,
                             positiveButtonClickListener: CommonDialog.ClickListener?, negativeButtonClickListener: CommonDialog.ClickListener?) {
        val dialogBuilder = CommonDialog.Builder()
        if(title.isNotEmpty()){
            dialogBuilder.title(title)
        }
        if(message.isNotEmpty()){
            dialogBuilder.message(message)
        }
        if(tailMessage.isNotEmpty()){
            dialogBuilder.tailMessage(tailMessage)
        }
        dialogBuilder.setPositiveButtonText(positiveButtonText)
        dialogBuilder.setNegativeButtonText(negativeButtonText)
        positiveButtonClickListener?.let {
            dialogBuilder.setPositiveButtonClickListener(it)
        }
        negativeButtonClickListener?.let{
            dialogBuilder.setNegativeButtonClickListener(it)
        }
        dialog = dialogBuilder.build()
        requireActivity().let {
            dialog.show(it.supportFragmentManager, "")
        }
    }

    fun showSoftInput(view:View){
        try{
            requireActivity().let {
                val imm: InputMethodManager =
                    (it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                imm.showSoftInput(view, 0)
            }
        }catch (e: Exception){
            Timber.d("showSoftInput err: "+e.message)
        }
    }

    fun hideSoftInput(view:View){
        try{
            requireActivity().let {
                val imm: InputMethodManager =
                    (it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }catch (e: Exception){
            Timber.d("hideSoftInput err: "+e.message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}