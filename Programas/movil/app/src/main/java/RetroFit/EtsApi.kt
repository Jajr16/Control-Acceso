package com.example.prueba3

import com.example.prueba3.Clases.ListadoETS
import retrofit2.http.GET
import retrofit2.http.Path

interface EtsApi {
    @GET("ETS/") // Asegúrate de que este sea el endpoint correcto.
    suspend fun getEtsList(): List<ListadoETS>

    @GET("ETS/InscripcionAlumno/{usuario}")
    suspend fun getEtsInscritos(@Path("usuario") usuario: String): List<ListadoETS>

    @GET("ETS/InscripcionDocente/{docente_rfc}")
    suspend fun getEtsInscritosDocente(@Path("docente_rfc") usuario: String): List<ListadoETS>


}