package com.example.prueba3

import retrofit2.http.GET

interface EtsApi {
    @GET("ets") // Cambia "ets" por el endpoint que tu servidor use.
    suspend fun getEtsList(): List<EtsInfo>
}