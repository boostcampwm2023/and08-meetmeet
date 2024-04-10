package com.teameetmeet.meetmeet.presentation.calendar.monthcalendar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.presentation.model.CalendarItem
import com.teameetmeet.meetmeet.presentation.model.EventSimple
import com.teameetmeet.meetmeet.util.convertDpToPx
import com.teameetmeet.meetmeet.util.date.toEndLong
import com.teameetmeet.meetmeet.util.date.toLocalDate
import com.teameetmeet.meetmeet.util.date.toStartLong
import java.time.LocalDate
import java.util.PriorityQueue
import kotlin.math.absoluteValue


class MonthCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var cellWidth: Float = 0f
    private var cellHeight: Float = 0f
    private var eventBarHeight: Float = 0f

    private var daysInMonth: List<CalendarItem> = emptyList()
    private var events: List<EventSimple> = emptyList()

    private val textPaint: Paint = Paint().apply {
        textSize = convertSPtoPX(TEXT_SIZE)
        textAlign = Paint.Align.CENTER
        color = context.getColor(R.color.black)
    }

    private val selectPaint: Paint = Paint().apply {
        color = context.getColor(R.color.calendar_background_purple)
    }

    private val eventPaint: Paint = Paint().apply {
        style = Paint.Style.FILL
    }

    private val eventTextPaint: TextPaint = TextPaint().apply {
        textSize = convertSPtoPX(EVENT_TEXT_SIZE)
        textAlign = Paint.Align.CENTER
        color = context.getColor(R.color.white)
    }

    private val comparator: (LocalDate, LocalDate) -> Comparator<EventSimple> =
        { firstDay, lastDay ->
            Comparator { event1, event2 ->
                val startDate1 = maxOf(event1.startDateTime.toLocalDate(), firstDay)
                val startDate2 = maxOf(event2.startDateTime.toLocalDate(), firstDay)

                val endDate1 = minOf(event1.endDateTime.toLocalDate(), lastDay)
                val endDate2 = minOf(event2.endDateTime.toLocalDate(), lastDay)

                val oneDay1 = startDate1 == endDate1
                val oneDay2 = startDate2 == endDate2

                if (startDate1 == startDate2 && oneDay1 == oneDay2) {
                    event1.startDateTime.compareTo(event2.startDateTime)
                } else if (startDate1 == startDate2) {
                    oneDay1.compareTo(oneDay2)
                } else {
                    startDate1.compareTo(startDate2)
                }
            }
        }

    fun setDaysInMonth(calendarItems: List<CalendarItem>) {
        this.daysInMonth = calendarItems
        invalidate()
    }

    fun setEvents(events: List<EventSimple>) {
        this.events = events
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
        drawEvent(canvas)
    }

    private fun drawEvent(canvas: Canvas) {
        for (i in 0..<WEEKS_PER_MONTH) {
            if (i * DAYS_PER_WEEK >= daysInMonth.size) break

            val eventBars = hashMapOf<Int, EventBar>()

            val firstDay =
                daysInMonth[i * DAYS_PER_WEEK].date ?: daysInMonth.last().date!!.withDayOfMonth(1)
            val lastDay =
                if ((i + 1) * DAYS_PER_WEEK <= daysInMonth.size) daysInMonth[(i + 1) * DAYS_PER_WEEK - 1].date!!
                else daysInMonth.last().date!!

            val eventsOfWeek = events.filter {
                it.startDateTime <= lastDay.toEndLong() &&
                        it.endDateTime >= firstDay.toStartLong()
            }

            val eventsPQ =
                PriorityQueue(comparator(firstDay, lastDay)).apply { this.addAll(eventsOfWeek) }

            val lastByRow = IntArray(EVENT_BAR_SIZE)
            val lastRowEvent = IntArray(DAYS_PER_WEEK)

            while (eventsPQ.isNotEmpty()) {
                val event = eventsPQ.poll()!!

                if (event.startDateTime > event.endDateTime) continue

                val startDay = maxOf(event.startDateTime.toLocalDate(), firstDay)
                val endDay = minOf(event.endDateTime.toLocalDate(), lastDay)

                val start = startDay.dayOfWeek.value
                val end = endDay.dayOfWeek.value

                val row = lastByRow.indexOfFirst { it < start }
                if (row == -1) {
                    val prevEventId = lastRowEvent[start - 1]
                    eventBars[prevEventId]?.let {
                        it.locations[it.locations.lastIndex] =
                            it.locations.last().copy(end = start - 1)

                        eventsPQ.add(
                            EventSimple(
                                id = prevEventId,
                                title = it.title,
                                startDateTime = startDay.plusDays(1).toStartLong(),
                                endDateTime = it.endDateTime,
                                color = it.color
                            )
                        )
                    }

                    eventsPQ.add(
                        event.copy(
                            startDateTime = startDay.plusDays(1).toStartLong()
                        )
                    )
                    lastRowEvent[start - 1] = minOf(lastRowEvent[start - 1] - 1, -2)
                    lastByRow[EVENT_BAR_SIZE - 1] = start
                } else {
                    eventBars[event.id] = eventBars.getOrDefault(
                        event.id,
                        EventBar(
                            title = event.title,
                            startDateTime = event.startDateTime,
                            endDateTime = event.endDateTime,
                            color = event.color,
                            locations = mutableListOf()
                        )
                    ).apply { this.locations.add(EventLocation(row, start, end)) }
                    if (row == EVENT_BAR_SIZE - 1) {
                        (start - 1..<end).forEach { lastRowEvent[it] = event.id }
                    }
                    lastByRow[row] = end
                }
            }
            drawEventPerWeek(eventBars, i, canvas)
            drawLastRowPerWeek(lastRowEvent, i, canvas)
        }

    }

    private fun drawLastRowPerWeek(
        lastRowEvent: IntArray,
        i: Int,
        canvas: Canvas
    ) {
        lastRowEvent.forEachIndexed { idx, eventId ->
            val padding = convertDpToPx(context, PADDING)
            val textSize = textPaint.textSize
            if (eventId < 0) {
                val eventBarY =
                    i * cellHeight + textSize + padding * (EVENT_BAR_SIZE + 1) + (EVENT_BAR_SIZE - 1) * eventBarHeight
                val rectF = RectF(
                    idx * cellWidth + padding,
                    eventBarY,
                    (idx + 1) * cellWidth - padding,
                    eventBarY + eventBarHeight
                )
                val radius = eventBarHeight / 4
                canvas.drawRoundRect(
                    rectF, radius, radius,
                    eventPaint.apply { color = context.getColor(R.color.grey3) }
                )
                val ellipsizedText = TextUtils.ellipsize(
                    "+${eventId.absoluteValue}",
                    eventTextPaint,
                    rectF.right - rectF.left,
                    TextUtils.TruncateAt.END
                )
                canvas.drawText(
                    ellipsizedText.toString(),
                    (rectF.left + rectF.right) / 2,
                    rectF.bottom - eventTextPaint.descent() - padding,
                    eventTextPaint
                )
            }
        }
    }

    private fun drawEventPerWeek(
        eventBars: HashMap<Int, EventBar>,
        week: Int,
        canvas: Canvas
    ) {
        eventBars.values.forEach { bar ->
            val padding = convertDpToPx(context, PADDING)
            val textSize = textPaint.textSize
            bar.locations.forEach { loc ->
                if (loc.start > loc.end) return
                val eventBarY =
                    week * cellHeight + textSize + padding * (loc.row + 2) + loc.row * eventBarHeight
                val rectF = RectF(
                    (loc.start - 1) * cellWidth + padding,
                    eventBarY,
                    (loc.end) * cellWidth - padding,
                    eventBarY + eventBarHeight
                )
                val radius = eventBarHeight / 4
                canvas.drawRoundRect(
                    rectF, radius, radius,
                    eventPaint.apply { color = bar.color }
                )
                val ellipsizedText = TextUtils.ellipsize(
                    bar.title,
                    eventTextPaint,
                    rectF.right - rectF.left,
                    TextUtils.TruncateAt.END
                )
                canvas.drawText(
                    ellipsizedText.toString(),
                    (rectF.left + rectF.right) / 2,
                    rectF.bottom - eventTextPaint.descent() - padding,
                    eventTextPaint
                )
            }
        }
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

    data class EventBar(
        val title: String,
        val startDateTime: Long,
        val endDateTime: Long,
        val color: Int,
        val locations: MutableList<EventLocation>
    )

    data class EventLocation(
        val row: Int,
        val start: Int,
        val end: Int
    )

    companion object {
        private const val WEEKS_PER_MONTH = 6
        private const val DAYS_PER_WEEK = 7
        private const val TEXT_SIZE = 12f
        private const val EVENT_TEXT_SIZE = 10f
        private const val PADDING = 1
        private const val EVENT_BAR_SIZE = 5
    }
}