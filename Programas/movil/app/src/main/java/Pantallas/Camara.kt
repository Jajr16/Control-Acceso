package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import RetroFit.RetrofitInstance
import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.os.Build
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.prueba3.R
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.ui.theme.BlueBackground
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executor

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Camara(navController: NavController, boleta: String, idETS: String, loginViewModel: LoginViewModel) {
    ValidateSession(navController = navController) {
        val permissions = rememberMultiplePermissionsState(
            permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                listOf(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                listOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        )
        val context = LocalContext.current
        val camaraController = remember { LifecycleCameraController(context) }
        val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current
        val userRole = loginViewModel.getUserRole()

        LaunchedEffect(key1 = Unit) {
            permissions.launchMultiplePermissionRequest()
        }

        Scaffold(
            topBar = {
                MenuTopBar(
                    true, true, loginViewModel,
                    navController
                )
            },
            bottomBar = {
                MenuBottomBar(navController = navController, userRole)
            },
            containerColor = Color.Transparent, // Color transparente para el contenedor
            contentColor = Color.White // Color del contenido
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground) // Aplica el color de fondo aquí
                    .padding(paddingValues)
            ) {
                // Título
                Text(
                    text = "Tome la fotografía",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                // Cámara
                if (permissions.allPermissionsGranted) {
                    CamaraComposable(
                        camaraController,
                        lifecycle,
                        modifier = Modifier
                            .weight(1f) // Expande la cámara
                    )
                } else {
                    Text(
                        text = "Permisos denegados",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }

                // Botón de captura
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    FloatingActionButton(
                        onClick = {
                            val executor = ContextCompat.getMainExecutor(context)
                            tomarFoto(camaraController, executor) { bytes ->
                                GlobalScope.launch(Dispatchers.IO) {
                                    enviarFotoAlServidor(bytes, boleta, idETS, context) // Pasa idETS aquí
                                }
                            }
                        }
                    ) {
                        Icon(
                            painterResource(id = R.drawable.icon_camara),
                            tint = Color.White,
                            contentDescription = ""
                        )
                    }
                }
            }
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
        modifier = modifier.fillMaxSize(), // Usar fillMaxSize()
        factory = {
            PreviewView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                controller = camaraController
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
            }
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
                enviarFotoAlServidor(bytes)
            }

            override fun onError(exception: ImageCaptureException) {
                println("Error al capturar la fotografía: ${exception.message}")
            }
        }
    )
}


fun enviarFotoAlServidor(bytes: ByteArray, boleta: String, idETS: String, context: Context) {
    val apiService = RetrofitInstance.apiRed

    val imageRequestBody = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, bytes.size)
    val imagePart = MultipartBody.Part.createFormData("image", "foto.jpg", imageRequestBody)

    val boletaRequestBody = boleta.toRequestBody("text/plain".toMediaTypeOrNull())

    // idETS como RequestBody con text/plain
    val idETSRequestBody = idETS.toRequestBody("text/plain".toMediaTypeOrNull())

    GlobalScope.launch(Dispatchers.IO) {
        try {
            val response = apiService.uploadImage(imagePart, boletaRequestBody, idETSRequestBody)

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Status: ${response.status}\nDetalles: ${response.detalles}",
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








