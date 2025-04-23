package RetroFit

import com.example.prueba3.Clases.AlumnoEspecifico
import com.example.prueba3.Clases.FotoAlumnoResponse
import com.google.android.gms.common.api.Response
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface AlumnoEspecificoApi {

    @GET("/alumno/{boleta}")
    suspend fun getAlumnoEspecifico(@Path("boleta") boleta: String): AlumnoEspecifico

    @GET("/alumno/foto/{boleta}")
    suspend fun getFotoAlumno(@Path("boleta") boleta: String): FotoAlumnoResponse




}