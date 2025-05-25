package com.example.prueba3.Clases

import androidx.compose.ui.graphics.Color

data class ListadoETS (
    val idETS: Int,
    val idPeriodo: String,
    val turno: String,
    val fecha: String,
    val unidadAprendizaje: String,
    val inscrito: Boolean,
    val carrera: String
)
