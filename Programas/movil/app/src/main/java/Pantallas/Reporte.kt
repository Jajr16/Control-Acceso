package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import android.content.Context
import android.graphics.BitmapFactory
import android.text.Layout
import android.util.Log
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.example.prueba3.Views.AlumnosViewModel
import com.example.prueba3.Views.EtsInfoViewModel
import com.example.prueba3.Views.PersonaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

@Composable
fun Reporte(
    navController: NavController,
    idETS: String,
    boleta: String,
    loginViewModel: LoginViewModel,
    viewModel: AlumnosViewModel,
    aceptadoInicial: Int,
    viewModel2: EtsInfoViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    viewModel3: PersonaViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    ValidateSession(navController = navController) {
        val userRole = loginViewModel.getUserRole()
        val reporteList by viewModel.reporte.collectAsState()
        val loadingReporte by viewModel.loadingState.collectAsState()
        val ingresoResultado by viewModel.ingresoResultado.collectAsState()
        val etsDetail by viewModel2.etsDetailState.collectAsState()

        val loadingImagenRedNeuronal = remember { mutableStateOf(false) }
        val loadingFotoAlumno = remember { mutableStateOf(false) }

        LaunchedEffect(idETS, boleta) {
            viewModel.fetchReporte(idETS.toInt(), boleta)
            viewModel.verificarIngresoSalon(idETS.toInt(), boleta)

            loadingFotoAlumno.value = true
            viewModel.fetchFotoAlumno(boleta) { loadingFotoAlumno.value = false }

            loadingImagenRedNeuronal.value = true
            viewModel.fetchImagenReporte(idETS.toInt(), boleta) { loadingImagenRedNeuronal.value = false }

            viewModel2.fetchEtsDetail(idETS.toInt())
        }

        val fotoAlumno by viewModel.fotoAlumno.collectAsState()
        val imagenBytes by viewModel.imagenBytes.collectAsState()

        val scrollState = rememberScrollState()

        val horaETS = etsDetail?.ets?.hora ?: ""
        val fechaETS = etsDetail?.ets?.fecha ?: ""
        val isEtsOver = remember(horaETS, fechaETS) {
            derivedStateOf {
                if (horaETS.isEmpty() || fechaETS.isEmpty()) {
                    false
                } else {
                    val currentTime = Calendar.getInstance(TimeZone.getTimeZone("America/Mexico_City"))
                    val etsCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Mexico_City")).apply {
                        time = currentTime.time
                        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        try {
                            timeFormatter.parse(horaETS)?.let { parsedTime ->
                                set(Calendar.HOUR_OF_DAY, parsedTime.hours)
                                set(Calendar.MINUTE, parsedTime.minutes)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                        } catch (e: Exception) {
                            Log.e("ReporteScreen", "Error parsing horaETS: $horaETS", e)
                        }
                        try {
                            dateFormatter.parse(fechaETS)?.let { parsedDate ->
                                set(Calendar.YEAR, parsedDate.year + 1900)
                                set(Calendar.MONTH, parsedDate.month)
                                set(Calendar.DAY_OF_MONTH, parsedDate.date)
                            }
                        } catch (e: Exception) {
                            Log.e("ReporteScreen", "Error parsing fechaETS: $fechaETS", e)
                        }
                    }
                    val etsEndTimeMillis = etsCalendar.timeInMillis + TimeUnit.HOURS.toMillis(2) + TimeUnit.MINUTES.toMillis(1)
                    currentTime.timeInMillis > etsEndTimeMillis
                }
            }
        }.value

        Scaffold(
            topBar = { MenuTopBar(true, true, loginViewModel, navController) },
            bottomBar = { MenuBottomBar(navController, userRole) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(padding)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (loadingReporte) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    if (ingresoResultado == "existe" && reporteList.isNotEmpty()) {
                        val reporte = reporteList.firstOrNull()

                        Text(
                            text = "Revisar información de asistencia",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp, bottom = 8.dp),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        // Sección de la foto de la credencial (arriba)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                if (loadingFotoAlumno.value) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(150.dp))
                                } else {
                                    if (fotoAlumno?.isNotEmpty() == true) {
                                        val bitmap = BitmapFactory.decodeByteArray(fotoAlumno, 0, fotoAlumno!!.size)
                                        Image(
                                            bitmap = bitmap.asImageBitmap(),
                                            contentDescription = "Imagen de la credencial",
                                            modifier = Modifier
                                                .size(150.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(150.dp)
                                                .background(Color.LightGray)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("Sin Imagen", color = Color.Gray)
                                        }
                                    }
                                }
                                Text(
                                    "Imagen de la credencial",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                            }
                        }

                        reporte?.let { reporteItem ->
                            val username = reporteItem.nombreDocente

                            LaunchedEffect(username) {
                                viewModel3.obtenerDatos(username!!)
                            }

                            val datos by viewModel3.datosPersona.collectAsState()
                            val nombreUsuario = datos.firstOrNull()?.nombre ?: ""
                            val appellidoP = datos.firstOrNull()?.apellidoP ?: ""
                            val appellidoM = datos.firstOrNull()?.apellidoM ?: ""
                            val docente = "${appellidoP.trim()} ${appellidoM.trim()} ${nombreUsuario.trim()}".trim()

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    "Información del Alumno:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                InfoRow("Boleta", boleta)
                                InfoRow(
                                    "Nombre completo",
                                    "${reporteItem.nombre} ${reporteItem.apellidoP} ${reporteItem.apellidoM}"
                                )
                                InfoRow("CURP", reporteItem.curp ?: "N/A")
                                InfoRow("Carrera", reporteItem.carrera ?: "N/A")
                                InfoRow("Unidad académica", reporteItem.escuela ?: "N/A")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    "Información del ETS:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                InfoRow("Periodo del ETS", reporteItem.periodo ?: "N/A")
                                InfoRow("Turno del ETS", reporteItem.turno ?: "N/A")
                                InfoRow("Materia del ETS", reporteItem.materia ?: "N/A")
                                val tipoText = if (reporteItem.tipo == "O") "Ordinario" else if (reporteItem.tipo == "E") "Extraordinario" else reporteItem.tipo ?: "N/A"
                                InfoRow("Tipo de ETS", tipoText)
                                InfoRow("Fecha del ingreso al ETS", reporteItem.fechaIngreso ?: "N/A")
                                InfoRow("Hora del ingreso al ETS", reporteItem.horaIngreso ?: "N/A")
                                InfoRow("Nombre del docente", docente ?: "N/A")
                                InfoRow("Razón del reporte", reporteItem.tipoEstado ?: "N/A")
                                if (reporteItem.motivo == "No Motivo") {
                                    InfoRow("Motivo del rechazo", "Sin motivo")
                                } else {
                                    InfoRow("Motivo del rechazo", reporteItem.motivo ?: "N/A")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (reporteItem.presicion != null && reporteItem.tipoEstado?.contains("Verificado con el reconocimiento facial.") == true) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.1f))
                                        .padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "Información de la IA:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Imagen de la red neuronal
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            if (loadingImagenRedNeuronal.value) {
                                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(80.dp))
                                            } else {
                                                if (imagenBytes != null && imagenBytes!!.isNotEmpty()) {
                                                    val bitmapRedNeuronal =
                                                        BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes!!.size)
                                                    Image(
                                                        bitmap = bitmapRedNeuronal.asImageBitmap(),
                                                        contentDescription = "Imagen IA",
                                                        modifier = Modifier
                                                            .size(80.dp)
                                                            .clip(RoundedCornerShape(8.dp)),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                } else {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(80.dp)
                                                            .background(Color.LightGray)
                                                            .clip(RoundedCornerShape(8.dp)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text("Sin IA", color = Color.Gray, textAlign = TextAlign.Center)
                                                    }
                                                }
                                            }
                                            Text(
                                                "Imagen IA",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White
                                            )
                                        }

                                        // Espacio entre las imágenes
                                        Spacer(modifier = Modifier.width(16.dp))

                                        // Imagen de la credencial (reducida para la sección de IA)
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            if (loadingFotoAlumno.value) {
                                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(80.dp))
                                            } else {
                                                if (fotoAlumno != null && fotoAlumno!!.isNotEmpty()) {
                                                    val bitmap = BitmapFactory.decodeByteArray(fotoAlumno, 0, fotoAlumno!!.size)
                                                    Image(
                                                        bitmap = bitmap.asImageBitmap(),
                                                        contentDescription = "Credencial",
                                                        modifier = Modifier
                                                            .size(80.dp)
                                                            .clip(RoundedCornerShape(8.dp)),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                } else {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(80.dp)
                                                            .background(Color.LightGray)
                                                            .clip(RoundedCornerShape(8.dp)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text("Sin Credencial", color = Color.Gray, textAlign = TextAlign.Center)
                                                    }
                                                }
                                            }
                                            Text(
                                                "Credencial",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White
                                            )
                                        }
                                    }
                                    if (reporteItem.presicion != null && reporteItem.presicion != "0" && reporteItem.presicion != "-1") {
                                        val numero = reporteItem.presicion.toFloat()
                                        val porcentaje = numero * 100
                                        InfoRow("Precisión del reconocimiento facial", porcentaje.toString() + "%")
                                    } else if (reporteItem.presicion != null && reporteItem.presicion == "0") {
                                        InfoRow("Precisión del reconocimiento facial", "Menor que el 70% porciento")
                                    }
                                }
                            }
                        }
                    } else if (ingresoResultado == "no existe") {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (isEtsOver) "El alumno no se presentó al ETS." else "No se ha creado reporte para este alumno.",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
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