package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
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
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
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

    var showInstructionsDialog by remember { mutableStateOf(true) }

    ValidateSession(navController = navController) {
        val permissions = rememberMultiplePermissionsState(
            permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                listOf(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                listOf(
                    Manifest.permission.CAMERA,
                    //Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    //Manifest.permission.READ_EXTERNAL_STORAGE
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
        var specificErrorMessage by remember { mutableStateOf<String?>(null) }

        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val alpha = if (isLoading) 0.5f else 1.0f

        LaunchedEffect(key1 = Unit) {
            permissions.launchMultiplePermissionRequest()
        }

        LaunchedEffect(key1 = pythonResponse) {
            if (pythonResponse != null) {
                isLoading = false
                if (!pythonResponse.verified && pythonResponse.distance.toInt() == 999) {
                    specificErrorMessage = "No se detecta un rostro en la fotografía."
                    reconocimientoExitoso = false
                } else {
                    reconocimientoExitoso = pythonResponse.verified
                    specificErrorMessage = null
                }
                showResultDialog = true
                cameraViewModel.setPythonResponse(null)
            }
        }

        LaunchedEffect(errorMessage) {
            if (errorMessage != null) {
                isLoading = false
                if (errorMessage.contains("502", ignoreCase = true) && errorMessage.contains("gateway", ignoreCase = true)) {
                    specificErrorMessage = "Error con el servidor."
                } else if (errorMessage.contains("timeout", ignoreCase = true) || errorMessage.contains("red", ignoreCase = true)) {
                    specificErrorMessage = "Error de red."
                } else {
                    specificErrorMessage = errorMessage
                }
                reconocimientoExitoso = false
                showResultDialog = true
                cameraViewModel.setErrorMessage(null)
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
                    modifier = Modifier.fillMaxSize()
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
                            if (userRole != null) {
                                CamaraComposable(
                                    camaraController,
                                    lifecycle,
                                    modifier = Modifier.fillMaxSize(),
                                    userRole
                                )
                            }
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
                                if (!isLoading) {
                                    isLoading = true
                                    cameraViewModel.setErrorMessage(null)
                                    cameraViewModel.setPythonResponse(null)
                                    specificErrorMessage = null
                                    val executor = ContextCompat.getMainExecutor(context)
                                    tomarFoto(camaraController, executor, cameraViewModel) { bytes ->
                                        val tempFile = File.createTempFile("captured_image", ".jpg", context.cacheDir).apply {
                                            FileOutputStream(this).use { it.write(bytes) }
                                        }
                                        cameraViewModel.uploadImage(tempFile, boleta)
                                    }
                                }
                            },
                            interactionSource = interactionSource,
                            modifier = Modifier
                                .semantics { role = Role.Button }
                                .clickable(
                                    enabled = !isLoading,
                                    onClick = {
                                        if (!isLoading) {
                                            isLoading = true
                                            cameraViewModel.setErrorMessage(null)
                                            cameraViewModel.setPythonResponse(null)
                                            specificErrorMessage = null
                                            val executor = ContextCompat.getMainExecutor(context)
                                            tomarFoto(camaraController, executor, cameraViewModel) { bytes ->
                                                val tempFile = File.createTempFile("captured_image", ".jpg", context.cacheDir).apply {
                                                    FileOutputStream(this).use { it.write(bytes) }
                                                }
                                                cameraViewModel.uploadImage(tempFile, boleta)
                                            }
                                        }
                                    }
                                )
                                .alpha(alpha)
                        ) {
                            Icon(
                                painterResource(id = R.drawable.icon_camara),
                                tint = Color.White.copy(alpha = alpha),
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
            if (userRole != null) {
                ResultDialog(
                    exito = reconocimientoExitoso,
                    precision = precision,
                    errorMessage = specificErrorMessage,
                    onDismiss = {
                        showResultDialog = false
                    },
                    navController = navController,
                    userRole = userRole,
                    idETS = idETS,
                    boleta = boleta,
                    cameraViewModel
                )
            }
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

        // Diálogo de instrucciones al entrar a la pantalla
        if (showInstructionsDialog) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Instrucciones") },
                text = {
                    Text(
                        "Para asegurar un reconocimiento facial exitoso, recuerda solicitar al alumno que se retire cualquier elemento que pueda obstruir su rostro. Algunos ejemplos incluyen gorras, cubrebocas y lentes. Asimismo, es importante que el entorno esté bien iluminado."
                    )
                },
                confirmButton = {
                    Button(onClick = { showInstructionsDialog = false }) {
                        Text("Entendido")
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
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
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
    modifier: Modifier = Modifier,
    userRole: String
) {

    if (userRole == "Personal Academico" || userRole == "Docente") {
        camaraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    }else{
        camaraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    }
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

                scaleType = PreviewView.ScaleType.FILL_CENTER
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

                val inputStream = ByteArrayInputStream(bytes)
                val exifInterface = ExifInterface(inputStream)
                val orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                inputStream.close()

                val originalBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                var rotatedBitmap = originalBitmap

                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> {
                        val matrix = Matrix()
                        matrix.postRotate(90f)
                        rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
                    }
                    ExifInterface.ORIENTATION_ROTATE_180 -> {
                        val matrix = Matrix()
                        matrix.postRotate(180f)
                        rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
                    }
                    ExifInterface.ORIENTATION_ROTATE_270 -> {
                        val matrix = Matrix()
                        matrix.postRotate(270f)
                        rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
                    }
                    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
                        val matrix = Matrix()
                        matrix.postScale(-1f, 1f)
                        rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
                    }
                    ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                        val matrix = Matrix()
                        matrix.postScale(1f, -1f)
                        rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
                    }
                    ExifInterface.ORIENTATION_TRANSPOSE -> {
                        val matrix = Matrix()
                        matrix.postRotate(90f)
                        matrix.postScale(-1f, 1f)
                        rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
                    }
                    ExifInterface.ORIENTATION_TRANSVERSE -> {
                        val matrix = Matrix()
                        matrix.postRotate(-90f)
                        matrix.postScale(-1f, 1f)
                        rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
                    }
                }

                // Ahora 'rotatedBitmap' debería estar en la orientación correcta (probablemente vertical si el usuario la tomó así)

                val targetWidth = 640
                val targetHeight = 480

                val scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap, targetWidth, targetHeight, true)

                cameraViewModel.setImagen(scaledBitmap)

                val outputStream = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                val resizedBytes = outputStream.toByteArray()
                outputStream.close()

                Log.d("Camara", "Tamaño de la imagen redimensionada: ${resizedBytes.size / 1024} KB (${scaledBitmap.width}x${scaledBitmap.height}), Orientación EXIF: $orientation")
                enviarFotoAlServidor(resizedBytes)
            }

            override fun onError(exception: ImageCaptureException) {
                println("Error al capturar la fotografía: ${exception.message}")
            }
        }
    )
}

@Composable
fun ResultDialog(
    exito: Boolean,
    precision: Float?,
    errorMessage: String?,
    onDismiss: () -> Unit,
    navController: NavController,
    userRole: String,
    idETS: String,
    boleta: String,
    cameraViewModel: CamaraViewModel
) {

    val isStudentNotFound = errorMessage?.contains("Student ID not found", ignoreCase = true) == true

    AlertDialog(
        onDismissRequest = {
            onDismiss()
            if (!isStudentNotFound && (errorMessage == null || !errorMessage.contains("No se detecta un rostro"))) {
                if (userRole == "Personal Academico" || userRole == "Docente") {
                    navController.navigate("InfoA/$idETS/$boleta")
                } else {
                    navController.navigate("Menu Alumno")
                }
            }
        },
        title = {
            Text(
                if (isStudentNotFound) {
                    "Reconocimiento Facial Fallido"
                } else {
                    if (exito) "Reconocimiento Facial Exitoso" else "Reconocimiento Facial Fallido"
                }
            )
        },
        text = {
            if (isStudentNotFound) {
                Text("El alumno todavía no está registrado en la red neuronal.")
            } else {
                errorMessage?.let {
                    Text(it)
                } ?: run {
                    if (exito) {
                        if (precision != null) {
                            val precisionPorcentaje = precision * 100
                            if (precision >= 0.8) {
                                Text("Es casi seguro que el alumno es quien dice ser. \nPrecisión del reconocimiento facial: ${String.format("%.2f", precisionPorcentaje)}%")
                            }
                            if (precision >= 0.6 && precision < 0.8) {
                                Text("Es dudosa la identidad del alumno. \nPrecisión del reconocimiento facial: ${String.format("%.2f", precisionPorcentaje)}%")
                            }
                        }
                    } else {
                        if (precision != null) {
                            if (precision < 0.6 && precision != -1.0f) {
                                Text("Es casi seguro que el alumno no es quien dice ser. \nPrecisión del reconocimiento facial: menor al 60%.")
                            } else if (precision?.toInt() == -1) {
                                Text("No se detecta un rostro en la fotografía.")
                            }
                        } else {
                            Text("No se pudo realizar el reconocimiento facial.")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onDismiss()
                if (!isStudentNotFound && (errorMessage == null || !errorMessage.contains("No se detecta un rostro"))) {
                    if (userRole == "Personal Academico" || userRole == "Docente") {
                        navController.navigate("InfoA/$idETS/$boleta")
                    } else {
                        navController.navigate("Menu Alumno")
                    }
                }
            }) {
                Text("OK")
            }
        }
    )
}
