package RetroFit

import com.example.prueba3.Clases.Docente
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

        @GET("api/reemplazos/pendientes")
        suspend fun obtenerSolicitudesPendientes(): List<Reemplazo>

        @POST("api/reemplazos/aprobar")
        suspend fun aprobarReemplazo(
                @Query("idETS") idETS: Int,
                @Query("docenteRFC") docenteRFC: String,
                @Query("docenteReemplazo") docenteReemplazo: String
        ): Reemplazo

        @POST("api/reemplazos/rechazar")
        suspend fun rechazarReemplazo(
                @Query("idETS") idETS: Int,
                @Query("docenteRFC") docenteRFC: String,
                @Query("motivo") motivo: String
        ): Reemplazo

        @GET("api/docentes")
        suspend fun obtenerDocentesDisponibles(): List<Docente>
}