package com.brisson.maxprocess.data.local.converter

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.TypeConverter

class ColorConverter {
    @TypeConverter
    fun fromHexValue(hex: String?): Color? {
        return hex?.let { Color(android.graphics.Color.parseColor(hex)) }
    }

    @TypeConverter
    fun toHexValue(color: Color?): String? {
        return color?.let { String.format("#%08X", (color.toArgb() and 0xFFFFFFFF.toInt())) }
    }
}
