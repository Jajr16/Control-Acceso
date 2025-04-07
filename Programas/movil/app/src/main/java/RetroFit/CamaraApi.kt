package RetroFit

import com.example.prueba3.Clases.PythonResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CamaraApi {
    @Multipart
    @POST("/procesar_imagen")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("boleta") boleta: RequestBody // Nuevo parámetro
    ): Response<PythonResponse>
}