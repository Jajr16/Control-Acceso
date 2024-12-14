package RetroFit

import com.example.prueba3.EtsApi
import com.example.prueba3.EtsInfoApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://192.168.100.6/" // Cambia esta URL al dominio real.

    val ETSapi: EtsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EtsApi::class.java)
    }

    val ETSListapi: EtsInfoApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EtsInfoApi::class.java)
    }
}


