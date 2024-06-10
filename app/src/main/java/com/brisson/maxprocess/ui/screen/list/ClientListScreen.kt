package com.brisson.maxprocess.ui.screen.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brisson.maxprocess.ui.theme.MaxProcessTheme
import com.brisson.maxprocess.ui.theme.errorColor
import com.brisson.maxprocess.ui.theme.lightStrokeColor
import com.brisson.maxprocess.ui.theme.unselectedColor

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
        onDeleteClient = { /*TODO*/ },
    )
}

@Composable
internal fun ClientListScreen(
    modifier: Modifier = Modifier,
    listUiState: ListUiState,
    onNewClient: () -> Unit,
    onClient: (id: Long) -> Unit,
    onDeleteClient: (id: Long) -> Unit,
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
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(20.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new client icon",
                )

                Text(text = "Novo", fontSize = 14.sp)
            }
        }

        //TODO: search component

        when (listUiState) {
            is ListUiState.Error -> { /*TODO*/ }
            ListUiState.Loading -> { /*TODO*/ }
            is ListUiState.Success -> {
                if (listUiState.clients.isEmpty()) {
                    ClientListEmptyState(modifier = Modifier.fillMaxSize())
                } else {
                    ClientList(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        listUiState = listUiState,
                        onClient = onClient,
                        onDeleteClient = onDeleteClient,
                    )
                }
            }
        }
    }
}

@Composable
private fun ClientListEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Nenhum resultado",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )

        Text(
            modifier = Modifier
                .padding(top = 12.dp)
                .widthIn(max = 280.dp),
            text = buildAnnotatedString {
                append("Adicione um cliente na sua agenda clicando no botão ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("+ Novo")
                }
                append(" acima")
            },
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.weight(2.5f))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ClientList(
    modifier: Modifier = Modifier,
    listUiState: ListUiState.Success,
    onClient: (id: Long) -> Unit,
    onDeleteClient: (id: Long) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 4.dp),
    ) {
        val groupedClients = listUiState.clients.sortedBy { it.name }.groupBy { it.name[0] }

        groupedClients.forEach { (char, clients) ->
            stickyHeader {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(top = 16.dp),
                    text = char.toString(),
                    fontWeight = FontWeight.Medium,
                    color = unselectedColor,
                )
            }

            items(clients) { client ->
                var showMenu by remember { mutableStateOf(false) }
                val toggleMenu: () -> Unit = { showMenu = !showMenu }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = client.name,
                        fontWeight = FontWeight.Medium,
                    )

                    IconButton(onClick = toggleMenu) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "More options icon",
                            tint = unselectedColor,
                        )
                        DropdownMenu(
                            modifier = Modifier.background(MaterialTheme.colorScheme.background),
                            expanded = showMenu,
                            onDismissRequest = toggleMenu,
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(20.dp),
                                            imageVector = Icons.Outlined.Edit,
                                            contentDescription = "Edit icon",
                                            tint = unselectedColor,
                                        )
                                        Text(text = "Editar", color = unselectedColor)
                                    }
                                },
                                onClick = { showMenu = false; onClient(client.id) },
                            )

                            Divider(modifier = Modifier.fillMaxWidth(), color = lightStrokeColor)

                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(20.dp),
                                            imageVector = Icons.Outlined.DeleteOutline,
                                            contentDescription = "Edit icon",
                                            tint = errorColor,
                                        )

                                        Text(text = "Excluir", color = errorColor)
                                    }
                                },
                                onClick = { showMenu = false; onDeleteClient(client.id) },
                            )
                        }
                    }
                }

                Divider(modifier = Modifier.fillMaxWidth(), color = lightStrokeColor)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewClientListScreen() {
    MaxProcessTheme {
        ClientListScreen(
            modifier = Modifier.fillMaxSize(),
            listUiState = ListUiState.Success(emptyList()),
            onNewClient = { },
            onClient = { },
            onDeleteClient = { },
        )
    }
}
