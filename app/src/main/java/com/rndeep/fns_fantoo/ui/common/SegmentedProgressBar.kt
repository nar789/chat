package com.rndeep.fns_fantoo.ui.common

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.content.ContextCompat
import com.rndeep.fns_fantoo.R


class SegmentedProgressBar : View {

    private val TAG = "SegmentedProgressBar"
    var bgRect: RectF? = null
    private val progressBarBackgroundPaint: Paint = Paint()
    private val progressBarPaint: Paint = Paint()
    private val dividerPaint: Paint = Paint()
    private var progressBarWidth = 0
    private var percentCompleted = 0f
    private var dividerWidth = 1f
    private var isDividerEnabled = true
    private var divisions = 1
    private var enabledDivisions: ArrayList<Int> = ArrayList()
    private var dividerPositions: ArrayList<Float>? = null
    private var cornerRadius = 20f


    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context,attrs :AttributeSet?) : super(context,attrs) {
        init(attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init(attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int) :super(context, attrs, defStyleAttr, defStyleRes){

        init(attrs)
   }

    private fun init(attrs : AttributeSet?){
        dividerPositions = ArrayList()
        cornerRadius = 0f

        val typedArray =
            context.theme.obtainStyledAttributes(attrs,R.styleable.SegmentedProgressBar, 0, 0)

        try {
            dividerPaint.color = typedArray.getColor(
                R.styleable.SegmentedProgressBar_dividerColor,
                ContextCompat.getColor(context, R.color.white)
            )
            progressBarBackgroundPaint.color = typedArray.getColor(
                R.styleable.SegmentedProgressBar_progressBarBackgroundColor,
                ContextCompat.getColor(context, R.color.gray_200)
            )
            progressBarPaint.color = typedArray.getColor(
                R.styleable.SegmentedProgressBar_progressBarColor,
                ContextCompat.getColor(context, R.color.primary_default)
            )
            dividerWidth =
                typedArray.getDimension(R.styleable.SegmentedProgressBar_dividerWidth, dividerWidth)
            isDividerEnabled =
                typedArray.getBoolean(R.styleable.SegmentedProgressBar_isDividerEnabled, true)
            divisions = typedArray.getInteger(R.styleable.SegmentedProgressBar_divisions, divisions)
            cornerRadius =
                typedArray.getDimension(R.styleable.SegmentedProgressBar_cornerRadius, 2f)
        } finally {
            typedArray.recycle()
        }

        val viewTreeObserver = viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (width > 0) {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this)
                        progressBarWidth = width
                        dividerPositions!!.clear()
                        if (divisions > 1) {
                            for (i in 1 until divisions) {
                                dividerPositions!!.add((progressBarWidth * i).toFloat() / divisions)
                            }
                        }
                        bgRect = RectF(0F, 0F, width.toFloat(), height.toFloat())
                        updateProgress()
                    }
                }
            })
        }
    }

    /**
     * Updates the progress bar based on manually passed percent value.
     */
    private fun updateProgress() {
        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (bgRect != null) {
            canvas!!.drawRoundRect(bgRect!!, cornerRadius, cornerRadius, progressBarBackgroundPaint)
            for (enabledDivision in enabledDivisions) {
                if (enabledDivision < divisions) {
                    var left = 0f
                    if (enabledDivision != 0) {
                        left = dividerPositions!![enabledDivision - 1] + dividerWidth
                    }
                    val right =
                        if (enabledDivision >= dividerPositions!!.size) progressBarWidth.toFloat() else dividerPositions!![enabledDivision]
                    val rect = RectF(left, 0f, right, height.toFloat())
                    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, progressBarPaint)
                    if (enabledDivision == 0) {
                        canvas.drawRect(
                            left + cornerRadius, 0f, right,
                            height.toFloat(), progressBarPaint
                        )
                    } else if (enabledDivision == divisions - 1) {
                        canvas.drawRect(
                            left, 0f, right - cornerRadius,
                            height.toFloat(), progressBarPaint
                        )
                    } else {
                        canvas.drawRect(rect, progressBarPaint)
                    }
                }
                if (divisions > 1 && isDividerEnabled) {
                    for (i in 1 until divisions) {
                        val leftPosition = dividerPositions!![i - 1]
                        canvas.drawRect(
                            leftPosition, 0f, leftPosition + dividerWidth,
                            height.toFloat(), dividerPaint
                        )
                    }
                }
            }
        }
    }

    override fun setBackgroundColor(color: Int) {
        progressBarBackgroundPaint.color = color
    }

    fun reset() {
        dividerPositions!!.clear()
        percentCompleted = 0f
        updateProgress()
    }

    /**
     * Set the color for the progress bar
     *
     * @param color
     */
    fun setProgressBarColor(color: Int) {
        progressBarPaint.color = color
    }

    /**
     * Set the color for the divider bar
     *
     * @param color
     */
    fun setDividerColor(color: Int) {
        dividerPaint.color = color
    }

    /**
     * set the width of the divider
     *
     * @param width
     */
    fun setDividerWidth(width: Float) {
        if (width < 0) {
            Log.w(TAG, "setDividerWidth: Divider width can not be negative")
            return
        }
        dividerWidth = width
    }

    /**
     * Set whether the dividers are enabled or not.
     *
     * @param value true or false
     */
    fun setDividerEnabled(value: Boolean) {
        isDividerEnabled = value
    }

    /**
     * Sets the number of divisions in the ProgressBar.
     *
     * @param divisions number of divisions
     */
    fun setDivisions(divisions: Int) {
        if (divisions < 1) {
            Log.w(TAG, "setDivisions: Number of Divisions cannot be less than 1")
            return
        }
        this.divisions = divisions
        dividerPositions!!.clear()
        if (divisions > 1) {
            for (i in 1 until divisions) {
                dividerPositions!!.add((progressBarWidth * i).toFloat() / divisions)
            }
        }
        updateProgress()
    }

    /**
     * Set the enabled divisions to specified value.
     *
     * @param enabledDivisions number of divisions to be enabled
     */
    fun setEnabledDivisions(enabledDivisions: ArrayList<Int>) {
        this.enabledDivisions = enabledDivisions
        updateProgress()
    }

    fun setEnabledDivisions(divisionCount :Int){
        if (divisionCount < 1) {
            Log.w(TAG, "setDivisions: Number of Divisions cannot be less than 1")
            return
        }
        val divisionList = arrayListOf<Int>()
        if(divisionCount>1){
            for(i in 0 until divisionCount){
                divisionList.add(i)
            }
        }
        this.enabledDivisions=divisionList
        updateProgress()
    }

    fun setCornerRadius(cornerRadius: Float) {
        this.cornerRadius = cornerRadius
    }

}