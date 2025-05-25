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
    val userRole = loginViewModel.getUserRole()


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
                if (userRole == "Presidente de academia" || userRole == "Jefe Departamento") {
                OptionButton(
                    title = "Solicitudes de Reemplazo",
                    icon = ImageVector.vectorResource(id = R.drawable.exam),
                    onClick = { navController.navigate("listadoReemplazos") },
                    modifier = Modifier.size(150.dp)
                    )
                }

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

