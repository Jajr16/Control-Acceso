package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import android.graphics.BitmapFactory
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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.filled.Email
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.example.prueba3.Views.AlumnosViewModel

//painter = painterResource(id = R.drawable.info),

@Composable
fun Reporte(
    navController: NavController,
    idETS: String,
    boleta: String,
    loginViewModel: LoginViewModel,
    viewModel: AlumnosViewModel,
    aceptado: Int
) {
    ValidateSession(navController = navController) {
        val userRole = loginViewModel.getUserRole()
        val reporteList by viewModel.reporte.collectAsState()
        val loading by viewModel.loadingState.collectAsState()
        val imagenBytes by viewModel.imagenBytes.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.fetchReporte(idETS.toInt(), boleta)
        }

        Scaffold(
            topBar = { MenuTopBar(true, true, loginViewModel, navController) },
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
                    contentAlignment = Alignment.Center
                ) {
                    // Mostrar la imagen si está disponible
                    imagenBytes?.let { bytes ->
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        Image(
                            bitmap.asImageBitmap(),
                            contentDescription = "Imagen del alumno",
                            modifier = Modifier.size(150.dp)
                        )
                    }
                }

                // Contenido del reporte con LazyColumn (scroll integrado)
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(reporteList) { reporte ->
                            Column {
                                InfoRow("Boleta", boleta)
                                InfoRow("Nombre completo", "${reporte.nombre} ${reporte.apellidoP} ${reporte.apellidoM}")
                                InfoRow("CURP", reporte.curp ?: "N/A")
                                InfoRow("Carrera", reporte.carrera ?: "N/A")
                                InfoRow("Unidad académica", reporte.escuela ?: "N/A")
                                InfoRow("Método", reporte.tipo ?: "N/A")
                                InfoRow("Razón", reporte.motivo ?: "N/A")
                                InfoRow("Periodo", reporte.periodo ?: "N/A")
                                InfoRow("Turno", reporte.turno ?: "N/A")
                                InfoRow("Materia", reporte.materia ?: "N/A")
                                InfoRow("Tipo", reporte.tipo ?: "N/A")
                                InfoRow("Fecha Ingreso", reporte.fechaIngreso?.toString() ?: "N/A")
                                InfoRow("Hora Ingreso", reporte.horaIngreso?.toString() ?: "N/A")
                                InfoRow("Nombre Docente", reporte.nombreDocente ?: "N/A")
                                InfoRow("Tipo Estado", reporte.tipoEstado ?: "N/A")
                                InfoRow("Presicion", reporte.presicion?.toString() ?: "N/A")
                            }
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
        elevation = 2.dp,
        shape = RoundedCornerShape(10.dp),
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
    }
}