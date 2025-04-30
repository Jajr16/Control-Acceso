package RetroFit

import com.example.prueba3.Clases.FirebaseResponse
import retrofit2.http.POST
import retrofit2.http.Query

interface TokenFirebase {
    @POST("/notificaciones/register")
    suspend fun registerToken(
        @Query("usuario") username: String,
        @Query("token") token: String
    ): retrofit2.Response<FirebaseResponse>
}

data class FirebaseResponse(
    val message: String,
    val status: Int
)
