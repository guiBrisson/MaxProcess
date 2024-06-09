package com.brisson.maxprocess.ui.screen.detail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brisson.maxprocess.domain.model.mockedBrazilStates
import com.brisson.maxprocess.ui.theme.MaxProcessTheme
import com.brisson.maxprocess.ui.theme.lightStrokeColor
import com.brisson.maxprocess.ui.theme.unselectedColor
import com.brisson.maxprocess.ui.util.MaskVisualTransformation

@Composable
fun ClientDetailRoute(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val viewModel: ClientDetailViewModel = hiltViewModel()
    val screenUiState by viewModel.screenUiState.collectAsStateWithLifecycle()
    val actionUiState by viewModel.actionUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.handleEvents(ClientDetailEvent.OnInitialLoad) }
    LaunchedEffect(actionUiState) { if (actionUiState.isSuccess()) onBack() }

    ClientDetailScreen(
        modifier = modifier then Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.ime)
            .windowInsetsPadding(WindowInsets.navigationBars),
        screenUiState = screenUiState,
        actionUiState = actionUiState,
        onEvent = viewModel::handleEvents,
        onBack = onBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ClientDetailScreen(
    modifier: Modifier = Modifier,
    screenUiState: ScreenUiState,
    actionUiState: ActionUiState,
    onEvent: (ClientDetailEvent) -> Unit,
    onBack: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    var clientName by remember { mutableStateOf("") }
    var clientBirthDate by remember { mutableStateOf("") }
    var clientUF by remember { mutableStateOf("") }
    var clientPhones by remember { mutableStateOf("") } // TODO: implement multiple phone numbers
    var clientCPF by remember { mutableStateOf("") }

    var ufDropdownExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)) {
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

        if (screenUiState.isLoading()) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                trackColor = lightStrokeColor,
            )
        } else {
            Divider(modifier = Modifier.fillMaxWidth(), color = lightStrokeColor)
        }

        if (!screenUiState.isLoading()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(28.dp),
                contentPadding = PaddingValues(vertical = 40.dp, horizontal = 20.dp),
            ) {
                item {
                    FormComponent(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Outlined.Person,
                        contentDescription = "Person icon",
                        unselected = clientName.isEmpty(),
                    ) {
                        FormTextField(
                            label = "Nome",
                            value = clientName,
                            onValueChange = { clientName = it },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next,
                            ),
                            keyboardActions = KeyboardActions(onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }),
                        )
                    }
                }
                item {
                    val dateMask = MaskVisualTransformation("##/##/####")
                    FormComponent(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Outlined.DateRange,
                        contentDescription = "Date icon",
                        unselected = clientBirthDate.isEmpty(),
                    ) {
                        FormTextField(
                            label = "Data de nascimento",
                            value = clientBirthDate,
                            onValueChange = {
                                if (it.length <= 8) clientBirthDate = it
                            },
                            visualTransformation = dateMask,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next,
                            ),
                            keyboardActions = KeyboardActions(onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }),
                        )
                    }
                }

                item {
                    val interactionSource = remember { MutableInteractionSource() }
                    FormComponent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = { ufDropdownExpanded = !ufDropdownExpanded }
                            ),
                        icon = Icons.Outlined.LocationOn,
                        contentDescription = "Location icon",
                        unselected = clientUF.isEmpty(),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            ExposedDropdownMenuBox(
                                modifier = Modifier.weight(1f),
                                expanded = ufDropdownExpanded,
                                onExpandedChange = { ufDropdownExpanded = !ufDropdownExpanded }
                            ) {
                                FormTextField(
                                    modifier = Modifier.menuAnchor(),
                                    readOnly = true,
                                    label = "UF",
                                    value = clientUF,
                                    onValueChange = { clientUF = it },
                                )

                                ExposedDropdownMenu(
                                    modifier = Modifier.background(Color.White),
                                    expanded = ufDropdownExpanded,
                                    onDismissRequest = { ufDropdownExpanded = false },
                                ) {
                                    mockedBrazilStates.forEach { (uf, name) ->
                                        DropdownMenuItem(
                                            text = { Text(text = "$uf - $name") },
                                            onClick = { clientUF = uf; ufDropdownExpanded = false },
                                        )
                                    }
                                }
                            }
                            val degrees by animateFloatAsState(
                                targetValue = if (ufDropdownExpanded) 180f else 0f,
                                label = "Keyboard arrow icon animation",
                            )

                            Icon(
                                modifier = Modifier.rotate(degrees),
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Keyboard arrow icon",
                                tint = unselectedColor,
                            )
                        }
                    }
                }

                item {
                    val phoneMask = MaskVisualTransformation("(##) ####-####")
                    val phone9DigitsMask = MaskVisualTransformation("(##) # ####-####")

                    FormComponent(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Outlined.Phone,
                        contentDescription = "Phone icon",
                        unselected = clientPhones.isEmpty(),
                    ) {
                        // TODO: implement multiple phone numbers
                        FormTextField(
                            label = "Telefone",
                            value = clientPhones,
                            onValueChange = {
                                if (it.length <= 11) clientPhones = it
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Next,
                            ),
                            visualTransformation = if (clientPhones.length <= 10) {
                                phoneMask
                            } else {
                                phone9DigitsMask
                            },
                            keyboardActions = KeyboardActions(onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }),
                        )
                    }
                }

                item {
                    val cpfMask = MaskVisualTransformation("###.###.###-##")
                    FormComponent(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Outlined.AccountBox,
                        contentDescription = "AccountBox icon",
                        unselected = clientCPF.isEmpty(),
                    ) {
                        FormTextField(
                            label = "CPF",
                            value = clientCPF,
                            onValueChange = {
                                if (it.length <= 11) clientCPF = it
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done,
                            ),
                            visualTransformation = cpfMask,
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    onEvent(
                                        ClientDetailEvent.OnMainAction(
                                            clientName,
                                            clientCPF,
                                            clientBirthDate,
                                            clientUF,
                                            listOf(clientPhones),
                                        )
                                    )
                                }
                            ),
                        )
                    }
                }
            }
        }

        // TODO: implement error handling
        when (actionUiState) {
            is ActionUiState.Errors -> {
                for (error in actionUiState.errorMessages) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = error,
                        color = Color.Red,
                    )
                }
            }
            else -> Unit
        }

        Spacer(modifier = Modifier.weight(1f))

        if (!screenUiState.isLoading()) {
            Button(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                onClick = {
                    onEvent(ClientDetailEvent.OnMainAction(
                        clientName, clientCPF, clientBirthDate, clientUF, listOf(clientPhones))
                    )
                },
                shape = RoundedCornerShape(8.dp),
                enabled = !actionUiState.isLoading(),
                contentPadding = PaddingValues(vertical = 12.dp),
            ) {
                val text = when (screenUiState) {
                    is ScreenUiState.EditClient -> "Salvar alterações"
                    ScreenUiState.NewClient -> "Adicionar"
                    else -> if (actionUiState.isLoading()) "Carregando..." else ""
                }

                if (actionUiState.isLoading()) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp).size(16.dp),
                        color = Color.Gray.copy(alpha = 0.5f),
                        strokeWidth = 3.dp,
                        strokeCap = StrokeCap.Round,
                    )
                }

                Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun FormComponent(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String,
    unselected: Boolean,
    content: @Composable () -> Unit,
) {
    val iconTint = if (unselected) unselectedColor else MaterialTheme.colorScheme.primary
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
        )
        content()
    }
}

@Composable
private fun FormTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    readOnly: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onValueChange: (String) -> Unit,
) {
    BasicTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
        ),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        readOnly = readOnly,
        visualTransformation = visualTransformation,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty()) Text(text = label, color = unselectedColor, fontSize = 16.sp)
                innerTextField()
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewClientDetailScreen() {
    MaxProcessTheme {
        ClientDetailScreen(
            modifier = Modifier.fillMaxSize(),
            screenUiState = ScreenUiState.NewClient,
            actionUiState = ActionUiState.Idle,
            onEvent = { },
            onBack = { },
        )
    }
}
