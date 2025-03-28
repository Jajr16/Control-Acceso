package Pantallas

import Pantallas.Reutilizables.ZoomableImage
import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import RetroFit.RetrofitInstance
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.Views.DiasETSModel
import com.example.prueba3.ui.theme.BlueBackground
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.InputStream

@Composable
fun CalendarScreen(navController: NavController, loginViewModel: LoginViewModel, diasETSModel: DiasETSModel) {
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isZoomed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val userRole = loginViewModel.getUserRole()

    val loadingState by diasETSModel.loadingState.collectAsState()
    val calendarDays by diasETSModel.text.collectAsState()

    ValidateSession(navController = navController) {
        Scaffold(
            topBar = {
                MenuTopBar(
                    true, true, loginViewModel,
                    navController
                )
            },
            bottomBar = { MenuBottomBar(navController = navController, userRole) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(top = 90.dp)
            ) {
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

                // Encabezado
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Calendario Escolar",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .align(Alignment.CenterHorizontally),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Divider(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .width(270.dp),
                        thickness = 1.dp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))

                Box(modifier = Modifier.fillMaxSize()) {

                    when {
                        isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.White
                            )
                        }

                        imageBitmap != null -> {
                            Column(
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

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = {
                                        diasETSModel.getDays()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = Color.Black
                                    ),
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .align(Alignment.CenterHorizontally)
                                ) {
                                    Text("Calcular cuántos días faltan \npara el periodo de ETS",
                                        textAlign = TextAlign.Center)
                                }

                                if (loadingState) {
                                    Text(
                                        text = "Calculando...",
                                        color = Color.White,
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                    )
                                }

                                if(calendarDays != null) {
                                    Spacer(modifier = Modifier.height(20.dp))

                                    Text(
                                        text = calendarDays!!,
                                        color = Color.White,
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                    )
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
                    LaunchedEffect(Unit) {
                        diasETSModel.resetDays()
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
                }
            }
        }
    }
}
