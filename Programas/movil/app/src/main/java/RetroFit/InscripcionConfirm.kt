package RetroFit

import com.example.prueba3.Clases.confirmInscripcion
import retrofit2.http.GET
import retrofit2.http.Path

interface InscripcionConfirm {
    @GET("/inscripciones/confirm/{Boleta}")
    suspend fun getConfirmInscrip(@Path("Boleta") Boleta: String): confirmInscripcion
}