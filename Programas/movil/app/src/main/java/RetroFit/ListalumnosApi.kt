package RetroFit

import com.example.prueba3.Clases.ListaInfor
import retrofit2.http.GET
import retrofit2.http.Path

interface ListalumnosApi {
    @GET("alumno/inscritosETS/{fecha}/{periodo}")
    suspend fun getAlumnoLista(
        @Path("fecha") fecha: String,
        @Path("periodo") periodo: String,
    ): List<ListaInfor>
}