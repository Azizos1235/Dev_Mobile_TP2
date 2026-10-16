package com.example.patients

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.patients.ui.consultations.ConsultationsScreen
import com.example.patients.ui.patients.PatientsScreen
import com.example.patients.ui.theme.PatientsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PatientsTheme {
                PatientsApp()
            }
        }
    }
}

@Composable
fun PatientsApp() {
    // Patient whose consultations are shown; null means the patient list is displayed.
    var consultationsPatientId by rememberSaveable { mutableStateOf<String?>(null) }
    var consultationsPatientName by rememberSaveable { mutableStateOf("") }

    val patientId = consultationsPatientId

    if (patientId != null) {
        BackHandler { consultationsPatientId = null }
        ConsultationsScreen(
            patientId = patientId,
            patientName = consultationsPatientName,
            onBack = { consultationsPatientId = null }
        )
    } else {
        PatientsScreen(
            onShowConsultations = { patient ->
                consultationsPatientName = patient.nom
                consultationsPatientId = patient.id
            }
        )
    }
}