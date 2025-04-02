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
import androidx.navigation.NavController
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.prueba3.Clases.Reemplazo
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.Views.ReemplazoViewModel
import com.example.prueba3.ui.theme.BlueBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitarRemplazo(
    navController: NavController,
    loginViewModel: LoginViewModel,
    reemplazoViewModel: ReemplazoViewModel
) {
    val userRole = loginViewModel.getUserRole()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Estados del formulario
    var selectedEts by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val loadingState by reemplazoViewModel.loadingState.collectAsState()

    ValidateSession(navController = navController) {
        Scaffold(
            topBar = {
                MenuTopBar(
                    false, true, loginViewModel,
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

                    Divider(
                        color = Color.White,
                        thickness = 1.dp,
                        modifier = Modifier.width(270.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Dropdown ETS
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        TextField(
                            value = selectedEts,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Selecciona ETS") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.LightGray,
                                unfocusedContainerColor = Color.LightGray,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            listOf("ETS 1", "ETS 2", "ETS 3").forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        selectedEts = item
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de motivo
                    TextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Motivo del reemplazo") },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(120.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.LightGray,
                            unfocusedContainerColor = Color.LightGray,
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
                            if (selectedEts.isEmpty() || reason.isEmpty()) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Complete todos los campos")
                                }
                            } else {
                                val nuevoReemplazo = Reemplazo(
                                    idETS = selectedEts.hashCode(), // Adaptar según tu lógica
                                    docenteRFC = "RFC_DEFAULT",     // Reemplazar con valor real
                                    motivo = reason,
                                    estatus = "Pendiente"
                                )
                                reemplazoViewModel.fetchReemplazoDocente(nuevoReemplazo)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Solicitud enviada con éxito")
                                    // Opcional: Limpiar formulario después de enviar
                                    selectedEts = ""
                                    reason = ""
                                }
                            }
                        },
                        enabled = !loadingState,
                        modifier = Modifier.width(200.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        if (loadingState) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("Enviar solicitud")
                        }
                    }
                }
            }
        }
    }
}