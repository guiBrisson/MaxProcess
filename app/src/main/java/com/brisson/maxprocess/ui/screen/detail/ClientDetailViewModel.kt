package com.brisson.maxprocess.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brisson.maxprocess.domain.model.Client
import com.brisson.maxprocess.domain.repository.ClientRepository
import com.brisson.maxprocess.ui.navigation.NavUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ClientDetailViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val clientId: Long = checkNotNull(savedStateHandle.get<Long>(NavUtils.CLIENT_ID_ARG))
    private val _screenUiState = MutableStateFlow<ScreenUiState>(ScreenUiState.Loading)
    val screenUiState: StateFlow<ScreenUiState> = _screenUiState.asStateFlow()

    private val _actionUiState = MutableStateFlow<ActionUiState>(ActionUiState.Idle)
    val actionUiState: StateFlow<ActionUiState> = _actionUiState.asStateFlow()

    fun handleEvents(event: ClientDetailEvent) {
        when (event) {
            ClientDetailEvent.OnInitialLoad -> updateScreenUiState()

            is ClientDetailEvent.OnMainAction -> {
                saveClient(event.name, event.cpf, event.birthDate, event.uf, event.phones)
            }
        }
    }

    private fun updateScreenUiState() {
        _screenUiState.value = ScreenUiState.Loading
        viewModelScope.launch {
            val client = clientRepository.client(clientId)

            _screenUiState.value = if (client == null) {
                ScreenUiState.NewClient
            } else {
                ScreenUiState.EditClient(client)
            }
        }
    }

    private fun saveClient(
        name: String,
        cpf: String?,
        birthDate: String?,
        uf: String?,
        phones: List<String>?,
    ) {
        _actionUiState.value = ActionUiState.Loading
        val errorMessages = mutableListOf<String>()

        // validate name
        if (name.isBlank()) errorMessages.add("Nome é obrigatório")

        // validate the birthDate
        val formattedBirthDate: Date? = if (!birthDate.isNullOrEmpty()) {
            parseBirthDate(birthDate) { error -> errorMessages.add(error) }
        } else null


        // enforce the CPF when UF is SP
        if (uf == "SP" && cpf.isNullOrEmpty()) {
            errorMessages.add("CPF é obrigatório para clientes de SP")
        }

        // enforce over 18 year old only when UF is MG
        val eighteenYearsAgo = Calendar.getInstance().apply { add(Calendar.YEAR, -18) }
        val ofLegalAge = formattedBirthDate?.after(eighteenYearsAgo.time) == false
        if (uf == "MG" && (formattedBirthDate == null || !ofLegalAge)) {
            errorMessages.add("Clientes de MG devem ter no mínimo 18 anos")
        }

        // if there are any errors, update the UI state with the error messages and early return
        if (errorMessages.isNotEmpty()) {
            _actionUiState.value = ActionUiState.Errors(errorMessages)
            return
        }

        val filteredPhones = phones?.filter { it.isNotEmpty() }

        val client: Client = when (val state = _screenUiState.value) {
            is ScreenUiState.EditClient -> state.client.copy(
                name = name,
                cpf = cpf,
                birthDate = formattedBirthDate,
                uf = uf,
                phones = filteredPhones,
            )

            else -> Client(name, cpf, formattedBirthDate, uf, filteredPhones)
        }

        viewModelScope.launch {
            val id: Long? = clientRepository.save(client)
            _actionUiState.value = if (id != null) {
                ActionUiState.Saved
            } else {
                ActionUiState.Errors(listOf("Erro interno ao salvar o cliente"))
            }
        }
    }

    private fun parseBirthDate(bd: String, onInvalid: (message: String) -> Unit): Date? {
        val pattern = "ddMMyyyy"
        if (bd.length != pattern.length) {
            onInvalid("Data de nascimento inválida (Exemplo: 01/02/2003)")
            return null
        }

        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.isLenient = false // This ensures strict date validation

        try {
            // If the parsing succeeds, the date is valid
            val date: Date? = sdf.parse(bd)

            // compare with current date
            val currentDate = Date()
            if (date?.after(currentDate) == true) {
                onInvalid("Data de nascimento inválida")
                return null
            }

            // date parsed successfully
            return date
        } catch (e: ParseException) {
            onInvalid("Data de nascimento inválida")
            return null
        }
    }
}
