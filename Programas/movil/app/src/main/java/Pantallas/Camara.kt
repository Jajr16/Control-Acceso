package Pantallas

import RetroFit.ApiRed
import RetroFit.RetrofitInstanceRed
import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import android.view.WindowInsetsAnimation
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.prueba3.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.common.api.Response
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.Executor

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Camara( navController: NavController) {
    val permissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    )

    val faceDetectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()

    val faceDetector = FaceDetection.getClient(faceDetectorOptions)

    //val permisoCamaraState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val context = LocalContext.current
    val camaraController = remember { LifecycleCameraController(context) }
    val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current

    val directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absoluteFile

    // Corutina para lanzar la solicitud de permiso de la camara
    LaunchedEffect(key1 = Unit) {
        //permisoCamaraState.launchPermissionRequest()
        permissions.launchMultiplePermissionRequest()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val executor = ContextCompat.getMainExecutor(context)
                    tomarFoto(camaraController, executor) { bytes ->
                        enviarFotoAlServidor(bytes, context)
                    }
                }
            ) {
                Icon(
                    painterResource(id = R.drawable.icon_camara),
                    tint = Color.White,
                    contentDescription = ""
                )
            }

        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        if (permissions.allPermissionsGranted) {
            CamaraComposable(
                camaraController,
                lifecycle,
                modifier = Modifier.padding(it)
            )
        } else {
            Text(
                text = "Permisos denegados",
                modifier = Modifier
                    .padding(it)
            )
        }
    }
}

@Composable
fun CamaraComposable(
    camaraController: LifecycleCameraController,
    lifecycle: LifecycleOwner,
    modifier: Modifier = Modifier
) {
    camaraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    camaraController.bindToLifecycle(lifecycle)
    AndroidView(
        modifier = modifier,
        factory = {
            val previaView = PreviewView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            previaView.controller = camaraController
            previaView
        }
    )
}

private fun tomarFoto(
    camaraController: LifecycleCameraController,
    executor: Executor,
    enviarFotoAlServidor: (ByteArray) -> Unit
) {
    camaraController.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val buffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)
                image.close()

                // Llamada para enviar la foto al servidor
                enviarFotoAlServidor(bytes)
            }

            override fun onError(exception: ImageCaptureException) {
                println("Error al capturar imagen: ${exception.message}")
            }
        }
    )
}

fun enviarFotoAlServidor(bytes: ByteArray, context: Context) {
    // Prepara la imagen como MultipartBody
    val imageFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, bytes.size)
    val multipartBody = MultipartBody.Part.createFormData("image", "foto.jpg", imageFile)

    val retrofitInstance = RetrofitInstanceRed.instance

    // Lanza una coroutine para enviar la imagen
    GlobalScope.launch(Dispatchers.IO) {
        try {
            // Llamada al servidor usando Retrofit
            val response = retrofitInstance.uploadImage(multipartBody)

            // Manejo de la respuesta con 'status' y 'detalles'
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Respuesta del servidor: ${response.status} - ${response.detalles}",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}







