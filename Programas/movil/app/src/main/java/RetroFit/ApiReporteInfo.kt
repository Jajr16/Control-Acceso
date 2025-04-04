package RetroFit


import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiReporteInfo {
    @GET("/reporte")
    suspend fun obtenerReporte(
        @Query("idets") idets: Int,
        @Query("boleta") boleta: String
    ): ResponseBody

    @GET("/imagenReporte")
    suspend fun obtenerImagenReporte(
        @Query("idets") idets: Int,
        @Query("boleta") boleta: String
    ): ResponseBody
}