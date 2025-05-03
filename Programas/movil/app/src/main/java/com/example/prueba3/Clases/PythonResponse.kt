package com.example.prueba3.Clases

data class PythonResponse(
    val verified: Boolean,
    val distance: Double,
    val threshold: Double,
    val model: String,
    val detector_backend: String,
    val similarity_metric: String
)
