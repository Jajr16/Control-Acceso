package RetroFit

import com.example.prueba3.Clases.AlumnosInfo
import retrofit2.http.GET
import retrofit2.http.Path


interface AlumnosApi {
    @GET("alumno/inscritosETS")
    suspend fun getAlumnoList(
        @Path("ETSid") ETSid: String,
    ): List<AlumnosInfo>
}

