package com.example.patients.ui.consultations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.patients.PatientsApplication
import com.example.patients.data.repository.ConsultationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConsultationsViewModel(
    private val repository: ConsultationRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<ConsultationsUiState>(ConsultationsUiState.Loading)
    val uiState: StateFlow<ConsultationsUiState> = _uiState.asStateFlow()

    init {
        loadConsultations()
    }

    fun loadConsultations() {
        _uiState.value = ConsultationsUiState.Loading

        viewModelScope.launch {
            repository.getConsultations()
                .onSuccess { consultations ->
                    _uiState.value = ConsultationsUiState.Success(consultations)
                }
                .onFailure { e ->
                    _uiState.value = ConsultationsUiState.Error(
                        e.message ?: "Erreur inconnue"
                    )
                }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                        as PatientsApplication

                ConsultationsViewModel(app.container.consultationRepository)
            }
        }
    }
}