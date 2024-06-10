package com.brisson.maxprocess.ui.screen.list

import com.brisson.maxprocess.domain.model.Client

sealed interface ListUiState {
    data class Success(val clients: List<Client>) : ListUiState
    data class Error(val message: String) : ListUiState
    data object Loading : ListUiState
}
