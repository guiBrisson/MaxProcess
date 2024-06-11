package com.brisson.maxprocess.ui.screen.list

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brisson.maxprocess.domain.model.Client
import com.brisson.maxprocess.ui.component.snackbar.SnackbarProperties
import com.brisson.maxprocess.ui.component.snackbar.SnackbarType
import com.brisson.maxprocess.ui.theme.MaxProcessTheme
import com.brisson.maxprocess.ui.theme.errorColor
import com.brisson.maxprocess.ui.theme.lightStrokeColor
import com.brisson.maxprocess.ui.theme.unselectedColor
import com.brisson.maxprocess.ui.util.generateRandomPastelColor
import com.brisson.maxprocess.ui.util.shimmerEffect
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ClientListRoute(
    modifier: Modifier = Modifier,
    onNewClient: () -> Unit,
    onClient: (id: Long) -> Unit,
    onShowSnackbar: suspend (SnackbarProperties) -> Boolean,
) {
    val viewModel: ClientListViewModel = hiltViewModel()
    val listUiState by viewModel.clientListUiState.collectAsStateWithLifecycle()

    ClientListScreen(
        modifier = modifier then Modifier.fillMaxSize(),
        listUiState = listUiState,
        onNewClient = onNewClient,
        onClient = onClient,
        onShowSnackbar = onShowSnackbar,
        onDeleteClient = { id -> viewModel.handleEvents(ClientListEvent.OnDeleteClient(id)) },
        onSearchClient = { query -> viewModel.handleEvents(ClientListEvent.OnSearch(query)) },
    )
}

@Composable
internal fun ClientListScreen(
    modifier: Modifier = Modifier,
    listUiState: ClientListUiState,
    onNewClient: () -> Unit,
    onClient: (id: Long) -> Unit,
    onShowSnackbar: suspend (SnackbarProperties) -> Boolean,
    onDeleteClient: (id: Long) -> Unit,
    onSearchClient: (query: String) -> Unit,
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

        SearchClientBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .heightIn(min = 44.dp),
            onSearch = onSearchClient,
        )

        when (listUiState) {
            is ClientListUiState.Error -> {
                LaunchedEffect(Unit) {
                    onShowSnackbar(SnackbarProperties(
                        message = "Algo aconteceu na aplicação",
                        title = "Erro interno",
                        type = SnackbarType.ERROR,
                    ))
                }
            }

            ClientListUiState.Loading -> {
                ClientListLoadingState(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f))
            }

            is ClientListUiState.Success -> {
                AnimatedContent(
                    targetState = listUiState.clients,
                    label = "Client list success content animation",
                ) { clients ->
                    if (clients.isEmpty()) {
                        ClientListEmptyState(modifier = Modifier.fillMaxSize())
                    } else {
                        ClientList(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            clients = clients,
                            onClient = onClient,
                            onDeleteClient = onDeleteClient,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchClientBar(
    modifier: Modifier = Modifier,
    onSearch: (query: String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }
    var query by remember { mutableStateOf("") }

    BasicTextField(
        value = query,
        onValueChange = {
            query = it
            searchJob?.cancel() // Cancel any pending search
            searchJob = coroutineScope.launch {
                delay(400)
                onSearch(query)
            }
        },
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
        decorationBox = { innerTextField ->
            Row(
                modifier = modifier then Modifier
                    .border(1.dp, Color(0xffE0E0E0), RoundedCornerShape(8.dp))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search icon",
                    tint = unselectedColor,
                )

                Box {
                    if (query.isEmpty()) {
                        Text(text = "Pesquisar", color = unselectedColor, fontSize = 16.sp)
                    }
                    innerTextField()
                }
            }
        }
    )
}

@Composable
private fun ClientListLoadingState(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        repeat(2) {
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .width(15.dp)
                    .height(25.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect(),
            )

            repeat(4) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth()
                        .height(55.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect(),
                )
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
            modifier = Modifier.padding(top = 12.dp),
            text = buildAnnotatedString {
                append("Adicione um cliente na sua agenda\nclicando no botão ")
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
    clients: List<Client>,
    onClient: (id: Long) -> Unit,
    onDeleteClient: (id: Long) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 4.dp),
    ) {
        val groupedClients = clients.sortedBy { it.name }.groupBy { it.name[0].uppercaseChar() }

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
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val containerColor = generateRandomPastelColor()

                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(containerColor),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = client.name.first().uppercase(),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.background,
                            fontWeight = FontWeight.Medium,
                        )
                    }

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
            listUiState = ClientListUiState.Success(emptyList()),
            onNewClient = { },
            onClient = { },
            onShowSnackbar = { _ -> false },
            onDeleteClient = { },
            onSearchClient = { },
        )
    }
}
