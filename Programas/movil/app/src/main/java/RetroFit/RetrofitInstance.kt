    package RetroFit

    import com.example.prueba3.LoginApi
    import com.example.prueba3.EtsApi
    import com.example.prueba3.EtsInfoApi
    import okhttp3.OkHttpClient
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory
    import java.util.concurrent.TimeUnit

    object RetrofitInstance {
//        private const val BASE_URL = "http://192.168.1.72:8080/"
        private const val BASE_URL = "https://serverspringboot-asceeudmhackgbfr.mexicocentral-01.azurewebsites.net/"

        private val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)  // Timeout de conexión
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

        val listalumnos: ListalumnosApi by lazy {
            retrofit.create(ListalumnosApi::class.java)
        }

        val alumnosDetalle: DetallesApi by lazy {
            retrofit.create(DetallesApi::class.java)
        }

        val alumnosCredencial: DetallesApi by lazy {
            retrofit.create(DetallesApi::class.java)
        }

        val alumnoEspecifico: AlumnoEspecificoApi by lazy {
            retrofit.create(AlumnoEspecificoApi::class.java)
        }

        // Instancia de AceptadoApi
        val aceptadoApi: AceptadoApi by lazy {
            retrofit.create(AceptadoApi::class.java)
        }

        //Instancia para el alumno
        val confirmacionInscripcion: InscripcionConfirm by lazy {
            retrofit.create(InscripcionConfirm::class.java)
        }

        //Instancia para el personal de seguridad y docente
        val confirmacionValidacion: InscripcionConfirm by lazy {
            retrofit.create(InscripcionConfirm::class.java)
        }

        //Instancia para solicitar Reemplazo
        val reemplazoApi: ReemplazoApi by lazy {
            retrofit.create(ReemplazoApi::class.java)
        }

        val getCalendar : Calendario by lazy {
            retrofit.create(Calendario::class.java)
        }

        val getDaysETS: Calendario by lazy {
            retrofit.create(Calendario::class.java)
        }

        val getdatospersona: PersonaApi by lazy {
            retrofit.create(PersonaApi::class.java)
        }

        val sendTokenToBack: TokenFirebase by lazy {
            retrofit.create(TokenFirebase::class.java)
        }

        val getListaUsuariosChat: Mensajes by lazy {
            retrofit.create(Mensajes::class.java)
        }

        val apiReporte: ApiReporte by lazy {
            retrofit.create(ApiReporte::class.java)
        }

        val apiReporteInfo: ApiReporteInfo by lazy {
            retrofit.create(ApiReporteInfo::class.java)
        }


        val ingresoSalonApi: IngresoSalonApi by lazy {
            retrofit.create(IngresoSalonApi::class.java)
        }

        val aplicaApi: AplicaApi by lazy {
            retrofit.create(AplicaApi::class.java)
        }

    }


