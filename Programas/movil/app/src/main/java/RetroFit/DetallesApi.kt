package RetroFit

import com.example.prueba3.Clases.ComparacionResponse
import com.example.prueba3.Clases.CredencialAlumnos
import com.example.prueba3.Clases.DatosWeb
import com.example.prueba3.Clases.DetalleAlumnos
import com.example.prueba3.Clases.credencialResponse
import com.example.prueba3.Clases.regitrarAsistencia
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface DetallesApi {

    @GET("alumno/detalle/{boleta}")
    suspend fun getalumnosDetalle(@Path("boleta") boleta: String): List<DetalleAlumnos>

    @GET("alumno/credencial/{boleta}")
    suspend fun getalumnosCredencial(@Path("boleta") boleta: String): List<CredencialAlumnos>

    @GET("/ImageDAE/capturar")
    suspend fun getCredencial(@Query("url") url: String): Response<credencialResponse>

    @POST("alumno/comparar/{boleta}")
    suspend fun compararDatos(@Path("boleta") boleta: String, @Body datosWeb: DatosWeb): ComparacionResponse

    @POST("alumno/registrar-asistencia")
    suspend fun registrarAsistencia(
        @Query("boleta") boleta: String,
        @Query("fecha") fecha: String,
        @Query("hora") hora: String,
        @Query("idETS") idETS: Int
    ): Response<regitrarAsistencia>

    @GET("alumno/verificar-asistencias")
    suspend fun verificarAsistencias(
        @Query("boletas") boletas: List<String>,
        @Query("fecha") fecha: String
    ): Response<Map<String, Boolean>>
}