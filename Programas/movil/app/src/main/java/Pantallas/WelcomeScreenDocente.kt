package Pantallas

import Pantallas.components.MenuBottomBar
import Pantallas.components.ValidateSession
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
fun WelcomeScreenDocente(navController: NavController, loginViewModel: LoginViewModel) {

    val userRole = loginViewModel.getUserRole()

        Scaffold(
            bottomBar = { MenuBottomBar(navController = navController, userRole) }
        ) { padding ->
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(padding)
            )
            ValidateSession(navController = navController) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(BlueBackground),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    item {
                        Text(
                            text = "Bienvenido Usuario.",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = Color.White,
                        )
                        Text(
                            text = "¿Qué deseas hacer?",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 32.dp),
                            color = Color.White,
                        )
                    }

                    // Botones organizados en dos filas
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(), // Asegura que el Box ocupe todo el ancho
                            contentAlignment = Alignment.Center // Centra el contenido dentro del Box
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp), // Espacio entre filas
                                horizontalAlignment = Alignment.CenterHorizontally, // Centra los botones en la columna
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Primera fila de botones
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center // Centra los botones dentro de la fila
                                ) {
                                    OptionButton(
                                        title = "Escanear Código QR",
                                        icon = ImageVector.vectorResource(id = R.drawable.qrc),
                                        onClick = { navController.navigate("scanQr") },
                                        modifier = Modifier.size(150.dp), // Tamaño fijo
                                        )
                                }


                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center // Centra los botones dentro de la fila
                                ) {
                                    OptionButton(
                                        title = "Información de acceso",
                                        icon = Icons.Default.Info,
                                        onClick = { navController.navigate("info") },
                                        modifier = Modifier.size(150.dp) // Tamaño fijo
                                    )

                                    Spacer(modifier = Modifier.width(20.dp))

                                    OptionButton(
                                        title = "ETS",
                                        icon = ImageVector.vectorResource(id = R.drawable.exam),
                                        onClick = { navController.navigate("LETS") },
                                        modifier = Modifier.size(150.dp) // Tamaño fijo
                                    )
                                }
                            }
                        }
                    }
                }
        }
    }
}
