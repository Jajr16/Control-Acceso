package RetroFit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



object RetrofitInstanceRed {

<<<<<<< HEAD
    private const val BASE_URL = "http://192.168.173.49:5000/"  // Usa tu IP local o el dominio de tu servidor
=======
    private const val BASE_URL = "http://192.168.1.69:5000/"  // Usa tu IP local o el dominio de tu servidor
>>>>>>> main

    val instance: ApiRed by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiRed::class.java)
    }


}