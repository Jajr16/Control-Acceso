package RetroFit

import com.example.prueba3.Clases.Reemplazo
import com.google.android.gms.common.api.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ReemplazoApi {
        @POST("api/reemplazos")
        suspend fun enviarSolicitud(@Body reemplazo: Reemplazo): Reemplazo
}