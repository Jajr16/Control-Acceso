package RetroFit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://6750f41069dc1669ec1cb59e.mockapi.io/ETS/" // Cambia esta URL al dominio real.

    val api: EtsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EtsApi::class.java)
    }
}
