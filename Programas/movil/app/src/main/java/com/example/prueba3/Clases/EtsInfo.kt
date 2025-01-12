package com.example.prueba3.Clases

data class Salon(
    val numSalon: String,
    val Edificio: String,
    val Piso: String,
    val tipoSalon: String
)

data class Ets(
    val UnidadAprendizaje: String,
    val tipoETS: String,
    val idETS: Int,
    val idPeriodo: String,
    val Turno: String,
    val Fecha: String,
    val Cupo: Int,
    val idUA: String,
    val Duracion: Int
)

data class SalonETSResponse(
    val ETS: Ets,
    val Salones: List<Salon>
)
