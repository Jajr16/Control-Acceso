package Pantallas

import Pantallas.components.MenuBottomBar
import Pantallas.components.ValidateSession
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba3.Views.EtsInfoViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.ui.theme.BlueBackground

@Composable
fun EtsDetailScreen(
    navController: NavController,
    idETS: Int,
    viewModel: EtsInfoViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    loginViewModel: LoginViewModel
) {

    val userRole = loginViewModel.getUserRole()

    ValidateSession(navController = navController) {
        // Escuchar los datos del ViewModel
        val etsDetail by remember { viewModel.etsDetailState }.collectAsState()
        val isLoading by remember { viewModel.loadingState }.collectAsState()

        // Ejecutar la solicitud a la API cuando se cargue la pantalla
        LaunchedEffect(idETS) {
            viewModel.fetchEtsDetail(idETS)
        }

        // Mostrar contenido basado en el estado
        Scaffold(
            bottomBar = {
                Column {
                    // Barra superior con el título
                    MenuBottomBar(navController = navController, userRole)
                }
            }
        ) { padding ->

            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(padding)
            )

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
                } else if (etsDetail != null) {
                    val ets = etsDetail!!.ETS
                    val salones = etsDetail!!.Salones

                    // Lista deslizante de ETS
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            StyledCard(
                                title = "Tipo de ETS",
                                content = if (ets.tipoETS == "O") "Ordinario" else "Especial"
                            )
                        }
                        item {
                            StyledCard(title = "Periodo", content = ets.idPeriodo)
                        }
                        item {
                            StyledCard(title = "Fecha", content = ets.Fecha)
                        }
                        item {
                            StyledCard(title = "Turno", content = ets.Turno)
                        }
                        item {
                            StyledCard(title = "Cupo", content = ets.Cupo.toString())
                        }
                        item {
                            StyledCard(title = "Unidad Académica", content = ets.idUA)
                        }
                        item {
                            StyledCard(title = "Duración", content = ets.Duracion.toString())
                        }

                        // Mostrar salones
                        salones.take(3).forEach { salon -> // Limita a 3 salones
                            item {
                                StyledCard(
                                    title = "Número de salón",
                                    content = salon.numSalon.toString()
                                )
                            }
                            item {
                                StyledCard(title = "Tipo de salón", content = salon.tipoSalon)
                            }
                        }
                    }

                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "La asignación del salón sigue pendiente",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }
                }
                if (userRole != "Alumno") {
                    // Botón fijo en la parte inferior
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    ) {
                        Button(
                            onClick = {
                                navController.navigate("ListaAlumnos/$idETS")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6c1d45))
                        ) {
                            Text(text = "Ir a la lista de alumnos", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StyledCard(title: String, content: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp), // Márgenes de cada tarjeta
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Elevación con Material 3
        shape = RoundedCornerShape(8.dp) // Bordes redondeados
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Espaciado interno
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