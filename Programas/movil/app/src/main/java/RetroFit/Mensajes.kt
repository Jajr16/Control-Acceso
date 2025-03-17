package RetroFit

import com.example.prueba3.Clases.ChatsResponses
import com.example.prueba3.Clases.ListadoUsuarios
import com.example.prueba3.Clases.Mensaje
import com.example.prueba3.Clases.MensajeResponse
import com.example.prueba3.Clases.sendMensaje
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Mensajes {
    @GET("mensajes/PersonasToChat")
    suspend fun getPersonasToChat(): List<ListadoUsuarios>

    @GET("mensajes/{usuario}")
    suspend fun getChats(@Path("usuario") user: String): Response<ChatsResponses>

    @GET("mensajes/historial/{remitente}/{destinatario}")
    suspend fun getHistorial(@Path("remitente") remitente: String,
                             @Path("destinatario") destinatario: String): Response<List<Mensaje>>

    @POST("mensajes/enviar")
    suspend fun enviarMensaje(@Body mensaje: sendMensaje): MensajeResponse
}