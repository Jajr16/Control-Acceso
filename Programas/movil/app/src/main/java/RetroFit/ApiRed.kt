package RetroFit

import com.example.prueba3.Clases.RespuestaRed
import com.google.android.gms.common.api.Response
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiRed {
    @Multipart
    @POST("/red/")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("boleta") boleta: RequestBody,
        @Part("idETS") idETS: RequestBody // Mantenemos RequestBody
    ): ServerResponse
}

data class ServerResponse(
    val status: String,
    val detalles: String
)