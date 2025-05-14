package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.ui.theme.BlueBackground
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.prueba3.R
import com.example.prueba3.Views.AlumnosViewModel

@Composable
fun CredencialScreen(navController: NavController, loginViewModel: LoginViewModel, viewModel: AlumnosViewModel, boleta: String, ) {
    ValidateSession(navController = navController) {
        val userRole = loginViewModel.getUserRole()
        val alumnosCredencial by viewModel.alumnosCredencial.collectAsState(initial = emptyList())
        val alumno = alumnosCredencial.firstOrNull()
        var showAsistenciaDialog by remember { mutableStateOf(false) }
        var showEscanearDialog by remember { mutableStateOf(false) }

        LaunchedEffect(boleta) {
            viewModel.fetchCredencialAlumnos(boleta)
        }

        Scaffold(topBar = {
            MenuTopBar(
                true, true, loginViewModel,
                navController
            )
        },bottomBar = { MenuBottomBar(navController, userRole) }) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(padding)
            ) {
                // Título
                Text(
                    text = "Información del Alumno",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                // Credencial del alumno
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = 4.dp,
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Fila superior: Foto y datos del alumno
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = alumno?.imagenCredencial.takeUnless { it.isNullOrBlank() } ?: R.drawable.placeholder_image,
                                contentDescription = "Foto del alumno",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                            )

                            // Datos del alumno y programa académico
                            Column(
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .weight(1f)
                            ) {
                                // Alumno
                                Text(
                                    text = "Alumno:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = "${alumno?.nombre} ${alumno?.apellidoP} ${alumno?.apellidoM}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                // Programa académico
                                Text(
                                    text = "Programa académico:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = alumno?.unidadAcademica ?: "No disponible",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Fila inferior: Boleta y CURP
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Boleta
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Boleta:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                                Text(
                                    text = alumno?.boleta ?: "No disponible",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // CURP
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "CURP:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                                Text(
                                    text = alumno?.curp ?: "No disponible",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Botones de verificación
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { showAsistenciaDialog = true  },
                        modifier = Modifier.weight(1f)
                            .padding(horizontal = 8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                    ) {
                        Text(
                            text = "Registrar asistencia",
                            color = Color.Black
                        )
                    }

                    Button(
                        onClick = {showEscanearDialog = true },
                        modifier = Modifier.weight(1f)
                            .padding(horizontal = 8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                    ) {
                        Text(
                            text = "Escanear credencial",
                            color = Color.Black
                        )
                    }
                }
            }

            if (showAsistenciaDialog) {
                AlertDialog(
                    onDismissRequest = { showAsistenciaDialog = false },
                    title = { Text("Confirmación", fontWeight = FontWeight.Bold) },
                    text = { Text("¿Estás seguro de querer registrar la asistencia del alumno?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showAsistenciaDialog = false
                                // TODO: Agregar la lógica para registrar la asistencia
                            }
                        ) {
                            Text("Aceptar")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showAsistenciaDialog = false }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            if (showEscanearDialog) {
                AlertDialog(
                    onDismissRequest = { showEscanearDialog = false },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Warning, contentDescription = "Alerta", tint = Color(0xFFFFC107))
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    },
                    text = { Text("Si tienes dudas sobre la identidad del alumno, escanea su credencial para confirmar su identidad.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showEscanearDialog = false
                                navController.navigate("qrScanner")
                            }
                        ) {
                            Text("Aceptar")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showEscanearDialog = false }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}
