package RetroFit

import com.example.prueba3.Clases.DataPersona
import retrofit2.http.GET
import retrofit2.http.Path


interface PersonaApi {
    @GET("persona/datos/{usuario}")
    suspend fun getdatospersona(@Path("usuario")usuario: String): List<DataPersona>
}