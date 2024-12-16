package Pantallas


import Pantallas.components.MenuTopBar
import Pantallas.components.ValidateSession
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.prueba3.Views.AlumnosViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ListaAlumnosScreen(navController: NavController, idETS: String, viewModel: AlumnosViewModel) {
    ValidateSession (navController = navController) {
        val alumnosList by viewModel.alumnosList.collectAsState()
        val isLoading by remember { viewModel.loadingState }.collectAsState()

        // Llama al ViewModel para obtener los datos al cambiar el idETS
        LaunchedEffect(idETS) {
            viewModel.fetchAlumno(idETS)
        }

        Scaffold(
            topBar = {
                MenuTopBar(navController = navController, title = "Lista de Alumnos")
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
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
                } else if (alumnosList.isNotEmpty()) {
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
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Columna de datos del alumno
                                        Column(
                                            modifier = Modifier
                                                .weight(0.8f)
                                                .padding(end = 8.dp)
                                        ) {
                                            Text(
                                                text = "Boleta: ${alumno.Boleta}",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = "Nombre: ${alumno.NombreA} ${alumno.ApellidoP} ${alumno.ApellidoM}",
                                                style = MaterialTheme.typography.bodyMedium
                                            )

                                        }

                                        // Columna para el estado
                                        Column(
                                            modifier = Modifier
                                                .weight(0.2f),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Asistencia",
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .background(
                                                        color = if (alumno.Aceptado) Color(0xFF4CAF50) else Color(0xFFF44336),
                                                        shape = CircleShape
                                                    )
                                            )
                                        }
                                    }

                                    // Botones de acción (con espacio igual)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Botón Reconocimiento Facial
                                        Button(
                                            onClick = {
                                                navController.navigate("camara/${alumno.Boleta}/${idETS}")
                                            },
                                            modifier = Modifier.weight(1f), // Peso igual
                                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                                        ) {
                                            Text(text = "Reconocimiento Facial")
                                        }

                                        // Botón Dinámico de Asistencia
                                        Button(
                                            onClick = {
                                                // Lógica para poner o quitar asistencia
                                                val aceptado = !alumno.Aceptado // Cambiar el estado
                                                // Lanzamos la coroutine dentro del onClick
                                                CoroutineScope(Dispatchers.Main).launch {
                                                    viewModel.updateAsistencia(alumno.Boleta, idETS.toInt(), aceptado)
                                                }
                                            },
                                            modifier = Modifier.weight(1f), // Peso igual
                                            colors = ButtonDefaults.buttonColors(
                                                if (alumno.Aceptado) Color(0xFFFF5722) else Color(0xFF4CAF50)
                                            )
                                        ) {
                                            Text(
                                                text = if (alumno.Aceptado) "Quitar Asistencia" else "Poner Asistencia"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Texto de carga
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