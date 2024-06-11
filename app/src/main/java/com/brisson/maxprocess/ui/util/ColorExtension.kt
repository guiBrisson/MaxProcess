package com.brisson.maxprocess.ui.util

import androidx.compose.ui.graphics.Color

fun generateRandomPastelColor(): Color {
    val hue = (0..360).random().toFloat()
    val saturation = 0.5f // Fixed saturation for pastel effect
    val lightness = 0.8f // Fixed lightness for pastel effect

    return Color.hsl(hue, saturation, lightness)
}
