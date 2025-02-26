package Pantallas

import Pantallas.components.MenuBottomBar
import Pantallas.components.ValidateSession
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

        val filteredList = alumnosListado.filter {
            it.boleta.contains(searchQuery, ignoreCase = true) ||
                    it.nombre.contains(searchQuery, ignoreCase = true) ||
                    it.apellidoP.contains(searchQuery, ignoreCase = true) ||
                    it.apellidoM.contains(searchQuery, ignoreCase = true)
        }

        LaunchedEffect(Unit) {
            viewModel.fetchListalumnos()
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
                    text = "Consultar Alumnos",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal, // Sin negrita
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por nombre o boleta") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray, shape = MaterialTheme.shapes.medium) // Color gris claro
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Cargando...", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    if (filteredList.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(filteredList) { alumno ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                        .clickable { navController.navigate("scanQr") },
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text(text = "Boleta: ${alumno.boleta}", style = MaterialTheme.typography.bodyMedium)
                                        Text(
                                            text = "Nombre: ${alumno.nombre} ${alumno.apellidoP} ${alumno.apellidoM}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No hay alumnos para mostrar.",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}