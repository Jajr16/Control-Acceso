package RetroFit

import com.example.prueba3.Clases.UpdateAceptadoRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AceptadoApi {

    @POST("inscripciones/updateAceptado")
    suspend fun updateAceptado(@Body request: UpdateAceptadoRequest): String

}