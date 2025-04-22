package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.prueba3.Clases.Docente
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.Views.ReemplazoViewModel
import com.example.prueba3.ui.theme.BlueBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenAsignaremplazo(
    navController: NavController,
    loginViewModel: LoginViewModel,
    reemplazoViewModel: ReemplazoViewModel = viewModel(),
    idETS: Int? = null,
    docenteRFC: String? = null
) {
    val userRole = loginViewModel.getUserRole()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Estados del formulario
    var docenteSeleccionado by remember { mutableStateOf<Docente?>(null) }
    var comentario by remember { mutableStateOf("") }
    var showDialogAprobar by remember { mutableStateOf(false) }
    var showDialogRechazar by remember { mutableStateOf(false) }
    var motivoRechazo by remember { mutableStateOf("") }
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Cargar docentes al iniciar
    LaunchedEffect(Unit) {
        reemplazoViewModel.cargarDocentesDisponibles()
    }

    // Estados del ViewModel
    val docentesDisponibles by reemplazoViewModel.docentesDisponibles.collectAsState()
    val loadingState by reemplazoViewModel.loadingState.collectAsState()
    val errorState by reemplazoViewModel.errorState.collectAsState()

    // Manejar errores
    errorState?.let { error ->
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(error)
//            reemplazoViewModel.errorState.value = null
        }
    }

    ValidateSession(navController = navController) {
        Scaffold(
            topBar = { MenuTopBar(true, true, loginViewModel, navController) },
            bottomBar = { MenuBottomBar(navController = navController, userRole) },
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
                        text = "Asignar Reemplazo",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Divider(
                        color = Color.White,
                        thickness = 1.dp,
                        modifier = Modifier.width(270.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Información de la solicitud
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0x33FFA000))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Solicitud de Reemplazo",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "ETS: ${idETS ?: "N/A"}", color = Color.White)
                            Text(text = "Docente: ${docenteRFC ?: "N/A"}", color = Color.White)
                            Text(text = "Estado: PENDIENTE", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Selector de docentes con autocompletado
                    Text(
                        text = "Asignar docente de reemplazo:",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (loadingState) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = docenteSeleccionado?.nombreDocente ?: query,
                                onValueChange = {
                                    query = it
                                    if (it.isEmpty()) {
                                        docenteSeleccionado = null
                                    }
                                },
                                label = { Text("Buscar docente...") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.LightGray,
                                    unfocusedContainerColor = Color.LightGray,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                docentesDisponibles
                                    .filter { it.nombreDocente.contains(query, ignoreCase = true) }
                                    .forEach { docente ->
                                        DropdownMenuItem(
                                            text = { Text(docente.nombreDocente) },
                                            onClick = {
                                                docenteSeleccionado = docente
                                                query = docente.nombreDocente
                                                expanded = false
                                            }
                                        )
                                    }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = comentario,
                        onValueChange = { comentario = it },
                        label = { Text("Comentario (opcional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.LightGray,
                            unfocusedContainerColor = Color.LightGray,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botones de acción
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { showDialogRechazar = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Rechazar")
                        }

                        Button(
                            onClick = { showDialogAprobar = true },
                            enabled = docenteSeleccionado != null,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                        ) {
                            Text("Aprobar")
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para aprobar
    if (showDialogAprobar) {
        AlertDialog(
            onDismissRequest = { showDialogAprobar = false },
            title = { Text("Confirmar aprobación") },
            text = {
                Text("¿Estás seguro de aprobar esta solicitud y asignar a ${docenteSeleccionado?.nombreDocente} como reemplazo?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialogAprobar = false
                        coroutineScope.launch {
                            try {
                                if (idETS != null && docenteRFC != null && docenteSeleccionado != null) {
                                    reemplazoViewModel.aprobarReemplazo(
                                        idETS = idETS,
                                        docenteRFC = docenteRFC,
                                        docenteReemplazo = docenteSeleccionado!!.rfcDocente
                                    )
                                    navController.popBackStack()
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error al aprobar: ${e.message}")
                            }
                        }
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialogAprobar = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo para rechazar
    if (showDialogRechazar) {
        AlertDialog(
            onDismissRequest = { showDialogRechazar = false },
            title = { Text("Motivo del rechazo") },
            text = {
                Column {
                    Text("Por favor, indica el motivo del rechazo:")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = motivoRechazo,
                        onValueChange = { motivoRechazo = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialogRechazar = false
                        coroutineScope.launch {
                            try {
                                if (idETS != null && docenteRFC != null) {
                                    reemplazoViewModel.rechazarReemplazo(
                                        idETS = idETS,
                                        docenteRFC = docenteRFC,
                                        motivo = motivoRechazo
                                    )
                                    navController.popBackStack()
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error al rechazar: ${e.message}")
                            }
                        }
                    },
                    enabled = motivoRechazo.isNotEmpty()
                ) {
                    Text("Confirmar rechazo")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialogRechazar = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

