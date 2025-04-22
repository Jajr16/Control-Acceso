package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import androidx.compose.foundation.layout.*
import Pantallas.components.ValidateSession
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.prueba3.Clases.Reemplazo
import com.example.prueba3.Views.EtsViewModel
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.Views.ReemplazoViewModel
import com.example.prueba3.ui.theme.BlueBackground

@Composable
fun ListadoSolicitudesReemplazo(
    navController: NavController,
    loginViewModel: LoginViewModel,
    reemplazoViewModel: ReemplazoViewModel = viewModel()
) {
    val solicitudes by reemplazoViewModel.solicitudesPendientes.collectAsState()
    val loading by reemplazoViewModel.loadingState.collectAsState()
    val errorState by reemplazoViewModel.errorState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorState) {
        errorState?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    LaunchedEffect(Unit) {
        reemplazoViewModel.cargarSolicitudesPendientes()
    }

    ValidateSession(navController = navController) {
        Scaffold(
            topBar = { MenuTopBar(true, true, loginViewModel, navController) },
            bottomBar = {
                MenuBottomBar(
                    navController = navController,
                    userRole = loginViewModel.getUserRole()
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(padding)
            ) {
                if (loading && solicitudes.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(80.dp))

                                Text(
                                    text = "Solicitudes de reemplazo",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )

                                Divider(
                                    modifier = Modifier.width(270.dp),
                                    thickness = 1.dp,
                                    color = Color.LightGray
                                )

                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }

                        if (solicitudes.isEmpty()) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "No hay solicitudes de reemplazo",
                                        color = Color.White
                                    )
                                }
                            }
                        } else {
                            items(solicitudes) { solicitud ->
                                SolicitudReemplazoCard(
                                    solicitud = solicitud,
                                    onClick = {
                                        if (solicitud.estatus == "PENDIENTE") {
                                            navController.navigate("detalleReemplazo/${solicitud.idETS}/${solicitud.docenteRFC}")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SolicitudReemplazoCard(
    solicitud: Reemplazo,
    onClick: () -> Unit
) {
    val statusColor = when (solicitud.estatus) {
        "PENDIENTE" -> Color(0xFFFFA000) // Amber
        "APROBADO" -> Color(0xFF4CAF50)  // Green
        "RECHAZADO" -> Color(0xFFF44336) // Red
        else -> Color.Gray
    }

    val statusBackground = when (solicitud.estatus) {
        "PENDIENTE" -> Color(0x33FFA000) // Amber claro
        "APROBADO" -> Color(0x334CAF50)  // Green claro
        "RECHAZADO" -> Color(0x33F44336) // Red claro
        else -> Color.LightGray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = solicitud.estatus == "PENDIENTE", onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = when(solicitud.estatus) {
                "PENDIENTE" -> Color(0x33FFA000)
                "APROBADO" -> Color(0x334CAF50)
                else -> Color(0x33F44336)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ETS #${solicitud.idETS}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BlueBackground
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(statusColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = solicitud.estatus,
                        color = statusColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )

            Text(
                text = "Docente: ${solicitud.docenteRFC}",
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Motivo: ${solicitud.motivo}",
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            if (solicitud.estatus == "APROBADO") {
                Text(
                    text = "",
                    color = Color.DarkGray
                )
            }
        }
    }
}