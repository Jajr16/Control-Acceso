package Pantallas

import androidx.compose.foundation.layout.Row
import Pantallas.components.BuscadorConLista
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
fun EtsListScreenAlumno(navController: NavController,
                        viewModel: EtsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
                        loginViewModel: LoginViewModel
) {

    val userRole = loginViewModel.getUserRole()

    ValidateSession(navController = navController) {
        val etsInscritos by viewModel.etsInscritos.collectAsState()
        val isLoading by remember { viewModel.loadingState }.collectAsState()

        val sharedPreferences = navController.context
            .getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""

//      ============= FILTRO DE ETS =============
        var selectedFilter by remember { mutableStateOf("Todos") }
        val filteredList = when (selectedFilter) {
            "Todos" -> etsInscritos
            "Mis ETS" -> etsInscritos.filter { it.inscrito == true }
            else -> etsInscritos
        }

        LaunchedEffect(username) {
            viewModel.fetchETSInscritos(username)
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
                    .padding(start = 16.dp, top = 90.dp, end = 16.dp)
            ) {

                // Barra de búsqueda
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Lista de ETS",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Divider(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .width(270.dp),
                        thickness = 1.dp,
                        color = Color.LightGray
                    )
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 0.dp, bottom = 110.dp)
                    ) {
//                      ============= BUSCADOR =============
                        BuscadorConLista(
                            lista = filteredList,
                            filtro = { ets, query ->
                                ets.idPeriodo.contains(query, ignoreCase = true) ||
                                        ets.turno.contains(query, ignoreCase = true) ||
                                        ets.fecha.contains(query, ignoreCase = true) ||
                                        ets.unidadAprendizaje.contains(query, ignoreCase = true)
                            },
                            onItemClick = {},
                            placeholder = "Buscar por nombre",
                            itemContent = { ets ->

                                EtsACardButton(
                                    navController = navController,
                                    idETS = ets.idETS,
                                    idPeriodo = ets.idPeriodo,
                                    Turno = ets.turno,
                                    Fecha = ets.fecha,
                                    UnidadAprendizaje = ets.unidadAprendizaje,
                                    carrera = ets.carrera

                                )
                            },
                            additionalContent = {

                                Spacer(modifier = Modifier.height(15.dp))

                                Row (
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ){
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .background(
                                                if (selectedFilter == "Todos") Color.Gray else Color(
                                                    0xFFF5F5F5
                                                )
                                            )
                                            .clickable(onClick = { selectedFilter = "Todos" })
                                            .weight(1f)
                                            .height(35.dp)
                                            .padding(4.dp)
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
                                                if (selectedFilter == "Mis ETS") Color.Gray else Color(
                                                    0xFFF5F5F5
                                                )
                                            )
                                            .clickable(onClick = { selectedFilter = "Mis ETS" })
                                            .weight(1f)
                                            .height(35.dp)
                                            .padding(4.dp)
                                    ) {
                                        Text(
                                            text = "Mis ETS",
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = TextAlign.Center,
                                            color = if (selectedFilter == "Mis ETS") Color.White else Color.Black
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(1.dp))
                            }
                        )
                    }
                }
            }
        }
    }
}