package com.rndeep.fns_fantoo.ui.club.settings

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

//첫번째 아이템 상단 라인 없이 표시, 리스트형
class ClubSettingItemDecoration (divider: Drawable?) : RecyclerView.ItemDecoration() {
    private var mDivider: Drawable? = divider
    private var hasDiffHeader:Boolean = false

    fun setHasDiffHeaderItem(hasDiffHeader:Boolean){
        this.hasDiffHeader = hasDiffHeader
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = mDivider!!.intrinsicHeight
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        try {
            val dividerLeft = parent.paddingLeft
            val dividerRight = parent.width - parent.paddingRight
            val childCount = parent.childCount
            for (i in 0 until childCount) {
                if(hasDiffHeader && i == 0)continue
                val child = parent.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams
                var dividerTop = child.bottom + params.bottomMargin
                var dividerBottom: Int = dividerTop + mDivider!!.intrinsicHeight
                mDivider!!.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                mDivider!!.draw(c)
                /*if (i == 0) {//첫번째 아이템 상단 라인 추가
                    dividerTop = child.top - params.topMargin
                    dividerBottom = dividerTop + mDivider!!.intrinsicHeight
                    mDivider!!.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                    mDivider!!.draw(c)
                }*/
            }
        } catch (e: Exception) {
            Timber.e("Divider ondraw ${e.message}")
        }
    }
}