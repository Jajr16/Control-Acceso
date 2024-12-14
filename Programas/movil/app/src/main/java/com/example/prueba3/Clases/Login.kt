package com.example.prueba3.Clases

data class LoginResponse(
    val Usuario: String,
    val Contraseña: String,
    val Error_code: Int,
    val Message: String
)
