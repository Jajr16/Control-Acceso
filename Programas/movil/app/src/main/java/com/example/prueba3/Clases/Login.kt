package com.example.prueba3.Clases

import java.util.Optional

data class LoginResponse(
    var usuario: String,
    var error_code: Int,
    var message: String,
    var rol: String,
    val cargos: Optional<List<String>>
)

data class LoginRequest(
    val usuario: String,
    val password: String
)