package Pantallas

import Pantallas.components.MenuTopBar
import Pantallas.components.ValidateSession
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba3.R

@Composable
fun WelcomeScreenAlumno(navController: NavController) {
    ValidateSession (navController = navController) {
        Scaffold(
            topBar = { MenuTopBar(navController = navController, title = "Bienvenido") }
        ) { padding ->
            // Contenedor principal centrado
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // Centrar verticalmente
            ) {
                Text(
                    text = "Bienvenido Usuario.",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "¿Qué deseas hacer?",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Contenedor para los botones
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp), // Espacio entre filas
                    horizontalAlignment = Alignment.CenterHorizontally // Centrar horizontalmente
                ) {
                    // Primera fila de botones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center // Centrar botones horizontalmente
                    ) {
                        OptionButton(
                            title = "ETS",
                            icon = ImageVector.vectorResource(id = R.drawable.exam),
                            onClick = { navController.navigate("LETSA") },
                            modifier = Modifier.size(150.dp) // Tamaño fijo
                        )

                        Spacer(modifier = Modifier.width(16.dp)) // Separador entre botones

                        OptionButton(
                            title = "Escanear Código QR",
                            icon = ImageVector.vectorResource(id = R.drawable.qrc),
                            onClick = { navController.navigate("scanQr") },
                            modifier = Modifier.size(150.dp) // Tamaño fijo
                        )
                    }

                    // Segunda fila de botones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center // Centrar botones horizontalmente
                    ) {
                        OptionButton(
                            title = "Información de acceso",
                            icon = Icons.Default.Info,
                            onClick = { navController.navigate("info") },
                            modifier = Modifier.size(150.dp) // Tamaño fijo
                        )
                    }
                }
            }
        }
    }
}
