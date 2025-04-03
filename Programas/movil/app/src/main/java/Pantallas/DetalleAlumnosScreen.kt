package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.prueba3.R
import com.example.prueba3.Views.AlumnosViewModel
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.ui.theme.BlueBackground
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

        // State for photo loading
        var photoLoading by remember { mutableStateOf(true) }
        var photoError by remember { mutableStateOf(false) }
        var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }

        var showAsistenciaDialog by remember { mutableStateOf(false) }
        var showEscanearDialog by remember { mutableStateOf(false) }

        // Load student details
        LaunchedEffect(boleta) {
            viewModel.fetchDetalleAlumnos(boleta)
        }



        Scaffold(
            topBar = {
                MenuTopBar(true, true, loginViewModel, navController)
            },
            bottomBar = {
                MenuBottomBar(navController = navController, loginViewModel.getUserRole())
            }
        ) { padding ->
            val scrollState = rememberScrollState() // Estado del scroll

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
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

                // Student photo display
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        photoLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(50.dp),
                                color = Color.White
                            )
                        }
                        photoBitmap != null -> {
                            Image(
                                bitmap = photoBitmap!!.asImageBitmap(),
                                contentDescription = "Foto del alumno",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
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

                if (photoError && !photoLoading) {
                    Text(
                        text = "No se pudo cargar la foto",
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Student information
                InfoCard("Nombre", "${alumno?.nombreAlumno ?: ""} ${alumno?.apellidoPAlumno ?: ""} ${alumno?.apellidoMAlumno ?: ""}")
                InfoCard("Boleta", alumno?.boleta ?: "No disponible")
                InfoCard("ETS inscrito", alumno?.nombreETS ?: "No disponible")
                InfoCard("Docente", "${alumno?.nombreDocente ?: ""} ${alumno?.apellidoPDocente ?: ""} ${alumno?.apellidoMDocente ?: ""}")
                InfoCard("Salón", alumno?.salon?.toString() ?: "No disponible")
                InfoCard("Turno", alumno?.nombreTurno ?: "No disponible")
                InfoCard("Fecha", alumno?.fecha ?: "No disponible")

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { showAsistenciaDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("Registrar Asistencia", color = Color.Black)
                    }
                }
            }
        }

        // Attendance dialog
        if (showAsistenciaDialog) {
            AlertDialog(
                onDismissRequest = { showAsistenciaDialog = false },
                title = { Text("Confirmar asistencia", fontWeight = FontWeight.Bold) },
                text = { Text("¿Deseas registrar la asistencia de ${alumno?.nombreAlumno}?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showAsistenciaDialog = false
                            alumno?.boleta?.let { viewModel.registrarAsistencia(it) }
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showAsistenciaDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }


        // Scan dialog
        if (showEscanearDialog) {
            AlertDialog(
                onDismissRequest = { showEscanearDialog = false },
                title = { /* ... */ },
                text = { /* ... */ },
                confirmButton = { /* ... */ }
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
