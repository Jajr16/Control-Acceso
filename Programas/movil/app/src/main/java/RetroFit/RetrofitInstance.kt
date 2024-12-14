    package RetroFit

    import com.example.prueba3.LoginApi
    import com.example.prueba3.EtsApi
    import com.example.prueba3.EtsInfoApi
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory

    object RetrofitInstance {
        private const val BASE_URL = "http://192.168.56.1:8000/" // Cambia esta URL al dominio real.

        val LoginApi: LoginApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(LoginApi::class.java)
        }

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


