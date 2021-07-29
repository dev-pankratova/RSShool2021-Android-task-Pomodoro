package com.project.pomodoro

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes

class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var periodMs = 0F
    private var currentMs = 0F
    private var color = 0
    private var style = FILL
    private val paint = Paint()

    init {
        if (attrs != null) {
            val styledAttrs = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CustomView,
                defStyleAttr,
                0
            )
            color = styledAttrs.getColor(R.styleable.CustomView_custom_color, Color.RED)
            style = styledAttrs.getInt(R.styleable.CustomView_custom_style, FILL)
            styledAttrs.recycle()
        }

        paint.color = color
        paint.style = if (style == FILL) Paint.Style.FILL else Paint.Style.STROKE
        paint.strokeWidth = 5F
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (periodMs == 0F || currentMs == 0F) return
        val startAngel = ((periodMs - currentMs) / periodMs) * 360

        canvas.drawArc(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            -90f,
            startAngel,
            true,
            paint
        )
    }

    /**
     * Set lasted milliseconds
     */
    fun setCurrent(current: Long) {
        currentMs = current.toFloat()
        postInvalidate()
    }

    /**
     * Set time period
     */
    fun setPeriod(period: Long) {
        periodMs = period.toFloat()
    }

    private companion object {

        private const val FILL = 0
    }
}