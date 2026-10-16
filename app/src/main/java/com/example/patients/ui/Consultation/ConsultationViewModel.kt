package com.example.patients.ui.consultations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.patients.PatientsApplication
import com.example.patients.data.repository.ConsultationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConsultationsViewModel(
    private val repository: ConsultationRepository,
    private val patientId: String
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<ConsultationsUiState>(ConsultationsUiState.Loading)
    val uiState: StateFlow<ConsultationsUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadConsultations()
    }

    fun loadConsultations() {
        loadJob?.cancel()
        _uiState.value = ConsultationsUiState.Loading

        loadJob = viewModelScope.launch {
            repository.getConsultations(patientId)
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
        fun factory(patientId: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                        as PatientsApplication

                ConsultationsViewModel(app.container.consultationRepository, patientId)
            }
        }
    }
}