package Pantallas

import Pantallas.components.MenuBottomBar
import Pantallas.components.ValidateSession
import RetroFit.RetrofitInstance
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.ui.theme.BlueBackground
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.InputStream

@Composable
fun CalendarScreen(navController: NavController, loginViewModel: LoginViewModel) {
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isZoomed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val userRole = loginViewModel.getUserRole()

    ValidateSession(navController = navController) {
        Scaffold(
            bottomBar = { MenuBottomBar(navController = navController, userRole) }
        ) { padding ->
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(padding)
            )
        }

                LaunchedEffect(Unit) {
                    scope.launch {
                        try {
                            val response = RetrofitInstance.getCalendar.getCalendar()
                            if (response.isSuccessful && response.body() != null) {
                                val inputStream: InputStream = response.body()!!.byteStream()
                                val bitmap = BitmapFactory.decodeStream(inputStream)
                                imageBitmap = bitmap
                                isLoading = false
                            } else {
                                errorMessage = "Error al obtener la imagen"
                                isLoading = false
                            }
                        } catch (e: HttpException) {
                            errorMessage = "Error en la solicitud: ${e.message()}"
                            isLoading = false
                        } catch (e: Exception) {
                            errorMessage = "Error general: ${e.message}"
                            isLoading = false
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.White
                            )
                        }

                        imageBitmap != null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = imageBitmap!!.asImageBitmap(),
                                    contentDescription = "Calendario",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(400.dp)
                                        .clickable {
                                            isZoomed = true
                                        } // Hacer zoom al hacer clic en la imagen
                                )

                                if (isZoomed) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.8f))
                                            .clickable {
                                                isZoomed = false
                                            }, // Salir del zoom al hacer clic en el fondo
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ZoomableImage(bitmap = imageBitmap!!)
                                    }
                                }
                            }
                        }

                        errorMessage != null -> {
                            Text(
                                text = errorMessage ?: "Error desconocido",
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        else -> {
                            Text(
                                text = "No hay imagen disponible",
                                color = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
@Composable
fun ZoomableImage(bitmap: Bitmap) {
    var scale by remember { mutableStateOf(1f) } // Escala inicial
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale *= zoom
                    offsetX += pan.x
                    offsetY += pan.y
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Imagen ampliada",
            modifier = Modifier
                .graphicsLayer(
                    scaleX = maxOf(1f, scale),
                    scaleY = maxOf(1f, scale),
                    translationX = offsetX,
                    translationY = offsetY
                )
                .fillMaxSize()
        )
    }
}
