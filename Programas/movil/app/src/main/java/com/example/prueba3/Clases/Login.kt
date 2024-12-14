package com.example.prueba3.Clases

data class LoginResponse(
    val Usuario: String,
    val Error_code: Int,
    val Message: String
)

data class LoginRequest(
    val Usuario: String,
    val Contraseña: String
)