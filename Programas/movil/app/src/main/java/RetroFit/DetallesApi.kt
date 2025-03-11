package RetroFit

import com.example.prueba3.Clases.CredencialAlumnos
import com.example.prueba3.Clases.DetalleAlumnos
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DetallesApi {

    @GET("alumno/detalle/{boleta}") suspend fun getalumnosDetalle(): List<DetalleAlumnos>

    @GET("alumno/credencial/{boleta}")
    suspend fun getalumnosCredencial(@Path("boleta") boleta: String): List<CredencialAlumnos>

    @GET("/ImageDAE/capturar")
    suspend fun getCredencial(): Response<ResponseBody>
}