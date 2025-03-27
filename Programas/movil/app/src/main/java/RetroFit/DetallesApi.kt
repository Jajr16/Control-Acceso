package RetroFit

import com.example.prueba3.Clases.CredencialAlumnos
import com.example.prueba3.Clases.DetalleAlumnos
import com.example.prueba3.Clases.credencialResponse
import com.example.prueba3.Clases.regitrarAsistencia
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DetallesApi {

    @GET("alumno/detalle/{boleta}") suspend fun getalumnosDetalle(@Path("boleta") boleta: String): List<DetalleAlumnos>

    @GET("alumno/credencial/{boleta}")
    suspend fun getalumnosCredencial(@Path("boleta") boleta: String): List<CredencialAlumnos>

    @GET("/ImageDAE/capturar")
    suspend fun getCredencial(@Query("url") url: String): Response<credencialResponse>

    @GET("alumno/ingreso/{boleta}")
    suspend fun getregistrarEntrada(@Path("boleta") boleta: String): List<regitrarAsistencia>
}