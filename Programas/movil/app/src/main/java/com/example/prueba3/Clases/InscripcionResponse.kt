package com.example.prueba3.Clases

data class InscripcionResponse(
    val idETS: Int,
    val Boleta: String,
    val CURP: String,
    val NombreA: String,
    val ApellidoP: String,
    val ApellidoM: String,
    val Sexo: String,
    val Correo: String,
    val Carrera: String
)