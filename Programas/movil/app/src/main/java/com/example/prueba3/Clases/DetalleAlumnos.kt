package com.example.prueba3.Clases

import java.sql.Time
import java.util.Date

data class DetalleAlumnos(
    val imagenCredencial: String?,
    val nombreAlumno: String,
    val apellidoPAlumno: String,
    val apellidoMAlumno: String,
    val boleta: String,
    val nombreETS: String,
    val nombreTurno: String,
    val salon: Integer,
    val fecha: String,
    val nombreDocente: String,
    val apellidoPDocente: String,
    val apellidoMDocente: String,
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

data class credencialResponse(
    val imagen: String,
    val credenciales: List<CredencialAlumnos>
)

data class regitrarAsistencia (
    val boleta: String,
    val nombre: String,
    val apellidoP: String,
    val apellidoM: String,
    val idETS: String,
    val fecha: Date,
    val horaIngreso: Time
)
