package com.rndeep.fns_fantoo.ui.common.viewgroup

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TopbarLayoutBinding

open class CommonTopbar : ConstraintLayout{
    private lateinit var binding:TopbarLayoutBinding
    private var typedArray: TypedArray? = null

    constructor(context: Context):this(context, null)

    constructor(context: Context, attribute: AttributeSet?):this(context, attribute, 0)

    constructor(context: Context, attribute: AttributeSet?, style:Int):super(context, attribute, style){
        typedArray = context.obtainStyledAttributes(attribute, R.styleable.CommonTopbar)
        init()
    }

    fun init(){
        binding = TopbarLayoutBinding.bind(LayoutInflater.from(context).inflate(R.layout.topbar_layout, this))
        typedArray?.let {
            binding.ivTopbarBack.setImageResource(it.getResourceId(R.styleable.CommonTopbar_backButtonImageResource, R.drawable.back))
            binding.tvTopbarTitle.text = it.getString(R.styleable.CommonTopbar_titleText)
            binding.tvTopTail.text = it.getString(R.styleable.CommonTopbar_tailText)
            binding.tvTopTail.setTextColor(it.getColor(R.styleable.CommonTopbar_tailTextColor, binding.root.resources.getColor(R.color.gray_300, null)))
            binding.ivTopTail.setImageResource(it.getResourceId(R.styleable.CommonTopbar_tailImageResource, 0))
            binding.ivTopTail.setColorFilter(it.getColor(R.styleable.CommonTopbar_tailImageTint, 0))
        }
    }

    fun setBackButtonImageResource(imageResource: Int){
        binding.ivTopbarBack.setImageResource(imageResource)
    }

    fun setTitle(title:String){
        binding.tvTopbarTitle.text = title
    }

    fun setTailText(text:String){
        binding.tvTopTail.text = text
    }

    fun setTailVisibility(visiblity:Int){
        binding.tvTopTail.visibility = visiblity
    }

    fun setTailTextEnable(isEnable:Boolean){
        binding.tvTopTail.let{
            it.setTextColor(if(isEnable)it.context.getColor(R.color.state_active_primary_default)
            else it.context.getColor(R.color.gray_300))
            it.isEnabled = isEnable
        }
    }

    fun setOnBackButtonClickListener(clickListener: OnClickListener){
        binding.ivTopbarBack.setOnClickListener { clickListener.onClick(it) }
    }

    fun setTailTextClickListener(clickListener: OnClickListener){
        binding.tvTopTail.setOnClickListener { clickListener.onClick(it) }
    }

    fun setTailImageClickListener(clickListener: OnClickListener){
        binding.ivTopTail.setOnClickListener { clickListener.onClick(it) }
    }

    fun setTailTextColor(color:Int){
        binding.tvTopTail.setTextColor(color)
    }

    fun setTailImageResource(imageResource:Int){
        binding.ivTopTail.setImageResource(imageResource)
    }

    fun setTailImageColorFilter(colorFilter: Int){
        binding.ivTopTail.setColorFilter(colorFilter)
    }
}