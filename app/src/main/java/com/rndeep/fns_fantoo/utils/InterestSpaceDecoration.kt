package com.rndeep.fns_fantoo.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class InterestSpaceDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val spacing = Math.round(SPACE * parent.context.resources.displayMetrics.density)
        val position = parent.getChildAdapterPosition(view)
        val column = position % SPAN_COUNT

        when(column) {
            0 -> {
                outRect.left = 0
                outRect.right = spacing / 2
                outRect.top = if (position < SPAN_COUNT) 0 else spacing
                outRect.bottom = 0
            }
            1 -> {
                outRect.left = spacing / 2
                outRect.right = spacing / 2
                outRect.top = if (position < SPAN_COUNT) 0 else spacing
                outRect.bottom = 0
            }
            2 -> {
                outRect.left = spacing / 2
                outRect.right = 0
                outRect.top = if (position < SPAN_COUNT) 0 else spacing
                outRect.bottom = 0
            }
        }

//        outRect.left = spacing - column * spacing / SPAN_COUNT
//        outRect.right = (column + 1) * spacing / SPAN_COUNT
        Timber.d("spacing: $spacing, position: $position, column: $column, outRect: $outRect")
    }

    companion object {
        const val SPAN_COUNT = 3
        const val SPACE = 10
    }
}