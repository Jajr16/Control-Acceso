package RetroFit

import com.example.prueba3.Clases.AlumnosInfo
import retrofit2.http.GET
import retrofit2.http.Path


interface AlumnosApi {
    // Endpoint para obtener una lista de alumnos
    @GET("alumno/inscritosETS/{ETSid}")
    suspend fun getAlumnoList(@Path("ETSid") ETSid: String): List<AlumnosInfo> // Cambiado a List
}

