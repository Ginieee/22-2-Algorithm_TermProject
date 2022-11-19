package com.example.boss.screens.calendar

import org.joda.time.DateTime

interface DateListener {
    fun onClick(date : DateTime)
}