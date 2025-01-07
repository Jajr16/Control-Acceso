package com.example.prueba3.Clases

data class LoginResponse(
    var Usuario: String,
    var Error_code: Int,
    var Message: String,
    var Rol: String
)

data class LoginRequest(
    val Usuario: String,
    val Contraseña: String
)