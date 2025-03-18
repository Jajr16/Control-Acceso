package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.components.ValidateSession
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.prueba3.R
import com.example.prueba3.Views.AlumnosViewModel
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.ui.theme.BlueBackground

@Composable
fun CredencialScreen(
    navController: NavController,
    viewModel: AlumnosViewModel,
    boleta: String,
    loginViewModel: LoginViewModel
) {
    ValidateSession(navController = navController) {
        val alumnosCredencial by viewModel.alumnosCredencial.collectAsState(initial = emptyList())
        val alumno = alumnosCredencial.firstOrNull()
        var showAsistenciaDialog by remember { mutableStateOf(false) }
        var showEscanearDialog by remember { mutableStateOf(false) }

        LaunchedEffect(boleta) {
            viewModel.fetchCredencialAlumnos(boleta)
        }

        Scaffold(
            bottomBar = { MenuBottomBar(navController = navController, loginViewModel.getUserRole()) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Credencial del Alumno",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )

                Divider(modifier = Modifier.width(270.dp), thickness = 1.dp, color = Color.Black)

                Spacer(modifier = Modifier.height(16.dp))

                AsyncImage(
                    model = if (!alumno?.ImagenCredencial.isNullOrBlank()) alumno?.ImagenCredencial else R.drawable.placeholder_image,
                    contentDescription = "Foto del alumno",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(16.dp))

                InfoCard("Nombre", "${alumno?.nombre} ${alumno?.apellidoP} ${alumno?.apellidoM}")
                InfoCard("Boleta", alumno?.boleta ?: "No disponible")
                InfoCard("CURP", alumno?.curp ?: "No disponible")
                InfoCard("Carrera", alumno?.carrera ?: "No disponible")
                InfoCard("Unidad Académica", alumno?.unidadAcademica ?: "No disponible")

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { showAsistenciaDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                    ) {
                        Text("Registrar asistencia")
                    }
                    Button(
                        onClick = { showEscanearDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                    ) {
                        Text("Escanear credencial")
                    }
                }
            }
        }

        if (showAsistenciaDialog) {
            AlertDialog(
                onDismissRequest = { showAsistenciaDialog = false },
                title = { Text("Confirmación", fontWeight = FontWeight.Bold) },
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
                        Text("¡Atención!", fontWeight = FontWeight.Bold)
                    }
                },
                text = { Text("Si tienes dudas sobre la identidad del alumno, escanea su credencial para confirmar su identidad.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showEscanearDialog = false
                            navController.navigate("qrScanner")
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
