package Pantallas

import Pantallas.Reutilizables.EtsACardButton
import Pantallas.Plantillas.MenuBottomBar
import Pantallas.Plantillas.MenuTopBar
import Pantallas.components.ValidateSession
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba3.Views.EtsViewModel
import com.example.prueba3.Views.LoginViewModel
import com.example.prueba3.ui.theme.BlueBackground

@Composable
fun EtsListScreen(
    navController: NavController,
    viewModel: EtsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    loginViewModel: LoginViewModel
) {
    val userRole = loginViewModel.getUserRole()

    ValidateSession(navController = navController) {
        val etsInscritos by viewModel.etsInscritos.collectAsState()
        val isLoading by remember { viewModel.loadingState }.collectAsState()
        val etsInscritos2 by viewModel.etsInscritosaplica.collectAsState()

        val sharedPreferences = navController.context
            .getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""

        //      ============= FILTRO DE ETS =============
        var selectedFilter by remember { mutableStateOf("Todos") }
        val filteredList = when (selectedFilter) {
            "Todos" -> etsInscritos
            "Mis ETS" -> etsInscritos2
            else -> etsInscritos
        }

        LaunchedEffect(username) {
            viewModel.fetchETSInscritos(username)
            viewModel.fetchETSInscritosAplica(username)
        }

        Scaffold(
            topBar = {
                MenuTopBar(
                    true, true, loginViewModel,
                    navController
                )
            },
            bottomBar = { MenuBottomBar(navController = navController, userRole) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(padding) // Aplicar el padding del Scaffold al Column principal
            ) {

                // Barra de búsqueda (FIJO)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = "Lista de ETS",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Divider(
                        modifier = Modifier
                            .padding(vertical = 0.dp)
                            .width(270.dp),
                        thickness = 1.dp,
                        color = Color.LightGray
                    )
                }

                // Filtros (FIJO)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, bottom = 10.dp, start = 16.dp, end = 16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(
                                if (selectedFilter == "Todos") Color.Gray else Color(0xFFF5F5F5)
                            )
                            .clickable(onClick = { selectedFilter = "Todos" })
                            .weight(1f)
                            .height(35.dp)
                            .padding(2.dp)
                    ) {
                        Text(
                            text = "Todos",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = if (selectedFilter == "Todos") Color.White else Color.Black
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(
                                if (selectedFilter == "Mis ETS") Color.Gray else Color(0xFFF5F5F5)
                            )
                            .clickable(onClick = { selectedFilter = "Mis ETS" })
                            .weight(1f)
                            .height(35.dp)
                            .padding(2.dp)
                    ) {
                        Text(
                            text = "Mis ETS",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = if (selectedFilter == "Mis ETS") Color.White else Color.Black
                        )
                    }
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cargando...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }
                } else {
                    if (filteredList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay ETS creados",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        // Lista scrollable de ETS
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 16.dp, end = 16.dp, bottom = 60.dp),
                            contentPadding = PaddingValues(top = 10.dp)
                        ) {
                            items(filteredList) { ets ->
                                EtsACardButton(
                                    navController = navController,
                                    idETS = ets.idETS,
                                    idPeriodo = ets.idPeriodo,
                                    Turno = ets.turno,
                                    Fecha = ets.fecha,
                                    UnidadAprendizaje = ets.unidadAprendizaje
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

//    val userRole = loginViewModel.getUserRole()

//    val etsInscritos by viewModel.etsInscritosaplica.collectAsState()

//    ValidateSession(navController = navController) {
//        val etsInscritos by viewModel.etsInscritosaplica.collectAsState()
//        val isLoading by remember { viewModel.loadingState }.collectAsState()
//
//        val sharedPreferences = navController.context
//            .getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
//        val username = sharedPreferences.getString("username", "") ?: ""
//
//        LaunchedEffect(username) {
//            viewModel.fetchETSInscritosAplica(username)
//        }
//
//        Scaffold(
//            bottomBar = { MenuBottomBar(navController = navController, userRole) }
//        ) { padding ->
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(BlueBackground)
//            ) {
//
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 70.dp)
//                ) {
//                    Text(
//                        text = "ETS inscritos",
//                        style = MaterialTheme.typography.titleLarge,
//                        modifier = Modifier
//                            .padding(bottom = 16.dp)
//                            .align(Alignment.CenterHorizontally),
//                        fontWeight = FontWeight.Bold,
//                        color = Color.White,
//                        textAlign = TextAlign.Center
//                    )
//
//                    Divider(
//                        modifier = Modifier
//                            .padding(vertical = 8.dp)
//                            .width(270.dp),
//                        thickness = 1.dp,
//                        color = Color.White
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(25.dp))
//
//
//                if (isLoading) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(padding),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = "Cargando...",
//                            style = MaterialTheme.typography.bodyLarge,
//                            color = Color.White
//                        )
//                    }
//                } else if (etsInscritos.isEmpty()) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(padding),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = "No tienes ETS inscritos.",
//                            style = MaterialTheme.typography.bodyLarge,
//                            color = Color.White
//                        )
//                    }
//                } else {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(padding),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.Center
//                    ) {
//                        LazyColumn(
//                            modifier = Modifier.fillMaxSize()
//                        ) {
//                            items(etsInscritos) { ets ->
//                                EtsACardButton(
//                                    navController = navController,
//                                    idETS = ets.idETS,
//                                    idPeriodo = ets.idPeriodo,
//                                    Turno = ets.turno,
//                                    Fecha = ets.fecha,
//                                    UnidadAprendizaje = ets.unidadAprendizaje
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//}



