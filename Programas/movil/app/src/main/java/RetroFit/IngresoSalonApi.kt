package RetroFit

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Query

interface IngresoSalonApi {
    @GET("/verificarReporte")
    suspend fun verificarIngreso(
        @Query("idets") idets: Int,
        @Query("boleta") boleta: String
    ): Response<JsonObject> // Cambia a JsonObject

    @DELETE("/eliminarReporte") // Usar @DELETE de Retrofit
    suspend fun eliminarReporte(
        @Query("idets") idets: Int,
        @Query("boleta") boleta: String
    ): Response<JsonObject>


}