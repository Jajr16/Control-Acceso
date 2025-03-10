package RetroFit

import com.example.prueba3.Clases.ListadoUsuarios
import retrofit2.http.GET

interface Mensajes {
    @GET("mensajes/PersonasToChat")
    suspend fun getPersonasToChat(): List<ListadoUsuarios>
}