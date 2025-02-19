    package RetroFit

    import com.example.prueba3.Clases.confirmInscripcion
    import com.example.prueba3.LoginApi
    import com.example.prueba3.EtsApi
    import com.example.prueba3.EtsInfoApi
    import okhttp3.OkHttpClient
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory
    import java.util.concurrent.TimeUnit


    object RetrofitInstance {
        private const val BASE_URL = "http://192.168.100.2:8080/" // Cambia esta URL al dominio real.

        private val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)  // Timeout de conexión
            .writeTimeout(120, TimeUnit.SECONDS)    // Timeout para escritura
            .readTimeout(120, TimeUnit.SECONDS)     // Timeout para lectura
            .build()

        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }

        val loginApi: LoginApi by lazy {
            retrofit.create(LoginApi::class.java)
        }

        val ETSapi: EtsApi by lazy {
            retrofit.create(EtsApi::class.java)
        }

        val ETSListapi: EtsInfoApi by lazy {
            retrofit.create(EtsInfoApi::class.java)
        }

        val alumnosApi: AlumnosApi by lazy {
            retrofit.create(AlumnosApi::class.java)
        }

        // Instancia de AceptadoApi
        val aceptadoApi: AceptadoApi by lazy {
            retrofit.create(AceptadoApi::class.java)
        }

        val confirmacionInscripcion: InscripcionConfirm by lazy {
            retrofit.create(InscripcionConfirm::class.java)
        }

        val getCalendar : Calendario by lazy {
            retrofit.create(Calendario::class.java)
        }

        val getDaysETS: Calendario by lazy {
            retrofit.create(Calendario::class.java)
        }


    }


