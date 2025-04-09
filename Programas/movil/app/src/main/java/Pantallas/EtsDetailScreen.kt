package Pantallas

import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.prueba3.Clases.Salon
import com.example.prueba3.Views.EtsInfoViewModel
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.ui.theme.BlueBackground

@Composable
fun EtsDetailScreen(
    navController: NavController,
    idETS: Int,
    viewModel: EtsInfoViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    loginViewModel: LoginViewModel
) {
    val userRole = loginViewModel.getUserRole()

    ValidateSession(navController = navController) {
        val etsDetail by remember { viewModel.etsDetailState }.collectAsState()
        val salonState by remember { viewModel.salonDetailState }.collectAsState()
        val isLoading by remember { viewModel.loadingState }.collectAsState()

        LaunchedEffect(idETS) {
            viewModel.fetchEtsDetail(idETS)
        }

        var savedRole by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            savedRole = loginViewModel.getUserRole()
        }

        Scaffold(
            topBar = {
                MenuTopBar(
                    true, true, loginViewModel,
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
                        .padding(bottom = 80.dp)
                ) {
                    // Encabezado
                    Text(
                        text = if (etsDetail != null) {
                            "Detalles del ETS de ${etsDetail!!.ets.unidadAprendizaje}"
                        } else {
                            "Detalles del ETS"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth(),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Divider(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .width(270.dp)
                            .align(Alignment.CenterHorizontally),
                        thickness = 1.dp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Contenido principal con una sola StyledCard
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cargando...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )
                        }
                    } else if (etsDetail != null) {
                        SingleStyledCard(
                            unidadAprendizaje = etsDetail!!.ets.unidadAprendizaje,
                            tipoETS = etsDetail!!.ets.tipoETS,
                            idPeriodo = etsDetail!!.ets.idPeriodo,
                            fecha = etsDetail!!.ets.fecha,
                            turno = etsDetail!!.ets.turno,
                            cupo = etsDetail!!.ets.cupo,
                            duracion = etsDetail!!.ets.duracion,
                            salon = etsDetail!!.salon,
                            salonState = salonState!!,
                            hora = etsDetail!!.ets.hora,
                            onRequestReplacement = {
                                navController.navigate("solicitarReemplazo/${etsDetail!!.ets.unidadAprendizaje}")                            }
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Ocurrió un error al desplegar los detalles del ETS",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )
                        }
                    }
                }

                // Botón fijo en la parte inferior, sin afectar el BottomBar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (savedRole == "Personal Academico" || savedRole == "Docente") {
                        Button(
                            onClick = {
                                navController.navigate("listaAlumnos/$idETS")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6c1d45))
                        ) {
                            Text(text = "Ir a la lista de alumnos", color = Color.White)
                        }
                    } else if (savedRole == "Personal Seguridad") {
                        Button(
                            onClick = { navController.navigate("ListaAlumnos/$idETS") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6c1d45))
                        ) {
                            Text(text = "Ale pon tu boton XD", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SingleStyledCard(
    unidadAprendizaje: String,
    tipoETS: String,
    idPeriodo: String,
    fecha: String,
    turno: String,
    cupo: Int,
    duracion: Int,
    salon: List<Salon>,
    salonState: Boolean,
    hora: String,
    onRequestReplacement: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Texto en formato de aviso
            Text(
                text = buildString {
                    append("El ETS es de la unidad de aprendizaje ")
                    append("$unidadAprendizaje\n\n")
                    append("Es de tipo de ETS  ")
                    append("${if (tipoETS == "O") "Ordinario" else "Especial"}\n\n")
                    append("Se presentara en el periodo ")
                    append("$idPeriodo\n\n")
                    append("La fecha de aplicación es ")
                    append("$fecha\n\n")
                    append("En el turno ")
                    append("$turno\n\n")
                    append("Con una duracion de ")
                    append("$duracion horas\n\n")
                    if (!salonState) {
                        append("Salones asignados:  \n")
                        salon.take(3).forEach { salon ->
                            append("• Salón ${salon.numSalon} (${salon.tipoSalon})\n\n")
                        }
                    } else {
                        append("Asignación de salones:\n")
                        append("• Pendiente\n")
                    }
                    append("El examen comienza a las $hora horas")
                },
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                lineHeight = 24.sp
            )

            // Botón para solicitar reemplazo
            OutlinedButton(
                onClick = onRequestReplacement,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Solicitar reemplazo",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Solicitar reemplazo")
            }
        }
    }
}