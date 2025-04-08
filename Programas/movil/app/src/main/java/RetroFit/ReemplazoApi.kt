package RetroFit

import com.example.prueba3.Clases.Reemplazo
import retrofit2.http.Body
import retrofit2.http.POST

interface ReemplazoApi {
        @POST("solicitud-reemplazo")
        suspend fun enviarSolicitud(@Body reemplazo: Reemplazo): List<Reemplazo>
    }
