package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.prueba3.R
import com.example.prueba3.Views.AlumnosViewModel
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.ui.theme.BlueBackground
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DetalleAlumnosScreen(
    navController: NavController,
    viewModel: AlumnosViewModel,
    boleta: String,
    loginViewModel: LoginViewModel
) {
    ValidateSession(navController = navController) {
        val alumnosDetalle by viewModel.alumnosDetalle.collectAsState(initial = emptyList())
        val alumno = alumnosDetalle.firstOrNull()
        val isLoading by viewModel.loadingState.collectAsState(initial = false)
        val registroSuccess by viewModel.registroSuccess.collectAsState()
        val fotoAlumno by viewModel.fotoAlumno.collectAsState()

        var showAsistenciaDialog by remember { mutableStateOf(false) }
        var showEscanearDialog by remember { mutableStateOf(false) }
        var showMensajeAsistencia by remember { mutableStateOf(false) }
        var fechaHoraRegistro by remember { mutableStateOf("") }

        LaunchedEffect(boleta) {
            viewModel.fetchDetalleAlumnos(boleta)
            viewModel.fetchFotoAlumno(boleta)
        }

        // Manejar éxito/error del registro
        LaunchedEffect(registroSuccess) {
            if (registroSuccess) {
                showMensajeAsistencia = true
                delay(3000)
                showMensajeAsistencia = false
            }
        }

        Scaffold(
            topBar = {
                MenuTopBar(true, true, loginViewModel, navController)
            },
            bottomBar = {
                MenuBottomBar(navController = navController, loginViewModel.getUserRole())
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Creación del reporte ",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )

                Divider(
                    modifier = Modifier.width(270.dp),
                    thickness = 1.dp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Foto del alumno
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        fotoAlumno != null -> {
                            val bitmap = BitmapFactory.decodeByteArray(fotoAlumno, 0, fotoAlumno!!.size)
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Foto del alumno",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(50.dp),
                                color = Color.White
                            )
                        }
                        else -> {
                            Image(
                                painter = painterResource(id = R.drawable.placeholder_image),
                                contentDescription = "Foto no disponible",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Información del alumno
                InfoCard("Nombre", "${alumno?.nombreAlumno ?: ""} ${alumno?.apellidoPAlumno ?: ""} ${alumno?.apellidoMAlumno ?: ""}")
                InfoCard("Boleta", alumno?.boleta ?: "No disponible")
                InfoCard("ETS inscrito", alumno?.nombreETS ?: "No disponible")
                InfoCard("Docente", "${alumno?.nombreDocente ?: ""} ${alumno?.apellidoPDocente ?: ""} ${alumno?.apellidoMDocente ?: ""}")
                InfoCard("Salón", alumno?.salon?.toString() ?: "No disponible")
                InfoCard("Turno", alumno?.nombreTurno ?: "No disponible")
                InfoCard("Fecha", alumno?.fecha ?: "No disponible")

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            showAsistenciaDialog = true
                            fechaHoraRegistro = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
                        },
                        modifier = Modifier.weight(1f)
                            .padding(horizontal = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(
                            text = "Registrar asistencia",
                            color = Color.Black
                        )
                    }
                }

                // Diálogo de confirmación de asistencia
                if (showAsistenciaDialog) {
                    if (alumno?.nombreETS.isNullOrEmpty()) {
                        AlertDialog(
                            onDismissRequest = { showAsistenciaDialog = false },
                            title = { Text("Error", fontWeight = FontWeight.Bold) },
                            text = {
                                Text("El alumno no cuenta con ETS inscritos ó su ETS está programada en otra fecha.")
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
                                        text = "El alumno ${alumno?.nombreAlumno ?: ""} ${alumno?.apellidoPAlumno ?: ""} con número de boleta ${alumno?.boleta ?: ""} está inscrito en:",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "${alumno?.nombreETS ?: ""}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                    Text(
                                        text = "Fecha y hora de registro:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                    Text(
                                        text = fechaHoraRegistro,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(top = 4.dp)
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
                                        viewModel.registrarAsistencia(boleta)
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

                // Mensaje de asistencia registrada
                if (showMensajeAsistencia) {
                    AlertDialog(
                        onDismissRequest = { showMensajeAsistencia = false },
                        title = {
                            Text(
                                "Asistencia registrada",
                                color = Color.Green,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Column {
                                Text(
                                    text = "La asistencia ha sido registrada correctamente",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = fechaHoraRegistro,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = { showMensajeAsistencia = false }) {
                                Text("Aceptar")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun InfoCard(title: String, content: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$title:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
    }
}