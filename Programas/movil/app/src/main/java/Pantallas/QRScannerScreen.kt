package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.prueba3.Views.LoginViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRScannerScreen(navController: NavController, loginViewModel: LoginViewModel) {

    val userRole = loginViewModel.getUserRole()

    ValidateSession(navController = navController) {
        val permissions = rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.CAMERA
            )
        )

        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val cameraController = remember { LifecycleCameraController(context) }
        var qrCodeResult by remember { mutableStateOf<String?>(null) }
        val qrScannerOptions = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        val qrScanner = BarcodeScanning.getClient(qrScannerOptions)

        LaunchedEffect(Unit) {
            permissions.launchMultiplePermissionRequest()
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                MenuTopBar(
                    true, true, loginViewModel,
                    navController
                )
            },
            bottomBar = { MenuBottomBar(navController = navController, userRole) }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                if (permissions.allPermissionsGranted) {
                    QRScannerComposable(
                        cameraController = cameraController,
                        lifecycleOwner = lifecycleOwner,
                        qrScanner = qrScanner,
                        onQrCodeDetected = { result ->
                            qrCodeResult = result.displayValue
                            result.displayValue?.let { url ->
                                openWebPage(context, url)
                            }
                        }
                    )

                    // Cuadro en el centro para marcar el área de escaneo
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .border(
                                width = 2.dp,
                                color = Color(0xFF800040), // Color del borde (personalizable)
                                shape = RoundedCornerShape(8.dp) // Bordes redondeados
                            )
                    )
                } else {
                    Text(text = "Permisos de cámara requeridos")
                }
            }
        }
    }
}

@Composable
fun QRScannerComposable(
    cameraController: LifecycleCameraController,
    lifecycleOwner: LifecycleOwner,
    qrScanner: BarcodeScanner,
    onQrCodeDetected: (Barcode) -> Unit
) {
    cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    cameraController.bindToLifecycle(lifecycleOwner)
    cameraController.setImageAnalysisAnalyzer(
        ContextCompat.getMainExecutor(LocalContext.current)
    ) { imageProxy ->
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            qrScanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    barcodes.firstOrNull()?.let { barcode ->
                        onQrCodeDetected(barcode)
                    }
                }
                .addOnFailureListener {
                    // Manejar errores en el análisis
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    AndroidView(
        factory = { context ->
            PreviewView(context).apply {
                controller = cameraController
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

fun openWebPage(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No se pudo abrir la URL", Toast.LENGTH_SHORT).show()
    }
}


