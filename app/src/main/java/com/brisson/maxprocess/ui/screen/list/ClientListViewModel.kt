package com.brisson.maxprocess.ui.screen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brisson.maxprocess.domain.model.Client
import com.brisson.maxprocess.domain.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ClientListViewModel @Inject constructor(
    clientRepository: ClientRepository,
) : ViewModel() {
    val clientListUiState: StateFlow<ListUiState> = clientRepository.clientList()
        .map { clients -> ListUiState.Success(clients) }
        .catch { t -> ListUiState.Error(t.message.toString()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), ListUiState.Loading)
}

sealed interface ListUiState {
    data class Success(val clients: List<Client>) : ListUiState
    data class Error(val message: String) : ListUiState
    data object Loading : ListUiState
}
