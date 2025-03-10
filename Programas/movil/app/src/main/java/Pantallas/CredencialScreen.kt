package Pantallas

import Pantallas.components.MenuBottomBar
import Pantallas.components.ValidateSession
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
        val isLoading by viewModel.loadingState.collectAsState(initial = false)

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
                InfoCard("Unidad Academica", alumno?.unidadAcademica ?: "No disponible")

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


