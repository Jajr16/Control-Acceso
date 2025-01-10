package RetroFit

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface Calendario {

    @GET("/ImagePDF")
    suspend fun getCalendar(): Response<ResponseBody>

}