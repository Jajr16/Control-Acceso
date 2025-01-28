package RetroFit

import com.example.prueba3.Clases.CalendarDays
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface Calendario {

    @GET("/ImagePDF")
    suspend fun getCalendar(): Response<ResponseBody>

    @GET("/TimeToETS")
    suspend fun getDaysETS(): CalendarDays

}