package RetroFit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

data class RfcResponse(val rfc: String)

interface AplicaApi {

    @GET("/api/aplica/docente/rfc/{idets}")
    fun obtenerRfcDocente(@Path("idets") idets: Int): Call<RfcResponse>

}