package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.prueba3.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import com.example.prueba3.Views.AlumnosViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InformacionAlumno(navController: NavController,idETS: String,boleta: String, loginViewModel: LoginViewModel, viewModel: AlumnosViewModel) {

    // Variables para aviso del boton "Registrar asistencia"
    val fotoAlumno by viewModel.fotoAlumno.collectAsState()
    val alumnoEspecifico by viewModel.alumnoEspecifico.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.fetchAlumnoEspecifico(boleta)
        viewModel.fetchFotoAlumno(boleta)
    }

    var showDialog by remember { mutableStateOf(false) }
    var razon by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) } // Estado para controlar la visibilidad del menú
    val tipos = listOf(
        "Aceptado: Verificado por el profesor.",
        "Aceptado: Verificado con el código QR de la credencial.",
        "Aceptado: Verificado con el reconocimiento facial."
    )

    val horaActual = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }
    var showError by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val razonValida = razon.length >= 5
    val tipoValido = tipo.isNotEmpty()

    // Variables para aviso del boton "Registrar incidencia"

    var showDialog2 by remember { mutableStateOf(false) }
    val tipos2 = listOf(
        "Rechazado: Verificado por el profesor.",
        "Rechazado: Verificado con el reconocimiento facial.",
        "Rechazado: Verificado con el código QR de la credencial."
    )



    var showSuccessDialog2 by remember { mutableStateOf(false) }

    ValidateSession(navController = navController) {



        val userRole = loginViewModel.getUserRole()

        Scaffold(topBar = {
            MenuTopBar(
                true, true, loginViewModel,
                navController
            )
        },bottomBar = { MenuBottomBar(navController, userRole) }) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground) // Fondo oscuro azulado
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
                            // Foto del alumno (más grande)
                            if (fotoAlumno != null) {
                                val bitmap = BitmapFactory.decodeByteArray(fotoAlumno, 0, fotoAlumno!!.size)
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier
                                        .size(150.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, Color.Gray, CircleShape),
                                    contentScale = ContentScale.Crop

                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.icon_camara), // Foto predeterminada
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier
                                        .size(150.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, Color.Gray, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            // Datos del alumno y programa académico

                            alumnoEspecifico?.let { alumno ->
                                Column(
                                    modifier = Modifier
                                        .padding(start = 16.dp)
                                        .fillMaxWidth()
                                ) {
                                    // Alumno
                                    Text(
                                        text = "Alumno:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = "${alumno.apellidoP.trim()} ${alumno.apellidoM.trim()} ${alumno.nombre.trim()}",
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
                                        text = alumno.unidadAcademica.trim(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } ?: run {
                                Text(
                                    text = "No se encontraron datos.",
                                    color = Color.Red,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }

                        }

                        // Fila inferior: Boleta y CURP
                        alumnoEspecifico?.let { alumno ->
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
                                        text = alumno.boleta.trim(),
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
                                        text = alumno.curp.trim(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                    }
                }

                // Botones de acción
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier.weight(1f)
                            .padding(horizontal = 8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF64BD67))

                    ) {
                        Text(
                            text = "Registrar asistencia",
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = { showDialog2 = true },
                        modifier = Modifier.weight(1f)
                            .padding(horizontal = 8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFCB5252))
                    ) {
                        Text(
                            text = "Registrar incidencia",
                            color = Color.White
                        )
                    }
                }

                // Sección de problemas
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Texto informativo
                    Text(
                        text = "Si detecta un problema como:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Lista de problemas
                    ProblemOption("El alumno no trae credencial")
                    ProblemOption("El alumno no coincide con la foto de su credencial")
                    ProblemOption("Dudo de la autenticidad de la credencial")

                    // Texto informativo para verificación
                    Text(
                        text = "Prueba verificar con:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }

                // Botones de verificación
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { navController.navigate("scanQr") },
                        modifier = Modifier.weight(1f)
                            .padding(horizontal = 8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                    ) {
                        Text(
                            text = "Verificar QR",
                            color = Color.Black
                        )
                    }

                    Button(
                        onClick = { /* Acción para verificar con IA */ },
                        modifier = Modifier.weight(1f)
                            .padding(horizontal = 8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                    ) {
                        Text(
                            text = "Verificar con IA",
                            color = Color.Black
                        )
                    }
                }
            }
        }


        if (showDialog) {


            fun cerrarDialogo() {
                showDialog = false
                showError = false
                razon = ""
                tipo = ""
            }

            Dialog(onDismissRequest = { cerrarDialogo() }) {
                Box(
                    modifier = Modifier
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        // Botón de cierre (X)
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            IconButton(onClick = { cerrarDialogo() }) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar")
                            }
                        }

                        // Título
                        Text(
                            text = "Registrar asistencia",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Mensaje con la hora actual
                        Text(
                            text = "Se registrará la asistencia del alumno a las $horaActual. ¿Está de acuerdo?",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Campo de Razón
                        TextField(
                            value = razon,
                            onValueChange = { razon = it },
                            label = { Text("Razón") },
                            isError = !razonValida && razon.isNotEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        if (!razonValida && razon.isNotEmpty()) {
                            Text(
                                text = "La razón debe tener al menos 5 letras.",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        // Campo de Tipo (Dropdown)
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = it }
                        ) {
                            TextField(
                                value = tipo,
                                onValueChange = { },
                                label = { Text("Tipo") },
                                readOnly = true,
                                trailingIcon = {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Desplegar")
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                tipos.forEach { item ->
                                    DropdownMenuItem(
                                        onClick = {
                                            tipo = item
                                            expanded = false
                                        },
                                        text = { Text(text = item) }
                                    )
                                }
                            }
                        }
                        if (!tipoValido && showError) {
                            Text(
                                text = "Debe seleccionar un tipo.",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        // Mensaje de error general si intenta dar "Sí" sin llenar los campos
                        if (showError) {
                            Text(
                                text = "Debe completar todos los campos correctamente.",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        // Botones "Sí" y "No"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    if (razonValida && tipoValido) {
                                        cerrarDialogo()
                                        showSuccessDialog = true // Muestra el diálogo de éxito
                                    } else {
                                        showError = true // Muestra el mensaje de error
                                    }
                                },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text("Sí")
                            }
                            Button(
                                onClick = { cerrarDialogo() },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text("No")
                            }
                        }
                    }
                }
            }
        }

// Dialogo de éxito
        if (showSuccessDialog) {
            Dialog(onDismissRequest = { showSuccessDialog = false }) {
                Box(
                    modifier = Modifier
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Reporte de asistencia creado con éxito",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Button(
                            onClick = { showSuccessDialog = false },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Aceptar")
                        }
                    }
                }
            }
        }



        if (showDialog2) {


            fun cerrarDialogo() {
                showDialog2 = false
                showError = false
                razon = ""
                tipo = ""
            }

            Dialog(onDismissRequest = { cerrarDialogo() }) {
                Box(
                    modifier = Modifier
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        // Botón de cierre (X)
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            IconButton(onClick = { cerrarDialogo() }) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar")
                            }
                        }

                        // Título
                        Text(
                            text = "Registrar incidencia",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Mensaje con la hora actual
                        Text(
                            text = "Se registrará la incidencia del alumno a las $horaActual. ¿Está de acuerdo?",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Campo de Razón
                        TextField(
                            value = razon,
                            onValueChange = { razon = it },
                            label = { Text("Razón") },
                            isError = !razonValida && razon.isNotEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        if (!razonValida && razon.isNotEmpty()) {
                            Text(
                                text = "La razón debe tener al menos 5 letras.",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        // Campo de Tipo (Dropdown)
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = it }
                        ) {
                            TextField(
                                value = tipo,
                                onValueChange = { },
                                label = { Text("Tipo") },
                                readOnly = true,
                                trailingIcon = {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Desplegar")
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                tipos2.forEach { item ->
                                    DropdownMenuItem(
                                        onClick = {
                                            tipo = item
                                            expanded = false
                                        },
                                        text = { Text(text = item) }
                                    )
                                }
                            }
                        }
                        if (!tipoValido && showError) {
                            Text(
                                text = "Debe seleccionar un tipo.",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        // Mensaje de error general si intenta dar "Sí" sin llenar los campos
                        if (showError) {
                            Text(
                                text = "Debe completar todos los campos correctamente.",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        // Botones "Sí" y "No"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    if (razonValida && tipoValido) {
                                        cerrarDialogo()
                                        showSuccessDialog = true // Muestra el diálogo de éxito
                                    } else {
                                        showError = true // Muestra el mensaje de error
                                    }
                                },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text("Sí")
                            }
                            Button(
                                onClick = { cerrarDialogo() },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text("No")
                            }
                        }
                    }
                }
            }
        }

// Dialogo de éxito
        if (showSuccessDialog2) {
            Dialog(onDismissRequest = { showSuccessDialog2 = false }) {
                Box(
                    modifier = Modifier
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Reporte de incidencia creado con éxito",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Button(
                            onClick = { showSuccessDialog2 = false },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Aceptar")
                        }
                    }
                }
            }
        }



    }
}

@Composable
fun ProblemOption(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Info, // Icono informativo
            contentDescription = "Problema",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}