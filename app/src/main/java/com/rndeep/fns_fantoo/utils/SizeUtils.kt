package com.rndeep.fns_fantoo.utils

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.Size
import android.util.TypedValue
import android.view.WindowManager

class SizeUtils {

    companion object {

        //디바이스 높이와 넓이
        fun getDeviceSize(context: Context): Size {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                val metrics =
                    context.getSystemService(WindowManager::class.java).currentWindowMetrics
                return Size(metrics.bounds.width(), metrics.bounds.height())
            } else {
                val display = context.getSystemService(WindowManager::class.java).defaultDisplay
                val metrics = if (display != null) {
                    DisplayMetrics().also { display.getRealMetrics(it) }
                } else {
                    Resources.getSystem().displayMetrics
                }
                return Size(metrics.widthPixels, metrics.heightPixels)
            }

        }

        //Dp 값 (value) 을 Int로 변형
        fun getDpValue(value: Float, context: Context?) = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            context?.resources?.displayMetrics
        )

        //Sp 값 을 변경
        fun getSpValue(value: Float, context: Context) = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            value,
            context.resources.displayMetrics
        )


    }

}