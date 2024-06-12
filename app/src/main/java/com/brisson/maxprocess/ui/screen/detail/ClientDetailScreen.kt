package com.brisson.maxprocess.ui.screen.detail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brisson.maxprocess.domain.model.mockedBrazilStates
import com.brisson.maxprocess.ui.component.form.FormComponent
import com.brisson.maxprocess.ui.component.form.FormTextField
import com.brisson.maxprocess.ui.component.snackbar.SnackbarProperties
import com.brisson.maxprocess.ui.component.snackbar.SnackbarType
import com.brisson.maxprocess.ui.theme.MaxProcessTheme
import com.brisson.maxprocess.ui.theme.lightStrokeColor
import com.brisson.maxprocess.ui.theme.unselectedColor
import com.brisson.maxprocess.ui.util.ButtonBottom
import com.brisson.maxprocess.ui.util.MaskVisualTransformation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ClientDetailRoute(
    modifier: Modifier = Modifier,
    onShowSnackbar: suspend (SnackbarProperties) -> Boolean,
    onBack: () -> Unit,
) {
    val viewModel: ClientDetailViewModel = hiltViewModel()
    val screenUiState by viewModel.screenUiState.collectAsStateWithLifecycle()
    val actionUiState by viewModel.actionUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.handleEvents(ClientDetailEvent.OnInitialLoad) }

    HandleActionUiState(actionUiState, screenUiState, onShowSnackbar, onBack)

    ClientDetailScreen(
        modifier = modifier then Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.ime),
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
    var ufDropdownExpanded by remember { mutableStateOf(false) }
    val toggleUfDropdown: () -> Unit = { ufDropdownExpanded = !ufDropdownExpanded }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    var clientName by remember { mutableStateOf("") }
    var clientBirthDate by remember { mutableStateOf("") }
    var clientUF by remember { mutableStateOf("") }
    val clientPhones = remember { mutableStateListOf("") }
    var clientCPF by remember { mutableStateOf("") }

    val onMainAction: () -> Unit = {
        onEvent(ClientDetailEvent.OnMainAction(clientName, clientCPF, clientBirthDate, clientUF, clientPhones))
        focusManager.clearFocus()
    }

    LaunchedEffect(screenUiState) {
        if (screenUiState is ScreenUiState.EditClient) {
            clientName = screenUiState.client.name
            clientUF = screenUiState.client.uf ?: ""
            clientCPF = screenUiState.client.cpf ?: ""

            screenUiState.client.phones?.let {
                clientPhones.clear()
                clientPhones.addAll(it)
            }

            val formatter = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
            clientBirthDate = screenUiState.client.birthDate?.let { formatter.format(it) } ?: ""
        }
    }

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

        if (screenUiState.isLoading()) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().height(1.dp),
                trackColor = lightStrokeColor,
            )
        } else {
            Divider(modifier = Modifier.fillMaxWidth(), color = lightStrokeColor)
        }

        if (!screenUiState.isLoading()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = ButtonBottom,
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp,
                    top = 40.dp, bottom = 16.dp
                ),
            ) {
                item {
                    val errorMessage = actionUiState.formError<FormError.NameError>()
                    FormComponent(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        icon = Icons.Outlined.Person,
                        contentDescription = "Person icon",
                        unselected = clientName.isEmpty(),
                        hasError = (errorMessage != null),
                    ) {
                        FormTextField(
                            label = "Nome",
                            value = clientName,
                            onValueChange = { clientName = it },
                            errorMessage = errorMessage?.message,
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
                    val errorMessage = actionUiState.formError<FormError.BirthDateError>()

                    FormComponent(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        icon = Icons.Outlined.DateRange,
                        contentDescription = "Date icon",
                        unselected = clientBirthDate.isEmpty(),
                        hasError = (errorMessage != null),
                    ) {
                        FormTextField(
                            label = "Data de nascimento",
                            value = clientBirthDate,
                            onValueChange = {
                                if (it.length <= 8) clientBirthDate = it
                            },
                            errorMessage = errorMessage?.message,
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
                            .padding(vertical = 8.dp)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = { toggleUfDropdown() }
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
                                onExpandedChange = { toggleUfDropdown() }
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
                    val cpfMask = MaskVisualTransformation("###.###.###-##")
                    val errorMessage = actionUiState.formError<FormError.CPFError>()

                    FormComponent(
                        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
                        icon = Icons.Outlined.AccountBox,
                        contentDescription = "AccountBox icon",
                        unselected = clientCPF.isEmpty(),
                        hasError = (errorMessage != null),
                    ) {
                        FormTextField(
                            label = "CPF",
                            value = clientCPF,
                            onValueChange = { if (it.length <= 11) clientCPF = it },
                            errorMessage = errorMessage?.message,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next,
                            ),
                            visualTransformation = cpfMask,
                            keyboardActions = KeyboardActions(onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }),
                        )
                    }
                }

                item {
                    val phoneMask = MaskVisualTransformation("(##) ####-####")
                    val phone9DigitsMask = MaskVisualTransformation("(##) # ####-####")

                    FormComponent(
                        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
                        icon = Icons.Outlined.Phone,
                        contentDescription = "Phone icon",
                        verticalAlignment = Alignment.Top,
                        unselected = clientPhones.find { it.isNotEmpty() } == null,
                    ) {
                        Column {
                            clientPhones.forEachIndexed { index, phone ->
                                Row(
                                    modifier = Modifier,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    if (index != 0) {
                                        IconButton(
                                            modifier = Modifier,
                                            onClick = { clientPhones.removeAt(index) },
                                            colors = IconButtonDefaults.iconButtonColors(
                                                contentColor = unselectedColor,
                                            ),
                                        ) {
                                            Icon(
                                                modifier = Modifier.size(20.dp),
                                                imageVector = Icons.Default.Remove,
                                                contentDescription = "Remove icon",
                                            )
                                        }
                                    }
                                    
                                    val formModifier = if (index == clientPhones.lastIndex) {
                                        Modifier.focusRequester(focusRequester)
                                    } else Modifier

                                    FormTextField(
                                        modifier = formModifier,
                                        label = "Telefone",
                                        value = phone,
                                        onValueChange = {
                                            if (it.length <= 11) clientPhones[index] = it
                                        },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Phone,
                                            imeAction = ImeAction.Next,
                                        ),
                                        visualTransformation = if (phone.length <= 10) {
                                            phoneMask
                                        } else {
                                            phone9DigitsMask
                                        },
                                        keyboardActions = KeyboardActions(
                                            onDone = { onMainAction() }
                                        )
                                    )
                                }
                            }

                            TextButton(
                                onClick = {
                                    coroutineScope.launch {
                                        clientPhones.add("")
                                        delay(100) // give time to create a new form field
                                        focusRequester.requestFocus()
                                    }
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = unselectedColor,
                                ),
                            ) {
                                Icon(
                                    modifier = Modifier.padding(end = 12.dp).size(24.dp),
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add icon",
                                )

                                Text(
                                    text = "Adicionar mais",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                )
                            }
                        }

                    }
                }

                item {
                    Button(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        onClick = { onMainAction() },
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
    }
}

@Composable
private fun HandleActionUiState(
    actionUiState: ActionUiState,
    screenUiState: ScreenUiState,
    onShowSnackbar: suspend (SnackbarProperties) -> Boolean,
    onBack: () -> Unit
) {
    LaunchedEffect(actionUiState) {
        when (actionUiState) {
            is ActionUiState.Errors -> {
                onShowSnackbar(
                    SnackbarProperties(
                        title = "Error",
                        message = "Ocorreu um erro interno ao salvar",
                        type = SnackbarType.ERROR,
                        actionLabel = "OK",
                    )
                )
            }

            ActionUiState.Saved -> {
                onBack()
                val snackbarProps = when (screenUiState) {
                    is ScreenUiState.EditClient -> SnackbarProperties(
                        title = "Editado com Sucesso",
                        message = "As informações foram atualizadas",
                        type = SnackbarType.SUCCESS,
                        actionLabel = "OK",
                    )

                    ScreenUiState.NewClient -> SnackbarProperties(
                        title = "Criado com Sucesso",
                        message = "Novo cliente adicionado",
                        type = SnackbarType.SUCCESS,
                        actionLabel = "OK",
                    )

                    else -> null
                }
                snackbarProps?.let { props -> onShowSnackbar(props) }
            }

            else -> Unit
        }
    }
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
