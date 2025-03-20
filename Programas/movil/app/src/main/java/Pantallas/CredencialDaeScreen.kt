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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.prueba3.R
import com.example.prueba3.Views.AlumnosViewModel
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.ui.theme.BlueBackground
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.InputStream

@Composable
fun CredencialDaeScreen(navController: NavController, loginViewModel: LoginViewModel, url: String?, viewModel: AlumnosViewModel, boleta: String) {
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isZoomed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val alumnosCredencial by viewModel.alumnosCredencial.collectAsState(initial = emptyList())
    val alumno = alumnosCredencial.firstOrNull()

    // Depuración: Verificar si la boleta se recibe correctamente
    println("Boleta recibida: $boleta")

    LaunchedEffect(boleta) {
        println("Llamando a fetchCredencialAlumnos con boleta: $boleta")
        viewModel.fetchCredencialAlumnos(boleta)
    }

    // Depuración: Verificar si el alumno se carga correctamente
    println("Alumno cargado: $alumno")

    ValidateSession(navController = navController) {
        Scaffold(
            topBar = {
                MenuTopBar(
                    true, true, loginViewModel,
                    navController,
                )
            },
            bottomBar = { MenuBottomBar(navController = navController, loginViewModel.getUserRole()) }
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
                            .clickable { isZoomed = false },
                        contentAlignment = Alignment.Center
                    ) {
                        ZoomableImage(bitmap = imageBitmap!!)
                    }
                }

                // Encabezado
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Credencial",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
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
                            println("Cargando...")
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.White
                            )
                        }

                        imageBitmap != null -> {
                            println("Imagen cargada correctamente")
                            Image(
                                bitmap = imageBitmap!!.asImageBitmap(),
                                contentDescription = "Credencial",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp)
                                    .clickable { isZoomed = true }
                            )
                        }

                        errorMessage != null -> {
                            println("Error: $errorMessage")
                            Text(
                                text = errorMessage ?: "Error desconocido",
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        else -> {
                            println("No hay imagen disponible")
                            Text(
                                text = "No hay imagen disponible",
                                color = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }

                // Mostrar información del alumno
                if (alumno != null) {
                    println("Mostrando información del alumno: $alumno")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        elevation = 4.dp,
                        shape = RoundedCornerShape(8.dp),
                        backgroundColor = Color.White
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = alumno.ImagenCredencial.takeUnless { it.isNullOrBlank() } ?: R.drawable.placeholder_image,
                                    contentDescription = "Foto del alumno",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                )

                                Column(
                                    modifier = Modifier
                                        .padding(start = 16.dp)
                                        .weight(1f)
                                ) {
                                    Text(
                                        text = "Alumno:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = "${alumno.nombre} ${alumno.apellidoP} ${alumno.apellidoM}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )

                                    Text(
                                        text = "Programa académico:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = alumno.unidadAcademica ?: "No disponible",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Boleta:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = alumno.boleta ?: "No disponible",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "CURP:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = alumno.curp ?: "No disponible",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                } else {
                    println("No se encontró información del alumno")
                }

                LaunchedEffect(url) {
                    if (url != null) {
                        println("Cargando imagen desde la URL: $url")
                        scope.launch {
                            try {
                                val response = RetrofitInstance.alumnosDetalle.getCredencial(url, boleta)
                                if (response.isSuccessful && response.body() != null) {
                                    println("Imagen descargada correctamente")
                                    val inputStream: InputStream = response.body()!!.byteStream()
                                    val bitmap = BitmapFactory.decodeStream(inputStream)
                                    imageBitmap = bitmap
                                    isLoading = false
                                } else {
                                    errorMessage = "Error al obtener la imagen"
                                    isLoading = false
                                    println("Error en la respuesta: ${response.errorBody()?.string()}")
                                }
                            } catch (e: HttpException) {
                                errorMessage = "Error en la solicitud: ${e.message()}"
                                isLoading = false
                                println("Error HTTP: ${e.message()}")
                            } catch (e: Exception) {
                                errorMessage = "Error general: ${e.message}"
                                isLoading = false
                                println("Error general: ${e.message}")
                            }
                        }
                    } else {
                        errorMessage = "URL no válida"
                        isLoading = false
                        println("URL no válida")
                    }
                }
            }
        }
    }
}