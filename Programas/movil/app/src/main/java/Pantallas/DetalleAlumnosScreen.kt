package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.components.ValidateSession
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    loginViewModel: LoginViewModel
) {

    ValidateSession(navController = navController) {
        val alumnosDetalle by viewModel.alumnosDetalle.collectAsState(initial = emptyList())
        val alumno = alumnosDetalle.firstOrNull()
        val isLoading by viewModel.loadingState.collectAsState(initial = false)

        LaunchedEffect(Unit) {
            viewModel.fetchDetalleAlumnos()
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
                    text = "Información del alumno",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )

                Divider(
                    modifier = Modifier
                        .width(270.dp),
                    thickness = 1.dp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Imagen del alumno
                AsyncImage(
                    model = if (!alumno?.foto.isNullOrBlank()) alumno?.foto else R.drawable.placeholder_image,
                    contentDescription = "Foto del alumno",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(16.dp))

                InfoCard("Nombre", "${alumno?.nombre ?: ""} ${alumno?.apellidoP ?: ""} ${alumno?.apellidoM ?: ""}")
                InfoCard("Boleta", alumno?.boleta ?: "No disponible")
                InfoCard("ETS inscrito", alumno?.ets ?: "No disponible")
                InfoCard("Docente", alumno?.docente ?: "No disponible")
                InfoCard("Salón", alumno?.salon?.toString() ?: "No disponible")
                InfoCard("Turno", alumno?.turno ?: "No disponible")
                InfoCard("Fecha", alumno?.fecha ?: "No disponible")

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { /* TODO: Implementar funcionalidad */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6c1d45))
                    ) {
                        Text("Registrar Asistencia", color = Color.White)
                    }
                    Button(
                        onClick = { /* TODO: Implementar funcionalidad */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6c1d45))
                    ) {
                        Text("Escanear Credencial", color = Color.White)
                    }
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