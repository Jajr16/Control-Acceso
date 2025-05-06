package com.example.prueba3.Clases

data class ListaInfor(
    val boleta: String,
    val nombre: String,
    val apellidoP: String,
    val apellidoM: String,
    val turno: String,
    val error: Int,
    val asistenciaRegistrada: Boolean = false
)
