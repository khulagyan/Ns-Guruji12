package com.nsguruji.app.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateFormatter {

    fun formatPostDate(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""
        return try {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            isoFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date: Date? = isoFormat.parse(dateString)

            if (date != null) {
                val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("hi", "IN"))
                outputFormat.format(date)
            } else {
                dateString.substringBefore("T")
            }
        } catch (e: Exception) {
            try {
                val simple = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val d = simple.parse(dateString.substringBefore("T"))
                if (d != null) {
                    val out = SimpleDateFormat("dd MMM yyyy", Locale("hi", "IN"))
                    out.format(d)
                } else dateString
            } catch (ex: Exception) {
                dateString
            }
        }
    }
}
