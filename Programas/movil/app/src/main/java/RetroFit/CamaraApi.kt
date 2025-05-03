package RetroFit

import com.example.prueba3.Clases.PythonResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CamaraApi {
    @Multipart
    @POST("search_verify_student")
    suspend fun uploadImage(
        @Part("student_id") studentId: RequestBody,
        @Part img: MultipartBody.Part,
        @Part("model_name") modelName: RequestBody = "Facenet".toRequestBody("text/plain".toMediaType()),
        @Part("detector_backend") detectorBackend: RequestBody = "fastmtcnn".toRequestBody("text/plain".toMediaType()),
        @Part("distance_metric") distanceMetric: RequestBody = "cosine".toRequestBody("text/plain".toMediaType()),
        @Part("enforce_detection") enforceDetection: RequestBody = "true".toRequestBody("text/plain".toMediaType()),
        @Part("align") align: RequestBody = "true".toRequestBody("text/plain".toMediaType())
    ): Response<PythonResponse>
}