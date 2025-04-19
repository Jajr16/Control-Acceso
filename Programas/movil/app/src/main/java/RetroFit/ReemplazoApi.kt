package RetroFit

import retrofit2.http.Query
import com.example.prueba3.Clases.Reemplazo
import com.example.prueba3.Clases.VerificacionSolicitudResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ReemplazoApi {
        @POST("api/reemplazos")
        suspend fun enviarSolicitud(@Body solicitud: Reemplazo): Reemplazo

        @GET("api/reemplazos/verificar-pendiente")
        suspend fun verificarSolicitudPendiente(
                @Query("etsId") etsId: Int,
                @Query("docenteRFC") docenteRFC: String
        ): VerificacionSolicitudResponse
}