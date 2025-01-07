package RetroFit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



object RetrofitInstanceRed {

    private const val BASE_URL = "http://192.168.1.175:5000/"  // Usa tu IP local o el dominio de tu servidor

    val instance: ApiRed by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiRed::class.java)
    }


}