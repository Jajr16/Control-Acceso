package com.example.prueba3.Clases

data class LoginResponse(
    var usuario: String,
    var error_code: Int,
    var message: String,
    var rol: String
)

data class LoginRequest(
    val usuario: String,
    val password: String
)