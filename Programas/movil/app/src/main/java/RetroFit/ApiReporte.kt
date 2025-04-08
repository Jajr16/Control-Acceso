package RetroFit

import com.example.prueba3.Clases.CreacionReporte
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.Response


interface ApiReporte {
    @Multipart
    @POST("red/") // Reemplaza con tu endpoint real
    suspend fun enviarDatos(
        @Part("razon") razon: RequestBody?,
        @Part("tipo") tipo: RequestBody,
        @Part("boleta") boleta: RequestBody,
        @Part("idETS") idETS: RequestBody,
        @Part("precision") precision: RequestBody?, // Acepta RequestBody?
        @Part("hora") hora: RequestBody,
        @Part imagen: MultipartBody.Part? // Acepta MultipartBody.Part?
    ): Response<CreacionReporte>
}

