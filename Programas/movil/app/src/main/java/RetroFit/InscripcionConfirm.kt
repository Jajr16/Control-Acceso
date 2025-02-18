package RetroFit

import com.example.prueba3.Clases.ConfirmValidacion
import com.example.prueba3.Clases.confirmInscripcion
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface InscripcionConfirm {

    // Validar inscripción de un alumno por boleta
    @GET("/inscripciones/confirm/boleta/{Boleta}")
    suspend fun getConfirmInscrip(@Path("Boleta") Boleta: String): confirmInscripcion

    // Validar usuario (Docente o Seguridad) por RFC
    @GET("/inscripciones/confirm/username/{username}")
    suspend fun getConfirmValid(@Path("username") username: String): ConfirmValidacion
}
