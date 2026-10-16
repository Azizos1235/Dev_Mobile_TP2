package com.example.patients.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Consultation(
    val id: String,
    val patient: String? = null,
    val date: String,
    val motif: String,
    val traitement: String? = null,
    val medecin: String? = null,
    val notes: String? = null,
    val statut: String = "En attente"
)