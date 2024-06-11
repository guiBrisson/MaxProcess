package com.brisson.maxprocess.ui.component.snackbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brisson.maxprocess.ui.theme.MaxProcessTheme

@Composable
fun BaseSnackbar(
    modifier: Modifier = Modifier,
    customProperties: SnackbarProperties,
    performAction: () -> Unit,
) {
    Row(
        modifier = modifier then Modifier
            .height(IntrinsicSize.Max)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        val colorType = when (customProperties.type) {
            SnackbarType.SUCCESS -> Color(0xff58BD7D)
            SnackbarType.ERROR -> Color(0xffD55655)
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colorType)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            val iconType = when (customProperties.type) {
                SnackbarType.SUCCESS -> Icons.Default.Check
                SnackbarType.ERROR -> Icons.Default.Close
            }

            val contentDescription = when (customProperties.type) {
                SnackbarType.SUCCESS -> "Check icon"
                SnackbarType.ERROR -> "Close icon"
            }

            Icon(
                imageVector = iconType,
                contentDescription = contentDescription,
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = customProperties.title,
                color = colorType,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = customProperties.message,
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        customProperties.actionLabel?.let { actionLabel ->
            TextButton(
                modifier = Modifier.fillMaxHeight(),
                onClick = { performAction() },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = actionLabel,
                    color = MaterialTheme.colorScheme.background,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }

}

@Preview
@Composable
private fun PreviewSnackbar() {
    MaxProcessTheme {

        BaseSnackbar(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            customProperties = SnackbarProperties(
                title = "Success",
                message = "New client added to the list",
                type = SnackbarType.SUCCESS,
                actionLabel = "OK",
            ),
            performAction = { },
        )
    }
}
