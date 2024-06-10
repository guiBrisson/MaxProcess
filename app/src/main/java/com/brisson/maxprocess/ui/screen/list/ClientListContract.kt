package com.brisson.maxprocess.ui.screen.list

import com.brisson.maxprocess.domain.model.Client

sealed interface ClientListUiState {
    data class Success(val clients: List<Client>) : ClientListUiState
    data class Error(val message: String) : ClientListUiState
    data object Loading : ClientListUiState
}

sealed interface ClientListEvent {
    data class OnDeleteClient(val clientId: Long) : ClientListEvent
}
