package com.rndeep.fns_fantoo.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class CustomDividerDecoration(
    private val height: Float,
    private val padding: Float,
    @ColorInt
    private val color: Int,
    private val isContainLastItem : Boolean
) : RecyclerView.ItemDecoration() {

    private val paint = Paint()


    init {
        paint.color = color
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val dividerHeight = SizeUtils.getDpValue(height,parent.context)
        val dividerPadding = SizeUtils.getDpValue(padding,parent.context)
        val left = parent.paddingStart + dividerPadding
        val right = parent.width - parent.paddingEnd - dividerPadding
//        if(!isLast(parent.getChildAdapterPosition(parent.rootView),parent))
        val itemCount =if (isContainLastItem) parent.childCount else parent.childCount-1
        for (i in 0 until itemCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = (child.bottom + params.bottomMargin).toFloat()
            val bottom = top - dividerHeight
            c.drawRect(left, top, right, bottom, paint)
        }

    }
}