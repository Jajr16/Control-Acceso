package com.example.prueba3.Clases

import com.google.gson.annotations.SerializedName

data class ListadoUsuarios (
    var usuario: String,
    var nombre: String,
    var tipoU: String
)

//  CLASES PARA RECIBIR LOS CHATS DEL USUARIO
data class ListChatResponse(
    @SerializedName("destinatario") val destinatario: String,
    @SerializedName("nombre") val nombre: String
)

data class ChatsResponses(
    val chats: List<ListChatResponse>?,
    val mensaje: String?
)

//  CLASE PARA RECIBIR LOS MENSAJES DE DOS USUARIOS
data class Mensaje(
    val usuario: String,
    val fecha: String,
    val mensaje: String
)

data class Page<T>(
    val content: List<T>,
    val number: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
)

// ENVIAR MENSAJES
data class sendMensaje(
    val remitente: String,
    val destinatario: String,
    val mensaje: String
)

data class MensajeResponse(
    val success: Boolean
)