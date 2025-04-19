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
fun WelcomeScreenAcademico(
    navController: NavController,
    loginViewModel: LoginViewModel,
    viewModel: PersonaViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val sharedPreferences = navController.context
        .getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val username = sharedPreferences.getString("username", "") ?: ""

    // Obtener datos personales del ViewModel
    LaunchedEffect(username) {
        viewModel.obtenerDatos(username)
    }

    val datos by viewModel.datosPersona.collectAsState()

    // Construir mensaje de bienvenida personalizado
    val nombreUsuario = datos.firstOrNull()?.nombre ?: ""
    val apellidoP = datos.firstOrNull()?.apellidoP ?: ""
    val apellidoM = datos.firstOrNull()?.apellidoM ?: ""

    val mensaje = when {
        nombreUsuario.isNotEmpty() -> "Bienvenido/a $nombreUsuario $apellidoP $apellidoM"
        else -> "Bienvenido/a Personal Académico"
    }

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
                Spacer(modifier = Modifier.width(20.dp))

                OptionButton(
                    title = "Solicitudes de Reemplazo",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_replacement),
                    onClick = { navController.navigate("") },
                    modifier = Modifier.size(150.dp)
                )
            }
        }
    }
}