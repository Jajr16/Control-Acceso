package Pantallas.Plantillas

import Pantallas.components.MenuBottomBar
import Pantallas.components.ValidateSession
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba3.R
import com.example.prueba3.Views.HomeViewModel
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.ui.theme.BlueBackground

@Composable
fun WelcomeScreenBase(
    navController: NavController,
    loginViewModel: LoginViewModel,
    title: String = "Bienvenido Usuario.",
    message: String = "¿Qué deseas hacer?",
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    buttons: @Composable () -> Unit, // Aquí se pasan los botones específicos de cada pantalla
) {
    val validacion by remember { viewModel.StatusValidacion }.collectAsState()
    val errorMessage by remember { viewModel.errorMessage }.collectAsState()

    val userRole = loginViewModel.getUserRole()
    val username = loginViewModel.getUserName()

    // Realizar la validación cuando el username esté disponible
    LaunchedEffect(username) {
        username?.let {
            viewModel.getConfirmationValidacion(it)
        }
    }

    Scaffold(
        bottomBar = { MenuBottomBar(navController = navController, userRole) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BlueBackground)
                .padding(padding)
        ) {
            ValidateSession(navController = navController) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.escom),
                        contentDescription = "Logo ESCOM",
                        modifier = Modifier.size(250.dp)
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = Color.White
                    )

                    Divider(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .width(200.dp),
                        thickness = 1.dp,
                        color = Color.White
                    )

                    when (validacion) {
                        true -> {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 32.dp),
                            color = Color.White
                        )
                                buttons () // Aquí se insertan los botones específicos de cada pantalla
                        }
                        false -> {
                            Text(
                                text = errorMessage ?: "No se pudo validar el usuario.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 32.dp),
                                color = Color.White
                            )
                        }
                        null -> {
                            Text(
                                text = "Cargando...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
