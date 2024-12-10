package RetroFit

import com.example.prueba3.EtsApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://172.100.91.74:8000/" // Cambia esta URL al dominio real.

    val api: EtsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EtsApi::class.java)
    }
}


