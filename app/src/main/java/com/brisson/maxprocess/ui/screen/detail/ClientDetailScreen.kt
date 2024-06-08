package com.brisson.maxprocess.ui.screen.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brisson.maxprocess.ui.theme.MaxProcessTheme

@Composable
fun ClientDetailRoute(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val viewModel: ClientDetailViewModel = hiltViewModel()
    val screenUiState by viewModel.screenUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.updateScreenUiState() } // Trigger initial load

    ClientDetailScreen(
        modifier = modifier then Modifier.fillMaxSize(),
        screenUiState = screenUiState,
        onBack = onBack,
    )
}

@Composable
internal fun ClientDetailScreen(
    modifier: Modifier = Modifier,
    screenUiState: ScreenUiState,
    onBack: () -> Unit,
) {
    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)) {
            val title = when (screenUiState) {
                is ScreenUiState.EditClient -> "Editar cliente"
                ScreenUiState.Loading -> ""
                ScreenUiState.NewClient -> "Novo cliente"
            }

            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Arrow back icon")
            }

            Text(
                modifier = Modifier.align(Alignment.Center),
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Divider(modifier = Modifier.fillMaxWidth(), color = Color(0xffE6E6E6))

        // TODO: Add forms
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewClientDetailScreen() {
    MaxProcessTheme {
        ClientDetailScreen(
            modifier = Modifier.fillMaxSize(),
            screenUiState = ScreenUiState.NewClient,
            onBack = { },
        )
    }
}
