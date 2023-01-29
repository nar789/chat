package com.rndeep.fns_fantoo.ui.common.viewgroup

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.LayoutLoadingBinding

class FantooLoadingView : ConstraintLayout {

    lateinit var binding : LayoutLoadingBinding
    lateinit var imageView :ImageView

    constructor(context: Context) : super(context){
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        initView()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes){
        initView()
    }


    private fun initView(){
        val infService = Context.LAYOUT_INFLATER_SERVICE
        val inflater = context.getSystemService(infService) as (LayoutInflater)
        binding=LayoutLoadingBinding.inflate(inflater)
        addView(binding.root)

        setImageView()

    }

    private fun setImageView(){
        val layoutManger = binding.ivLoadingImage.layoutParams as ConstraintLayout.LayoutParams

        layoutManger.topToTop=LayoutParams.PARENT_ID
        layoutManger.bottomToBottom=LayoutParams.PARENT_ID
        layoutManger.startToStart=LayoutParams.PARENT_ID
        layoutManger.endToEnd=LayoutParams.PARENT_ID
        binding.ivLoadingImage.layoutParams=layoutManger

        Glide.with(context)
            .load(R.drawable.character_loading)
            .into(binding.ivLoadingImage)

    }

    override fun generateDefaultLayoutParams(): LayoutParams {
//        return super.generateDefaultLayoutParams()
        return LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT)
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
    }
}