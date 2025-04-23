package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.prueba3.Views.AlumnosViewModel
import com.example.prueba3.Views.CamaraViewModel
import com.example.prueba3.Views.InformacionAlumnoViewModel
import java.net.ConnectException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InformacionAlumno(
    navController: NavController,
    idETS: String,
    boleta: String,
    loginViewModel: LoginViewModel,
    viewModel: AlumnosViewModel,
    camaraViewModel: CamaraViewModel,
    informacionAlumnoViewModel: InformacionAlumnoViewModel

) {

    val bitmap = camaraViewModel.imagenBitmap.value
    val precision = camaraViewModel.precision.value
    Log.d("InformacionAlumno", "Precision: $precision") // Log para depuración


    Log.d("InformacionAlumno", "Bitmap from ViewModel: $bitmap")

    // Obterner los valores de los parámetros iniciales
    val idETSParam = remember { idETS }
    val boletaParam = remember { boleta }

    // Guarda los valores en el AlumnosViewModel al inicio
    LaunchedEffect(Unit) {
        viewModel.guardarIdETSFlujo(idETSParam)
        viewModel.guardarBoletaFlujo(boletaParam)
    }

    LaunchedEffect(boleta, idETS) {
        camaraViewModel.updateBoletaAndIdETS(boleta, idETS)
    }


    // Variables para aviso del boton "Registrar asistencia"
    val fotoAlumno by viewModel.fotoAlumno.collectAsState()
    val alumnoEspecifico by viewModel.alumnoEspecifico.collectAsState()
    val ingresoResultado by viewModel.ingresoResultado.collectAsState()
    val loadingFotoAlumno = remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        viewModel.fetchAlumnoEspecifico(boleta)
        loadingFotoAlumno.value = true
        viewModel.fetchFotoAlumno(boleta) { loadingFotoAlumno.value = false }
    }

    LaunchedEffect(Unit) { // Ejecutar solo una vez
        camaraViewModel.setPythonResponse(null) // Limpiar el estado del ViewModel
    }

    LaunchedEffect(boleta) {
        viewModel.verificarIngresoSalon(idETS.toInt(), boleta) // Verifica si ya existe el reporte
    }

    val eliminacionExitosa by viewModel.eliminacionExitosa.collectAsState()
    val mensajeEliminacion by viewModel.mensajeEliminacion.collectAsState()
    val eliminacionCompletada by viewModel.eliminacionCompletada.collectAsState() // Observa este estado


    var mostrarDialogoEliminacion by remember { mutableStateOf(false) }

    var mostrarDialogoErrorEliminacion by remember { mutableStateOf(false) }

    // Observar el estado de eliminación exitosa para mostrar el diálogo
    LaunchedEffect(eliminacionExitosa) {
        if (eliminacionExitosa == true) {
            mostrarDialogoEliminacion = true
        }
    }

    // Observar el mensaje de eliminación (para errores)
    LaunchedEffect(mensajeEliminacion) {
        if (!mensajeEliminacion.isNullOrEmpty() && eliminacionExitosa == false) {
            mostrarDialogoErrorEliminacion = true
        }
    }


//    // Diálogo de error de eliminación
//    val mensajeEliminacion by viewModel.mensajeEliminacion.collectAsState()
//    var mostrarDialogoErrorEliminacion by remember { mutableStateOf(false) }
//
//    LaunchedEffect(mensajeEliminacion) {
//        if (!mensajeEliminacion.isNullOrEmpty()) {
//            mostrarDialogoErrorEliminacion = true
//        }
//    }


    // Estado para controlar la visibilidad del botón de eliminación
    var showDeleteButton by remember { mutableStateOf(false) }


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


    var showErrorDialog2 by remember { mutableStateOf(false) }



    // Nuevo estado para el diálogo de error
    var errorMessage by remember { mutableStateOf("") } // Mensaje de error

    val razonValida = razon.length >= 5
    val tipoValido = tipo.isNotEmpty()

    // Variables para aviso del boton "Registrar incidencia"

    var showDialog2 by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val tipos2 = listOf(
        "Rechazado: Verificado por el profesor.",
        "Rechazado: Verificado con el reconocimiento facial.",
        "Rechazado: Verificado con el código QR de la credencial."
    )


    var showSuccessDialog2 by remember { mutableStateOf(false) }

    ValidateSession(navController = navController) {


        val userRole = loginViewModel.getUserRole()
        val scrollState = rememberScrollState()

        Scaffold(topBar = {
            MenuTopBar(
                true, true, loginViewModel,
                navController
            )
        }, bottomBar = { MenuBottomBar(navController, userRole) }) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground) // Fondo oscuro azulado
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {


                if (ingresoResultado == "existe") {
                    // Mostrar mensaje de reporte existente y botón de eliminación
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center

                    ) {
                        Text(
                            text = "El reporte para este alumno ya fue creado con anterioridad. ¿Desea eliminar el reporte?",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = {
                               viewModel.eliminarReporte(boleta, idETS.toInt())


                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFCB5252))
                        ) {
                            Text("Borrar Reporte", color = Color.White)
                        }





                        if (mostrarDialogoErrorEliminacion) {
                            AlertDialog(
                                onDismissRequest = { mostrarDialogoErrorEliminacion = false },
                                title = { Text("Error") },
                                text = {
                                    if (mensajeEliminacion == "Error de conexión") {
                                        Text("Error de conexión")
                                    } else {
                                        Text(mensajeEliminacion ?: "Ocurrió un fallo en el proceso")
                                    }
                                },
                                confirmButton = {
                                    Button(onClick = { mostrarDialogoErrorEliminacion = false }) {
                                        Text("Aceptar")
                                    }
                                }
                            )
                        }

                        // Diálogo de confirmación de eliminación exitosa
                        if (mostrarDialogoEliminacion) {
                            AlertDialog(
                                onDismissRequest = {
                                    mostrarDialogoEliminacion = false
                                    viewModel.clearEliminacionEstado()
                                    viewModel.verificarIngresoSalon(idETS.toInt(), boleta) // Recargar al descartar (opcional)
                                    viewModel.clearEliminacionCompletada()
                                },
                                title = { Text("Éxito") },
                                text = { Text(mensajeEliminacion ?: "El reporte se eliminó exitosamente.") },
                                confirmButton = {
                                    Button(onClick = {
                                        mostrarDialogoEliminacion = false
                                        viewModel.clearEliminacionEstado()
                                        viewModel.verificarIngresoSalon(idETS.toInt(), boleta) // Recargar al aceptar
                                        viewModel.clearEliminacionCompletada()
                                    }) {
                                        Text("Aceptar")
                                    }
                                }
                            )
                        }





                    }
                } else {

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
//                            // Foto del alumno (más grande)
//                            if (bitmap != null) {
//                                //Log.d("InformacionAlumno", "Bitmap is not null, showing image")
//                                Image(
//                                    bitmap = bitmap.asImageBitmap(),
//                                    contentDescription = "Foto del alumno",
//                                    modifier = Modifier
//                                        .size(150.dp)
//                                        .clip(CircleShape)
//                                        .border(2.dp, Color.Gray, CircleShape),
//                                    contentScale = ContentScale.Crop
//                                )
                                if (fotoAlumno != null) {
                                    val bitmap = BitmapFactory.decodeByteArray(
                                        fotoAlumno,
                                        0,
                                        fotoAlumno!!.size
                                    )
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
                                    //Log.d("InformacionAlumno", "Bitmap is null, showing default image")
                                    Image(
                                        painter = painterResource(id = R.drawable.icon_camara),
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
                        ProblemOption("Duda de la autenticidad de la credencial")
                        ProblemOption("Duda de la identidad del alumno")

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
                                text = "Verificar con QR",
                                color = Color.Black
                            )
                        }

                        Button(
                            onClick = { navController.navigate("camara/$boleta/$idETS") },
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

                    Text(
                        text = "Realiza los reportes con:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )

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



                    // Mostrar la imagen si bitmap y precision no son null
                    if (bitmap != null && precision != null) {
                        ProblemOption("Fotos y precisión")
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally // Centrar horizontalmente
                        ) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Imagen verificada por IA",
                                modifier = Modifier
                                    .size(200.dp) // Ajusta el tamaño según necesites
                                    .clip(RoundedCornerShape(8.dp)), // Opcional: añadir esquinas redondeadas
                                contentScale = ContentScale.Fit // Ajustar la imagen dentro del tamaño
                            )
                            Text(
                                text = "Similitud: ${String.format("%.2f", precision * 100)}%",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                }

            }
        }


        // Observar el resultado del envío
        val envioExitoso by informacionAlumnoViewModel.envioExitoso.collectAsState(initial = null)
        val errorEnvio by informacionAlumnoViewModel.errorEnvio.collectAsState(initial = null)
        val cargando by informacionAlumnoViewModel.cargando.collectAsState(initial = false)
        val eliminandoReporteLoading by viewModel.eliminandoReporte.collectAsState()
        val context = LocalContext.current
        var showErrorDialog by remember { mutableStateOf(false) }
        var mensajeErrorEnvio by remember { mutableStateOf("") }

        LaunchedEffect(errorEnvio) {
            errorEnvio?.let { error ->
                mensajeErrorEnvio = when (error) {
                    is ConnectException -> "Error de conexión"
                    else -> "Ocurrió un fallo en el proceso"
                }
                showErrorDialog = true
            }
        }

        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Error") },
                text = { Text(mensajeErrorEnvio) },
                confirmButton = {
                    Button(onClick = {
                        showErrorDialog = false
                        informacionAlumnoViewModel.clearErrorEnvio()
                    }) {
                        Text("Aceptar")
                    }
                }
            )
        }

        LaunchedEffect(envioExitoso) {
            envioExitoso?.let { mensaje ->
                showSuccessDialog2 = true
                informacionAlumnoViewModel.clearEnvioExitoso() // Limpiar el estado
            }
        }

        if (eliminandoReporteLoading) {
            Dialog(
                onDismissRequest = { /* No permitir cerrar tocando fuera */ },
                DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFFCB5252)) // Puedes usar otro color
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Borrando reporte...", color = Color.Black)
                    }
                }
            }
        }

        // Diálogo de Cargando
        if (cargando) {
            Dialog(
                onDismissRequest = { /* No permitir cerrar tocando fuera */ },
                DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Blue)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Enviando datos...", color = Color.Black)
                    }
                }
            }
        }

        var isServidorTrabajandoAsistencia by remember { mutableStateOf(false) }
        var isServidorTrabajandoIncidencia by remember { mutableStateOf(false) }

        if (showDialog) {
            fun cerrarDialogoAsistencia() {
                if (!isServidorTrabajandoAsistencia) {
                    showDialog = false
                    showError = false
                    tipo = ""
                }
            }

            Dialog(onDismissRequest = { cerrarDialogoAsistencia() }) {
                Box(
                    modifier = Modifier
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            IconButton(onClick = { cerrarDialogoAsistencia() }) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar")
                            }
                        }

                        Text(
                            text = "Registrar asistencia",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Text(
                            text = "Se registrará la asistencia del alumno a las $horaActual. ¿Está de acuerdo?",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

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
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = "Desplegar"
                                    )
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

                        if ((tipo.contains("reconocimiento facial") && precision == null && bitmap == null) && showError) {
                            Text(
                                text = "Para hacer un reporte de reconocimiento facial necesita haber hecho el proceso de verificar con IA.",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        if (showError && !(tipo.contains("reconocimiento facial") && precision == null && bitmap == null)) {
                            Text(
                                text = "Debe completar todos los campos correctamente.",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    val esReconocimientoFacial = tipo.contains("reconocimiento facial")
                                    val verificacionIAHecha = precision != null && bitmap != null

                                    if (tipoValido && (!esReconocimientoFacial || verificacionIAHecha)) {
                                        isServidorTrabajandoAsistencia = true
                                        informacionAlumnoViewModel.enviarDatosAlServidor(
                                            razon = null,
                                            tipo,
                                            boleta,
                                            idETS,
                                            camaraViewModel.precision.value?.toString(),
                                            horaActual,
                                            camaraViewModel.imagenBitmap.value,
                                            context,
                                            onComplete = {
                                                isServidorTrabajandoAsistencia = false
                                                showError = false
                                                showSuccessDialog2 = true
                                            },
                                            onError = { error ->
                                                isServidorTrabajandoAsistencia = false
                                                showError = true
                                                showErrorDialog2 = true
                                                errorMessage = error
                                            }
                                        )
                                    } else {
                                        showError = true
                                    }
                                },
                                modifier = Modifier.padding(horizontal = 8.dp),
                                enabled = !isServidorTrabajandoAsistencia
                            ) {
                                Text("Sí")
                            }
                            Button(
                                onClick = { cerrarDialogoAsistencia() },
                                modifier = Modifier.padding(horizontal = 8.dp),
                                enabled = !isServidorTrabajandoAsistencia
                            ) {
                                Text("No")
                            }
                        }
                    }
                }
            }




            if (showSuccessDialog2) {
                Dialog(onDismissRequest = { showSuccessDialog2 = false; navController.navigate("listaAlumnos/$idETS") }) {
                    Box(
                        modifier = Modifier
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Asistencia registrada con éxito",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Button(
                                onClick = {
                                    showSuccessDialog2 = false
                                    navController.navigate("listaAlumnos/$idETS")
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Aceptar")
                            }
                        }
                    }
                }
            }

            // Diálogo de error
            if (showErrorDialog2) {
                Dialog(onDismissRequest = { showErrorDialog2 = false }) {
                    Box(
                        modifier = Modifier
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error al crear el reporte de asistencia",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Button(
                                onClick = { showErrorDialog2 = false },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Aceptar")
                            }
                        }
                    }
                }
            }
        }




        if (showDialog2) {
            fun cerrarDialogoIncidencia() {
                if (!isServidorTrabajandoIncidencia) {
                    showDialog2 = false
                    showError = false
                    razon = ""
                    tipo = ""
                }
            }

            Dialog(onDismissRequest = { cerrarDialogoIncidencia() }) {
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
                            IconButton(onClick = { cerrarDialogoIncidencia() }) {
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
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = "Desplegar"
                                    )
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

                        // Mensaje de error de reconocimiento facial
                        if ((tipo.contains("reconocimiento facial") && precision == null && bitmap == null) && showError) {
                            Text(
                                text = "Para hacer un reporte de reconocimiento facial necesita haber hecho el proceso de verificar con IA.",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        // Mensaje de error general si intenta dar "Sí" sin llenar los campos
                        if (showError && !(tipo.contains("reconocimiento facial") && precision == null && bitmap == null)) {
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
                                    val esReconocimientoFacial = tipo.contains("reconocimiento facial")
                                    val verificacionIAHecha = precision != null && bitmap != null

                                    if ((razonValida && tipoValido) && (!esReconocimientoFacial || verificacionIAHecha)) {
                                        isServidorTrabajandoIncidencia = true
                                        informacionAlumnoViewModel.enviarDatosAlServidor(
                                            razon,
                                            tipo,
                                            boleta,
                                            idETS,
                                            camaraViewModel.precision.value?.toString(),
                                            horaActual,
                                            camaraViewModel.imagenBitmap.value,
                                            context,
                                            onComplete = {
                                                isServidorTrabajandoIncidencia = false
                                                showError = false
                                                showSuccessDialog2 = true
                                            },
                                            onError = { error ->
                                                isServidorTrabajandoIncidencia = false
                                                showError = true
                                                showErrorDialog2 = true
                                                errorMessage = error
                                            }
                                        )
                                    } else {
                                        showError = true
                                    }
                                },
                                modifier = Modifier.padding(horizontal = 8.dp),
                                enabled = !isServidorTrabajandoIncidencia
                            ) {
                                Text("Sí")
                            }
                            Button(
                                onClick = { cerrarDialogoIncidencia() },
                                modifier = Modifier.padding(horizontal = 8.dp),
                                enabled = !isServidorTrabajandoIncidencia
                            ) {
                                Text("No")
                            }
                        }
                    }
                }
            }

// Diálogo de éxito para incidencia
            if (showSuccessDialog2) {
                Dialog(onDismissRequest = {
                    showSuccessDialog2 = false
                    navController.navigate("listaAlumnos/$idETS")
                }) {
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
                                onClick = {
                                    showSuccessDialog2 = false
                                    navController.navigate("listaAlumnos/$idETS")
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Aceptar")
                            }
                        }
                    }
                }
            }

            // Diálogo de error
            if (showErrorDialog2) {
                Dialog(onDismissRequest = { showErrorDialog2 = false }) {
                    Box(
                        modifier = Modifier
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error al crear el reporte de incidencia",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Button(
                                onClick = { showErrorDialog2 = false },
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
            imageVector = Icons.Default.Info,
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