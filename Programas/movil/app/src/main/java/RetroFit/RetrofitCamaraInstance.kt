package RetroFit

import com.example.prueba3.Clases.PythonResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response // Importación correcta
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitCamaraInstance {
        private val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Log request and response details
        }

        private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()

        private val retrofit = Retrofit.Builder()
            .baseUrl("https://face-verification-app-bze0emevhsh5cvdz.mexicocentral-01.azurewebsites.net/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()


        private val cameraApi = retrofit.create(CamaraApi::class.java)

    suspend fun uploadImage(studentId: String, imageFile: File): Result<PythonResponse> {
        return try {
            val imgPart = MultipartBody.Part.createFormData(
                "img",
                imageFile.name,
                imageFile.asRequestBody("image/png".toMediaType())
            )
            val studentIdBody = studentId.toRequestBody("text/plain".toMediaType())
            val response = cameraApi.uploadImage(studentIdBody, imgPart)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido en la respuesta del servidor."
                Result.failure(Exception("HTTP ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    }