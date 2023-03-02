package com.devbin.domain

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun getDate(): String {
    val sdf = SimpleDateFormat("dd/M/yyyy", Locale.getDefault())
    return sdf.format(Calendar.getInstance().time)
}