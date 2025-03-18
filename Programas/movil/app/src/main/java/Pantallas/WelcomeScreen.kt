package Pantallas

import Pantallas.Plantillas.WelcomeScreenBase
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba3.R
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.Views.PersonaViewModel


@Composable
fun WelcomeScreen(navController: NavController, loginViewModel: LoginViewModel, viewModel: PersonaViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
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

    val mensaje = if (nombreUsuario.isNotEmpty()) "Bienvenido $nombreUsuario $appellidoP $appellidoM " else "Bienvenido Personal de Seguridad"

    WelcomeScreenBase(navController, loginViewModel, mensaje) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OptionButton(
                title = "Consultar Alumnos",
                icon = ImageVector.vectorResource(id = R.drawable.exam),
                onClick = { navController.navigate("ConsultarAlumnos") },
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.width(20.dp))

            OptionButton(
                title = "Escanear credencial",
                icon = ImageVector.vectorResource(id = R.drawable.qrc),
                onClick = { navController.navigate("scanQr") },
                modifier = Modifier.size(150.dp)
            )
        }
    }
}

//@Composable
//fun WelcomeScreen(
//    navController: NavController,
//    loginViewModel: LoginViewModel,
//    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
//) {
//    // Obtener el estado de validación
//    val validacion by remember { viewModel.StatusValidacion }.collectAsState()
//
//    // Obtener el nombre de usuario y rol
//    val userRole = loginViewModel.getUserRole()
//    val username = loginViewModel.getUserName()
//
//    // Realizar la validación cuando el username esté disponible
//    LaunchedEffect(username) {
//        username?.let {
//            viewModel.getConfirmationValidacion(it)
//        }
//    }
//
//    Scaffold(
//        bottomBar = { MenuBottomBar(navController = navController, userRole) }
//    ) { padding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(BlueBackground)
//                .padding(padding)
//        ) {
//            ValidateSession(navController = navController) {
//                // Contenedor principal centrado
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(top = 100.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    // Logo de la ESCom
//                    Image(
//                        painter = painterResource(id = R.drawable.escom),
//                        contentDescription = "Logo IPN",
//                        modifier = Modifier
//                            .size(250.dp)
//                    )
//
//                    Spacer(modifier = Modifier.height(15.dp))
//
//                    Text(
//                        text = "Bienvenido Usuario.",
//                        style = MaterialTheme.typography.titleLarge,
//                        modifier = Modifier.padding(bottom = 16.dp),
//                        color = Color.White
//                    )
//
//                    Divider(
//                        modifier = Modifier
//                            .padding(vertical = 8.dp)
//                            .width(200.dp),
//                        thickness = 1.dp,
//                        color = Color.White
//                    )
//
//                    // Comportamiento dependiendo de la validación
//                    when (validacion) {
//                        true -> {
//                            Text(
//                                text = "¿Qué deseas hacer?",
//                                style = MaterialTheme.typography.bodyMedium,
//                                modifier = Modifier.padding(bottom = 32.dp),
//                                color = Color.White
//                            )
//
//                            Spacer(modifier = Modifier.height(50.dp))
//
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.Center
//                            ) {
//                                // Botón para consultar alumnos, pasando fecha y periodo
//                                OptionButton(
//                                    title = "Consultar Alumnos",
//                                    icon = ImageVector.vectorResource(id = R.drawable.exam),
//                                    onClick = {
//                                            navController.navigate("ConsultarAlumnos")
//
//                                    },
//                                    modifier = Modifier.size(150.dp)
//                                )
//
//                                Spacer(modifier = Modifier.width(20.dp))
//
//                                // Botón para escanear credencial
//                                OptionButton(
//                                    title = "Escanear credencial",
//                                    icon = Icons.Default.Info,
//                                    onClick = { navController.navigate("scanQr") },
//                                    modifier = Modifier.size(150.dp)
//                                )
//                            }
//                        }
//                        false -> {
//                            Text(
//                                text = "HOLA SOY DE SEGURIDAD",
//                                style = MaterialTheme.typography.bodyMedium,
//                                modifier = Modifier.padding(bottom = 32.dp),
//                                color = Color.White
//                            )
//                        }
//                        null -> {
//                            Text(
//                                text = "Cargando...",
//                                style = MaterialTheme.typography.bodyMedium,
//                                color = Color.White
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
