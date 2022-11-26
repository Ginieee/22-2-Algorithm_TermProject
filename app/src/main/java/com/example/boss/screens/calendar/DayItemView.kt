package com.example.boss.screens.calendar

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import androidx.navigation.findNavController
import com.example.boss.MainActivity
import com.example.boss.R
import com.example.boss.data.ScheduleDatabase
import com.example.boss.data.entity.DailySchedule
import com.example.boss.utils.CalendarUtils.Companion.getDateColor
import com.example.boss.utils.CalendarUtils.Companion.isSameMonth
import org.joda.time.DateTime

class DayItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes private val defStyleAttr: Int = R.attr.itemViewStyle,
    @StyleRes private val defStyleRes: Int = R.style.Calendar_ItemViewStyle,
    private val date: DateTime = DateTime(),
    private val firstDayOfMonth: DateTime = DateTime()
) : View(ContextThemeWrapper(context, defStyleRes), attrs, defStyleAttr) {

    private val bounds = Rect()

    private var paint: Paint = Paint()

    private var noticePaint : Paint = Paint()
    private var radius : Int = 9

    lateinit var db : ScheduleDatabase
    private var daily : ArrayList<DailySchedule> = ArrayList<DailySchedule>()

    init {

        db = ScheduleDatabase.getInstance(context)!!

        /* Attributes */
        context.withStyledAttributes(attrs, R.styleable.CalendarView, defStyleAttr, defStyleRes) {
            val dayTextSize = getDimensionPixelSize(R.styleable.CalendarView_dayTextSize, 0).toFloat()

            val assetManager : AssetManager = context.assets
            val tf : Typeface = Typeface.createFromAsset(assetManager, "librefranklin_semibold.ttf")

            /* 흰색 배경에 유색 글씨 */
            paint = TextPaint().apply {
                isAntiAlias = true
                textSize = dayTextSize
                color = getDateColor(date.dayOfWeek)
                if (!isSameMonth(date, firstDayOfMonth)) {
                    alpha = 50
                }
                setTypeface(tf)
            }

            noticePaint.setColor(resources.getColor(R.color.MainGreen))
        }
    }

//    private lateinit var dateClickListeners : DateClickListener
//
//    interface DateClickListener {
//        fun onDateClick(date : DateTime)
//    }
//
//    fun setDateClickListener(dateClickListener : DateClickListener) {
//        dateClickListeners = dateClickListener
//    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return

        val day = date.dayOfMonth.toString()
        paint.getTextBounds(day, 0, day.length, bounds)
        canvas.drawText(
            day,
            (width / 2 - bounds.width() / 2).toFloat() - 2,
            (height / 2 + bounds.height() / 2).toFloat(),
            paint
        )

        if (isThereData()) {
            canvas.drawCircle((width / 2).toFloat(), (height / 2 + bounds.height() / 2).toFloat() + 25, radius.toFloat(), noticePaint )
        }

        setOnClickListener {
            //dateClickListeners.onDateClick(date)
            val action = CalendarFragmentDirections.actionCalendarFragmentToDailyScheduleFragment(date.year, date.monthOfYear, date.dayOfMonth, date.dayOfWeek)
            findNavController().navigate(action)
            Log.d("DAYITEMVIEW_CHECK", "${date.year}년 ${date.monthOfYear}월 ${date.dayOfMonth}일 ${date.dayOfWeek}요일")
        }
    }

    private fun isThereData() : Boolean{
        var forDailyDB : Thread = Thread {
            daily = db.dailyDao.getDateDaily(date.year, date.monthOfYear, date.dayOfMonth) as ArrayList
        }
        forDailyDB.start()

        try {
            forDailyDB.join()
        } catch (e : InterruptedException) {
            e.printStackTrace()
        }

        if (daily.size != 0) {
            return true
        }
        return false
    }
}