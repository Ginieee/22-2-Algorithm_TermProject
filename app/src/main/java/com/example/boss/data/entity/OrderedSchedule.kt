package com.example.boss.data.entity

data class OrderedSchedule(
    var name : String = "",
    var startH : String = "HH",
    var startM : String = "MM",
    var endH : String = "HH",
    var endM : String = "MM",
    var leftMinute : Int = 0
)