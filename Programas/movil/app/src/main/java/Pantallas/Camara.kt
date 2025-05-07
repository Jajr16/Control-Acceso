package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import android.Manifest
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.prueba3.R
import com.example.prueba3.Views.CamaraViewModel
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.ui.theme.BlueBackground
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executor


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Camara(
    navController: NavController,
    boleta: String,
    idETS: String,
    loginViewModel: LoginViewModel,
    cameraViewModel: CamaraViewModel,
) {

    val context = LocalContext.current
    val userRole = loginViewModel.getUserRole()

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

        val camaraController = remember { LifecycleCameraController(context) }
        val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current

        val pythonResponse = cameraViewModel.pythonResponse.collectAsState().value
        val errorMessage = cameraViewModel.errorMessage.collectAsState().value
        val precision = cameraViewModel.precision.value

        var showResultDialog by remember { mutableStateOf(false) }
        var reconocimientoExitoso by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }
        var showConnectionErrorDialog by remember { mutableStateOf(false) }

        LaunchedEffect(key1 = Unit) {
            permissions.launchMultiplePermissionRequest()
        }

        LaunchedEffect(key1 = pythonResponse) {
            isLoading = false
            pythonResponse?.let {
                reconocimientoExitoso = it.verified
                showResultDialog = true
            }
        }

        LaunchedEffect(errorMessage) {
            isLoading = false
            if (errorMessage != null) {
                if (errorMessage.contains("Error de red", ignoreCase = true)) {
                    showConnectionErrorDialog = true
                }
            }
        }

        Scaffold(
            topBar = { MenuTopBar(true, true, loginViewModel, navController) },
            bottomBar = { MenuBottomBar(navController = navController, userRole) },
            containerColor = Color.Transparent,
            contentColor = Color.White
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize() // Asegura que la Column también ocupe todo el espacio
                ) {
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

                    if (permissions.allPermissionsGranted) {
                        Box(modifier = Modifier.weight(1f)) {
                            CamaraComposable(
                                camaraController,
                                lifecycle,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
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

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        FloatingActionButton(
                            onClick = {
                                isLoading = true
                                cameraViewModel.setErrorMessage(null) // Limpiar errores previos
                                cameraViewModel.setPythonResponse(null) // Limpiar respuesta previa
                                val executor = ContextCompat.getMainExecutor(context)
                                tomarFoto(camaraController, executor, cameraViewModel) { bytes ->
                                    val tempFile = File.createTempFile("captured_image", ".jpg", context.cacheDir).apply {
                                        FileOutputStream(this).use { it.write(bytes) }
                                    }
                                    cameraViewModel.uploadImage(tempFile, boleta)
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

                if (isLoading) {
                    LoadingDialog()
                }
            }
        }

        if (showResultDialog) {
            ResultDialog(
                exito = reconocimientoExitoso,
                precision = precision,
                errorMessage = errorMessage, // Pasa el errorMessage
                onDismiss = {
                    showResultDialog = false
                    if (reconocimientoExitoso) {
                        if (userRole == "Docente")
                            navController.navigate("InfoA/$idETS/$boleta")
                        else {
                            navController.navigate("Menu Alumno")
                        }
                    }

                    if (!reconocimientoExitoso) {
                        if (userRole == "Docente")
                            navController.navigate("InfoA/$idETS/$boleta")
                        else {
                            navController.navigate("Menu Alumno")
                        }
                    }


                }
            )
        }

        if (showConnectionErrorDialog) {
            AlertDialog(
                onDismissRequest = { showConnectionErrorDialog = false },
                title = { Text("Error de Conexión") },
                text = { Text("No se pudo conectar con el servidor. Por favor, verifica tu conexión a internet e intenta de nuevo.") },
                confirmButton = {
                    Button(onClick = { showConnectionErrorDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }


    }
}

@Composable
fun LoadingDialog() {
    AlertDialog(
        onDismissRequest = { /* No se puede cerrar tocando fuera */ },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center, // Centrar verticalmente el contenido
                modifier = Modifier.fillMaxWidth() // Ocupar todo el ancho disponible
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Cargando...")
            }
        },
        confirmButton = {}
    )
}

@Composable
fun CamaraComposable(
    camaraController: LifecycleCameraController,
    lifecycle: LifecycleOwner,
    modifier: Modifier = Modifier
) {
    camaraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    camaraController.bindToLifecycle(lifecycle)
    AndroidView(
        modifier = modifier.fillMaxSize(),
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
    cameraViewModel: CamaraViewModel,
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

                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                cameraViewModel.setImagen(bitmap) // Actualiza imagenBitmap directamente

                Log.d("Camara", "ViewModel Bitmap: ${cameraViewModel.imagenBitmap.value}")

                enviarFotoAlServidor(bytes)
            }

            override fun onError(exception: ImageCaptureException) {
                println("Error al capturar la fotografía: ${exception.message}")
            }
        }
    )
}

@Composable
fun ResultDialog(exito: Boolean, precision: Float?, onDismiss: () -> Unit, errorMessage: String?) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (exito) "Reconocimiento Facial Exitoso" else "Reconocimiento Facial Fallido") },
        text = {
            if (exito) {
                if (precision != null) {
                    val precisionPorcentaje = precision * 100
                    if (precision >= 0.8) {
                        Text("Es casi seguro que el alumno es quien dice ser. \nPrecisión del reconocimiento facial: ${precisionPorcentaje}%")

                    }
                    if (precision >= 0.6 && precision < 0.8){
                        Text("Es dudosa la identidad del alumno. \nPrecisión del reconocimiento facial: ${precisionPorcentaje}%")

                    }

                }
            } else if (!exito) {
                if (precision != null) {
                    val precisionPorcentaje = precision * 100
                if (precision < 0.6 || precision == null) {
                    Text("El casi seguro que el alumno no es quien dice ser. \nPrecisión del reconocimiento facial: menor al 60% porciento%")

                }else if(precision?.toInt() == 999){

                    Text("No se detecta un rostro en la fotografia")

                }
                }
            }else {
                if (errorMessage != null && errorMessage.isNotEmpty()) {
                    Text("Error al realizar el reconocimiento facial: $errorMessage")
                } else {
                    Text("No se pudo realizar el reconocimiento facial.")
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}









