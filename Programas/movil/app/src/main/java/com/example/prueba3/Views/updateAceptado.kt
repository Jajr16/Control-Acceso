package com.example.prueba3.Views

import RetroFit.RetrofitInstance
import com.example.prueba3.Clases.UpdateAceptadoRequest
import retrofit2.HttpException

// Función que hace la solicitud para actualizar el estado de aceptación
suspend fun updateAceptado(boleta: String, idETS: Int, aceptado: Boolean) {
    val request = UpdateAceptadoRequest(boleta, idETS, aceptado)

    try {
        val response = RetrofitInstance.aceptadoApi.updateAceptado(request)

        // Aquí puedes manejar la respuesta
        println("Respuesta del servidor: $response")
    } catch (e: HttpException) {
        // Manejar errores relacionados con la solicitud HTTP
        println("Error al hacer la solicitud: ${e.message()}")
    } catch (e: Exception) {
        // Manejar otros errores
        println("Error desconocido: ${e.localizedMessage}")
    }
}