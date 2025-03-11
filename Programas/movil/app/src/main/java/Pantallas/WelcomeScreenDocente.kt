package Pantallas

import Pantallas.Plantillas.WelcomeScreenBase
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba3.R
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.Views.PersonaViewModel
import androidx.compose.runtime.getValue

@Composable
fun WelcomeScreenDocente(navController: NavController, loginViewModel: LoginViewModel, viewModel: PersonaViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    val sharedPreferences = navController.context
        .getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val username = sharedPreferences.getString("username", "") ?: ""

    LaunchedEffect(username) {
        viewModel.obtenerDatos(username)
    }

    val datos by viewModel.datosPersona.collectAsState()

    // Obtener el nombre del primer elemento si hay datos
    val nombreUsuario = datos.firstOrNull()?.nombre ?: ""

    val appellidoP = datos.firstOrNull()?.apellidoP ?: ""

    val appellidoM = datos.firstOrNull()?.apellidoM ?: ""

    // Construir el mensaje dinámico
    val mensaje = if (nombreUsuario.isNotEmpty()) "Bienvenido $nombreUsuario $appellidoP $appellidoM " else "Bienvenido Docente"

    WelcomeScreenBase(navController, loginViewModel, mensaje) {
        Spacer(modifier = Modifier.height(50.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                OptionButton(
                    title = "Reconocimiento facial yeah yeah",
                    icon = ImageVector.vectorResource(id = R.drawable.qrc),
                    onClick = { navController.navigate("scanQr") },
                    modifier = Modifier.size(150.dp)
                )

                Spacer(modifier = Modifier.width(20.dp))

                OptionButton(
                    title = "ETS",
                    icon = ImageVector.vectorResource(id = R.drawable.exam),
                    onClick = { navController.navigate("LETS") },
                    modifier = Modifier.size(150.dp)
                )
            }
        }
    }
}
//
//@Composable
//fun WelcomeScreenDocente(navController: NavController, loginViewModel: LoginViewModel) {
//
//    val userRole = loginViewModel.getUserRole();
//
//        Scaffold(
//            bottomBar = { MenuBottomBar(navController = navController, userRole) }
//        ) { padding ->
//            androidx.compose.foundation.layout.Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(BlueBackground)
//                    .padding(padding)
//            )
//            ValidateSession(navController = navController) {
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(padding)
//                        .background(BlueBackground),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ) {
//                    item {
//                        Text(
//                            text = "Bienvenido Usuario.",
//                            style = MaterialTheme.typography.titleLarge,
//                            modifier = Modifier.padding(bottom = 16.dp),
//                            color = Color.White,
//                        )
//                        Text(
//                            text = "¿Qué deseas hacer?",
//                            style = MaterialTheme.typography.bodyMedium,
//                            modifier = Modifier.padding(bottom = 32.dp),
//                            color = Color.White,
//                        )
//                    }
//
//                    // Botones organizados en dos filas
//                    item {
//                        Box(
//                            modifier = Modifier.fillMaxWidth(), // Asegura que el Box ocupe todo el ancho
//                            contentAlignment = Alignment.Center // Centra el contenido dentro del Box
//                        ) {
//                            Column(
//                                verticalArrangement = Arrangement.spacedBy(16.dp), // Espacio entre filas
//                                horizontalAlignment = Alignment.CenterHorizontally, // Centra los botones en la columna
//                                modifier = Modifier.fillMaxWidth()
//                            ) {
//                                // Primera fila de botones
//                                Row(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.Center // Centra los botones dentro de la fila
//                                ) {
//                                    OptionButton(
//                                        title = "Escanear Código QR",
//                                        icon = ImageVector.vectorResource(id = R.drawable.qrc),
//                                        onClick = { navController.navigate("scanQr") },
//                                        modifier = Modifier.size(150.dp), // Tamaño fijo
//                                        )
//                                }
//
//
//                                Row(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.Center // Centra los botones dentro de la fila
//                                ) {
//                                    OptionButton(
//                                        title = "Información de acceso",
//                                        icon = Icons.Default.Info,
//                                        onClick = { navController.navigate("info") },
//                                        modifier = Modifier.size(150.dp) // Tamaño fijo
//                                    )
//
//                                    Spacer(modifier = Modifier.width(20.dp))
//
//                                    OptionButton(
//                                        title = "ETS",
//                                        icon = ImageVector.vectorResource(id = R.drawable.exam),
//                                        onClick = { navController.navigate("LETS") },
//                                        modifier = Modifier.size(150.dp) // Tamaño fijo
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//        }
//    }
//}
