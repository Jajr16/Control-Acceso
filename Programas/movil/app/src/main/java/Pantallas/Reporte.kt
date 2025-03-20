package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import android.text.Layout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.ui.theme.BlueBackground
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.prueba3.R
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Email

//painter = painterResource(id = R.drawable.info),


@Composable
fun Reporte(navController: NavController, loginViewModel: LoginViewModel) {
    ValidateSession(navController = navController) {
        val userRole = loginViewModel.getUserRole()

        Scaffold(
            topBar = {
                MenuTopBar(
                    true, true, loginViewModel,
                    navController
                )
            },
            bottomBar = { MenuBottomBar(navController, userRole) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(padding)
            ) {
                // Título
                Text(
                    text = "Reporte",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 8.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                // Contenedor para la foto y el ícono
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center // Centra la foto
                ) {
                    // Foto circular (grande)
                    Image(
                        painter = painterResource(id = R.drawable.info),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(160.dp) // Tamaño grande para la foto
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                    )

                    // Icono pequeño superpuesto en la esquina inferior derecha de la foto
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center) // Centra el ícono
                            .offset(x = 50.dp, y = 50.dp) // Desplaza ligeramente hacia la derecha y abajo
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Icono de perfil",
                            modifier = Modifier
                                .size(60.dp) // Tamaño pequeño para el ícono
                                .background(Color.LightGray, CircleShape) // Fondo circular
                                .padding(4.dp),
                            tint = Color.White
                        )
                    }
                }

                // Contenido del reporte con LazyColumn (scroll integrado)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Ocupa el espacio restante
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(1) { // Solo un ítem para el contenido
                        Column {
                            InfoRow("Boleta", "2021330022")
                            InfoRow("Nombre completo", "Huertas Ramírez Daniel Martín")
                            InfoRow("CURP", "HURD030120HDFRMNA0")
                            InfoRow("Carrera", "Inteligencia artificial")
                            InfoRow("Unidad académica", "ESCOM")
                            InfoRow("Método", "Visual")
                            InfoRow("Razón", "El docente reconoció al alumno")
                            InfoRow("Periodo", "2025-1")
                            InfoRow("Turno", "Matutino")
                            InfoRow("Materia", "Señales")
                            InfoRow("Tipo", "Regular")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 2.dp, // Reducimos la sombra
        shape = RoundedCornerShape(10.dp),
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp) // Reducimos el padding interno
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium, // Texto más pequeño
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,                style = MaterialTheme.typography.bodyMedium, // Texto más pequeño
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
    }
}