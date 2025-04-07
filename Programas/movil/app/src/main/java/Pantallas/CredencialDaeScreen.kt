package Pantallas

import Pantallas.Reutilizables.ZoomableImage
import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import RetroFit.RetrofitInstance
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import com.example.prueba3.Clases.CredencialAlumnos
import com.example.prueba3.R
import com.example.prueba3.Views.AlumnosViewModel
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.ui.theme.BlueBackground
import kotlinx.coroutines.launch
import retrofit2.HttpException
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun CredencialDaeScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    url: String?,
    viewModel: AlumnosViewModel,
    boleta: String
) {

    val userRole = loginViewModel.getUserRole()

    Log.d("CredencialDaeScreen", "Dato: $userRole")

    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isZoomed by remember { mutableStateOf(false) }
    var alumnoInfo by remember { mutableStateOf<CredencialAlumnos?>(null) }
    val alumnosDetalle by viewModel.alumnosDetalle.collectAsState(initial = emptyList())
    val alumno = alumnosDetalle.firstOrNull()
    var showAsistenciaDialog by remember { mutableStateOf(false) }
    var showEscanearDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val fotoAlumno by viewModel.fotoAlumno.collectAsState()
    var showMensajeAsistencia by remember { mutableStateOf(false) }

//    LaunchedEffect(boleta) {
//        viewModel.fetchFotoAlumno(boleta)
//    }
    val idETSFinal by viewModel.idETSFlujo.collectAsState()
    val boletaFinal by viewModel.boletaFlujo.collectAsState()


    ValidateSession(navController = navController) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                MenuTopBar(
                    false, false, loginViewModel,
                    navController,
                )
            },
            bottomBar = { MenuBottomBar(navController = navController, loginViewModel.getUserRole()) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(
                        top = 90.dp,
                        bottom = padding.calculateBottomPadding()
                    )
                    .verticalScroll(rememberScrollState())
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
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.White
                            )
                        }

                        imageBitmap != null -> {
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

                if (alumnoInfo != null) {

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Información del alumno - MODIFICADO para usar fotoAlumno
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (fotoAlumno != null) {
                                    val bitmap = BitmapFactory.decodeByteArray(fotoAlumno, 0, fotoAlumno!!.size)
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "Foto de perfil",
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    // Mostrar placeholder si no hay foto
                                    Image(
                                        painter = painterResource(id = R.drawable.placeholder_image),
                                        contentDescription = "Foto de perfil",
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }

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
                                        text = "${alumnoInfo?.nombre} ${alumnoInfo?.apellidoP} ${alumnoInfo?.apellidoM}",
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
                                        text = alumnoInfo?.carrera ?: "No disponible",
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
                                        text = alumnoInfo?.boleta ?: "No disponible",
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
                                        text = alumnoInfo?.curp ?: "No disponible",
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
                                        text = "Unidad Académica:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = alumnoInfo?.unidadAcademica ?: "No disponible",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Botones de verificación
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (userRole == "Personal Academico") {
                            Button(
                                onClick = { navController.navigate("infoA/${idETSFinal}/${boletaFinal}") }, // Asegúrate de tener la ruta correcta
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                            ) {
                                Text(
                                    text = "Regresar a la creación del reporte",
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            Button(
                                onClick = { showAsistenciaDialog = true },
                                modifier = Modifier.weight(1f)
                                    .padding(horizontal = 8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                            ) {
                                Text(
                                    text = "Registrar asistencia",
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Button(
                                onClick = { showEscanearDialog = true },
                                modifier = Modifier.weight(1f)
                                    .padding(horizontal = 8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                            ) {
                                Text(
                                    text = "Lista de alumnos",
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    if (showAsistenciaDialog) {
                        if (alumno?.nombreETS.isNullOrEmpty()) {
                            AlertDialog(
                                onDismissRequest = { showAsistenciaDialog = false },
                                title = { Text("Error", fontWeight = FontWeight.Bold) },
                                text = {
                                    Text("El alumno no cuenta con ETS inscritos o su ETS está programada en otra fecha.")
                                },
                                confirmButton = {
                                    Button(onClick = { showAsistenciaDialog = false }) {
                                        Text("Aceptar")
                                    }
                                }
                            )
                        } else {
                            AlertDialog(
                                onDismissRequest = { showAsistenciaDialog = false },
                                title = {
                                    Text(
                                        "Confirmar asistencia",
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                text = {
                                    Column {
                                        Text(
                                            text = "El alumno ${alumnoInfo?.nombre ?: ""} ${alumnoInfo?.apellidoP ?: ""} con número de boleta ${alumnoInfo?.boleta ?: ""} está inscrito en los siguientes ETS:",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "${alumno?.nombreETS ?: ""}",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                        Text(
                                            text = "¿Deseas registrar su asistencia?",
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(top = 16.dp)
                                        )
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            showAsistenciaDialog = false
                                            showMensajeAsistencia = true
                                            alumnoInfo?.boleta?.let { boleta ->
                                                viewModel.registrarAsistencia(boleta)
                                            }
                                        }
                                    ) {
                                        Text("Aceptar")
                                    }
                                },
                                dismissButton = {
                                    Button(onClick = { showAsistenciaDialog = false }) {
                                        Text("Cancelar")
                                    }
                                }
                            )
                        }
                    }

                    if (showMensajeAsistencia) {
                        LaunchedEffect(Unit) {
                            kotlinx.coroutines.delay(3000)
                            showMensajeAsistencia = false
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ASISTENCIA REGISTRADA",
                                color = Color.Green,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }

                    if (showEscanearDialog) {
                        AlertDialog(
                            onDismissRequest = { showEscanearDialog = false },
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.Warning, contentDescription = "Alerta", tint = Color(0xFFFFC107))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Confirmar identidad")
                                }
                            },
                            text = { Text("Si el alumno no cuenta con su credencial, puedes verificar la lista con los alumnos inscritos.") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        showEscanearDialog = false
                                        navController.navigate("ConsultarAlumnos")
                                    }
                                ) {
                                    Text("Aceptar")
                                }
                            },
                            dismissButton = {
                                Button(
                                    onClick = { showEscanearDialog = false }
                                ) {
                                    Text("Cancelar")
                                }
                            }
                        )
                    }
                } else {
                    Text(
                        text = "No se encontró información del alumno",
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }

    LaunchedEffect(url) {
        if (url != null) {
            scope.launch {
                try {
                    val response = RetrofitInstance.alumnosDetalle.getCredencial(url)

                    if (response.isSuccessful && response.body() != null) {
                        val credencialResponse = response.body()!!

                        // Decodificar la imagen base64
                        val imageBytes = Base64.decode(credencialResponse.imagen)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        imageBitmap = bitmap

                        // Guardar la información del alumno
                        alumnoInfo = credencialResponse.credenciales.firstOrNull()

                        val boletausable  = alumnoInfo?.boleta

                        boletausable?.let { boletaNonNull ->
                            viewModel.fetchFotoAlumno(boletaNonNull)
                        }

                        isLoading = false
                    } else {
                        errorMessage = "Error al obtener la credencial"
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
        } else {
            errorMessage = "URL no válida"
            isLoading = false
        }
    }



}