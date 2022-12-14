package com.example.boss.screens.calendar

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import com.example.boss.MainActivity
import com.example.boss.R
import com.example.boss.utils.CalendarUtils.Companion.WEEKS_PER_MONTH
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants.DAYS_PER_WEEK
import kotlin.math.max

class CalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.calendarViewStyle,
    @StyleRes defStyleRes: Int = R.style.Calendar_CalendarViewStyle
) : ViewGroup(ContextThemeWrapper(context, defStyleRes), attrs, defStyleAttr) {

    private var _height: Float = 0f

    init {
        context.withStyledAttributes(attrs, R.styleable.CalendarView, defStyleAttr, defStyleRes) {
            _height = getDimension(R.styleable.CalendarView_dayHeight, 0f)
        }
    }

    /**
     * Measure
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val h = paddingTop + paddingBottom + max(suggestedMinimumHeight, (_height * WEEKS_PER_MONTH).toInt())
        setMeasuredDimension(getDefaultSize(suggestedMinimumWidth, widthMeasureSpec), h)
    }

    /**
     * Layout
     */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val iWidth = (width / DAYS_PER_WEEK).toFloat()
        val iHeight = (height / WEEKS_PER_MONTH).toFloat()

        var index = 0
        children.forEach { view ->
            val left = (index % DAYS_PER_WEEK) * iWidth
            val top = (index / DAYS_PER_WEEK) * iHeight

            view.layout(left.toInt(), top.toInt(), (left + iWidth).toInt(), (top + iHeight).toInt())

            index++
        }
    }

    /**
     * ?????? ????????? ????????????.
     * @param firstDayOfMonth   ??? ?????? ?????? ??????
     * @param list              ????????? ????????? ?????? ????????? ????????? ?????? (??? 42???)
     */
    fun initCalendar(firstDayOfMonth: DateTime, list: List<DateTime>) {
        list.forEach {
            addView(DayItemView(
                context = context,
                date = it,
                firstDayOfMonth = firstDayOfMonth
            ))
        }
    }
}