package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.components.ValidateSession
import androidx.compose.foundation.background
import Pantallas.Plantillas.MenuTopBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.prueba3.R
import com.example.prueba3.Views.AlumnosViewModel
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.ui.theme.BlueBackground

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

        var showAsistenciaDialog by remember { mutableStateOf(false) }
        var showEscanearDialog by remember { mutableStateOf(false) }

        LaunchedEffect(boleta) {
            viewModel.fetchDetalleAlumnos(boleta)
        }

        Scaffold(
            topBar = {
                MenuTopBar(true, true, loginViewModel, navController)
            },
            bottomBar = { MenuBottomBar(navController = navController, loginViewModel.getUserRole()) }
        ) { padding ->
            val scrollState = rememberScrollState() // Estado del scroll

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState), // Habilitar scroll
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Información del alumno",
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

                AsyncImage(
                    model = if (!alumno?.imagenCredencial.isNullOrBlank()) alumno?.imagenCredencial else R.drawable.placeholder_image,
                    contentDescription = "Foto del alumno",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(16.dp))

                InfoCard("Nombre", "${alumno?.nombreAlumno ?: ""} ${alumno?.apellidoPAlumno ?: ""} ${alumno?.apellidoMAlumno ?: ""}")
                InfoCard("Boleta", alumno?.boleta ?: "No disponible")
                InfoCard("ETS inscrito", alumno?.nombreETS ?: "No disponible")
                InfoCard("Docente", "${alumno?.nombreDocente ?: ""} ${alumno?.apellidoPDocente ?: ""} ${alumno?.apellidoMDocente ?: ""}")
                InfoCard("Salón", alumno?.salon?.toString() ?: "No disponible")
                InfoCard("Turno", alumno?.nombreTurno ?: "No disponible")
                InfoCard("Fecha", alumno?.fecha ?: "No disponible")

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { showAsistenciaDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF))
                    ) {
                        Text("Registrar Asistencia", color = Color.Black)
                    }
                    Button(
                        onClick = { showEscanearDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF))
                    ) {
                        Text("Escanear Credencial", color = Color.Black)
                    }
                }
            }
        }

        if (showAsistenciaDialog) {
            AlertDialog(
                onDismissRequest = { showAsistenciaDialog = false },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Warning, contentDescription = "Advertencia", tint = Color(0xFFFFC107))
                        Spacer(modifier = Modifier.width(8.dp))
                        //Text("Confirmación", fontWeight = FontWeight.Bold)
                    }
                },
                text = { Text("¿Estás seguro de querer registrar la asistencia del alumno?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showAsistenciaDialog = false
                            // TODO: Agregar la lógica para registrar la asistencia
                        }
                    ) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showAsistenciaDialog = false }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
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
                    }
                },
                text = { Text("Si tienes dudas sobre la identidad del alumno, escanea su credencial para confirmar su identidad.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showEscanearDialog = false
                            navController.navigate("scanQr")
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
