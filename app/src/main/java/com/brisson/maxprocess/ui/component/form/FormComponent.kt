package com.brisson.maxprocess.ui.component.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.brisson.maxprocess.ui.theme.errorColor
import com.brisson.maxprocess.ui.theme.unselectedColor

@Composable
fun FormComponent(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String,
    unselected: Boolean,
    hasError: Boolean = false,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable () -> Unit,
) {
    val iconTint =
        if (hasError) errorColor
        else if (unselected) unselectedColor
        else MaterialTheme.colorScheme.primary

    Row(
        modifier = modifier,
        verticalAlignment = verticalAlignment,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Icon(
            modifier = Modifier.padding(top = 12.dp),
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
        )
        content()
    }
}
