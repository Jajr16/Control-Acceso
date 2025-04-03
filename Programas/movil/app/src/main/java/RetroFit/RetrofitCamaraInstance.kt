package RetroFit

import com.example.prueba3.Clases.PythonResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Response // Importación correcta
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitCamaraInstance {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(120, TimeUnit.SECONDS)  // Timeout de conexión
        .writeTimeout(120, TimeUnit.SECONDS)    // Timeout para escritura
        .readTimeout(120, TimeUnit.SECONDS)     // Timeout para lectura
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.131:5000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val cameraApi = retrofit.create(CamaraApi::class.java)

    suspend fun uploadImage(image: MultipartBody.Part, boleta: RequestBody): Response<PythonResponse> { // Tipo de retorno correcto
        return cameraApi.uploadImage(image, boleta)
    }
}