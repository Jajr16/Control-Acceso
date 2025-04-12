package Pantallas

import Pantallas.Reutilizables.ZoomableImage
import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import RetroFit.RetrofitInstance
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.prueba3.Clases.CredencialAlumnos
import com.example.prueba3.R
import com.example.prueba3.Views.AlumnosViewModel
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.ui.theme.BlueBackground
import kotlinx.coroutines.launch
import retrofit2.HttpException
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.layout.ContentScale
import com.example.prueba3.Clases.DatosWeb

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun CredencialDaeScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    url: String?,
    viewModel: AlumnosViewModel,
    boleta: String
) {

    val userRole = loginViewModel.getUserRole()

    Log.d("CredencialDaeScreen", "Dato: $userRole")

    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isZoomed by remember { mutableStateOf(false) }
    var alumnoInfo by remember { mutableStateOf<CredencialAlumnos?>(null) }
    val alumnosDetalle by viewModel.alumnosDetalle.collectAsState(initial = emptyList())
    val alumno = alumnosDetalle.firstOrNull()
    var showAsistenciaDialog by remember { mutableStateOf(false) }
    var showEscanearDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val fotoAlumno by viewModel.fotoAlumno.collectAsState()
    var showMensajeAsistencia by remember { mutableStateOf(false) }
    var fechaHoraRegistro by remember { mutableStateOf("") }
    val registroSuccess by viewModel.registroSuccess.collectAsState()

    val comparacionResultado by viewModel.comparacionResultado.collectAsState()
    val mostrarDialogoComparacion by viewModel.mostrarDialogoComparacion.collectAsState()
    var mostrarComparacionAlRegistrar by remember { mutableStateOf(false) }

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)
        offset += panChange
    }

    val loadingFotoAlumno = remember { mutableStateOf(false) }

//    LaunchedEffect(boleta) {
//        viewModel.fetchFotoAlumno(boleta)
//    }
    val idETSFinal by viewModel.idETSFlujo.collectAsState()
    val boletaFinal by viewModel.boletaFlujo.collectAsState()
    val alumnoEspecifico by viewModel.alumnoEspecifico.collectAsState()


    LaunchedEffect(registroSuccess) {
        if (registroSuccess) {
            showMensajeAsistencia = true
            delay(3000)
            showMensajeAsistencia = false
        }
    }

    LaunchedEffect(alumnoInfo) {
        alumnoInfo?.let { info ->
            val datosWeb = DatosWeb(
                boleta = info.boleta,
                curp = info.curp,
                nombre = "${info.nombre} ${info.apellidoP} ${info.apellidoM}",
                carrera = info.carrera,
                escuela = info.unidadAcademica
            )
            viewModel.compararDatos(info.boleta, datosWeb)
        }
    }

    ValidateSession(navController = navController) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                MenuTopBar(
                    false, false, loginViewModel,
                    navController,
                )
            },
            bottomBar = { MenuBottomBar(navController = navController, loginViewModel.getUserRole()) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(
                        top = 90.dp,
                        bottom = padding.calculateBottomPadding()
                    )
                    .verticalScroll(rememberScrollState())
            ) {
                if (isZoomed) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.8f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    isZoomed = false
                                    scale = 1f
                                    offset = Offset.Zero
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            imageBitmap?.let { bitmap ->
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Credencial ampliada",
                                    modifier = Modifier
                                        .graphicsLayer(
                                            scaleX = scale,
                                            scaleY = scale,
                                            translationX = offset.x,
                                            translationY = offset.y
                                        )
                                        .transformable(state = transformableState)
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                if (mostrarComparacionAlRegistrar && comparacionResultado?.coinciden == false) {
                    AlertDialog(
                        onDismissRequest = {
                            mostrarComparacionAlRegistrar = false
                            viewModel.cerrarDialogoComparacion()
                        },
                        title = {
                            Text(
                                "Los datos no coinciden",
                                color = Color.Red
                            )
                        },
                        text = {
                            Column {
                                comparacionResultado?.errores?.forEach { error ->
                                    Text(text = "• $error", modifier = Modifier.padding(4.dp))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No se puede registrar la asistencia con datos inconsistentes")
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    mostrarComparacionAlRegistrar = false
                                    viewModel.cerrarDialogoComparacion()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Aceptar")
                            }
                        }
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Credencial",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Divider(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .width(270.dp),
                        thickness = 1.dp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))

                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.White
                            )
                        }

                        imageBitmap != null -> {
                            Image(
                                bitmap = imageBitmap!!.asImageBitmap(),
                                contentDescription = "Credencial",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp)
                                    .clickable { isZoomed = true }
                            )
                        }

                        errorMessage != null -> {
                            Text(
                                text = errorMessage ?: "Error desconocido",
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        else -> {
                            Text(
                                text = "No hay imagen disponible",
                                color = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }

                if (alumnoInfo != null && idETSFinal == null && boletaFinal == null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (fotoAlumno != null) {
                                    val bitmap = BitmapFactory.decodeByteArray(fotoAlumno, 0, fotoAlumno!!.size)
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "Foto de perfil",
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.placeholder_image),
                                        contentDescription = "Foto de perfil",
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .padding(start = 16.dp)
                                        .weight(1f)
                                ) {
                                    Text(
                                        text = "Alumno:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = "${alumnoInfo?.nombre} ${alumnoInfo?.apellidoP} ${alumnoInfo?.apellidoM}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )

                                    Text(
                                        text = "Programa académico:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = alumnoInfo?.carrera ?: "No disponible",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Boleta:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = alumnoInfo?.boleta ?: "No disponible",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "CURP:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = alumnoInfo?.curp ?: "No disponible",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Unidad Académica:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = alumnoInfo?.unidadAcademica ?: "No disponible",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
//                        if (userRole == "Personal Academico") {
//                            Button(
//                                onClick = { navController.navigate("infoA/${idETSFinal}/${boletaFinal}") },
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(horizontal = 8.dp),
//                                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
//                            ) {
//                                Text(
//                                    text = "Regresar a la creación del reporte",
//                                    color = Color.Black,
//                                    textAlign = TextAlign.Center
//                                )
//                            }
//                        } else {
                            Button(
                                onClick = {
                                    alumnoInfo?.let { info ->
                                        val datosWeb = DatosWeb(
                                            boleta = info.boleta,
                                            curp = info.curp,
                                            nombre = "${info.nombre} ${info.apellidoP} ${info.apellidoM}",
                                            carrera = info.carrera,
                                            escuela = info.unidadAcademica
                                        )
                                        viewModel.compararDatos(info.boleta, datosWeb)

                                        if (comparacionResultado?.coinciden == true) {
                                            showAsistenciaDialog = true
                                            fechaHoraRegistro = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
                                        } else {
                                            mostrarComparacionAlRegistrar = true
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f)
                                    .padding(horizontal = 8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                            ) {
                                Text(
                                    text = "Registrar asistencia",
                                    color = Color.Black
                                )
                            }

//                    }

                    if (showAsistenciaDialog) {
                        if (alumno?.nombreETS.isNullOrEmpty()) {
                            AlertDialog(
                                onDismissRequest = { showAsistenciaDialog = false },
                                title = { Text("Error", fontWeight = FontWeight.Bold) },
                                text = {
                                    Text("El alumno no cuenta con ETS inscritos ó su ETS está programada en otra fecha.")
                                },
                                confirmButton = {
                                    Button(onClick = { showAsistenciaDialog = false }) {
                                        Text("Aceptar")
                                    }
                                }
                            )
                        } else {
                            AlertDialog(
                                onDismissRequest = { showAsistenciaDialog = false },
                                title = {
                                    Text(
                                        "Confirmar asistencia",
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                text = {
                                    Column {
                                        Text(
                                            text = "El alumno ${alumno?.nombreAlumno ?: ""} ${alumno?.apellidoPAlumno ?: ""} con número de boleta ${alumno?.boleta ?: ""} está inscrito en:",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "${alumno?.nombreETS ?: ""}",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                        Text(
                                            text = "Fecha y hora de registro:",
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                        Text(
                                            text = fechaHoraRegistro,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                        Text(
                                            text = "¿Deseas registrar su asistencia?",
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(top = 16.dp)
                                        )
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            showAsistenciaDialog = false
                                            viewModel.registrarAsistencia(boleta)
                                        }
                                    ) {
                                        Text("Aceptar")
                                    }
                                },
                                dismissButton = {
                                    Button(onClick = { showAsistenciaDialog = false }) {
                                        Text("Cancelar")
                                    }
                                }
                            )
                        }
                    }

                    if (showMensajeAsistencia) {
                        AlertDialog(
                            onDismissRequest = { showMensajeAsistencia = false },
                            title = {
                                Text(
                                    "Asistencia registrada",
                                    color = Color.Green,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            text = {
                                Column {
                                    Text(
                                        text = "La asistencia ha sido registrada correctamente",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = fechaHoraRegistro,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            },
                            confirmButton = {
                                Button(onClick = { showMensajeAsistencia = false }) {
                                    Text("Aceptar")
                                }
                            }
                        )
                    }
                }
            } else if (boletaFinal != null && idETSFinal != null){

                    LaunchedEffect(Unit) {
                        viewModel.fetchAlumnoEspecifico(boletaFinal!!)
                    }

                    androidx.compose.material.Card(
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
                                        androidx.compose.material.Text(
                                            text = "Alumno:",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        androidx.compose.material.Text(
                                            text = "${alumno.apellidoP.trim()} ${alumno.apellidoM.trim()} ${alumno.nombre.trim()}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(bottom = 12.dp)
                                        )

                                        // Programa académico
                                        androidx.compose.material.Text(
                                            text = "Programa académico:",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        androidx.compose.material.Text(
                                            text = alumno.unidadAcademica.trim(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } ?: run {
                                    androidx.compose.material.Text(
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
                                        androidx.compose.material.Text(
                                            text = "Boleta:",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Gray
                                        )
                                        androidx.compose.material.Text(
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
                                        androidx.compose.material.Text(
                                            text = "CURP:",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Gray
                                        )
                                        androidx.compose.material.Text(
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


                    Button(
                        onClick = { navController.navigate("infoA/${idETSFinal}/${boletaFinal}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(
                            text = "Regresar a la creación del reporte",
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }






                }
        }
    }

    LaunchedEffect(url) {
        if (url != null) {
            scope.launch {
                try {
                    val response = RetrofitInstance.alumnosDetalle.getCredencial(url)

                    if (response.isSuccessful && response.body() != null) {
                        val credencialResponse = response.body()!!

                        // Decodificar la imagen base64
                        val imageBytes = Base64.decode(credencialResponse.imagen)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        imageBitmap = bitmap

                        // Guardar la información del alumno
                        alumnoInfo = credencialResponse.credenciales.firstOrNull()

                        if (alumnoInfo != null){



                            viewModel.limpiarInfoFlujo()



                        }

                        val boletausable = alumnoInfo?.boleta

                        if (idETSFinal == null && boletaFinal == null){
                            boletausable?.let { boletaNonNull ->
                                loadingFotoAlumno.value = true
                                viewModel.fetchFotoAlumno(boletaNonNull) { loadingFotoAlumno.value = false }
                            }
                        }else{

                            boletaFinal?.let { boletaNonNull ->
                                loadingFotoAlumno.value = true
                                viewModel.fetchFotoAlumno(boletaNonNull) { loadingFotoAlumno.value = false }
                            }

                        }



                        isLoading = false
                    } else {
                        errorMessage = "Error al obtener la credencial"
                        isLoading = false
                    }
                } catch (e: HttpException) {
                    errorMessage = "Error en la solicitud: ${e.message()}"
                    isLoading = false
                } catch (e: Exception) {
                    errorMessage = "Error general: ${e.message}"
                    isLoading = false
                }
            }
        } else {
            errorMessage = "URL no válida"
            isLoading = false
        }
    }
    }
}