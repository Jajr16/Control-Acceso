package com.example.prueba3.Clases

data class ReporteData(
    val curp: String?,
    val nombre: String?,
    val apellidoP: String?,
    val apellidoM: String?,
    val escuela: String?,
    val carrera: String?,
    val periodo: String?,
    val tipo: String?,
    val turno: String?,
    val materia: String?,
    val fechaIngreso: String?,
    val horaIngreso: String?,
    val nombreDocente: String?,
    val tipoEstado: String?,
    val imagenAlumno: ByteArray?,
    val presicion: String?,
    val motivo: String?
)