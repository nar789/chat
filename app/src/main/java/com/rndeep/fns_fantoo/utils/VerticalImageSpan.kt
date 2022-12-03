package com.rndeep.fns_fantoo.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan
import timber.log.Timber

class VerticalImageSpan constructor(
    private val spanDrawable: Drawable
) : ImageSpan(spanDrawable) {

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val drawable = drawable
        val rect: Rect = drawable.bounds
        fm ?: return rect.right
        val fmPaint = paint.fontMetricsInt
        val fontHeight = fmPaint.descent - fmPaint.ascent
        val drHeight = rect.bottom - rect.top
        val centerY = fmPaint.ascent + fontHeight / 2

        fm.ascent = centerY - drHeight / 2
        fm.top = fm.ascent
        fm.bottom = centerY + drHeight / 2
        fm.descent = fm.bottom

        return rect.right
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        canvas.save()
        val fmPain = paint.fontMetricsInt
        val fontHeight = fmPain.descent - fmPain.ascent
        val centerY = y + fmPain.descent - fontHeight / 2
        val transY = centerY - (drawable.bounds.bottom - drawable.bounds.top) / 2
        canvas.translate(x, transY.toFloat())
        drawable.draw(canvas)
        canvas.restore()


    }

}