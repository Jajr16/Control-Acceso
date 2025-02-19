package Pantallas

import Pantallas.components.MenuBottomBar
import Pantallas.components.ValidateSession
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.prueba3.Views.AlumnosViewModel
import com.example.prueba3.Views.LoginViewModel

@Composable
fun ConsultarScreen(navController: NavController, idETS: String, viewModel: AlumnosViewModel,
                    loginViewModel: LoginViewModel
) {

    // Validar sesión
    ValidateSession(navController = navController) {

        // Recolectar el estado de la lista de alumnos y el estado de carga
        val alumnosList by viewModel.alumnosList.collectAsState(initial = emptyList())
        val isLoading by viewModel.loadingState.collectAsState(initial = false)

        // Obtener el rol del usuario
        val userRole = loginViewModel.getUserRole()

        // Llamar a la función fetchAlumno cuando cambia el idETS
        LaunchedEffect(idETS) {
            viewModel.fetchAlumno(idETS)
        }

        // Scaffold para la estructura básica
        Scaffold(
            bottomBar = {
                MenuBottomBar(navController = navController, userRole)
            }
        ) { padding ->

            // Contenedor principal
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Si está cargando, mostrar el texto de carga
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cargando...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    // Si la lista de alumnos tiene datos, mostrarla en un LazyColumn
                    if (alumnosList.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(alumnosList) { alumno ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        // Mostrar boleta y nombre completo
                                        Text(
                                            text = "Boleta: ${alumno.Boleta}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "Nombre: ${alumno.NombreA} ${alumno.ApellidoP} ${alumno.ApellidoM}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Si no hay alumnos, mostrar un mensaje
                        Text(
                            text = "No hay alumnos inscritos al ETS.",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
