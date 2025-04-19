package com.example.prueba3.Clases

data class Reemplazo(
    val idETS: Int,
    val docenteRFC: String,
    val motivo: String,
    val estatus: String
)

data class VerificacionSolicitudResponse(
    val tieneSolicitudPendiente: Boolean,
    val solicitudExistente: Reemplazo? = null
)