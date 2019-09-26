package com.example.todayzero.util

import java.text.NumberFormat

class NumberFormatter {
    companion object {
        fun format(s: String): String {
            var formatted: String = s
            if (s.contains(",")) {
                formatted = formatted.replace(",", "")
            }
            if(formatted.isNotEmpty()) {
                val sLong = formatted.toLong()
                formatted = NumberFormat.getNumberInstance().format(sLong)
            }
            return formatted
        }
    }
}