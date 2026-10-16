package com.example.patients.data.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer

/**
 * Accepts either an id string ("6ac2...") or a populated object
 * ({ "_id": "6ac2...", "nom": "..." }) and keeps only the id.
 */
object IdOrObjectSerializer : JsonTransformingSerializer<String?>(String.serializer().nullable) {
    override fun transformDeserialize(element: JsonElement): JsonElement =
        if (element is JsonObject) {
            element["_id"] ?: element["id"] ?: JsonNull
        } else {
            element
        }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Consultation(
    @SerialName("_id") @JsonNames("id")
    val id: String,
    @Serializable(with = IdOrObjectSerializer::class)
    val patient: String? = null,
    val date: String,
    val motif: String,
    val traitement: String? = null,
    val medecin: String? = null,
    val notes: String? = null,
    val statut: String = "En attente"
)