package com.example.patients.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.patients.PatientsApplication
import com.example.patients.data.model.Patient
import com.example.patients.data.model.PatientRequest
import com.example.patients.data.repository.PatientRepository
import com.example.patients.ui.patients.PatientsUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PatientViewModel(
    private val repository: PatientRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<PatientsUiState>(PatientsUiState.Loading)
    val uiState: StateFlow<PatientsUiState> = _uiState.asStateFlow()

    // Filtre courant : null = "Tous"
    private val _statut = MutableStateFlow<String?>(null)
    val statut: StateFlow<String?> = _statut.asStateFlow()

    private val _messages = MutableSharedFlow<String>()
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    init {
        loadPatients()
    }

    fun setStatut(value: String?) {
        _statut.value = value
        loadPatients()
    }

    fun loadPatients() {
        _uiState.value = PatientsUiState.Loading

        viewModelScope.launch {
            repository.getPatients(_statut.value)
                .onSuccess { patients ->
                    _uiState.value = PatientsUiState.Success(patients)
                }
                .onFailure { e ->
                    _uiState.value = PatientsUiState.Error(
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

                PatientViewModel(app.container.patientRepository)
            }
        }
    }

    fun addPatient(request: PatientRequest) {
        viewModelScope.launch {
            repository.createPatient(request)
                .onSuccess { patient ->
                    updateList { it + patient }
                    _messages.emit("${patient.prenom} ${patient.nom} ajouté")
                }
                .onFailure { e -> _messages.emit(e.message ?: "Ajout impossible") }
        }
    }

    private fun updateList(transform: (List<Patient>) -> List<Patient>) {
        _uiState.update { state ->
            if (state is PatientsUiState.Success) state.copy(patients = transform(state.patients))
            else state
        }
    }

    fun deletePatient(patient: Patient) {
        viewModelScope.launch {
            repository.deletePatient(patient.id)
                .onSuccess {
                    updateList { list -> list.filterNot { it.id == patient.id } }
                    _messages.emit("${patient.prenom} ${patient.nom} supprimé")
                }
                .onFailure { e -> _messages.emit(e.message ?: "Suppression impossible") }
        }
    }

    fun updatePatient(id: String, request: PatientRequest) {
        viewModelScope.launch {
            repository.updatePatient(id, request)
                .onSuccess { patient ->
                    _uiState.update { state ->
                        if (state is PatientsUiState.Success) {
                            state.copy(
                                patients = state.patients.map {
                                    if (it.id == patient.id) patient else it
                                }
                            )
                        } else state
                    }
                    _messages.emit("Patient modifié")
                }
                .onFailure { e ->
                    _messages.emit("Erreur lors de la modification : ${e.message}")
                }
        }
    }
}