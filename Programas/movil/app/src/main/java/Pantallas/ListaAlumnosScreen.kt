package Pantallas


import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prueba3.Views.AlumnosViewModel
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.ui.theme.BlueBackground
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ListaAlumnosScreen(
    navController: NavController,
    idETS: String,
    viewModel: AlumnosViewModel,
    loginViewModel: LoginViewModel
) {
    ValidateSession(navController = navController) {
        val alumnosList by viewModel.alumnosList.collectAsState()
        val isLoading by remember { viewModel.loadingState }.collectAsState()

        val userRole = loginViewModel.getUserRole()

         //Llama al ViewModel para obtener los datos al cambiar el idETS
        LaunchedEffect(idETS) {
            viewModel.fetchAlumno(idETS)
        }

        Scaffold(
            topBar = {
                MenuTopBar(
                    true, true, loginViewModel,
                    navController
                )
            },
            bottomBar = {
                MenuBottomBar(navController = navController, userRole)
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground) // Fondo oscuro azulado
                    .padding(padding)
            ) {
                // Título con fondo estilizado
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BlueBackground) // Color de fondo del título
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ETS Big Data",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(top = 50.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Lista de alumnos inscritos",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFD9D9D9), // Color gris claro para el subtítulo
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Divider(
                        color = Color(0xFFFFFFFF ),
                        thickness = 5.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
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
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF )),
                                 // Color de la tarjeta
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Botón con boleta y nombre
                                    Button(
                                        onClick = {navController.navigate("InfoA")},
                                        modifier = Modifier.weight(0.8f),
                                        colors = ButtonDefaults.buttonColors(Color(0xFFFFFFFF )) // Color más oscuro
                                    ) {
                                        Column {
                                            Text(
                                                text = "Boleta: ${alumno.boleta}",
                                                fontSize = 14.sp,
                                                color = Color.Black
                                            )
                                            Text(
                                                text = "Nombre: ${alumno.nombreA} ${alumno.apellidoP} ${alumno.apellidoM}",
                                                fontSize = 14.sp,
                                                color = Color.Black
                                            )

                                        }
                                    }

                                    // Botón redondo con estado
                                    val (color, icon) = when (alumno.aceptado) {
                                        true -> Color(0xFF4CAF50) to Icons.Filled.Check
                                        false -> Color(0xFFF44336) to Icons.Filled.Close
                                        else -> Color.Gray to Icons.Filled.Info
                                    }

                                    IconButton(
                                        onClick = {
//                                            val nuevoEstado = alumno.aceptado != true
//                                            CoroutineScope(Dispatchers.Main).launch {
//                                                viewModel.updateAsistencia(alumno.boleta, idETS.toInt(), nuevoEstado)
//                                            }
                                            navController.navigate("Reporte")
                                        },
                                        modifier = Modifier
                                            .size(50.dp)
                                            .padding(8.dp)
                                            .background(Color(0xFFF0F0F0)) // Gris claro
                                            .clip(RoundedCornerShape(12.dp)) // Bordes redondeados
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = "Estado",
                                            tint = color,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No hay alumnos inscritos al ETS.",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}