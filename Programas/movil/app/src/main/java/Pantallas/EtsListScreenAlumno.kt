package Pantallas

import androidx.compose.foundation.layout.Row
import Pantallas.Plantillas.BuscadorConLista
import Pantallas.components.MenuBottomBar
import Pantallas.components.ValidateSession
import android.content.Context
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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

//      ============= BUSCADOR =============

        LaunchedEffect(username) {
            viewModel.fetchETSInscritos(username)
        }

        Scaffold(
            bottomBar = { MenuBottomBar(navController = navController, userRole) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(start = 16.dp, top = 0.dp, end = 16.dp)
            ) {

                // Barra de búsqueda
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp)
                ) {
                    Text(
                        text = "Lista de ETS",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
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


                Spacer(modifier = Modifier.height(25.dp))

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
                            .padding(padding)
                    ) {

                        BuscadorConLista(
                            lista = etsInscritos,
                            filtro = { ets, query ->
                                ets.idPeriodo.contains(query, ignoreCase = true) ||
                                        ets.turno.contains(query, ignoreCase = true) ||
                                        ets.fecha.contains(query, ignoreCase = true) ||
                                        ets.unidadAprendizaje.contains(query, ignoreCase = true)
                            },
                            onItemClick = {},
                            placeholder = "Buscar por nombre o boleta",
                            itemContent = { ets ->

                                EtsACardButton(
                                    navController = navController,
                                    idETS = ets.idETS,
                                    idPeriodo = ets.idPeriodo,
                                    Turno = ets.turno,
                                    Fecha = ets.fecha,
                                    UnidadAprendizaje = ets.unidadAprendizaje
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
                                            .weight(1f)
                                            .height(35.dp)
                                            .background(Color(0xFFF5F5F5))
                                            .clickable(onClick = { })
                                    ) {
                                        Text(
                                            text = "Todos",
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = TextAlign.Center,
                                        )
                                    }

                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(35.dp)
                                            .background(Color(0xFFF5F5F5))
                                            .clickable(onClick = { })
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = "Mis ETS",
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(15.dp))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EtsACardButton(
    navController: NavController,
    idETS: Int,
    idPeriodo: String,
    Turno: String,
    Fecha: String,
    UnidadAprendizaje: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("unicETSDetail/$idETS")
            },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.Black),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEAEAEA) // Color similar al fondo de la tarjeta en la imagen
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = "Unidad de Aprendizaje: $UnidadAprendizaje", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Periodo: $idPeriodo", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Fecha: $Fecha", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Turno: $Turno", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}