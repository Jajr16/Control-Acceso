package com.example.prueba3

import com.example.prueba3.Clases.SalonETSResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface EtsInfoApi {
    @GET("SalonETS/{ETSid}")
    suspend fun getEtsDetail(@Path("ETSid") ETSid: Int): SalonETSResponse
}