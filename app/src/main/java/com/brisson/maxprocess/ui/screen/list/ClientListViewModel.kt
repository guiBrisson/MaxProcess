package com.brisson.maxprocess.ui.screen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brisson.maxprocess.domain.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientListViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
) : ViewModel() {
    val clientListUiState: StateFlow<ClientListUiState> = clientRepository.clientList()
        .map { clients -> ClientListUiState.Success(clients) }
        .catch { t -> ClientListUiState.Error(t.message.toString()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), ClientListUiState.Loading)

    fun handleEvents(event: ClientListEvent) {
        when (event) {
            is ClientListEvent.OnDeleteClient -> deleteClient(event.clientId)
        }
    }

    private fun deleteClient(id: Long) {
        viewModelScope.launch {
            clientRepository.client(id)?.let { client ->
                clientRepository.delete(client)
            }
        }
    }
}
