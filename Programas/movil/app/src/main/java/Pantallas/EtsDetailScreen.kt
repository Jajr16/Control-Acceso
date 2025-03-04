package Pantallas

import Pantallas.components.MenuBottomBar
import Pantallas.components.ValidateSession
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba3.Views.EtsInfoViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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

        Scaffold(
            bottomBar = {
                MenuBottomBar(navController = navController, userRole)
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(padding)
            ) {
                // Encabezado
                Text(
                    text = if (etsDetail != null) {
                        val ets3 = etsDetail!!.ets
                        "Detalles del ETS de ${ets3.unidadAprendizaje} "
                    } else {
                        "Detalles del ETS"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
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

                // Contenido principal con LazyColumn
                if (isLoading) {
                    Box(
                        modifier = Modifier.weight(1f), // ✅ Ocupar el espacio restante
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cargando...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }
                } else if (etsDetail != null) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f) // ✅ Ocupar el espacio restante
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item { StyledCard(title = "Unidad de Aprendizaje", content = etsDetail!!.ets.unidadAprendizaje) }
                        item { StyledCard(title = "Tipo de ETS", content = if (etsDetail!!.ets.tipoETS == "O") "Ordinario" else "Especial") }
                        item { StyledCard(title = "Periodo", content = etsDetail!!.ets.idPeriodo) }
                        item { StyledCard(title = "Fecha", content = etsDetail!!.ets.fecha) }
                        item { StyledCard(title = "Turno", content = etsDetail!!.ets.turno) }
                        item { StyledCard(title = "Cupo", content = etsDetail!!.ets.cupo.toString()) }
                        item { StyledCard(title = "Duración", content = etsDetail!!.ets.duracion.toString() + " horas") }

                        if (!salonState!!) {
                            etsDetail!!.salon.take(3).forEach { salon ->
                                item {
                                    StyledCard(
                                        title = "Salón ${salon.numSalon}",
                                        content = "Tipo: ${salon.tipoSalon}"
                                    )
                                }
                            }
                        } else {
                            item {
                                StyledCard(
                                    title = "Asignación",
                                    content = "La asignación de salones sigue pendiente"
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Ocurrió un error al desplegar los detalles del ETS",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botón fijo debajo de las tarjetas, sin afectar el BottomBar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { navController.navigate("ListaAlumnos/$idETS") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6c1d45))
                    ) {
                        Text(text = "Ir a la lista de alumnos", color = Color.White)
                    }
                }
            }
        }




    }



}



//                            if (!salonState!!) {
//                                Column (
//                                    modifier = Modifier
//                                        .padding(16.dp)
//                                        .align(Alignment.CenterHorizontally)
//                                ) {
//                                    Text(
//                                        text = "Salon(es) Asignado(s)",
//                                        style = MaterialTheme.typography.titleLarge,
//                                        modifier = Modifier.padding(bottom = 16.dp)
//                                            .align(Alignment.CenterHorizontally),
//                                        color = Color.White,
//                                        textAlign = TextAlign.Center
//                                    )
//
//                                    Divider(
//                                        modifier = Modifier
//                                            .padding(vertical = 8.dp)
//                                            .width(300.dp),
//                                        thickness = 1.dp,
//                                        color = Color.White
//                                    )
//                                }
//                                LazyColumn(
//                                    modifier = Modifier
//                                        .weight(1.6f)
//                                        .padding(16.dp),
//                                    verticalArrangement = Arrangement.spacedBy(8.dp),
//                                    horizontalAlignment = Alignment.CenterHorizontally
//                                ) {
//
//                                    // Mostrar salones
//                                    salones.take(3).forEach { salon -> // Limita a 3 salones
//                                        item {
//                                            SalonCard(
//                                                numSalon = salon.numSalon,
//                                                tipoSalon = salon.tipoSalon
//                                            )
//                                        }
//                                    }
//                                }
//                            } else {
//                                LazyColumn(
//                                    modifier = Modifier
//                                        .weight(1f)
//                                        .padding(16.dp),
//                                    verticalArrangement = Arrangement.spacedBy(8.dp),
//                                    horizontalAlignment = Alignment.CenterHorizontally
//                                ) {
//                                    item {
//                                        Column {
//                                            Text(
//                                                text = "La asignación de salones sigue pendiente.",
//                                                style = MaterialTheme.typography.titleLarge,
//                                                modifier = Modifier.padding(bottom = 16.dp)
//                                                    .align(Alignment.CenterHorizontally),
//                                                color = Color.White,
//                                                textAlign = TextAlign.Center
//                                            )
//                                        }
//                                    }
//                                }
//                            }


//                    Spacer(modifier = Modifier.height(1.dp))
//
//                    if (userRole != "Alumno") {
//                        // Botón fijo en la parte inferior
//                        Box(
//                            modifier = Modifier
//                                .align(Alignment.BottomCenter)
//                                .padding(bottom = 4.dp)
//                        ) {
//                            Button(
//                                onClick = {
//                                    navController.navigate("ListaAlumnos/$idETS")
//                                },
//                                colors = ButtonDefaults.buttonColors(
//                                    containerColor = Color(
//                                        0xFF6c1d45
//                                    )
//                                )
//                            ) {
//                                Text(text = "Ir a la lista de alumnos", color = Color.White)
//                            }
//                        }
//                    }


//@Composable
//fun SalonCard(numSalon: String, tipoSalon: String) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 8.dp, vertical = 4.dp), // Márgenes de cada tarjeta
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Elevación con Material 3
//        shape = RoundedCornerShape(8.dp) // Bordes redondeados
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp), // Espaciado interno
//            verticalArrangement = Arrangement.spacedBy(8.dp) // Espaciado entre filas
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text(
//                    text = "Número de salón:",
//                    style = MaterialTheme.typography.bodyMedium
//                )
//                Text(
//                    text = numSalon.toString(),
//                    style = MaterialTheme.typography.bodyMedium,
//                    textAlign = TextAlign.End
//                )
//            }
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text(
//                    text = "Tipo de salón:",
//                    style = MaterialTheme.typography.bodyMedium
//                )
//                Text(
//                    text = tipoSalon,
//                    style = MaterialTheme.typography.bodyMedium,
//                    textAlign = TextAlign.End
//                )
//            }
//        }
//    }
//}


@Composable
fun StyledCard(title: String, content: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp), // Márgenes de cada tarjeta
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Elevación con Material 3
        shape = RoundedCornerShape(8.dp) // Bordes redondeados
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Espaciado interno
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$title:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
    }
}