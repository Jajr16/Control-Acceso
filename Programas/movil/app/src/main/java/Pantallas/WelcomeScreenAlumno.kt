package Pantallas

import Pantallas.Plantillas.WelcomeScreenBase
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba3.R
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.ui.theme.BlueBackground

@Composable
fun WelcomeScreenAlumno(navController: NavController, loginViewModel: LoginViewModel){
    WelcomeScreenBase(navController, loginViewModel, "Bienvenido Alumno") {
        Spacer(modifier = Modifier.height(50.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OptionButton(
                title = "Listado de ETS",
                icon = ImageVector.vectorResource(id = R.drawable.exam),
                onClick = { navController.navigate("LETSA") },
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.width(20.dp))

            OptionButton(
                title = "Información de acceso",
                icon = Icons.Default.Info,
                onClick = { navController.navigate("info") },
                modifier = Modifier.size(150.dp)
            )
        }
    }

}

//@Composable
//fun WelcomeScreenAlumno(navController: NavController,
//                           loginViewModel: LoginViewModel,
//                           viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),) {
//
//    val userRole = loginViewModel.getUserRole()
//    val username = loginViewModel.getUserName()
//
//    val confirmation by remember { viewModel.StatusInscripcion }.collectAsState()
//
//    LaunchedEffect(username) {
//        username?.let {
//            viewModel.getConfirmationInscription(it)
//        }
//    }
//
//    Scaffold(
//        bottomBar = { MenuBottomBar(navController = navController, userRole) }
//    ) { padding ->
//        androidx.compose.foundation.layout.Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(BlueBackground)
//                .padding(padding)
//        )
//
//        ValidateSession (navController = navController) {
//            // Contenedor principal centrado
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(top = 50.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//
//                Image(
//                    painter = painterResource(id = R.drawable.escom),
//                    contentDescription = "Logo IPN",
//                    modifier = Modifier
//                        .size(250.dp)
//                )
//
//                Spacer(modifier = Modifier.height(15.dp))
//
//                Text(
//                    text = "Bienvenido Usuario.",
//                    style = MaterialTheme.typography.titleLarge,
//                    modifier = Modifier.padding(bottom = 16.dp),
//                    color = Color.White,
//                )
//
//                Divider(
//                    modifier = Modifier
//                        .padding(vertical = 8.dp)
//                        .width(200.dp),
//                    thickness = 1.dp,
//                    color = Color.White
//                )
//
//                when (confirmation) {
//                    true -> {
//                        Text(
//                            text = "¿Qué deseas hacer?",
//                            style = MaterialTheme.typography.bodyMedium,
//                            modifier = Modifier.padding(bottom = 32.dp),
//                            color = Color.White,
//                        )
//
//                        Spacer(modifier = Modifier.height(50.dp))
//
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.Center
//                        ) {
//                            OptionButton(
//                                title = "Mis ETS",
//                                icon = ImageVector.vectorResource(id = R.drawable.exam),
//                                onClick = { navController.navigate("LETSA") },
//                                modifier = Modifier.size(150.dp)
//                            )
//
//                            Spacer(modifier = Modifier.width(20.dp))
//
//                            OptionButton(
//                                title = "Información de acceso",
//                                icon = Icons.Default.Info,
//                                onClick = { navController.navigate("info") },
//                                modifier = Modifier.size(150.dp),
//                            )
//                        }
//                    }
//                    false -> {
//                        Text(
//                            text = "No es tiempo de ETS",
//                            style = MaterialTheme.typography.bodyMedium,
//                            modifier = Modifier.padding(bottom = 32.dp),
//                            color = Color.White,
//                        )
//                    }
//                    null -> {
//                        Text(
//                            text = "Cargando...",
//                            style = MaterialTheme.typography.bodyMedium,
//                            color = Color.White,
//                        )
//                    }
//                }
//            }
//        }
//    }
//}

@Composable
fun OptionButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF5F5F5))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = BlueBackground
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
