package com.teameetmeet.meetmeet.presentation.calendar.monthcalendar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.presentation.model.CalendarItem
import com.teameetmeet.meetmeet.util.convertDpToPx


class MonthCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var cellWidth: Float = 0f
    private var cellHeight: Float = 0f
    private var eventBarHeight: Float = 0f

    private var daysInMonth: List<CalendarItem> = emptyList()

    private val textPaint: Paint = Paint().apply {
        textSize = convertSPtoPX(TEXT_SIZE)
        textAlign = Paint.Align.CENTER
        color = context.getColor(R.color.black)
    }

    private val selectPaint: Paint = Paint().apply {
        color = context.getColor(R.color.calendar_background_purple)
    }

    fun setDaysInMonth(calendarItems: List<CalendarItem>) {
        this.daysInMonth = calendarItems
        invalidate()
    }

    fun findDay(x: Float, y: Float): Int {
        return ((y / cellHeight).toInt() * DAYS_PER_WEEK) + (x / cellWidth).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val parentHeight = MeasureSpec.getSize(heightMeasureSpec)

        cellWidth = parentWidth.toFloat() / DAYS_PER_WEEK
        cellHeight = parentHeight.toFloat() / WEEKS_PER_MONTH

        val eventsHeight = cellHeight - (textPaint.textSize + convertDpToPx(context, PADDING))

        eventBarHeight = (eventsHeight / EVENT_BAR_SIZE) - convertDpToPx(context, PADDING)

        setMeasuredDimension(parentWidth, parentHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCalendar(canvas)
    }

    private fun drawCalendar(canvas: Canvas) {
        for (i in daysInMonth.indices) {
            val x = (i % DAYS_PER_WEEK) * cellWidth
            val y = (i / DAYS_PER_WEEK) * cellHeight

            val day = daysInMonth[i].getDay()

            if (daysInMonth[i].isSelected) {
                val rectF = RectF(x, y, x + cellWidth, y + cellHeight)
                canvas.drawRect(rectF, selectPaint)
            }

            if (day != "") {
                canvas.drawText(
                    day,
                    x + cellWidth / 2,
                    y + textPaint.textSize,
                    textPaint.apply {
                        this.color = context.getColor(
                            if (i % 7 == 5) R.color.blue
                            else if (i % 7 == 6) R.color.red
                            else R.color.black
                        )
                    }
                )
            }
        }
    }


    private fun convertSPtoPX(sp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            resources.displayMetrics
        )
    }

    companion object {
        private const val WEEKS_PER_MONTH = 6
        private const val DAYS_PER_WEEK = 7
        private const val TEXT_SIZE = 12f
        private const val EVENT_TEXT_SIZE = 10f
        private const val PADDING = 1
        private const val EVENT_BAR_SIZE = 5
    }
}