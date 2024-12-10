package com.example.prueba3

import com.example.prueba3.Clases.ListadoETS
import retrofit2.http.GET

interface ListadoEtsApi {
    @GET("ETS/") // Asegúrate de que este sea el endpoint correcto.
    suspend fun getEtsList(): List<ListadoETS>
}
