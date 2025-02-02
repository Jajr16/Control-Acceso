package Pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun ConsultarAlumnoScreen(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    val alumnos = remember { mutableStateListOf<Alumno>() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            alumnos.addAll(fetchAlumnos())
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF003366))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Consultar Alumnos",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.LightGray)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                BasicTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (searchText.isEmpty()) {
                            Text("Buscar por nombre o boleta", color = Color.DarkGray, fontSize = 14.sp)
                        }
                        innerTextField()
                    }
                )
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                alumnos.forEach { alumno ->
                    AlumnoCard(alumno, navController)
                    Divider(color = Color.LightGray, thickness = 1.dp)
                }
            }
        }

        // Posicionar la barra de navegación en la parte inferior
        BottomNavigationBar(navController = navController, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun AlumnoCard(alumno: Alumno, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { navController.navigate("detalle_alumno/${alumno.boleta}") },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Boleta: ${alumno.boleta}", fontSize = 16.sp, color = Color.Black)
            Text(text = "Nombre: ${alumno.nombreCompleto}", fontSize = 16.sp, color = Color.Black)
        }
    }
}

data class Alumno(val boleta: String, val nombreCompleto: String)

suspend fun fetchAlumnos(): List<Alumno> {
    return listOf(
        Alumno("2021340022", "Alejandra De la Cruz De la Cruz"),
        Alumno("2021340023", "Alfredo Jimenez Rodriguez")
    )
}

// Barra de navegación reutilizable
@Composable
fun BottomNavigationBar(navController: NavController, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color.LightGray),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Home,
            contentDescription = "Inicio",
            modifier = Modifier
                .size(32.dp)
                .clickable { navController.navigate("login") }
        )
        Icon(
            imageVector = Icons.Filled.DateRange,
            contentDescription = "Calendario",
            modifier = Modifier
                .size(32.dp)
                .clickable { navController.navigate("table") }
        )
        Icon(
            imageVector = Icons.Filled.Notifications,
            contentDescription = "Notificaciones",
            modifier = Modifier
                .size(32.dp)
                .clickable { navController.navigate("calculator") }
        )
    }
}
