package Pantallas


import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prueba3.R
import com.example.prueba3.Views.AlumnosViewModel
import com.example.prueba3.Views.EtsInfoViewModel
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.ui.theme.BlueBackground
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.log

@Composable
fun ListaAlumnosScreen(
    navController: NavController,
    idETS: String,
    viewModel: AlumnosViewModel,
    loginViewModel: LoginViewModel,
    viewModel2: EtsInfoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    ValidateSession(navController = navController) {
        val alumnosList by viewModel.alumnosList.collectAsState()
        val isLoading by remember { viewModel.loadingState }.collectAsState()
        val etsDetail by remember { viewModel2.etsDetailState }.collectAsState()
        val docenteRfc by viewModel2.rfcDocenteState.collectAsState() // Observa el RFC del docente

        val horaETS = etsDetail?.ets?.hora ?: ""
        val fechaETS = etsDetail?.ets?.fecha ?: ""

        val sharedPreferences = navController.context
            .getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""

        val scrollState = rememberScrollState()

        LaunchedEffect(idETS) {
            viewModel.fetchAlumno(idETS)

            val idETS2 = idETS.toInt()
            viewModel2.fetchRfcDocente(idETS2)
            viewModel2.fetchEtsDetail(idETS2)
        }

        val currentTime = remember { Calendar.getInstance(TimeZone.getTimeZone("America/Mexico_City")) }
        val etsCalendar = remember(horaETS, fechaETS) {
            Calendar.getInstance(TimeZone.getTimeZone("America/Mexico_City")).apply {
                time = currentTime.time
                if (horaETS.isNotEmpty()) {
                    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                    try {
                        timeFormatter.parse(horaETS)?.let { parsedTime ->
                            set(Calendar.HOUR_OF_DAY, parsedTime.hours)
                            set(Calendar.MINUTE, parsedTime.minutes)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                    } catch (e: Exception) {
                        Log.e("ListaAlumnosScreen", "Error parsing horaETS: $horaETS", e)
                    }
                }
                if (fechaETS.isNotEmpty()) {
                    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    try {
                        dateFormatter.parse(fechaETS)?.let { parsedDate ->
                            set(Calendar.YEAR, parsedDate.year + 1900)
                            set(Calendar.MONTH, parsedDate.month)
                            set(Calendar.DAY_OF_MONTH, parsedDate.date)
                        }
                    } catch (e: Exception) {
                        Log.e("ListaAlumnosScreen", "Error parsing fechaETS: $fechaETS", e)
                    }
                }
            }
        }

        val isReportExpired = remember(currentTime.timeInMillis, etsCalendar.timeInMillis) {
            derivedStateOf {
                if (horaETS.isEmpty() || fechaETS.isEmpty() || etsCalendar.timeInMillis == 0L) {
                    false
                } else {
                    val etsEndTimeMillis = etsCalendar.timeInMillis + TimeUnit.HOURS.toMillis(2) + TimeUnit.MINUTES.toMillis(1)
                    currentTime.timeInMillis > etsEndTimeMillis
                }
            }
        }.value

        val isButtonEnabled = remember(currentTime.timeInMillis, etsCalendar.timeInMillis) {
            derivedStateOf {
                if (horaETS.isEmpty() || fechaETS.isEmpty() || etsCalendar.timeInMillis == 0L) {
                    false
                } else {
                    val etsMinutes = TimeUnit.HOURS.toMinutes(etsCalendar.get(Calendar.HOUR_OF_DAY).toLong()) + etsCalendar.get(Calendar.MINUTE)
                    val nowMinutes = TimeUnit.HOURS.toMinutes(currentTime.get(Calendar.HOUR_OF_DAY).toLong()) + currentTime.get(Calendar.MINUTE)

                    val startTimeRange = etsMinutes - 10
                    val endTimeRange = etsMinutes + 120

                    currentTime.get(Calendar.YEAR) == etsCalendar.get(Calendar.YEAR) &&
                            currentTime.get(Calendar.MONTH) == etsCalendar.get(Calendar.MONTH) &&
                            currentTime.get(Calendar.DAY_OF_MONTH) == etsCalendar.get(Calendar.DAY_OF_MONTH) &&
                            nowMinutes in startTimeRange..endTimeRange
                }
            }
        }.value

        val showReportExpiredDialog = remember { mutableStateOf(false) }
        val showReportNotYetDialog = remember { mutableStateOf(false) }
        val showNotApplicatorDialog = remember { mutableStateOf(false) } // Nuevo diálogo
        val timeLeftMessage = remember { mutableStateOf("") }

        val userRole = loginViewModel.getUserRole()
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = { MenuTopBar(true, true, loginViewModel, navController) },
            bottomBar = { MenuBottomBar(navController = navController, userRole) }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(padding)

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BlueBackground)
                        .verticalScroll(scrollState)
                        .padding(top = 30.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Text(
                        text = if (etsDetail != null) {
                            val ets3 = etsDetail!!.ets
                            "ETS de ${ets3.unidadAprendizaje} ${ets3.idPeriodo} "
                        } else {
                            "Detalles del ETS"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Lista de alumnos inscritos",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFD9D9D9),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Divider(color = Color(0xFFFFFFFF), thickness = 5.dp, modifier = Modifier.padding(vertical = 8.dp))
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else if (alumnosList.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 150.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(alumnosList) { alumno ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Botón con boleta y nombre (Navega a InfoA SOLO SI es el aplicador y dentro del periodo)
                                    Button(
                                        onClick = {
                                            if (username == docenteRfc) {
                                                if (isButtonEnabled) {
                                                    navController.navigate("InfoA/$idETS/${alumno.boleta}")
                                                } else if (isReportExpired) {
                                                    showReportExpiredDialog.value = true
                                                } else {
                                                    val etsStartTimeMinus10Millis = etsCalendar.timeInMillis - TimeUnit.MINUTES.toMillis(10)
                                                    val timeLeft = etsStartTimeMinus10Millis - currentTime.timeInMillis
                                                    val days = TimeUnit.MILLISECONDS.toDays(timeLeft)
                                                    val hours = TimeUnit.MILLISECONDS.toHours(timeLeft) % 24
                                                    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft) % 60
                                                    timeLeftMessage.value = "$days días, $hours horas y $minutes minutos"
                                                    showReportNotYetDialog.value = true
                                                }
                                            } else {
                                                showNotApplicatorDialog.value = true
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(0.8f),
                                        colors = ButtonDefaults.buttonColors(Color(0xFFFFFFFF))
                                    ) {
                                        Column {
                                            Text(text = "Boleta: ${alumno.boleta}", fontSize = 14.sp, color = Color.Black)
                                            Text(
                                                text = "Nombre: ${alumno.nombreA} ${alumno.apellidoP} ${alumno.apellidoM}",
                                                fontSize = 14.sp,
                                                color = Color.Black
                                            )
                                        }
                                    }

                                    // Botón redondo con estado (Navega a Reporte - SIEMPRE visible)
                                    IconButton(
                                        onClick = {
                                            navController.navigate("Reporte/$idETS/${alumno.boleta}/${alumno.aceptado}")
                                        },
                                        modifier = Modifier
                                            .size(75.dp)
                                            .padding(8.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(
                                                id = if (alumno.aceptado != 0) {
                                                    when (alumno.aceptado) {
                                                        -1 -> R.drawable.icono4
                                                        1 -> R.drawable.icono1
                                                        2 -> R.drawable.icono2
                                                        3 -> R.drawable.icono3
                                                        4 -> R.drawable.icono5
                                                        5 -> R.drawable.icono6
                                                        6 -> R.drawable.icono7
                                                        else -> R.drawable.icono1
                                                    }
                                                } else {
                                                    if (isReportExpired) {
                                                        R.drawable.icono4
                                                    } else {
                                                        R.drawable.icono8
                                                    }
                                                }
                                            ),
                                            contentDescription = "Estado",
                                            tint = Color.Unspecified,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No hay alumnos inscritos al ETS.",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        // Dialog for expired report period
        if (showReportExpiredDialog.value) {
            AlertDialog(
                onDismissRequest = { showReportExpiredDialog.value = false },
                title = { Text("Periodo concluido") },
                text = { Text("El periodo para registrar los reportes ha concluido.") },
                confirmButton = {
                    Button(onClick = { showReportExpiredDialog.value = false }) {
                        Text("Aceptar")
                    }
                }
            )
        }

        if (showReportNotYetDialog.value) {
            AlertDialog(
                onDismissRequest = { showReportNotYetDialog.value = false },
                title = { Text("Periodo no iniciado") },
                text = { Text("Aún no es periodo para crear los reportes. Faltan ${timeLeftMessage.value}.") },
                confirmButton = {
                    Button(onClick = { showReportNotYetDialog.value = false }) {
                        Text("Aceptar")
                    }
                }
            )
        }

        // Dialog for not the applicator
        if (showNotApplicatorDialog.value) {
            AlertDialog(
                onDismissRequest = { showNotApplicatorDialog.value = false },
                title = { Text("Acceso denegado") },
                text = { Text("Usted no está autorizado para crear el reporte de este alumno.") },
                confirmButton = {
                    Button(onClick = { showNotApplicatorDialog.value = false }) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}