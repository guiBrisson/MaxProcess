package com.brisson.maxprocess.ui.screen.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brisson.maxprocess.ui.theme.MaxProcessTheme

@Composable
fun ClientListRoute(
    modifier: Modifier = Modifier,
    onNewClient: () -> Unit,
    onClient: (id: Long) -> Unit,
) {
    val viewModel: ClientListViewModel = hiltViewModel()
    val listUiState by viewModel.clientListUiState.collectAsStateWithLifecycle()

    ClientListScreen(
        modifier = modifier then Modifier.fillMaxSize(),
        listUiState = listUiState,
        onNewClient = onNewClient,
        onClient = onClient,
    )
}

@Composable
internal fun ClientListScreen(
    modifier: Modifier = Modifier,
    listUiState: ListUiState,
    onNewClient: () -> Unit,
    onClient: (id: Long) -> Unit,
) {
    Column(modifier = modifier then Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Agenda", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)

            Button(
                modifier = Modifier.height(32.dp),
                shape = RoundedCornerShape(8.dp),
                onClick = onNewClient,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            ) {
                Icon(
                    modifier = Modifier.padding(end = 4.dp).size(20.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new client icon",
                )

                Text(text = "Novo", fontSize = 14.sp)
            }

            //TODO: search component
            
            //TODO: handle listUiState
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewClientListScreen() {
    MaxProcessTheme {
        ClientListScreen(
            modifier = Modifier.fillMaxSize(),
            listUiState = ListUiState.Loading,
            onNewClient = { },
            onClient = { },
        )
    }
}
