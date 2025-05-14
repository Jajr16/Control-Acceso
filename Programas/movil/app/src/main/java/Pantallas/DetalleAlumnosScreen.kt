package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
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
        val registroSuccess by viewModel.registroSuccess.collectAsState()
        val asistenciaYaRegistrada by viewModel.asistenciaYaRegistrada.collectAsState()
        val fotoAlumno by viewModel.fotoAlumno.collectAsState()

        var showAsistenciaDialog by remember { mutableStateOf(false) }
        var showMensajeAsistencia by remember { mutableStateOf(false) }
        var showMensajeAsistenciaExistente by remember { mutableStateOf(false) }
        var showDeserializationError by remember { mutableStateOf(false) }
        var fechaHoraRegistro by remember { mutableStateOf("") }
        var isProcessingRegistration by remember { mutableStateOf(false) }

        val errorMessage by viewModel.errorMessage.collectAsState()

        // Cargar datos del alumno al entrar
        LaunchedEffect(boleta) {
            viewModel.resetAsistenciaFlags()
            viewModel.fetchDetalleAlumnos(boleta)
            viewModel.fetchFotoAlumno(boleta) {}
        }

        // Manejar mensajes de error
        LaunchedEffect(errorMessage) {
            errorMessage?.let { message ->
                println("Error detectado: $message")

                when {
                    message.contains("Expected BEGIN_ARRAY but was BEGIN_OBJECT") -> {
                        showDeserializationError = true
                        isProcessingRegistration = false
                        viewModel.clearErrorMessage()
                    }
                    else -> {
                        // Otros errores
                        isProcessingRegistration = false
                    }
                }
            }
        }

        // Manejar éxito del registro
        LaunchedEffect(registroSuccess) {
            if (registroSuccess) {
                if (!showDeserializationError) {
                    showMensajeAsistencia = true
                    fechaHoraRegistro = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
                    isProcessingRegistration = false
                    delay(2000)
                    showMensajeAsistencia = false
                    navController.popBackStack()
                }
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
                    text = "Detalle del alumno",
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
                if (fotoAlumno != null) {
                    val bitmap = BitmapFactory.decodeByteArray(
                        fotoAlumno,
                        0,
                        fotoAlumno!!.size
                    )
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.icon_camara),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Información del alumno
                InfoCard("Nombre", "${alumno?.nombreAlumno ?: ""} ${alumno?.apellidoPAlumno ?: ""} ${alumno?.apellidoMAlumno ?: ""}")
                InfoCard("Boleta", alumno?.boleta ?: "No disponible")
                InfoCard("ETS inscrito", alumno?.nombreETS ?: "No disponible")
                InfoCard("Docente", "${alumno?.nombreDocente ?: "El ETS aún no tiene un docente asignado."} ${alumno?.apellidoPDocente ?: ""} ${alumno?.apellidoMDocente ?: ""}")
                InfoCard("Salón", alumno?.salon?.toString() ?: "El salon no ha sido asignado")
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
                    when {
                        alumno?.nombreETS.isNullOrEmpty() -> {
                            AlertDialog(
                                onDismissRequest = {
                                    showAsistenciaDialog = false
                                },
                                title = { Text("Error", fontWeight = FontWeight.Bold) },
                                text = {
                                    Text("El alumno no cuenta con ETS inscritos ó su ETS está programada en otra fecha.")
                                },
                                confirmButton = {
                                    Button(onClick = {
                                        showAsistenciaDialog = false
                                    }) {
                                        Text("Aceptar")
                                    }
                                }
                            )
                        }
                        asistenciaYaRegistrada && !isProcessingRegistration -> {
                            AlertDialog(
                                onDismissRequest = {
                                    showAsistenciaDialog = false
                                },
                                title = { Text("Asistencia ya registrada", fontWeight = FontWeight.Bold) },
                                text = {
                                    Column {
                                        Text("El alumno ya tiene registrada su asistencia el día de hoy.")
                                        Text("Boleta: $boleta", fontWeight = FontWeight.Bold)
                                        Text("ETS: ${alumno?.nombreETS ?: "N/A"}", fontWeight = FontWeight.Bold)
                                    }
                                },
                                confirmButton = {
                                    Button(onClick = {
                                        showAsistenciaDialog = false
                                    }) {
                                        Text("Aceptar")
                                    }
                                }
                            )
                        }
                        else -> {
                            AlertDialog(
                                onDismissRequest = {
                                    showAsistenciaDialog = false
                                },
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
                                            isProcessingRegistration = true
                                            showAsistenciaDialog = false
                                            alumno?.idETS?.let { idETS ->
                                                viewModel.registrarAsistencia(boleta, idETS)
                                            }
                                        }
                                    ) {
                                        Text("Aceptar")
                                    }
                                },
                                dismissButton = {
                                    Button(onClick = {
                                        showAsistenciaDialog = false
                                    }) {
                                        Text("Cancelar")
                                    }
                                }
                            )
                        }
                    }
                }

                // Mensaje de asistencia registrada exitosamente
                if (showMensajeAsistencia) {
                    AlertDialog(
                        onDismissRequest = {
                            showMensajeAsistencia = false
                            navController.popBackStack()
                        },
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
                            Button(onClick = {
                                showMensajeAsistencia = false
                                navController.popBackStack()
                            }) {
                                Text("Aceptar")
                            }
                        }
                    )
                }

                // Diálogo para error de deserialización
                if (showDeserializationError) {
                    AlertDialog(
                        onDismissRequest = {
                            showDeserializationError = false
                            navController.popBackStack()
                        },
                        title = {
                            Text("Asistencia registrada", color = Color.Green, fontWeight = FontWeight.Bold)
                        },
                        text = {
                            Column {
                                Text("La asistencia se registró correctamente en el servidor.")
                                Text("Hubo un pequeño problema al interpretar la respuesta, pero tu registro fue exitoso.")
                                Text(
                                    text = fechaHoraRegistro,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                showDeserializationError = false
                                navController.popBackStack()
                            }) {
                                Text("Aceptar")
                            }
                        }
                    )
                }

                // Diálogo de error general
                if (!errorMessage.isNullOrEmpty() && !showDeserializationError) {
                    AlertDialog(
                        onDismissRequest = { viewModel.clearErrorMessage() },
                        title = {
                            Text(
                                if (errorMessage!!.contains("servidor") ||
                                    errorMessage!!.contains("conexión", ignoreCase = true))
                                    "Problema de conexión"
                                else "Error",
                                color = Color.Red
                            )
                        },
                        text = {
                            Column {
                                Text(errorMessage!!)
                                if (errorMessage!!.contains("servidor") ||
                                    errorMessage!!.contains("conexión", ignoreCase = true)) {
                                    Text(
                                        "Por favor verifica tu conexión e intenta nuevamente",
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            Button(onClick = { viewModel.clearErrorMessage() }) {
                                Text("Aceptar")
                            }
                        }
                    )
                }

                // Mensaje de asistencia ya registrada (al entrar a la pantalla)
                if (showMensajeAsistenciaExistente) {
                    AlertDialog(
                        onDismissRequest = {
                            showMensajeAsistenciaExistente = false
                            viewModel.clearErrorMessage()
                        },
                        title = { Text("Asistencia ya registrada", fontWeight = FontWeight.Bold) },
                        text = {
                            Column {
                                Text("El alumno ya tiene registrada su asistencia el día de hoy.")
                                Text("Boleta: $boleta", fontWeight = FontWeight.Bold)
                                Text("ETS: ${alumno?.nombreETS ?: "N/A"}", fontWeight = FontWeight.Bold)
                                Text("Fecha: ${SimpleDateFormat("dd/MM/yyyy").format(Date())}")
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                showMensajeAsistenciaExistente = false
                                viewModel.clearErrorMessage()
                            }) {
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