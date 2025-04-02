package Pantallas

import Pantallas.components.BuscadorConLista
import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Consultar Alumnos",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Cargando...", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
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
                        }
                    )
                }
            }
        }
    }
}
