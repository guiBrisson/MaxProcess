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
import javax.inject.Inject

@HiltViewModel
class ClientDetailViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val clientId: Long = checkNotNull(savedStateHandle.get<Long>(NavUtils.CLIENT_ID_ARG))

    private val _screenUiState = MutableStateFlow<ScreenUiState>(ScreenUiState.Loading)
    val screenUiState: StateFlow<ScreenUiState> = _screenUiState.asStateFlow()

    fun updateScreenUiState() {
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

}

sealed interface ScreenUiState {
    data object Loading : ScreenUiState
    data object NewClient : ScreenUiState
    data class EditClient(val client: Client) : ScreenUiState
}