package Pantallas

import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba3.EtsViewModel

    @Composable
    fun EtsPeriodScreen(navController: NavController, viewModel: EtsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
        val etsList by viewModel.etsList.collectAsState()

        Scaffold(
            topBar = { MenuTopBar(navController = navController, title = "ETS") }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Divider(color = Color.Gray, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(etsList) { ets ->
                        EtsCardButton(
                            navController = navController,
                            tipo = ets.tipo,
                            unidad = ets.unidad,
                            docente = ets.docente,
                            coordinador = ets.coordinador,
                            periodo = ets.periodo,
                            fecha = ets.fecha,
                            horario = ets.horario,
                            salon = ets.salon,
                            cupo = ets.cupo
                        )
                    }
                }
            }
        }
    }

@Composable
fun EtsCardButton(
    navController: NavController,
    tipo: String,
    unidad: String,
    docente: String,
    coordinador: String,
    periodo: String,
    fecha: String,
    horario: String,
    salon: String,
    cupo: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                val encodedType = Uri.encode(tipo)
                val encodeUni = Uri.encode(unidad)
                val encodedocente = Uri.encode(docente)
                val encodecor = Uri.encode(coordinador)
                val encodeperiodo = Uri.encode(periodo)
                val encodedDate = Uri.encode(fecha)
                val encodehorario = Uri.encode(horario)
                val encodesalon = Uri.encode(salon)
                val encodedDcupo = Uri.encode(cupo)

                navController.navigate("etsDetail/$encodedType/$encodeUni/$encodedocente/$encodecor/$encodeperiodo/$encodedDate/$encodehorario/$encodesalon/$encodedDcupo")
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
            Text(text = "Unidad Académica: $unidad", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Periodo: $periodo", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Fecha: $fecha", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Carrera: $docente", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Turno: $horario", style = MaterialTheme.typography.bodyLarge)
        }
    }
}