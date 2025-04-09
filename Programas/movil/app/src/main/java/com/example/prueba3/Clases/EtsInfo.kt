package com.example.prueba3.Clases

data class Salon(
    val numSalon: String,
    val edificio: Int,
    val piso: Int,
    val tipoSalon: String
)

data class Ets(
    val unidadAprendizaje: String,
    val tipoETS: String,
    val idETS: Int,
    val idPeriodo: String,
    val turno: String,
    val fecha: String,
    val cupo: Int,
    val duracion: Int,
    val hora: String // Nuevo campo para la hora
)

data class SalonETSResponse(
    val ets: Ets,
    val salon: List<Salon>
)
