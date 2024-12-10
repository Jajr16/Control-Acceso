package Pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun EtsDetailScreen(
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
    Scaffold(
        topBar = { MenuTopBar(navController = navController, title = "Detalles del ETS") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp), // Espaciado general
            verticalArrangement = Arrangement.spacedBy(8.dp), // Espaciado entre elementos
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Lista estilizada
            StyledCard(title = "Tipo de ETS", content = tipo)
            StyledCard(title = "Unidad Académica", content = unidad)
            StyledCard(title = "Docente", content = docente)
            StyledCard(title = "Coordinador", content = coordinador)
            StyledCard(title = "Periodo", content = periodo)
            StyledCard(title = "Fecha", content = fecha)
            StyledCard(title = "Horario", content = horario)
            StyledCard(title = "Salón", content = salon)
            StyledCard(title = "Cupo", content = cupo)
        }
    }
}

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