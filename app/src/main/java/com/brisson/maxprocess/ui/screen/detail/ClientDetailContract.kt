package com.brisson.maxprocess.ui.screen.detail

import android.provider.Telephony.MmsSms.PendingMessages
import com.brisson.maxprocess.domain.model.Client

/**
This UI state represents if the user is creating a new client or editing an existing one.
Is loading when the data is being fetched from the repository.
 **/
sealed interface ScreenUiState {
    data object Loading : ScreenUiState
    data object NewClient : ScreenUiState
    data class EditClient(val client: Client) : ScreenUiState

    fun isLoading(): Boolean = this is Loading
}

/**
This UI state represents the result of the action performed by the user when the main button
is clicked.
 **/
sealed interface ActionUiState {
    data object Idle : ActionUiState
    data object Loading : ActionUiState
    data object Saved : ActionUiState
    data class Errors(val errorMessages: List<String>) : ActionUiState

    fun isLoading(): Boolean = this is Loading
    fun isSuccess(): Boolean = this is Saved
}

sealed interface ClientDetailEvent {
    data class OnMainAction(
        val name: String,
        val cpf: String?,
        val birthDate: String?, // this is a string so the validation is done by the viewModel
        val uf: String?,
        val phones: List<String>?,
    ): ClientDetailEvent

    data object OnInitialLoad : ClientDetailEvent
}
