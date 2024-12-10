package Pantallas

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba3.R


@Composable
fun WelcomeScreen(navController: NavController) {
    Scaffold(
        topBar = { MenuTopBar(navController = navController, title = "Bienvenido") }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
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
            }

            // Botones organizados en dos filas
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp), // Espacio entre filas
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Primera fila de botones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OptionButton(
                            title = "Reconocimiento Facial",
                            icon = Icons.Default.Face,
                            onClick = { navController.navigate("camara") },
                            modifier = Modifier.weight(1f)
                        )

                        OptionButton(
                            title = "Escanear Código QR",
                            icon = ImageVector.vectorResource(id = R.drawable.qrc),
                            onClick = { navController.navigate("scanQr") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Segunda fila de botones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OptionButton(
                            title = "Información de acceso",
                            icon = Icons.Default.Info,
                            onClick = { navController.navigate("info") },
                            modifier = Modifier.weight(1f)
                        )

                        OptionButton(
                            title = "ETS",
                            icon = ImageVector.vectorResource(id = R.drawable.exam),
                            onClick = { navController.navigate("LETS") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

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
            .aspectRatio(1f) // Asegura que el botón sea cuadrado
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
                tint = Color(0xFF6c1d45)
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
