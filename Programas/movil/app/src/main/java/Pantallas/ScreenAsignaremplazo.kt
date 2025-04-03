package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.ui.theme.BlueBackground
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenAsignaremplazo(
    navController: NavController,
    loginViewModel: LoginViewModel
) {
    val userRole = loginViewModel.getUserRole()

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
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(padding)
            ) {
                // Contenido principal desplazable
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(bottom = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally  // Centra los hijos horizontalmente
                ) {
                    // Encabezado
                    Text(
                        text = "Asignar remplazo",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(top = 20.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Divider(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .width(270.dp),
                        thickness = 1.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    var selectedEts by remember { mutableStateOf("ETS fijo seleccionado") }
                    var selectedDocente by remember { mutableStateOf("Docente asignado") }
                    var expandedEts by remember { mutableStateOf(false) }
                    var expandedDocente by remember { mutableStateOf(false) }

// Dropdown para ETS
                    ExposedDropdownMenuBox(
                        expanded = expandedEts,
                        onExpandedChange = { expandedEts = it },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        TextField(
                            value = selectedEts,
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEts)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.LightGray,
                                unfocusedContainerColor = Color.LightGray,
                                disabledContainerColor = Color.LightGray,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedEts,
                            onDismissRequest = { expandedEts = false }
                        ) {
                            listOf("ETS 1", "ETS 2", "ETS 3").forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item) },
                                    onClick = {
                                        selectedEts = item
                                        expandedEts = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedDocente,
                        onExpandedChange = { expandedDocente = it },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        TextField(
                            value = selectedDocente,
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDocente)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.LightGray,
                                unfocusedContainerColor = Color.LightGray,
                                disabledContainerColor = Color.LightGray,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedDocente,
                            onDismissRequest = { expandedDocente = false }
                        ) {
                            listOf("Ulises Vélez", "Saúl de la O", "Eleonor Montes").forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item) },
                                    onClick = {
                                        selectedDocente = item
                                        expandedDocente = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { /* Lógica de envío */ },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(text = "Enviar", color = Color.Black)
                    }
                }
            }
        }
    }
}