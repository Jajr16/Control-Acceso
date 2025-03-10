package com.example.prueba3.Clases

import RetroFit.RetrofitInstance.sendTokenToBack
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

data class FirebaseResponse(
    val message: String,
    val status: Int
)

private fun sendTokenToBackend(username: String, token: String) {
    GlobalScope.launch {
        try {
            val response = sendTokenToBack.registerToken(username, token)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null) {
                    println("Respuesta del backend: ${apiResponse.message}")
                }
            } else {
                println("Error al registrar el token en el backend: ${response.message()}")
            }
        } catch (e: Exception) {
            println("Error de red: ${e.message}")
        }
    }
}

fun getFCMToken(username: String) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val token = task.result
            println("Token obtenido: $token")
            sendTokenToBackend(username, token)
        } else {
            println("Error al obtener el token " + task.exception)
        }
    }
}