package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.TextFieldDefaults
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prueba3.Clases.Reemplazo
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.Views.ReemplazoViewModel
import com.example.prueba3.ui.theme.BlueBackground
import kotlinx.coroutines.launch
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitarReemplazo(
    navController: NavController,
    loginViewModel: LoginViewModel,
    reemplazoViewModel: ReemplazoViewModel = viewModel(),
    nombreETS: String? = null,
    idETS: Int? = null
) {
    val userRole = loginViewModel.getUserRole()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Estados del formulario
    var reason by remember { mutableStateOf("") }
    val loadingState by reemplazoViewModel.loadingState.collectAsState()
    val errorState by reemplazoViewModel.errorState.collectAsState()
    val reemplazoState by reemplazoViewModel.reemplazoState.collectAsState()

    // Verificar solicitud pendiente al iniciar
    LaunchedEffect(idETS) {
        idETS?.let { etsId ->
            reemplazoViewModel.verificarSolicitudPendiente(
                etsId = etsId,
                docenteRFC = loginViewModel.getUserName() ?: ""
            )
        }
    }

    // Mostrar errores
    errorState?.let { error ->
        LaunchedEffect(error) {
            if (!error.contains("Ya existe una solicitud pendiente")) {
                snackbarHostState.showSnackbar(error)
            }
//            reemplazoViewModel.errorState.value = null
        }
    }

    // Diálogo de éxito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = {
                Text(
                    "Solicitud Exitosa",
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )
            },
            text = { Text("Tu solicitud de reemplazo ha sido registrada correctamente") },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Aceptar", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White
        )
    }

    // Mostrar estado pendiente
    val showPendingState = reemplazoState != null ||
            (errorState?.contains("Ya existe una solicitud pendiente") == true)

    ValidateSession(navController = navController) {
        Scaffold(
            topBar = {
                MenuTopBar(
                    true, true, loginViewModel,
                    navController
                )
            },
            bottomBar = {
                MenuBottomBar(navController = navController, userRole)
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Título
                    Text(
                        text = "Solicitar reemplazo",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Mostrar estado pendiente
                    if (showPendingState) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0x33FFA000)
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color.Yellow, CircleShape)
                                        .padding(end = 12.dp)
                                )
                                Text(
                                    text = if (errorState?.contains("Ya existe una solicitud pendiente") == true) {
                                        "Estado: PENDIENTE (solicitud existente)"
                                    } else {
                                        "Estado: ${reemplazoState?.estatus ?: "PENDIENTE"}"
                                    },
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    Divider(
                        color = Color.White,
                        thickness = 1.dp,
                        modifier = Modifier.width(270.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Campo ETS
                    TextField(
                        value = nombreETS ?: "No se especificó ETS",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("ETS seleccionado") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.LightGray,
                            unfocusedContainerColor = Color.LightGray,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth(0.8f),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo motivo
                    TextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Motivo del reemplazo") },
                        enabled = !showPendingState,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(120.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = if (showPendingState) Color.Gray else Color.LightGray,
                            unfocusedContainerColor = if (showPendingState) Color.Gray else Color.LightGray,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botón de envío
                    Button(
                        onClick = {
                            if (idETS == null || reason.isEmpty()) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Complete todos los campos")
                                }
                            } else {
                                reemplazoViewModel.enviarSolicitudReemplazo(
                                    idETS = idETS,
                                    docenteRFC = loginViewModel.getUserName() ?: "",
                                    motivo = reason
                                )
                                showSuccessDialog = true
                                reason = ""
                            }
                        },
                        enabled = !loadingState && !showPendingState,
                        modifier = Modifier.width(200.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showPendingState) Color.Gray else Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        if (loadingState) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                when {
                                    showPendingState -> "Solicitud Pendiente"
                                    else -> "Enviar solicitud"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}