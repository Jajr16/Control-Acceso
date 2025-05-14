package Pantallas

import Pantallas.components.BuscadorConLista
import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.prueba3.Views.AlumnosViewModel
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.ui.theme.BlueBackground

@Composable
fun ConsultarScreen(
    navController: NavController,
    viewModel: AlumnosViewModel,
    loginViewModel: LoginViewModel
) {
    ValidateSession(navController = navController) {
        val alumnosListado by viewModel.alumnosListado.collectAsState(initial = emptyList())
        val isLoading by viewModel.loadingState.collectAsState(initial = false)
        var searchQuery by remember { mutableStateOf("") }

        // ============= FILTRO DE TURNO =============
        var selectedTurno by remember { mutableStateOf("Matutino") }

        LaunchedEffect(Unit) {
            viewModel.fetchListalumnos()
        }

        Scaffold(
            topBar = {
                MenuTopBar(
                    true, true, loginViewModel, navController
                )
            },
            bottomBar = { MenuBottomBar(navController = navController, loginViewModel.getUserRole()) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .weight(1f)
                ) {
                    val filteredList = alumnosListado
                        .filter { alumno ->
                            alumno.boleta.contains(searchQuery, ignoreCase = true) ||
                                    alumno.nombre.contains(searchQuery, ignoreCase = true) ||
                                    alumno.apellidoP.contains(searchQuery, ignoreCase = true) ||
                                    alumno.apellidoM.contains(searchQuery, ignoreCase = true)
                        }
                        .filter { alumno ->
                            alumno.turno == selectedTurno
                        }

                    // Título
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Consultar Alumnos",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .padding(bottom = 0.dp, top = 20.dp),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Divider(
                            modifier = Modifier
                                .padding(vertical = 0.dp)
                                .width(270.dp),
                            thickness = 1.dp,
                            color = Color.LightGray
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Cargando...", style = MaterialTheme.typography.bodyLarge)
                        }
                    } else {
                        // Contenedor principal con scroll
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                        ) {
                            BuscadorConLista(
                                lista = filteredList,
                                filtro = { alumno, query ->
                                    alumno.boleta.contains(query, ignoreCase = true) ||
                                            alumno.nombre.contains(query, ignoreCase = true) ||
                                            alumno.apellidoP.contains(query, ignoreCase = true) ||
                                            alumno.apellidoM.contains(query, ignoreCase = true)
                                },
                                onItemClick = { alumno -> navController.navigate("detallealumnos/${alumno.boleta}") },
                                placeholder = "Buscar por nombre o boleta",
                                itemContent = { alumno ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = "Boleta: ${alumno.boleta}",
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = "${alumno.nombre} ${alumno.apellidoP} ${alumno.apellidoM}",
                                            fontSize = 16.sp
                                        )
                                    }
                                },
                                additionalContent = {
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        // Botón Matutino
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .background(
                                                    if (selectedTurno == "Matutino") Color.Gray else Color(
                                                        0xFFF5F5F5
                                                    )
                                                )
                                                .clickable(onClick = { selectedTurno = "Matutino" })
                                                .weight(1f)
                                                .height(35.dp)
                                                .padding(2.dp)
                                        ) {
                                            Text(
                                                text = "Matutino",
                                                style = MaterialTheme.typography.bodyMedium,
                                                textAlign = TextAlign.Center,
                                                color = if (selectedTurno == "Matutino") Color.White else Color.Black
                                            )
                                        }

                                        // Botón Vespertino
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .background(
                                                    if (selectedTurno == "Vespertino") Color.Gray else Color(
                                                        0xFFF5F5F5
                                                    )
                                                )
                                                .clickable(onClick = { selectedTurno = "Vespertino" })
                                                .weight(1f)
                                                .height(35.dp)
                                                .padding(2.dp)
                                        ) {
                                            Text(
                                                text = "Vespertino",
                                                style = MaterialTheme.typography.bodyMedium,
                                                textAlign = TextAlign.Center,
                                                color = if (selectedTurno == "Vespertino") Color.White else Color.Black
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}