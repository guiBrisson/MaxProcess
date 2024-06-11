package com.brisson.maxprocess.ui.component.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

data class SnackbarProperties(
    override val message: String,
    override val actionLabel: String? = null,
    override val duration: SnackbarDuration =
        if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
    override val withDismissAction: Boolean = false,

    // Custom properties
    val title: String,
    val type: SnackbarType,
) : SnackbarVisuals

enum class SnackbarType {
    SUCCESS,
    ERROR,
}
