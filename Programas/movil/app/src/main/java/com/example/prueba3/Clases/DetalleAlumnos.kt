package com.example.prueba3.Clases

data class DetalleAlumnos(
    val foto: String?,
    val boleta: String,
    val nombre: String,
    val apellidoP: String,
    val apellidoM: String,
    val ets: String,
    val docente: String,
    val salon: Int,
    val turno: String,
    val fecha: String,
    val error: Int,
)

data class CredencialAlumnos(
    val ImagenCredencial: String?,
    val nombre: String,
    val apellidoP: String,
    val apellidoM: String,
    val boleta: String,
    val curp: String,
    val carrera: String,
    val unidadAcademica: String
)
